package restaurant_service.s2aAuth;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import restaurant_service.enums.ErrorCode;
import restaurant_service.exception.ApplicationException;
import restaurant_service.util.Rest;

@Service
public class AccessTokenService {

    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Autowired
    private Rest rest;

    @Autowired
    Gson gson;

    public String getAccessToken(String clientId, String clientSecret) {
        String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        FormBody formBody = new FormBody.Builder()
                .add("grant_type", "client_credentials")
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();

        Request request = new Request.Builder()
                .url(tokenUrl)
                .post(formBody)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        try (Response response = rest.okHttpClient().newCall(request).execute()) {
            String body = (response.body() != null) ? response.body().string() : "";

            if (!response.isSuccessful()) {
                String errorDescription = "Unknown error from Keycloak token endpoint";

                if (!body.isEmpty()) {
                    try {
                        JsonObject json = gson.fromJson(body, JsonObject.class);
                        if (json != null) {
                            if (json.has("error_description")) {
                                errorDescription = json.get("error_description").getAsString();
                            } else if (json.has("error")) {
                                errorDescription = json.get("error").getAsString();
                            }
                        }
                    } catch (Exception ignored) {}
                }

                throw new ApplicationException(
                        ErrorCode.UNAUTHORIZED,
                        "Failed to get access token from Keycloak: " + errorDescription,
                        "keycloak"
                );
            }

            try {
                JsonObject json = gson.fromJson(body, JsonObject.class);
                if (json != null && json.has("access_token")) {
                    return json.get("access_token").getAsString();
                } else {
                    throw new ApplicationException(
                            ErrorCode.UNAUTHORIZED,
                            "Keycloak response did not contain access_token",
                            "keycloak"
                    );
                }
            } catch (Exception e) {
                throw new ApplicationException(
                        ErrorCode.UNAUTHORIZED,
                        "Failed to parse Keycloak token response",
                        "keycloak"
                );
            }

        } catch (Exception e) {
            throw new ApplicationException(
                    ErrorCode.UNAUTHORIZED,
                    "Failed to contact Keycloak token endpoint",
                    "keycloak"
            );
        }
    }

}
