import { createSlice } from "@reduxjs/toolkit";

const cartSlice = createSlice({
  name: "cart",
  initialState: {
    cartId: null,
    items: [],
  },

  reducers: {
    setCart: (state, action) => {
      return action.payload; // payload must match {cartId, items: []}
    },

    // addToCart: (state, action) => {

    //   console.log("state from slice add to cart", state);

    //   const { cartId, menuItemId, cartItemId, quantity } = action.payload;

    //   if (cartId && !state.cartId) {
    //     state.cartId = cartId;
    //   }

    //   const existingItem = state.items.find((i) => i.menuItemId === menuItemId);

    //   if (existingItem) {
    //     existingItem.quantity += quantity || 1;
    //     existingItem.status = "ADDED";
    //   } else {
    //     state.items.push({
    //       cartItemId, // ✅ persist cartItemId from backend
    //       menuItemId,
    //       quantity: quantity || 1,
    //       status: "ADDED",
    //     });
    //   }
    // },

    addToCart: (state, action) => {
      console.log("state from slice add to cart", state);

      const { cartId, menuItemId, cartItemId, quantity } = action.payload;

      if (cartId && !state.cartId) {
        state.cartId = cartId;
      }

      const existingItem = state.items.find((i) => i.menuItemId === menuItemId);

      if (existingItem) {
        existingItem.quantity += quantity || 1;
        existingItem.cartItemId = cartItemId; // ✅ Update cartItemId if provided
        existingItem.status = "ADDED";
      } else {
        state.items.push({
          cartItemId: cartItemId || null, // ✅ Use from payload
          menuItemId,
          quantity: quantity || 1,
          status: "ADDED",
        });
      }
    },

    // updateQuantity: (state, action) => {
    //   console.log("state from slice updateQuantity", state);

    //   const { menuItemId, quantity } = action.payload;
    //   const item = state.items.find((i) => i.menuItemId === menuItemId);
    //   if (item) {
    //     item.quantity = quantity;
    //     item.status = quantity > 0 ? "ADDED" : "REMOVED";
    //   }
    // },

    updateQuantity: (state, action) => {
      console.log("state from slice updateQuantity", state);

      const { menuItemId, quantity, cartItemId } = action.payload;
      const item = state.items.find((i) => i.menuItemId === menuItemId);
      if (item) {
        item.quantity = quantity;
        if (cartItemId) item.cartItemId = cartItemId; // ✅ Update if provided
        item.status = quantity > 0 ? "ADDED" : "REMOVED";
      }
    },

    removeFromCart: (state, action) => {
      const menuItemId = action.payload;
      state.items = state.items.filter((i) => i.menuItemId !== menuItemId);
    },

    cancelCartItem: (state, action) => {
      const itemId = action.payload;
      const item = state.items.find((i) => i.itemId === itemId);
      if (item) {
        item.status = "CANCELLED";
      }
    },

    updateCartItemId: (state, action) => {
      console.log("state from slice Update cart item id ", state);

      const { menuItemId, cartItemId } = action.payload;
      const item = state.items.find((i) => i.menuItemId === menuItemId);
      if (item) {
        item.cartItemId = cartItemId;
      }
    },

    clearCart: () => ({
      cartId: null,
      items: [],
    }),
  },
});

export const {
  setCart,
  addToCart,
  updateQuantity,
  removeFromCart,
  cancelCartItem,
  clearCart,
  updateCartItemId,
} = cartSlice.actions;

export default cartSlice.reducer;
