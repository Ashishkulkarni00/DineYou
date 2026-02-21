import React, { useEffect, useState } from "react";
import { House, Handbag, User, X, ArrowRight, Utensils } from "lucide-react";
import { NavLink, useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import {
  openItemCategories,
  closeItemCategories,
  openLoginPrompt,
  closeLoginPrompt,
} from "../../store/uiSlice";
import { setRestaurant } from "../../store/RestaurantSlice";
import { setCurrentPageId } from "../../store/uiSlice";
import { clearCart } from "../../store/CartSlice";
import { getKeycloak } from "../../service/keycloak";
import { fetchRestaurantDetails } from "../../service/RestaurantService";
import LoginPrompt from "../authentication/LoginPrompt";
import { ensureAuthenticated } from "../../service/authCheck";
import { fetchActiveOrders, placeOrderAPI } from "../../service/OrderService";
import { setOrder } from "../../store/OrderSlice";

const BottomNavigation = () => {
  const cart = useSelector((state) => state.cart);
  const menucard = useSelector((state) => state.menucard);
  const restaurant = useSelector((state) => state.restaurant);
  const allMenuItems = menucard?.categories.flatMap((cat) => cat.menuItemList);
  const paymentDetails = useSelector((state) => state.paymentDetails);
  const orders = useSelector((state) => state.orders);
  const [ordersCount, setOrdersCount] = useState(null);
  const showLoginPrompt = useSelector((state) => state.ui.showLoginPrompt);
  const keycloak = getKeycloak();
  const dispatch = useDispatch();

  const mappedCartItems = cart.items?.map((ci) => {
    const menuItem = allMenuItems?.find((mi) => mi.itemId === ci.itemId);
    return {
      ...menuItem,
      ...ci,
      orderedQuantity: ci.quantity,
    };
  });

  useEffect(() => {
    setOrdersCount(orders.orders?.length || 0);
  }, [orders]);

  const loadOrders = async () => {
    try {
      const isAuthenticated = keycloak?.authenticated;
      const userId = isAuthenticated ? keycloak.tokenParsed.sub : null;
      if (isAuthenticated) {
        const orders = await fetchActiveOrders(userId);
        dispatch(setOrder(orders.data));
      }
    } catch (e) {
      console.error("Error fetching orders:", e);
    }
  };

  useEffect(() => {
    loadOrders();
  }, []);

  useEffect(() => {
    const loadRestaurant = async () => {
      try {
        const restaurantId = 4;
        const result = await fetchRestaurantDetails(restaurantId);

        if (result.success && result.data && result.data.data) {
          const data = result.data.data;
          dispatch(
            setRestaurant({
              restaurantId: data.restaurantId || null,
              restaurantName: data.name || "",
              location: [data.city, data.state].filter(Boolean).join(", "),
              address: [data.addressLine1, data.addressLine2]
                .filter(Boolean)
                .join(", "),
              averageRating: data.averageRating ?? null,
              totalReviews: data.totalReviews ?? null,
              logoImagePath: data.logUrl || "",
              lastUpdated: data.updatedAt || "",
            }),
          );
        }
      } catch (e) {
        console.error("Hero useEffect: crashed", e);
      }
    };

    loadRestaurant();
  }, [dispatch]);

  const areItemCategoriesOpen = useSelector(
    (state) => state.ui.areItemCategoriesOpen,
  );

  const currentPageId = useSelector((state) => state.ui.currentPageId);
  const navigate = useNavigate();

  const navigatToMenuCard = (e) => {
    e.preventDefault();
    dispatch(openItemCategories());
    navigate("/menucard");
  };

  const navigateToHome = (e) => {
    e.preventDefault();
    dispatch(setCurrentPageId(0));
    navigate("/");
  };

  // const placeOrder = async (orderRequest) => {
  //   const result = await placeOrderAPI(orderRequest);

  //   if (result.success) {
  //     dispatch(clearCart());
  //     dispatch(setCurrentPageId(3));
  //     navigate("/order");
  //   } else {
  //     console.log("Error placing order", result.error);
  //   }
  // };

  return (
    
    <div className="fixed bottom-0 left-0 right-0 z-50 max-w-md mx-auto">
      <div
        className="bg-white border-t border-gray-100"
        style={{ boxShadow: "0 -4px 20px rgba(0,0,0,0.05)" }}
      >
        <div className="flex w-full items-center h-[72px] px-4">
          {/* LEFT GROUP — Nav Links */}
          <div className="flex flex-1 justify-around">
            {/* Home */}
            <NavLink
              to="/"
              onClick={navigateToHome}
              className="flex justify-center"
            >
              {({ isActive }) => (
                <div
                  className={`flex flex-col items-center gap-1 transition-all duration-200 ${
                    isActive
                      ? "text-orange-500"
                      : "text-gray-400 hover:text-gray-600"
                  }`}
                >
                  <House strokeWidth={isActive ? 2.5 : 2} size={22} />
                  <p
                    className={`text-[10px] ${isActive ? "font-bold" : "font-medium"}`}
                  >
                    Home
                  </p>
                </div>
              )}
            </NavLink>

            {/* Orders */}
            <NavLink to="/order" end className="relative flex justify-center">
              {({ isActive }) => (
                <div
                  className={`flex flex-col items-center gap-1 transition-all duration-200 relative ${
                    isActive
                      ? "text-orange-500"
                      : "text-gray-400 hover:text-gray-600"
                  }`}
                >
                  {ordersCount > 0 && !isActive && (
                    <div className="absolute -top-1 right-2 w-2.5 h-2.5 rounded-full bg-orange-500 border-2 border-white animate-pulse" />
                  )}
                  <Handbag strokeWidth={isActive ? 2.5 : 2} size={22} />
                  <p
                    className={`text-[10px] ${isActive ? "font-bold" : "font-medium"}`}
                  >
                    Orders
                  </p>
                </div>
              )}
            </NavLink>

            {/* Profile */}
            <NavLink to="/profile" className="flex justify-center">
              {({ isActive }) => (
                <div
                  className={`flex flex-col items-center gap-1 transition-all duration-200 ${
                    isActive
                      ? "text-orange-500"
                      : "text-gray-400 hover:text-gray-600"
                  }`}
                >
                  <User strokeWidth={isActive ? 2.5 : 2} size={22} />
                  <p
                    className={`text-[10px] ${isActive ? "font-bold" : "font-medium"}`}
                  >
                    Profile
                  </p>
                </div>
              )}
            </NavLink>
          </div>

          {/* RIGHT GROUP — Action Button */}
          <div className="ml-4 shrink-0">
            {areItemCategoriesOpen ? (
              <button
                className="
              h-10 px-5 rounded-xl
              bg-gray-100 text-gray-600
              border border-gray-200
              text-xs font-bold
              hover:bg-gray-200
              active:scale-95 transition-all
              flex items-center gap-1.5
            "
                onClick={() => dispatch(closeItemCategories())}
              >
                <X size={16} strokeWidth={2.5} />
                Close
              </button>
            ) : (
              <button
                className="
              h-10 px-5 rounded-xl
              bg-gray-900 text-white
              text-xs font-bold
              shadow-md
              hover:bg-black
              active:scale-95
              transition-all
              flex items-center gap-2
            "
                onClick={navigatToMenuCard}
              >
                <Utensils size={14} strokeWidth={2.5} />
                {paymentDetails?.paymentDetails?.paymentStatus === "SUCCESS"
                  ? "Add More"
                  : "Menu"}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default BottomNavigation;
