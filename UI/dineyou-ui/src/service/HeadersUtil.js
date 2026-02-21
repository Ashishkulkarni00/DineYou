// Detect device type
const getDeviceType = () => {
  const ua = navigator.userAgent.toLowerCase();
  if (/mobile|android|iphone|ipad/.test(ua)) return "mobile";
  if (/tablet/.test(ua)) return "tablet";
  return "desktop";
};

// Detect platform
const getPlatform = () => {
  const ua = navigator.userAgent.toLowerCase();
  if (ua.includes("android")) return "Android";
  if (ua.includes("iphone") || ua.includes("ipad")) return "iOS";
  return "Web";
};

export const getHeaders = () => {
  const anonymousSessionId = localStorage.getItem("anonymousSessionId");
  const headers = {
    "x-anonymous-session-id": anonymousSessionId,
    "x-device-type": getDeviceType(),
    "x-user-agent": navigator.userAgent,
    "x-platform": getPlatform(),
    "x-app-version": "1.0.0",
    "x-timezone": Intl.DateTimeFormat().resolvedOptions().timeZone,
  };
  return headers;
};
