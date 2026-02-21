import { configureStore } from "@reduxjs/toolkit";
import uiSlice from "./uiSlice";
import restaurantReducer from "./RestaurantSlice";
import popularItemsReducer from "./PopularItemsSlice";
import currentlyClickedMenuItemReducer from "./CurrentlyClickedMenuItemSlice";
import menuCardReducer from "./MenucardSlice";
import orderReducer from "./OrderSlice";
import cartReducer from "./CartSlice";
import cartMiddleware from "./CartMiddleware";
import paymentDetailsReducer from './PaymentSlice';

export const store = configureStore({
  reducer: {
    restaurant: restaurantReducer,
    currentlyClickedMenuItem: currentlyClickedMenuItemReducer,
    popularItems: popularItemsReducer,
    ui: uiSlice,
    menucard: menuCardReducer,
    cart: cartReducer,
    orders: orderReducer,
    paymentDetails: paymentDetailsReducer
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware().concat(cartMiddleware),
});
