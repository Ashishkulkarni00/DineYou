import { getKeycloak } from "./keycloak";

export const ensureAuthenticated = async () => {
  const keycloak = getKeycloak();
  if (!keycloak) {
    console.error("Keycloak not initialized");
    return false;
  }

  try {
    const refreshed = await keycloak.updateToken(30);

    if (refreshed) {
      console.log("🔄 Token refreshed successfully");
      localStorage.setItem("kc_token", keycloak.token);
      localStorage.setItem("kc_refresh_token", keycloak.refreshToken);
    }

    if (keycloak.authenticated && keycloak.token) {
      console.log("✅ User authenticated");
      return true;
    } else {
      console.warn("❌ User not authenticated, redirecting...");
      await keycloak.login();
      return false;
    }
  } catch (err) {
    console.error("⚠️ Error validating token:", err);
    await keycloak.login();
    return false;
  }
};
