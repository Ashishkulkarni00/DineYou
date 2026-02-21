// import React, { useState } from "react";
// import { Bookmark, Share2, X, Plus, Minus } from "lucide-react";
// import { useDispatch, useSelector } from "react-redux";
// import {
//   closeItemDetails,
//   openLoginPrompt,
//   closeLoginPrompt,
// } from "../../store/uiSlice";
// import { addToCart } from "../../store/CartSlice";
// import { ensureAuthenticated } from "../../service/authCheck";
// import { getKeycloak } from "../../service/keycloak";
// import LoginPrompt from "../authentication/LoginPrompt";

// const menucardServiceHost = import.meta.env.VITE_MENUCARD_SERVICE_HOST;

// const ItemDetails = () => {
//   const dispatch = useDispatch();

//   const isItemDetailsOpen = useSelector((state) => state.ui.isItemDetailsOpen);
//   const showLoginPrompt = useSelector((state) => state.ui.showLoginPrompt);

//   const currentlyClickedMenuItem = useSelector(
//     (state) => state.currentlyClickedMenuItem
//   );

//   if (!isItemDetailsOpen) return null;

//   return (
//     <>
//       {/* Item Details UI */}
//       <div className="h-[90%] w-full bottom-0">
//         {/* Close Button */}
//         <div className="flex justify-center mb-4">
//           <button
//             className="h-11 w-11 rounded-full bg-gray-800 hover:bg-gray-900 flex items-center justify-center transition-all duration-200 active:scale-95 shadow-lg"
//             onClick={() => dispatch(closeItemDetails())}
//           >
//             <X size={22} strokeWidth={2.5} className="text-white" />
//           </button>
//         </div>

//         {/* Content Card */}
//         <div className="h-full w-full bg-white rounded-3xl p-5 shadow-2xl">
//           {/* Image */}
//           <img
//             src={currentlyClickedMenuItem?.imagePath
//               .replace("http://localhost:8081", menucardServiceHost)
//               .replace(".jpg", ".webp")}
//             className="object-cover rounded-2xl h-[13rem] w-full shadow-md"
//             alt={currentlyClickedMenuItem?.itemName}
//           />

//           {/* Title & Actions */}
//           <div className="mt-4 flex justify-between items-start">
//             <div className="flex-1">
//               <h2 className="text-xl text-gray-900 font-bold leading-tight">
//                 {currentlyClickedMenuItem?.itemName}
//               </h2>
//               <p className="text-sm text-gray-500 leading-relaxed mt-1.5">
//                 {currentlyClickedMenuItem?.itemDescription}
//               </p>
//             </div>

//             <div className="flex gap-2 ml-3">
//               <button className="p-2.5 rounded-xl border border-gray-200 hover:bg-gray-50 hover:border-gray-300 transition-all duration-200 active:scale-95">
//                 <Bookmark size={18} className="text-gray-600" strokeWidth={2} />
//               </button>
//               <button className="p-2.5 rounded-xl border border-gray-200 hover:bg-gray-50 hover:border-gray-300 transition-all duration-200 active:scale-95">
//                 <Share2 size={18} className="text-gray-600" strokeWidth={2} />
//               </button>
//             </div>
//           </div>

//           {/* Cooking Note */}
//           <div className="mt-5">
//             <label className="text-sm font-semibold text-gray-700 mb-2 block">
//               Add a cooking note (Optional)
//             </label>
//             <textarea
//               className="border-2 border-gray-200 focus:border-green-500 focus:ring-4 focus:ring-green-100 focus:outline-none w-full h-[5.5rem] rounded-xl p-3 text-sm text-gray-800 placeholder:text-gray-400 transition-all duration-200 resize-none hover:border-gray-300 bg-gray-50 focus:bg-white shadow-sm focus:shadow-md"
//               placeholder="e.g., Make it extra spicy 🌶️"
//             />
//           </div>

//           {/* Bottom Buttons */}
//           <div className="fixed bottom-5 left-5 right-5">
//             <CheckoutButtons menuItem={currentlyClickedMenuItem} />
//           </div>
//         </div>
//       </div>

