package cart_service.util;

import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class RequestInfoProvider {

    private ServletRequestAttributes getAttrs() {
        return (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    }

    public String getKeycloakSessionId() {
        var attrs = getAttrs();
        return attrs != null ? (String) attrs.getRequest().getAttribute("kc_session_id") : null;
    }

    public String getAnonymousSessionId() {
        var attrs = getAttrs();
        return attrs != null ? (String) attrs.getRequest().getAttribute("anonymousSessionId") : null;
    }

    public String getRequestId() {
        var attrs = getAttrs();
        return attrs != null ? (String) attrs.getRequest().getAttribute("requestId") : null;
    }
}