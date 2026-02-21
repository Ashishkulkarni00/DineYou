import Keycloak from "keycloak-js";
const keycloakServiceHost = import.meta.env.VITE_KEYCLOAK_SERVICE_HOST;

let keycloak;

export const initKeycloak = () => {
  return new Promise((resolve, reject) => {
    const keycloakConfig = {
      // url: "http://localhost:8585",
      url: keycloakServiceHost,
      realm: "dineyou",
      clientId: "public_client",
    };

    keycloak = new Keycloak(keycloakConfig);

    keycloak
    .init({
      onLoad: "check-sso",
      silentCheckSsoRedirectUri:
        window.location.origin + "/silent-check-sso.html",
      pkceMethod: "S256",
      checkLoginIframe: false,
      token: localStorage.getItem("kc_token") || undefined,
      refreshToken: localStorage.getItem("kc_refresh_token") || undefined,
    })
    // keycloak
    //   .init({
    //     onLoad: "login-required", // or "check-sso"
    //     pkceMethod: "S256",
    //     checkLoginIframe: false,
    //   })
      .then((authenticated) => {
        console.log(
          authenticated
            ? "✅ Keycloak: Authenticated"
            : "❌ Keycloak: Not authenticated",
        );

        if (authenticated) {
          storeTokens();
          startTokenRefresh();
        }

        resolve(authenticated);
      })
      .catch((err) => {
        console.error("❌ Keycloak init failed", err);
        reject(err);
      });
  });
};

export const getKeycloak = () => keycloak;

/* 🔐 Store tokens */
const storeTokens = () => {
  if (!keycloak?.token) return;

  console.log("💾 Storing tokens in localStorage");
  localStorage.setItem("kc_token", keycloak.token);
  localStorage.setItem("kc_refresh_token", keycloak.refreshToken);
};

/* 🔄 Refresh tokens periodically */
const startTokenRefresh = () => {
  console.log("⏱ Starting token refresh loop (every 2 min)");

  setInterval(
    async () => {
      try {
        const refreshed = await keycloak.updateToken(30);
        if (refreshed) console.log("🔄 Token refreshed");
      } catch (err) {
        console.warn("⚠️ Token refresh failed — redirecting to login");
        keycloak.login();
      }
    },
    2 * 60 * 1000,
  );
};