//       {/* Login Prompt (GLOBAL OVERLAY) */}
//       {showLoginPrompt && (
//         <LoginPrompt
//           isOpen={showLoginPrompt}
//           onClose={() => dispatch(closeLoginPrompt())}
//           onLogin={() => {
//             dispatch(closeLoginPrompt());
//             ensureAuthenticated();
//           }}
//         />
//       )}
//     </>
//   );
// };

// const CheckoutButtons = ({ menuItem }) => {
//   const dispatch = useDispatch();
//   const keycloak = getKeycloak();

//   const cart = useSelector((state) => state.cart);

//   const existingItem = cart?.items.find(
//     (item) => item?.menuItemId === menuItem?.itemId
//   );

//   const [quantity, setQuantity] = useState(
//     existingItem ? existingItem.quantity : 1
//   );

//   // Increase quantity logic
//   const increaseQuantity = () => {
//     if (!keycloak?.authenticated) {
//       dispatch(openLoginPrompt());
//       return;
//     }
//     setQuantity(quantity + 1);
//   };

//   // Decrease quantity logic
//   const decreaseQuantity = () => {
//     if (!keycloak?.authenticated) {
//       dispatch(openLoginPrompt());
//       return;
//     }
//     if (quantity > 1) setQuantity(quantity - 1);
//   };

//   // Add item to cart
//   const handleAddItem = async () => {
//     if (!keycloak?.authenticated) {
//       dispatch(openLoginPrompt());
//       return;
//     }

//     const obj = {
//       menuItemId: menuItem.itemId,
//       quantity: quantity,
//       cartId: cart?.cartId,
//     };

//     dispatch(addToCart(obj));
//     dispatch(closeItemDetails());
//   };

//   return (
//     <div className="flex gap-3">
//       {/* Quantity Selector */}
//       <div className="flex items-center gap-3 px-4 py-3 bg-gray-100 rounded-xl border border-gray-200">
//         <button
//           onClick={decreaseQuantity}
//           className="w-8 h-8 flex items-center justify-center bg-white hover:bg-gray-50 rounded-lg transition-colors shadow-sm active:scale-95"
//         >
//           <Minus size={16} strokeWidth={2.5} className="text-gray-700" />
//         </button>

//         <span className="text-xl font-bold text-gray-900 min-w-[2rem] text-center">
//           {quantity}
//         </span>

//         <button
//           onClick={increaseQuantity}
//           className="w-8 h-8 flex items-center justify-center bg-white hover:bg-gray-50 rounded-lg transition-colors shadow-sm active:scale-95"
//         >
//           <Plus size={16} strokeWidth={2.5} className="text-gray-700" />
//         </button>
//       </div>

//       {/* Add to Cart Button */}
//       <button
//         onClick={handleAddItem}
//         className="flex-1 py-3 px-3 bg-gradient-to-r from-green-600 to-green-700 hover:from-green-700 hover:to-green-800 text-white font-bold rounded-xl shadow-lg hover:shadow-xl transition-all duration-200 active:scale-[0.98]"
//       >
//         <div className="flex items-center justify-center gap-1.5 whitespace-nowrap">
//           <span className="text-sm">Add item</span>
//           <span className="text-base">₹{menuItem?.itemPrice * quantity}</span>
//         </div>
//       </button>
//     </div>
//   );
// };

// export default ItemDetails;

import React, { useState } from "react";
import {
  Bookmark,
  Share2,
  X,
  Plus,
  Minus,
  MessageSquarePlus,
} from "lucide-react";
import { useDispatch, useSelector } from "react-redux";
import {
  closeItemDetails,
  openLoginPrompt,
  closeLoginPrompt,
} from "../../store/uiSlice";
import { addToCart } from "../../store/CartSlice";
import { ensureAuthenticated } from "../../service/authCheck";
import { getKeycloak } from "../../service/keycloak";
import LoginPrompt from "../authentication/LoginPrompt";

const menucardServiceHost = import.meta.env.VITE_MENUCARD_SERVICE_HOST;

