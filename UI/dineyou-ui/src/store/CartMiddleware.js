import axios from "axios";
import { setCart, updateCartItemId } from "../store/CartSlice";
import { getKeycloak } from "../service/keycloak";
import { getHeaders } from "../service/HeadersUtil";

const cartServiceHost = import.meta.env.VITE_CART_SERVICE_HOST;

const CartMiddleware = (store) => (next) => async (action) => {
  const keycloak = getKeycloak();
  const accessToken = keycloak.token;

  const isAuthenticated = keycloak?.authenticated;
  const userId = isAuthenticated ? keycloak.tokenParsed.sub : null;

  const headers = getHeaders();

  // Only intercept cart-related actions
  if (!action.type.startsWith("cart/")) {
    return next(action);
  }

  // Skip middleware for direct setCart dispatch
  if (action.type === "cart/setCart") {
    return next(action);
  }

  const state = store.getState();
  const cart = state.cart;
  const restaurantId = 4; // replace with actual selected restaurantId
  const tableId = 0;
  const sessionId = "dummy-session-id";


  try {
    // ---------------------------
    // 🧹 Remove from Cart
    // ---------------------------
    if (action.type === "cart/removeFromCart") {
      const itemToRemove = cart.items.find(
        (item) => item.menuItemId === action.payload
      );

      if (!itemToRemove) return next(action);

      try {
        // Wait for backend delete first — atomic
        await axios.delete(
          `${cartServiceHost}/api/v1/cartItem/${itemToRemove.cartItemId}`,
          {
            headers: {
              ...headers,
              Authorization: `Bearer ${accessToken}`,
              "Content-Type": "application/json",
            },
          }
        );

        // Now update Redux
        return next(action);
      } catch (err) {
        console.error("❌ Failed to remove item from server:", err);
        return; // Do not update Redux if backend failed
      }
    }

    // ---------------------------
    // 🟢 No existing Cart → Create new one first
    // ---------------------------
    if (!cart.cartId) {
      console.log("🟢 Global Sync → creating new cart...");

      const createCartReqDto = { userId, restaurantId };

      const res = await axios.post(
        `${cartServiceHost}/api/v1/cart/create`,
        createCartReqDto,
        {
          headers: {
            ...headers,
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!res.data.success) {
        console.error("🔴 Cart creation failed:", res.data.message);
        return; // Don't update Redux
      }

      const cartId = res.data.data.cartId;

      if (action.type === "cart/addToCart") {

        const menuItemToBeAdd = action.payload;

        if (!menuItemToBeAdd) return;

        const response = await axios.post(
          `${cartServiceHost}/api/v1/cartItem/addCartItem`,
          {
            userId,
            cartId,
            menuItemId: menuItemToBeAdd.menuItemId,
            tableId,
            sessionId,
            quantity: menuItemToBeAdd.quantity,
            notes: menuItemToBeAdd.notes || "",
            cartItemId: null, // new item
          },
          {
            headers: {
              ...headers,
              Authorization: `Bearer ${accessToken}`,
              "Content-Type": "application/json",
            },
          }
        );

        if (!response.data.success) {
          console.error(
            "🔴 Failed to add item to new cart:",
            response.data.message
          );
          return; // Do not update Redux
        }

        // ✅ Include cartItemId in the action payload before dispatching
        const savedItem = response.data.data;
        const enrichedAction = {
          ...action,
          payload: {
            ...action.payload,
            cartItemId: savedItem.cartItemId, // ✅ Add cartItemId here
          },
        };

        // ✅ Backend succeeded — update Redux with cartItemId
        store.dispatch(setCart({ cartId, items: cart.items }));

        // ✅ Now dispatch the enriched action with cartItemId
        return next(enrichedAction);
      }
    }

  
    if (
      action.type === "cart/addToCart" ||
      action.type === "cart/updateQuantity"
    ) {
      const menuItemToBeAdd = action.payload;
      if (!menuItemToBeAdd) return;

      const existingItemOfCart = cart.items?.find(
        (item) => item.menuItemId === menuItemToBeAdd.menuItemId
      );

      const response = await axios.post(
        `${cartServiceHost}/api/v1/cartItem/addCartItem`,
        {
          userId,
          cartId: cart.cartId,
          menuItemId: menuItemToBeAdd.menuItemId ?? null,
          tableId,
          sessionId,
          quantity: menuItemToBeAdd.quantity,
          notes: menuItemToBeAdd.notes || "",
          cartItemId: existingItemOfCart?.cartItemId ?? null,
        },
        {
          headers: {
            ...headers,
            Authorization: `Bearer ${accessToken}`,
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.data.success) {
        console.error(
          "🔴 Failed to sync item with backend:",
          response.data.message
        );
        return;
      }

      // ✅ Enrich the action with cartItemId from backend
      const savedItem = response.data.data;
      const enrichedAction = {
        ...action,
        payload: {
          ...action.payload,
          cartItemId: savedItem.cartItemId, // ✅ Include cartItemId
        },
      };

      // ✅ Dispatch enriched action so reducer gets cartItemId immediately
      return next(enrichedAction);
    }
  } catch (err) {
    console.error("❌ Cart sync failed:", err);
    return; // prevent inconsistent state
  }

  return next(action);
};

export default CartMiddleware;
