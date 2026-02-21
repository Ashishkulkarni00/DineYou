import { createSlice } from "@reduxjs/toolkit";

const currentlyClickedMenuItemSlice = createSlice({
  name: "currentlyClickedMenuItem",
  initialState: null,
  reducers: {
    setCurrentlyClickedMenuItem: (state, action) => action.payload,
    clearCurrentlyClickedMenuItem: () => null
  }
});

export const { setCurrentlyClickedMenuItem, clearCurrentlyClickedMenuItem } = currentlyClickedMenuItemSlice.actions;
export default currentlyClickedMenuItemSlice.reducer;