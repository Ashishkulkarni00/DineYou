import { createSlice } from "@reduxjs/toolkit";

const menucardSlice = createSlice({
  name: "menucard",
  initialState: null,
  reducers: {
    setMenucard: (state, action) => action.payload,
    clearMenucard: () => null
  }
});

export const { setMenucard, clearMenucard } = menucardSlice.actions;
export default menucardSlice.reducer;