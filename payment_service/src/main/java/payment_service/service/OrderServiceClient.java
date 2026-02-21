package payment_service.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import payment_service.enums.ErrorCode;
import payment_service.exception.ApplicationException;
import payment_service.s2sAuth.AccessTokenService;
import payment_service.util.RequestInfoProvider;
import payment_service.util.Rest;
import javax.security.sasl.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderServiceClient {

    @Autowired
    private Rest rest;

    @Value("${order.service.base-url}")
    private String orderServiceBaseUrl;

    @Value("${keycloak.payment-service-client-id}")
    private String clientId;

    @Value("${keycloak.payment-service-client-secret}")
    private String clientSecret;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private RequestInfoProvider requestInfoProvider;

    @Autowired
    private Gson gson;

    public boolean doesOrdersExist(List<Long> orderIds, String requestId) {

        String orderIdsParam = orderIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));

        String url = orderServiceBaseUrl + "/exists?orderIds=" + orderIdsParam;

        String accessToken = accessTokenService.getAccessToken(clientId, clientSecret);

        Request request = new Request.Builder()
                .header("request-id", requestId)
                .header("Authorization", "Bearer " + accessToken)
                .header("x-anonymous-session-id",requestInfoProvider.getAnonymousSessionId())
                .url(url)
                .get()
                .build();

        try (Response response = rest.okHttpClient().newCall(request).execute()) {
            String body = (response.body() != null) ? response.body().string() : "";
            JsonObject json = !body.isEmpty() ? gson.fromJson(body, JsonObject.class) : null;

            if (!response.isSuccessful()) {
                String errorCode = "UNKNOWN";
                String errorMessage = "Unknown error from Order Service";

                if (json != null && json.has("error")) {
                    JsonObject errorObj = json.getAsJsonObject("error");
                    if (errorObj.has("code"))
                        errorCode = errorObj.get("code").getAsString();
                    if (errorObj.has("message"))
                        errorMessage = errorObj.get("message").getAsString();
                }

                if (response.code() == 401) {
                    throw new AuthenticationException();
                }

                if (response.code() == 403) {
                    throw new AccessDeniedException("Invalid permissions to call order service");
                }
                // If restaurant not found, handle gracefully
                if (response.code() == 404 && "RES_404".equals(errorCode)) {
                    return false;
                }

                // For all other errors, throw downstream service exception
                throw new ApplicationException(
                        ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "Order Service responded with error: " + errorMessage,
                        "orderService");
            }

            // Validate response success and extract 'exists' value
            if (json == null || !json.has("success") || !json.get("success").getAsBoolean()) {
                throw new ApplicationException(
                        ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "Unexpected response structure",
                        "orderService");
            }

            JsonObject data = json.getAsJsonObject("data");

            for (Long id : orderIds) {
                String idStr = String.valueOf(id);

                // (a) Missing entry in map = error
                if (!data.has(idStr)) {
                    throw new ApplicationException(
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "OrderId " + id + " not found in order-service",
                            "orderService");
                }

                if (!data.get(idStr).getAsBoolean()) {
                    throw new ApplicationException(
                            ErrorCode.RESOURCE_NOT_FOUND,
                            "OrderId " + id + " does not exist",
                            "orderService");
                }
            }

            return true;

        } catch (AuthenticationException e) {
            throw new ApplicationException(
                    ErrorCode.UNAUTHORIZED,
                    "Failed to authenticate with order Service",
                    "restaurantService");
        } catch (AccessDeniedException e) {
            throw new ApplicationException(
                    ErrorCode.FORBIDDEN,
                    "Invalid access, for order service",
                    "orderService");
        } catch (Exception e) {
            throw new ApplicationException(
                    ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                    "Failed to contact Order Service",
                    "orderService");
        }
    }

}
