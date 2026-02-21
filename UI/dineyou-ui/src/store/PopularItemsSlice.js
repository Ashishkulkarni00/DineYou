import { createSlice } from "@reduxjs/toolkit";

const popularItemsSlice = createSlice({
  name: "popularItems",
  initialState: [],
  reducers: {
    setPopularItems: (state, action) => action.payload,
    clearPopularItems: () => []
  }
});

export const { setPopularItems, clearPopularItems } = popularItemsSlice.actions;
export default popularItemsSlice.reducer;
