package order_service.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import order_service.enums.ErrorCode;
import order_service.exception.ApplicationException;
import order_service.s2sAuth.AccessTokenService;
import order_service.util.RequestInfoProvider;
import order_service.util.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.nio.file.AccessDeniedException;

@Service
public class RestaurantServiceClient {

    @Autowired
    private Rest rest;

    @Autowired
    Gson gson;

    @Value("${restaurant.service.base-url}")
    private String restaurantServiceBaseUrl;

    @Value("${keycloak.order-service-client-id}")
    private String clientId;

    @Value("${keycloak.order-service-client-secret}")
    private String clientSecret;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    RequestInfoProvider requestInfoProvider;

    public boolean doesRestaurantExist(Long restaurantId, String requestId) {

        String url = restaurantServiceBaseUrl + "/" + restaurantId + "/exists";

        String accessToken = accessTokenService.getAccessToken(clientId,clientSecret);

        Request request = new Request.Builder()
                .header("request-id", requestId)
                .header("Authorization", "Bearer " + accessToken)
                .header("x-anonymous-session-id", requestInfoProvider.getAnonymousSessionId())
                .url(url)
                .get()
                .build();

            try (Response response = rest.okHttpClient().newCall(request).execute()) {
            String body = (response.body() != null) ? response.body().string() : "";
            JsonObject json = !body.isEmpty() ? gson.fromJson(body, JsonObject.class) : null;

            if (!response.isSuccessful()) {
                String errorCode = "UNKNOWN";
                String errorMessage = "Unknown error from Restaurant Service";

                if (json != null && json.has("error")) {
                    JsonObject errorObj = json.getAsJsonObject("error");
                    if (errorObj.has("code")) errorCode = errorObj.get("code").getAsString();
                    if (errorObj.has("message")) errorMessage = errorObj.get("message").getAsString();
                }

                if(response.code() == 401){
                    throw new AuthenticationException();
                }

                if(response.code() == 403){
                    throw new AccessDeniedException("Invalid permissions to call restaurant service");
                }
                if (response.code() == 404 && "RES_404".equals(errorCode)) {
                    return false;
                }
                throw new ApplicationException(
                        ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "Restaurant service responded with error: " + errorMessage,
                        "restaurantService"
                );
            }

            // Validate response success and extract 'exists' value
            if (json == null || !json.has("success") || !json.get("success").getAsBoolean()) {
                throw new ApplicationException(
                        ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "Unexpected response structure",
                        "restaurantService"
                );
            }

            return json.getAsJsonObject("data").get("exists").getAsBoolean();

        }
        catch (AuthenticationException e){
            throw new ApplicationException(
                    ErrorCode.UNAUTHORIZED,
                    "Failed to authenticate with restaurant Service",
                    "restaurantService"
            );
        }
        catch (AccessDeniedException e){
            throw new ApplicationException(
                    ErrorCode.FORBIDDEN,
                    "Invalid access, for restaurant service",
                    "restaurantService"
            );
        }
        catch (Exception e) {
            throw new ApplicationException(
                    ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                    "Failed to contact restaurant service",
                    "restaurantService"
            );
        }
    }

}