const ItemDetails = () => {
  const dispatch = useDispatch();

  const isItemDetailsOpen = useSelector((state) => state.ui.isItemDetailsOpen);
  const showLoginPrompt = useSelector((state) => state.ui.showLoginPrompt);

  const currentlyClickedMenuItem = useSelector(
    (state) => state.currentlyClickedMenuItem,
  );

  if (!isItemDetailsOpen) return null;

  return (
    <>
      {/* Item Details UI */}
      {/* Added 'animate-in' for smooth slide-up effect */}
      <div className="h-[80%] w-full bottom-0 flex flex-col relative animate-in slide-in-from-bottom-10 duration-300">
        {/* Floating Close Button */}
        <div className="absolute -top-16 left-0 right-0 flex justify-center z-50">
          <button
            className="h-12 w-12 rounded-full bg-black/80 backdrop-blur-md border border-white/10 flex items-center justify-center transition-transform duration-200 active:scale-90 shadow-xl"
            onClick={() => dispatch(closeItemDetails())}
          >
            <X size={24} strokeWidth={2.5} className="text-white" />
          </button>
        </div>

        {/* Content Card */}
        <div className="h-full w-full bg-white rounded-t-[2.5rem] shadow-2xl overflow-hidden flex flex-col relative">
          {/* Scrollable Content Area */}
          <div className="flex-1 overflow-y-auto pb-28 custom-scrollbar">
            {/* Image Container */}
            <div className="p-4 pb-0">
              <div className="relative w-full aspect-[4/3] rounded-[2rem] overflow-hidden shadow-sm bg-gray-50">
                <img
                  src={currentlyClickedMenuItem?.imagePath
                    .replace("http://localhost:8081", menucardServiceHost)
                    .replace(".jpg", ".webp")}
                  className="object-cover w-full h-full"
                  alt={currentlyClickedMenuItem?.itemName}
                />

                {/* Optional: Overlay gradient for depth */}
                <div className="absolute inset-0 bg-gradient-to-t from-black/10 to-transparent pointer-events-none" />
              </div>
            </div>

            {/* Content Body */}
            <div className="px-6 pt-5">
              {/* Header Row */}
              <div className="flex justify-between items-start gap-4">
                <div className="flex-1">
                  <h2 className="text-2xl font-black text-gray-900 leading-tight tracking-tight">
                    {currentlyClickedMenuItem?.itemName}
                  </h2>
                  <div className="mt-2 flex items-center gap-2">
                    <span className="text-xl font-bold text-gray-900">
                      ₹{currentlyClickedMenuItem?.itemPrice}
                    </span>
                    {currentlyClickedMenuItem?.discountPercentage > 0 && (
                      <span className="px-2 py-0.5 rounded-full bg-orange-50 text-orange-700 text-[10px] font-black border border-orange-100 uppercase tracking-wide">
                        {currentlyClickedMenuItem?.discountPercentage}% OFF
                      </span>
                    )}
                  </div>
                </div>

                {/* Actions */}
                <div className="flex gap-2">
                  <button className="p-2.5 rounded-2xl bg-gray-50 text-gray-400 hover:text-orange-500 hover:bg-orange-50 transition-colors active:scale-95">
                    <Bookmark size={20} strokeWidth={2} />
                  </button>
                  <button className="p-2.5 rounded-2xl bg-gray-50 text-gray-400 hover:text-orange-500 hover:bg-orange-50 transition-colors active:scale-95">
                    <Share2 size={20} strokeWidth={2} />
                  </button>
                </div>
              </div>

              {/* Description */}
              <p className="text-sm text-gray-500 font-medium leading-relaxed mt-4">
                {currentlyClickedMenuItem?.itemDescription}
              </p>

              {/* Divider */}
              <div className="h-px w-full bg-gray-100 my-6"></div>

              {/* Cooking Note */}
              <div className="space-y-3">
                <div className="flex items-center gap-2 text-gray-800">
                  <MessageSquarePlus size={18} className="text-orange-500" />
                  <label className="text-sm font-bold">
                    Cooking Request{" "}
                    <span className="text-gray-400 font-normal text-xs">
                      (Optional)
                    </span>
                  </label>
                </div>
                <textarea
                  className="
                    w-full h-24 p-4 rounded-2xl
                    bg-gray-50 border border-gray-100
                    text-sm font-medium text-gray-800 placeholder:text-gray-400
                    focus:bg-white focus:border-orange-200 focus:ring-4 focus:ring-orange-50
                    outline-none transition-all duration-200 resize-none
                  "
                  placeholder="e.g., Make it extra spicy, less oil..."
                />
              </div>
            </div>
          </div>

          {/* Sticky Bottom Footer */}
          <div className="absolute bottom-0 left-0 right-0 p-4 bg-white/90 backdrop-blur-md border-t border-gray-100">
            <CheckoutButtons menuItem={currentlyClickedMenuItem} />
          </div>
        </div>
      </div>

      {/* Login Prompt (GLOBAL OVERLAY) */}
      {showLoginPrompt && (
        <LoginPrompt
          isOpen={showLoginPrompt}
          onClose={() => dispatch(closeLoginPrompt())}
          onLogin={() => {
            dispatch(closeLoginPrompt());
            ensureAuthenticated();
          }}
        />
      )}
    </>
  );
};

