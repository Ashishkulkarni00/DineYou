import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import { Provider } from "react-redux";
import { store } from "./store/Strore.js";
import { initKeycloak } from "./service/keycloak.js";

// Uncomment below code to enable developer tools on
// if (import.meta.env.DEV) {
//   import("eruda").then(eruda => eruda.default.init());
// }

const getClientSessionId = () => {
  let anonymousSessionId = localStorage.getItem("anonymousSessionId");
  
  if (!anonymousSessionId) {
    // Check if the secure crypto API is available
    if (typeof crypto !== 'undefined' && crypto.randomUUID) {
      anonymousSessionId = crypto.randomUUID();
    } else {
      // Fallback for non-HTTPS / Local IP testing
      anonymousSessionId = ([1e7]+-1e3+-4e3+-8e3+-1e11).replace(/[018]/g, c =>
        (c ^ crypto.getRandomValues(new Uint8Array(1))[0] & 15 >> c / 4).toString(16)
      );
    }
    localStorage.setItem("anonymousSessionId", anonymousSessionId);
  }
  return anonymousSessionId;
};

getClientSessionId();


window.addEventListener("error", function (e) {
  console.log("🔥 GLOBAL ERROR CAUGHT:", e.message, e.error);
});
window.addEventListener("unhandledrejection", function (e) {
  console.log("💥 UNHANDLED PROMISE:", e.reason);
});


const renderApp = () => {
  ReactDOM.createRoot(document.getElementById("root")).render(
    <Provider store={store}>
      <App/>
    </Provider>
  );
};

initKeycloak()
  .then(() => {
    console.log("Keycloak initialized");
    renderApp();
  })
  .catch((error) => {
    console.error("Keycloak init failed", error);
  });


