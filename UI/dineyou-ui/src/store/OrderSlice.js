// store/orderSlice.js
import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  orders: [], 
};

const orderSlice = createSlice({
  name: "orders",
  initialState,
  reducers: {

    setOrder(state, action) {      
      state.orders = action.payload || []; 
    },

    addOrderItem(state, action) {
      state.orderedItems.push(action.payload);
    },

    updateOrderItem(state, action) {
      const { orderItemId, updates } = action.payload;
      const index = state.orderedItems.findIndex(
        (item) => item.orderItemId === orderItemId
      );
      if (index !== -1) {
        state.orderedItems[index] = {
          ...state.orderedItems[index],
          ...updates,
        };
      }
    },

    removeOrderItem(state, action) {
      state.orderedItems = state.orderedItems.filter(
        (item) => item.orderItemId !== action.payload
      );
    },
    resetOrder(state) {
      state.orderId = null;
      state.orderedItems = [];
    },
  },
});

export const {
  setOrder,
  addOrderItem,
  updateOrderItem,
  removeOrderItem,
  resetOrder,
} = orderSlice.actions;

export default orderSlice.reducer;
