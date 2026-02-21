package order_service.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import order_service.model.RequestLogs;
import order_service.service.RequestLoggingService;
import order_service.util.DateTimeUtil;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.Principal;
import java.util.UUID;


@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final String REQUEST_ID_HEADER = "request-id";
    private static final String ANONYMOUS_SESSION_HEADER = "x-anonymous-session-id";

    @Autowired
    private DateTimeUtil dateTimeUtil;

    @Autowired
    private RequestLoggingService requestLoggingService;

    // Skip logging for static uploads
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();

        // Skip uploads folder
        if (path.startsWith("/uploads/")) {
            return true;
        }

        // Skip common static file extensions
        if (path.matches(".*\\.(css|js|png|jpg|jpeg|gif|webp|svg|ico|woff2|woff|ttf)$")) {
            return true;
        }

        // Optionally skip HTML pages if not an API
        if (!path.startsWith("/api/")) {
            return true;
        }

        return false;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {


        if(shouldNotFilter(request)){
            filterChain.doFilter(request, response);
            return;
        }

        long startTime = System.currentTimeMillis();
        String requestTimestamp = dateTimeUtil.getDateTime();

        // -------------------------------
        // REQUEST ID & CLIENT SESSION
        // -------------------------------
        String requestId = headerOrDefault(request, REQUEST_ID_HEADER, UUID.randomUUID().toString());
        String anonymousSessionId = headerOrDefault(request, ANONYMOUS_SESSION_HEADER, UUID.randomUUID().toString() + " - generated-from-filter");
        String queryParams = request.getQueryString();
        response.setHeader(REQUEST_ID_HEADER, requestId);
        response.setHeader(ANONYMOUS_SESSION_HEADER, anonymousSessionId);

        request.setAttribute("requestId", requestId);
        MDC.put("requestId", requestId);
        request.setAttribute("anonymousSessionId", anonymousSessionId);

        // -------------------------------
        // Extract headers
        // -------------------------------
        String deviceType = headerOrDefault(request, "x-device-type", "unknown");
        String userAgent = headerOrDefault(request, "x-user-agent", request.getHeader("User-Agent"));
        String clientIp = headerOrDefault(request, "x-client-ip", getClientIp(request));
        String appVersion = headerOrDefault(request, "x-app-version", "unknown");
        String timezone = headerOrDefault(request, "x-timezone", "unknown");

        // -------------------------------
        // Auth info (if JWT present)
        // -------------------------------
        String userId = null;
        String keycloakSessionId = null;
        String s2sClientId = null;

        Principal principal = request.getUserPrincipal();
        if (principal instanceof JwtAuthenticationToken jwtToken) {
            Jwt jwt = jwtToken.getToken();
            userId = jwt.getClaimAsString("sub");
            keycloakSessionId = jwt.getClaimAsString("sid");
            s2sClientId = jwt.getClaimAsString("client_id");
            request.setAttribute("kc_session_id", keycloakSessionId);
        }

        Exception exceptionCaught = null;
        try {
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            exceptionCaught = ex;
            throw ex; // rethrow after capturing
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            int status = response.getStatus();
            String responseTimestamp = dateTimeUtil.getDateTime();

            RequestLogs requestLogs = RequestLogs.builder()
                    .queryParams(queryParams)
                    .interactionType("API_CALL")
                    .serviceName("order-service")
                    .requestId(requestId)
                    .spanId(UUID.randomUUID().toString())
                    .anonymousSessionId(anonymousSessionId)
                    .s2sClientId(s2sClientId)
                    .userId(userId)
                    .keycloakSessionId(keycloakSessionId)
                    .requestPath(request.getRequestURI())
                    .httpMethod(request.getMethod())
                    .requestTimestamp(requestTimestamp)
                    .responseTimestamp(responseTimestamp)
                    .durationMs(duration)
                    .statusCode(status)
                    .success(status < 400)
                    .deviceType(deviceType)
                    .userAgent(userAgent)
                    .platform(detectPlatform(userAgent))
                    .ipAddress(clientIp)
                    .appVersion(appVersion)
                    .timezone(timezone)
                    .errorCode(exceptionCaught != null ? exceptionCaught.getClass().getSimpleName() : null)
                    .errorMessage(exceptionCaught != null ? exceptionCaught.getMessage() : null)
                    .errorStackTrace(exceptionCaught != null ? getStackTrace(exceptionCaught) : null)
                    .timestamp(dateTimeUtil.getDateTime())
                    .build();

            // Async logging to avoid slowing down API
            requestLoggingService.saveRequestLog(requestLogs);
        }
    }




    private String headerOrDefault(HttpServletRequest req, String header, String defaultValue) {
        String value = req.getHeader(header);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    private String getClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null) return xf.split(",")[0];
        return request.getRemoteAddr();
    }

    private String detectPlatform(String userAgent) {
        if (userAgent == null) return "unknown";
        String ua = userAgent.toLowerCase();
        if (ua.contains("android")) return "Android";
        if (ua.contains("iphone") || ua.contains("ipad")) return "iOS";
        return "Web";
    }

    private String getStackTrace(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }

}
