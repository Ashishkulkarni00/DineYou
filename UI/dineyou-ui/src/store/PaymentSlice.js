// store/orderSlice.js
import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  paymentDetails: [],
};

const paymentSlice = createSlice({
  name: "paymentDetails",
  initialState,
  reducers: {
    setPaymentDetails(state, action) {
      console.log(action.payload);
      if (action.payload.byOrderIds) {
        state.paymentDetails = action.payload.statuses;
      } else {
        state.paymentDetails = [
          ...state.paymentDetails,
          {
            paymentReferenceId: action.payload.paymentReferenceId,
            orderId: action.payload.orderId,
            paidAmount: action.payload.paidAmount,
            paymentStatus: action.payload.paymentStatus ?? "FAILED",
          },
        ];
      }
    },
  },
});

export const { setPaymentDetails } = paymentSlice.actions;

export default paymentSlice.reducer;
