import { createSlice } from "@reduxjs/toolkit";

const restaurantSlice = createSlice({
  name: "restaurant",
  initialState: null,
  reducers: {
    setRestaurant: (state, action) => action.payload,
    clearRestaurant: () => null
  }
});

export const { setRestaurant, clearRestaurant } = restaurantSlice.actions;
export default restaurantSlice.reducer;
