import React, { useEffect, useState, useMemo } from "react";
import { useSelector, useDispatch } from "react-redux";
import { setMenucard } from "../../store/MenucardSlice";
import { setPopularItems } from "../../store/PopularItemsSlice";
const menucardServiceHost = import.meta.env.VITE_MENUCARD_SERVICE_HOST;
import { clearCart, removeFromCart, setCart, updateQuantity } from "../../store/CartSlice";
import { useNavigate } from "react-router-dom";
import {
  ChevronRight,
  NotepadText,
  Plus,
  ShoppingBag,
  ArrowRight,
  Utensils,
  ChefHat,
  Sparkles,
  Minus,
} from "lucide-react";
import { getKeycloak } from "../../service/keycloak";
import {
  fetchMenuCard,
  fetchPopularItems,
} from "../../service/MenucardService";
import { fetchCartItems, calculateCartTotal } from "../../service/CartService";
import { openLoginPrompt, setCurrentPageId } from "../../store/uiSlice";
import { placeOrderAPI } from "../../service/OrderService";

const Cart = () => {
  const cart = useSelector((state) => state.cart);
  const menuCard = useSelector((state) => state.menucard);
  const restaurant = useSelector((state) => state.restaurant);
  const popularItems = useSelector((state) => state.popularItems);
  const dispatch = useDispatch();
  const [cartTotal, setCartTotal] = useState(0);
  const keycloak = getKeycloak();
  const navigate = useNavigate();

  // --- DATA LOADING ---
  useEffect(() => {
    setCurrentPageId(2);
  }, [dispatch]);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const restaurantId = 4;
        const isAuthenticated = keycloak?.authenticated;
        const userId = isAuthenticated ? keycloak.tokenParsed.sub : null;

        const promiseList = [
          fetchMenuCard(restaurantId),
          fetchPopularItems(restaurantId),
        ];

        if (isAuthenticated) {
          promiseList.push(fetchCartItems(userId));
        }

        const [menuCardResult, popularItemsResult, cartResult] =
          await Promise.allSettled(promiseList);

        if (
          menuCardResult.status === "fulfilled" &&
          menuCardResult.value.success
        ) {
          dispatch(setMenucard(menuCardResult.value.data.data));
        }

        if (
          popularItemsResult.status === "fulfilled" &&
          popularItemsResult.value.success
        ) {
          dispatch(setPopularItems(popularItemsResult.value.data.data));
        }

        if (
          isAuthenticated &&
          cartResult.status === "fulfilled" &&
          cartResult.value.success
        ) {
          const rawItems = cartResult.value.data.data.cartItemList;
          const cartItems = Array.from(
            new Map(
              rawItems.map((item) => [
                item.menuItemId,
                {
                  cartItemId: item.cartItemId,
                  menuItemId: item.menuItemId,
                  quantity: item.quantity,
                },
              ]),
            ).values(),
          );
          dispatch(
            setCart({
              cartId: cartResult.value.data.data.cartId,
              items: cartItems,
            }),
          );
        }
      } catch (err) {
        console.error("Unexpected error in fetchData:", err);
      }
    };
    fetchData();
  }, [keycloak?.authenticated, dispatch]);

  useEffect(() => {
    const loadCartTotal = async () => {
      if (!cart.cartId) return;
      const response = await calculateCartTotal(cart.cartId);
      if (response.success) {
        setCartTotal(response.data.totalPrice);
      }
    };
    loadCartTotal();
  }, [cart.cartId, cart.items]);

  // --- HELPERS ---
  const allMenuItems = useMemo(
    () =>
      menuCard?.categories?.flatMap((category) => category.menuItemList) || [],
    [menuCard],
  );

  const itemMap = useMemo(
    () => new Map(allMenuItems.map((item) => [item.itemId, item])),
    [allMenuItems],
  );

  // Filter out items already in cart from popular items to avoid duplicates in upsell
  const upsellItems = useMemo(() => {
    if (!popularItems) return [];
    const cartItemIds = new Set(cart.items.map((i) => i.menuItemId));
    return popularItems.filter((i) => !cartItemIds.has(i.itemId));
  }, [popularItems, cart.items]);

  const mappedCartItems = cart.items?.map((ci) => {
    const menuItem = allMenuItems?.find((mi) => mi.itemId === ci.itemId);
    return {
      ...menuItem,
      ...ci,
      orderedQuantity: ci.quantity,
    };
  });

  const handlePlaceOrder = async (e) => {
    const isAuthenticated = keycloak?.authenticated;

    if (!isAuthenticated) {
      dispatch(openLoginPrompt());
      return;
    }
    if (isAuthenticated) {
      const userId = keycloak.tokenParsed.sub;
      const orderRequest = {};
      orderRequest.restaurantId = restaurant?.restaurantId;
      orderRequest.userId = userId;
      orderRequest.cartId = cart?.cartId;
      const orderItems = [];
      mappedCartItems?.map((mappedItem) => {
        const orderItem = {};
        orderItem.menuItemId = mappedItem.menuItemId;
        orderItem.quantity = mappedItem.orderedQuantity;
        orderItem.specialInstruction = mappedItem.specialInstruction;
        orderItems.push(orderItem);
      });
      orderRequest.orderItems = orderItems;
      placeOrder(orderRequest);
    }
  };

  const placeOrder = async (orderRequest) => {
    const result = await placeOrderAPI(orderRequest);

    if (result.success) {
      dispatch(clearCart());
      dispatch(setCurrentPageId(3));
      navigate("/order");
    } else {
      console.log("Error placing order", result.error);
    }
  };

  // --- EMPTY STATE ---
  if (!cart.items || cart.items.length === 0) {
    return (
      <div className="min-h-screen bg-white flex flex-col items-center justify-center pb-20 px-6">
        <div className="h-20 w-20 bg-orange-50 rounded-full flex items-center justify-center mb-6 animate-pulse">
          <ShoppingBag size={40} className="text-orange-400" />
        </div>
        <h2 className="text-2xl font-black text-gray-900 mb-2">
          Your Cart is Empty
        </h2>
        <p className="text-gray-500 text-center mb-8 text-sm">
          Looks like you haven't added anything to your cart yet.
        </p>
        <button
          onClick={() => navigate("/menucard")}
          className="px-8 py-3.5 bg-gray-900 text-white rounded-2xl font-bold shadow-xl shadow-gray-200 active:scale-95 transition-all"
        >
          Browse Menu
        </button>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-white pb-56 pt-20">
      {/* --- Header --- */}
      <div className="px-5 mb-6">
        <h1 className="text-2xl font-black text-gray-900 flex items-center gap-2">
          <span className="w-2 h-2 rounded-full bg-orange-500 shadow-[0_0_8px_rgba(249,115,22,0.6)]"></span>
          Cart
        </h1>
        <p className="text-xs text-gray-500 font-medium ml-4 mt-1">
          {cart.items.length} items selected
        </p>
      </div>

      {/* --- Cart Items List --- */}
      <div className="flex flex-col gap-4 px-4">
        {cart.items.map((cartItem) => {
          const menuItem = itemMap.get(cartItem.menuItemId);
          if (!menuItem) return null;

          return (
            <div
              key={cartItem.cartItemId}
              className="flex gap-4 p-3 bg-gray-50 rounded-3xl border border-gray-100 relative overflow-hidden group"
            >
              {/* Item Image */}
              <div className="h-20 w-20 flex-shrink-0 bg-white rounded-2xl p-1 shadow-sm">
                <img
                  src={menuItem.imagePath
                    .replace("http://localhost:8081", menucardServiceHost)
                    .replace(".jpg", ".webp")}
                  alt={menuItem.itemName}
                  className="h-full w-full object-cover rounded-xl"
                />
              </div>

              {/* Item Details */}
              <div className="flex-1 flex flex-col justify-between py-0.5">
                <div>
                  <h3 className="text-[14px] font-extrabold text-gray-900 leading-tight line-clamp-1">
                    {menuItem.itemName}
                  </h3>
                  <p className="text-xs font-bold text-gray-500 mt-1">
                    ₹{menuItem.itemPrice}
                  </p>
                </div>

                <div className="flex items-center justify-between mt-2">
                  <button className="text-[10px] font-bold text-orange-600 flex items-center gap-1 hover:bg-orange-50 px-2 py-1 rounded-lg -ml-2 transition-colors">
                    Edit <ChevronRight size={10} />
                  </button>
                </div>
              </div>

              {/* Add Button & Total */}
              <div className="flex flex-col items-end justify-between py-1">
                <div className="w-20 transform scale-90 origin-top-right">
                  <AddButton menuItem={menuItem} />
                </div>
                <div className="text-[14px] font-black text-gray-900">
                  ₹{cartItem.quantity * menuItem.itemPrice}
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* --- AOV BOOSTER: Complete Your Meal --- */}
      <div className="px-4">
        {upsellItems.length > 0 && (
          <div className="mt-8">
            {/* Header */}
            <div className="flex items-center gap-2 mb-4">
              <Sparkles size={16} className="text-orange-500 fill-orange-500" />
              <h2 className="text-sm font-black text-gray-900 uppercase tracking-wide">
                Complete your meal
              </h2>
            </div>

            <div className="flex gap-4 overflow-x-auto px-5 pb-6 scrollbar-hide snap-x w-full after:w-5 after:flex-shrink-0 after:content-['']">
              {upsellItems.slice(0, 5).map((item) => (
                <div
                  key={item.itemId}
                  className="snap-start flex-shrink-0 w-[140px] bg-white rounded-2xl border border-gray-100 shadow-[0_4px_20px_rgba(0,0,0,0.04)] overflow-hidden flex flex-col"
                >
                  <div className="h-24 w-full relative">
                    <img
                      src={item.imagePath
                        .replace("http://localhost:8081", menucardServiceHost)
                        .replace(".jpg", ".webp")}
                      alt={item.itemName}
                      className="h-full w-full object-cover"
                    />
                    {/* Quick Add Overlay */}
                    <div className="absolute bottom-2 right-2 shadow-lg z-10">
                      <div className="scale-75 origin-bottom-right">
                        <AddButton menuItem={item} />
                      </div>
                    </div>
                  </div>
                  <div className="p-3 flex-1 flex flex-col">
                    <h4 className="text-[12px] font-bold text-gray-900 line-clamp-2 leading-tight mb-1">
                      {item.itemName}
                    </h4>
                    <div className="mt-auto pt-2 flex items-center justify-between">
                      <span className="text-[12px] font-black text-gray-900">
                        ₹{item.itemPrice}
                      </span>
                      <div className="flex items-center gap-0.5">
                        <ChefHat size={10} className="text-gray-400" />
                      </div>
                    </div>
                  </div>
                </div>
              ))}

              {/* "See Menu" Card */}
              <div
                onClick={() => navigate("/menucard")}
                className="snap-start flex-shrink-0 w-[100px] bg-gray-50 rounded-2xl border border-dashed border-gray-300 flex flex-col items-center justify-center cursor-pointer active:scale-95 transition-transform"
              >
                <div className="h-8 w-8 rounded-full bg-white border border-gray-200 flex items-center justify-center mb-2">
                  <ArrowRight size={14} className="text-gray-600" />
                </div>
                <span className="text-[11px] font-bold text-gray-500 text-center px-2">
                  View Full Menu
                </span>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* --- Bill Details --- */}
      <div className="px-4 mt-2">
        <div className="bg-white rounded-[2rem] border border-gray-100 shadow-sm p-5 relative overflow-hidden">
          {/* Top jagged edge decor (optional visual flair) */}
          <div className="absolute top-0 left-0 w-full h-1 bg-gradient-to-r from-orange-400 to-orange-500 opacity-20"></div>

          <h3 className="text-sm font-black text-gray-900 mb-4 flex items-center gap-2">
            <Utensils size={14} /> Bill Summary
          </h3>

          <div className="space-y-3">
            <div className="flex justify-between text-xs font-medium text-gray-500">
              <span>Item Total</span>
              <span className="text-gray-900 font-bold">₹{cartTotal}</span>
            </div>
            <div className="flex justify-between text-xs font-medium text-gray-500">
              <span>Taxes & Charges</span>
              <span className="text-gray-900 font-bold">
                ₹{(cartTotal * 0.05).toFixed(2)}
              </span>
            </div>
          </div>

          <div className="my-4 border-t border-dashed border-gray-200"></div>

          <div className="flex justify-between items-center">
            <span className="text-sm font-extrabold text-gray-900">
              Grand Total
            </span>
            <span className="text-xl font-black text-gray-900">
              ₹{(cartTotal * 1.05).toFixed(2)}
            </span>
          </div>
        </div>
      </div>

      {/* --- Notes --- */}
      <div className="px-4 mt-4">
        <button className="w-full py-3.5 px-4 rounded-2xl border border-gray-200 bg-gray-50 flex items-center justify-between group active:bg-gray-100 transition-colors">
          <div className="flex items-center gap-3">
            <div className="bg-white p-1.5 rounded-lg border border-gray-200 text-gray-400 group-hover:text-orange-500 transition-colors">
              <NotepadText size={16} strokeWidth={2.5} />
            </div>
            <span className="text-xs font-bold text-gray-600">
              Add cooking instructions
            </span>
          </div>
          <ChevronRight size={14} className="text-gray-400" />
        </button>
      </div>

      {/* --- Sticky Footer --- */}
      <div className="fixed bottom-16 left-0 w-full bg-white border-t border-gray-100 p-4 pb-6 z-20 shadow-[0_-10px_40px_rgba(0,0,0,0.03)]">
        <button
          onClick={handlePlaceOrder} // Or confirm order logic
          className="w-full py-4 rounded-2xl bg-gray-900 text-white font-bold text-[14px] shadow-lg shadow-gray-200 hover:bg-black active:scale-[0.98] transition-all flex items-center justify-between px-6"
        >
          <span className="flex flex-col items-start">
            <span className="text-[10px] text-gray-400 font-medium uppercase tracking-wider">
              Total Payable
            </span>
            <span className="text-lg font-black leading-none">
              ₹{(cartTotal * 1.05).toFixed(2)}
            </span>
          </span>

          <div className="flex items-center gap-2">
            <span>Place Order</span>
            <ArrowRight size={18} strokeWidth={2.5} />
          </div>
        </button>
      </div>
    </div>
  );
};

const AddButton = ({ menuItem }) => {
  const cart = useSelector((state) => state.cart);
  const dispatch = useDispatch();

  const handleEditButtonClick = () => {
    if (!existingItem) dispatch(openItemDetails());
    dispatch(setCurrentlyClickedMenuItem(menuItem));
  };

  const existingItem = cart?.items.find(
    (item) => item.menuItemId === menuItem.itemId,
  );

  const handleMinus = () => {
    if (existingItem?.quantity === 1) {
      dispatch(removeFromCart(menuItem.itemId));
    } else {
      dispatch(
        updateQuantity({
          menuItemId: menuItem.itemId,
          quantity: existingItem.quantity - 1,
        }),
      );
    }
  };

  const handlePlus = () => {
    dispatch(
      updateQuantity({
        menuItemId: menuItem.itemId,
        quantity: existingItem.quantity + 1,
      }),
    );
  };

  return (
    <div className="w-full h-11 relative">
      <div
        className="
            flex items-center justify-around w-full h-9 px-1
            bg-white 
            border border-green-500
            rounded-xl
            shadow-sm 
            animate-in fade-in zoom-in-95 duration-200
          "
      >
        {/* Decrease */}
        <button
          onClick={handleMinus}
          className="
              w-8 h-full flex items-center justify-center 
              text-gray-400 hover:text-green-700 hover:bg-green-50 
              rounded transition-colors active:scale-90
            "
        >
          <Minus size={16} strokeWidth={3} />
        </button>

        {/* Count */}
        <span className="text-sm font-black text-green-700 min-w-[1rem] text-center select-none">
          {existingItem?.quantity}
        </span>

        {/* Increase */}
        <button
          onClick={handlePlus}
          className="
              w-8 h-full flex items-center justify-center 
              text-green-600 hover:text-green-800 hover:bg-green-50 
              rounded transition-colors active:scale-90
            "
        >
          <Plus size={16} strokeWidth={3} />
        </button>
      </div>
    </div>
  );
};

export default Cart;
