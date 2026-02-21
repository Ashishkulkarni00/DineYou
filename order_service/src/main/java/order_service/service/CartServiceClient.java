package order_service.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import order_service.dto.ValidateCartAndMenuItemsReqDto;
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
import java.util.Map;


@Service
public class CartServiceClient {

    @Autowired
    private Rest rest;

    @Autowired
    Gson gson;

    @Value("${cart.service.base-url}")
    private String cartServiceBaseUrl;

    @Value("${keycloak.order-service-client-id}")
    private String clientId;

    @Value("${keycloak.order-service-client-secret}")
    private String clientSecret;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    private RequestInfoProvider requestInfoProvider;

    public boolean validateCartAndMenuItems(ValidateCartAndMenuItemsReqDto validateCartAndMenuItemsReqDto, String requestId) {

        String url = cartServiceBaseUrl + "/validate-cart-and-menu-items";
        String jsonBody = gson.toJson(validateCartAndMenuItemsReqDto);
        RequestBody requestBody = RequestBody.create(jsonBody, MediaType.parse("application/json"));

        String accessToken = accessTokenService.getAccessToken(clientId,clientSecret);
        Request request = new Request.Builder()
                .url(url)
                .header("request-id", requestId)
                .header("Authorization", "Bearer " + accessToken)
                .header("x-anonymous-session-id", requestInfoProvider.getAnonymousSessionId())
                .method("POST", requestBody)
                .build();

        try (Response response = rest.okHttpClient().newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            JsonObject json = !body.isEmpty() ? gson.fromJson(body, JsonObject.class) : null;

            if (!response.isSuccessful()) {
                String errorCode = "UNKNOWN";
                String errorMessage = "Unknown error from Cart Service";

                if (json != null && json.has("error")) {
                    JsonObject errorObj = json.getAsJsonObject("error");
                    if (errorObj.has("code")) errorCode = errorObj.get("code").getAsString();
                    if (errorObj.has("message")) errorMessage = errorObj.get("message").getAsString();
                }

                if(response.code() == 401){
                    throw new AuthenticationException();
                }

                if(response.code() == 403){
                    throw new AccessDeniedException("Invalid permissions to call cart service");
                }

                if (response.code() == 404 && "RES_404".equals(errorCode)) {
                    return false;
                }

                throw new ApplicationException(ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "Cart Service responded with error: " + errorMessage, "cartService");
            }

            if (json == null || !json.has("success") || !json.get("success").getAsBoolean()) {
                throw new ApplicationException(ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "Unexpected response structure from cart Service", "cartService");
            }

            // Correct handling for primitive `data: true`
            if (json.has("data") && json.get("data").isJsonPrimitive()) {
                return json.get("data").getAsBoolean();
            } else {
                throw new ApplicationException(ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "Invalid 'data' field in Cart Service response", "cartService");
            }

        }catch (AuthenticationException e){
            throw new ApplicationException(
                    ErrorCode.UNAUTHORIZED,
                    "Failed to authenticate with cart Service",
                    "cartService"
            );
        }
        catch (AccessDeniedException e){
            throw new ApplicationException(
                    ErrorCode.FORBIDDEN,
                    "Invalid access, for cart service",
                    "cartService"
            );
        }
        catch (Exception e) {
            throw new ApplicationException(
                    ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                    "Failed to contact cart service",
                    "cartService"
            );
        }

    }


    public JsonObject updateCartStatus(String cartStatus, Long cartId, String requestId) {

        HttpUrl url = HttpUrl.parse(cartServiceBaseUrl + "/updateCartStatus")
                .newBuilder()
                .addQueryParameter("cartId",String.valueOf(cartId))
                .addQueryParameter("cartStatus", cartStatus)
                .build();


        String accessToken = accessTokenService.getAccessToken(clientId,clientSecret);
        RequestBody emptyBody = RequestBody.create(new byte[0], null);
        Request request = new Request.Builder()
                .url(url)
                .header("request-id", requestId)
                .header("Authorization", "Bearer " + accessToken)
                .header("x-anonymous-session-id", requestInfoProvider.getAnonymousSessionId())
                .put(emptyBody)
                .build();

        try (Response response = rest.okHttpClient().newCall(request).execute()) {
            String body = response.body() != null ? response.body().string() : "";
            JsonObject json = !body.isEmpty() ? gson.fromJson(body, JsonObject.class) : null;

            if (!response.isSuccessful()) {
                String errorCode = "UNKNOWN";
                String errorMessage = "Unknown error from Cart Service";

                if (json != null && json.has("error")) {
                    JsonObject errorObj = json.getAsJsonObject("error");
                    if (errorObj.has("code")) errorCode = errorObj.get("code").getAsString();
                    if (errorObj.has("message")) errorMessage = errorObj.get("message").getAsString();
                }

                if(response.code() == 401){
                    throw new AuthenticationException();
                }

                if(response.code() == 403){
                    throw new AccessDeniedException("Invalid permissions to call cart service");
                }

                throw new ApplicationException(ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "Cart Service responded with error: " + errorMessage, "cartService");
            }

            if (json == null || !json.has("success") || !json.get("success").getAsBoolean()) {
                throw new ApplicationException(ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "Unexpected response structure from cart Service", "cartService");
            }

            // Correct handling for primitive `data: true`
            if (json.has("data")) {
                return json.get("data").getAsJsonObject();
            } else {
                throw new ApplicationException(ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "Invalid 'data' field in Cart Service response", "cartService");
            }

        }catch (AuthenticationException e){
            throw new ApplicationException(
                    ErrorCode.UNAUTHORIZED,
                    "Failed to authenticate with cart Service",
                    "cartService"
            );
        }
        catch (AccessDeniedException e){
            throw new ApplicationException(
                    ErrorCode.FORBIDDEN,
                    "Invalid access, for cart service",
                    "cartService"
            );
        }
        catch (Exception e) {
            throw new ApplicationException(
                    ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                    "Failed to contact cart service",
                    "cartService"
            );
        }

    }


    


}
