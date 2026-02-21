package order_service.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import order_service.dto.MenuItemResDto;
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
import java.util.List;

@Service
public class MenucardServiceClient {

    @Autowired
    private Rest rest;

    @Autowired
    Gson gson;

    @Value("${menucardItem.service.base-url}")
    private String menucardServiceBaseUrl;

    @Value("${keycloak.order-service-client-id}")
    private String clientId;

    @Value("${keycloak.order-service-client-secret}")
    private String clientSecret;

    @Autowired
    private AccessTokenService accessTokenService;

    @Autowired
    RequestInfoProvider requestInfoProvider;


    public List<MenuItemResDto> getAllMenuItems(List<Long> menuItemIds, String requestId) {

        String url = menucardServiceBaseUrl + "/getMenuItemsByIds";

        HttpUrl.Builder urlBuilder = HttpUrl.parse(url).newBuilder();

        for (Long id : menuItemIds) {
            urlBuilder.addQueryParameter("menuItemIds", id.toString());
        }

        HttpUrl finalUrl = urlBuilder.build();

        String accessToken = accessTokenService.getAccessToken(clientId,clientSecret);

        Request request = new Request.Builder()
                .header("request-id", requestId)
                .header("Authorization","Bearer " + accessToken)
                .header("x-anonymous-session-id", requestInfoProvider.getAnonymousSessionId())
                .url(finalUrl)
                .get()
                .build();

        try (Response response = rest.okHttpClient().newCall(request).execute()) {
            String body = (response.body() != null) ? response.body().string() : "";
            JsonObject json = !body.isEmpty() ? gson.fromJson(body, JsonObject.class) : null;

            if (!response.isSuccessful()) {
                
                String errorCode = "UNKNOWN";
                String errorMessage = "Unknown error from Menucard Service";

                if(response.code() == 401){
                    throw new AuthenticationException();
                }

                if(response.code() == 403){
                    throw new AccessDeniedException("Invalid permissions to call Menucard service");
                }

                if (json != null && json.has("error")) {
                    JsonObject errorObj = json.getAsJsonObject("error");
                    if (errorObj.has("code")) errorCode = errorObj.get("code").getAsString();
                    if (errorObj.has("message")) errorMessage = errorObj.get("message").getAsString();
                }

                // For all other errors, throw downstream service exception
                throw new ApplicationException(
                        ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "menucard Service responded with error: " + errorMessage,
                        "menucardService"
                );
            }

            // Validate response success and extract 'exists' value
            if (json == null || !json.has("success") || !json.get("success").getAsBoolean()) {
                throw new ApplicationException(
                        ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                        "Unexpected response structure",
                        "menuCardService"
                );
            }

            JsonArray data = json.getAsJsonArray("data");
            return new Gson().fromJson(data, new TypeToken<List<MenuItemResDto>>(){}.getType());

        }catch (AuthenticationException e){
            throw new ApplicationException(
                    ErrorCode.UNAUTHORIZED,
                    "Failed to authenticate with menucard Service",
                    "menuCardService"
            );
        }
        catch (AccessDeniedException e){
            throw new ApplicationException(
                    ErrorCode.FORBIDDEN,
                    "Invalid access, for menucard service",
                    "menuCardService"
            );
        }
        catch (Exception e) {
            throw new ApplicationException(
                    ErrorCode.DOWNSTREAM_SERVICE_ERROR,
                    "Failed to contact menucard Service",
                    "menuCardService"
            );
        }
    }
}
