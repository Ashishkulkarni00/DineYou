import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  isItemDetailsOpen: false,
  areItemCategoriesOpen: false,
  selectedMenucardCategory: null,
  isOrderAndPaymentDetailsOpen: false,
  // 0 - HomePage
  // 1 - MenuCardPage
  // 2 - CartPage
  // 3 - ordersPage
  currentPageId: 0,
  searchedMenuItem: null,
  showLoginPrompt: false,
  menucardFilters: {},

};

const uiSlice = createSlice({
  name: "ui",
  initialState,
  reducers: {
    openItemDetails: (state) => {
      state.isItemDetailsOpen = true;
    },
    closeItemDetails: (state) => {
      state.isItemDetailsOpen = false;
    },
    toggleItemDetails: (state) => {
      state.isItemDetailsOpen = !state.isItemDetailsOpen;
    },
    openItemCategories: (state) => {
      state.areItemCategoriesOpen = true;
    },
    closeItemCategories: (state) => {
      state.areItemCategoriesOpen = false;
    },
    toggleItemCategories: (state) => {
      state.areItemCategoriesOpen = !state.areItemCategoriesOpen;
    },
    setMenucardCategory: (state, action) => {
      state.selectedMenucardCategory = action.payload;
    },
    setCurrentPageId: (state, action) => {
      state.currentPageId = action.payload;
    },
    openOrderDetailsAndPayment: (state) => {
      state.isOrderAndPaymentDetailsOpen = true;
    },
    closeOrderAndPaymentDetails: (state) => {
      state.isOrderAndPaymentDetailsOpen = false;
    },
    setSearchedMenuItem: (state, action) => {
      state.searchedMenuItem = action.payload;
    },
    clearSearchedMenuItem: (state) => {
      state.searchedMenuItem = null;
    },
    openLoginPrompt: (state) => {
      state.showLoginPrompt = true;
    },
    closeLoginPrompt: (state) => {
      state.showLoginPrompt = false;
    },
    setMenucardFilters: (state, action) => {
      state.menucardFilters = action.payload;
    },
    clearMenucardFilters: (state) => {
      state.menucardFilters = {};
    }
  },
});

export const {
  openItemDetails,
  closeItemDetails,
  toggleItemDetails,
  openItemCategories,
  closeItemCategories,
  toggleItemCategories,
  setMenucardCategory,
  setCurrentPageId,
  openOrderDetailsAndPayment,
  closeOrderAndPaymentDetails,
  setSearchedMenuItem,
  clearSearchedMenuItem,
  openLoginPrompt,
  closeLoginPrompt,
  setMenucardFilters,
  clearMenucardFilters
} = uiSlice.actions;
export default uiSlice.reducer;