const CheckoutButtons = ({ menuItem }) => {
  const dispatch = useDispatch();
  const keycloak = getKeycloak();

  const cart = useSelector((state) => state.cart);

  const existingItem = cart?.items.find(
    (item) => item?.menuItemId === menuItem?.itemId,
  );

  const [quantity, setQuantity] = useState(
    existingItem ? existingItem.quantity : 1,
  );

  // Increase quantity logic
  const increaseQuantity = () => {
    if (!keycloak?.authenticated) {
      dispatch(openLoginPrompt());
      return;
    }
    setQuantity(quantity + 1);
  };

  // Decrease quantity logic
  const decreaseQuantity = () => {
    if (!keycloak?.authenticated) {
      dispatch(openLoginPrompt());
      return;
    }
    if (quantity > 1) setQuantity(quantity - 1);
  };

  // Add item to cart
  const handleAddItem = async () => {
    if (!keycloak?.authenticated) {
      dispatch(openLoginPrompt());
      return;
    }

    const obj = {
      menuItemId: menuItem.itemId,
      quantity: quantity,
      cartId: cart?.cartId,
    };

    dispatch(addToCart(obj));
    dispatch(closeItemDetails());
  };

  return (
    <div className="flex gap-3 w-full items-stretch">
      {/* Quantity Selector - Takes 50% Width */}
      <div
        className="
      flex h-12
      items-center justify-between px-2
      bg-white 
      border border-green-500
      rounded-xl
      shadow-sm 
    "
      >
        {/* Decrease */}
        <button
          onClick={decreaseQuantity}
          className="
        w-10 h-full flex items-center justify-center 
        text-gray-400 hover:text-green-700 hover:bg-green-50 
        rounded-lg transition-colors active:scale-90
      "
        >
          <Minus size={18} strokeWidth={2.5} />
        </button>

        {/* Count */}
        <span className="text-lg font-black text-green-700 min-w-[1.5rem] text-center select-none">
          {quantity}
        </span>

        {/* Increase */}
        <button
          onClick={increaseQuantity}
          className="
        w-10 h-full flex items-center justify-center 
        text-green-600 hover:text-green-800 hover:bg-green-50 
        rounded-lg transition-colors active:scale-90
      "
        >
          <Plus size={18} strokeWidth={2.5} />
        </button>
      </div>

      {/* Add to Cart Button - Takes 50% Width */}
      <button
        onClick={handleAddItem}
        className="
    flex-1 h-12 px-4
    bg-gray-900 text-white
    rounded-xl
    shadow-md
    hover:bg-black
    active:scale-[0.98] transition-all duration-200
    flex items-center justify-between
  "
      >
        <span className="text-sm font-bold tracking-wide">Add to Cart</span>

        {/* Price tag with subtle contrast against the dark background */}
        <div className="bg-white/20 px-3 py-1 rounded-lg backdrop-blur-sm">
          <span className="text-sm font-bold">
            ₹{menuItem?.itemPrice * quantity}
          </span>
        </div>
      </button>
    </div>
  );
};

export default ItemDetails;
