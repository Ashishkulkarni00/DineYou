// import React from "react";
// import { X, ReceiptIndianRupee, IndianRupee, CreditCard } from "lucide-react";
// import { useSelector, useDispatch } from "react-redux";
// import { closeOrderAndPaymentDetails } from "../../store/uiSlice";
// import { useNavigate } from "react-router-dom";
// import { getKeycloak } from "../../service/keycloak";
// import { initializePayment, payInCash } from "../../service/PaymentService";

// const OrdersSummary = ({ groupedOrders }) => {
//   const dispatch = useDispatch();
//   const navigate = useNavigate();
//   const keycloak = getKeycloak();

//   const isOrderAndPaymentDetailsOpen = useSelector(
//     (state) => state.ui.isOrderAndPaymentDetailsOpen
//   );
//   if (!isOrderAndPaymentDetailsOpen) return null;

//   const subTotal = groupedOrders
//     .flatMap((order) => Object.values(order.statusGroups).flat())
//     .reduce(
//       (total, item) => total + item.quantity * item.menuItem.itemPrice,
//       0
//     );

//   const taxPercent = 8.5;
//   const taxAmount = (subTotal * taxPercent) / 100;
//   const grandTotal = subTotal + taxAmount;

//   const handlePaymentClick = async (method) => {
    
//     dispatch(closeOrderAndPaymentDetails());
//     const orderIds = groupedOrders.map((o) => o.orderId);
//     const restaurantId = 4;
//     const currency = "INR";
//     const paymentMethod = method;
//     const amount = grandTotal;
//     const isAuthenticated = keycloak?.authenticated;
//     const userId = isAuthenticated ? keycloak.tokenParsed.sub : null;

//     if (method === "UPI") {
//       const result = await initializePayment({
//         restaurantId,
//         orderIds,
//         currency,
//         gatewayName: "STRIPE",
//         paymentMethod,
//         amount,
//         userId,
//       });

//       if (result.success && result.data?.sessionUrl) {
//         window.location.href = result.data.sessionUrl;

//         window.addEventListener("message", (event) => {
//           if (event.data?.type === "STRIPE_PAYMENT_SUCCESS") {
//             const paymentReferenceId = event.data.paymentReferenceId;
//             navigate(
//               `/payment-success?paymentReferenceId=${paymentReferenceId}`
//             );
//           }
//         });
//       }
//     }

//     if (method === "CASH") {
//       const result = await payInCash({
//         restaurantId,
//         orderIds,
//         currency,
//         paymentMethod,
//         amount,
//         userId,
//       });

//       if (result.success && result.data?.paymentReferenceId) {
//         navigate(
//           `/payment-success?paymentReferenceId=${result.data.paymentReferenceId}`
//         );
//       }
//     }
//   };

//   return (
//     <div className="h-[90%] w-full bottom-0 flex flex-col">
//       {/* Handle + Close */}
//       <div className="flex flex-col items-center gap-3 mb-4 flex-shrink-0">
//         <button
//           className="h-11 w-11 rounded-full bg-gray-900 hover:bg-black flex items-center justify-center transition-all duration-200 active:scale-95 shadow-lg"
//           onClick={() => dispatch(closeOrderAndPaymentDetails())}
//           aria-label="Close"
//         >
//           <X size={22} strokeWidth={2.5} className="text-white" />
//         </button>
//       </div>

//       {/* Main container */}
//       <div className="flex-1 w-full rounded-3xl shadow-2xl overflow-hidden bg-white border border-gray-100 flex flex-col">
//         {/* Header (soft orange accents) */}
//         <div className="mx-auto h-1.5 w-12 rounded-full bg-gray-200 mt-3" />
//         <div className="px-5 py-5 border-b border-gray-100">
//           <div className="flex items-center justify-between gap-3">
//             <div className="flex items-center gap-3 min-w-0">
//               <div className="rounded-2xl p-2.5 border border-orange-100 bg-orange-50">
//                 <ReceiptIndianRupee
//                   size={22}
//                   className="text-orange-600"
//                   strokeWidth={2}
//                 />
//               </div>

//               <div className="min-w-0">
//                 <h2 className="text-[18px] font-extrabold text-gray-900 leading-tight">
//                   Order Summary
//                 </h2>
//                 <p className="text-xs text-gray-500 mt-0.5">
//                   Review items and complete payment
//                 </p>
//               </div>
//             </div>

//             <span className="inline-flex items-center px-3 py-1 rounded-lg text-[11px] font-bold bg-orange-50 text-orange-700 border border-orange-200 whitespace-nowrap">
//               {groupedOrders.length} order{groupedOrders.length > 1 ? "s" : ""}
//             </span>
//           </div>
//         </div>

//         {/* Scroll content */}
//         <div className="flex-1 min-h-0 overflow-y-auto p-5 bg-white">
//           {/* Total card (green highlight, orange only as accent) */}
//           <div className="rounded-2xl border border-gray-100 shadow-sm overflow-hidden">
//             <div className="p-4 bg-gradient-to-br from-green-50 via-white to-white">
//               <div className="flex items-start justify-between gap-3">
//                 <div>
//                   <div className="text-xs font-semibold text-gray-500">
//                     Payable Amount
//                   </div>
//                   <div className="mt-1 text-2xl font-extrabold text-gray-900">
//                     ₹{grandTotal.toFixed(2)}
//                   </div>
//                   <div
//                     className="mt-2 inline-flex items-center gap-2 px-2.5 py-1 rounded-full text-[11px] font-bold
//                                   bg-green-50 text-green-700 border border-green-200"
//                   >
//                     Recommended: UPI
//                   </div>
//                 </div>

//                 <div className="text-right">
//                   <div className="text-xs font-semibold text-gray-500">
//                     Subtotal
//                   </div>
//                   <div className="text-sm font-extrabold text-gray-900">
//                     ₹{subTotal}
//                   </div>
//                   <div className="text-xs text-gray-500 mt-1">
//                     Tax ({taxPercent}%):{" "}
//                     <span className="font-semibold text-gray-700">
//                       ₹{taxAmount.toFixed(2)}
//                     </span>
//                   </div>
//                 </div>
//               </div>

//               <div className="mt-4 h-2 rounded-full bg-gray-100 overflow-hidden">
//                 <div className="h-full w-[65%] bg-gradient-to-r from-orange-400 to-green-500 rounded-full" />
//               </div>
//               <p className="text-[11px] text-gray-500 mt-2">
//                 Includes applicable taxes. Ask staff if you need a GST invoice.
//               </p>
//             </div>
//           </div>

//           {/* Orders */}
//           <div className="mt-5 space-y-4">
//             {groupedOrders.map((order, orderIndex) => {
//               const items = Object.values(order.statusGroups).flat();
//               const orderTotal = items.reduce(
//                 (sum, item) => sum + item.quantity * item.menuItem.itemPrice,
//                 0
//               );

//               return (
//                 <div
//                   key={order.orderId}
//                   className="bg-white border border-gray-100 rounded-2xl p-4 shadow-sm"
//                 >
//                   <div className="flex items-center justify-between gap-3">
//                     <div className="flex items-center gap-2 min-w-0">
//                       <div className="h-2 w-2 rounded-full bg-gradient-to-r from-orange-400 to-green-500" />
//                       <h3 className="text-[15px] font-extrabold text-gray-900 truncate">
//                         Order {orderIndex + 1}
//                       </h3>
//                     </div>

//                     <span className="px-2.5 py-1 rounded-full text-[11px] font-bold bg-gray-50 text-gray-700 border border-gray-200">
//                       #{order.orderId}
//                     </span>
//                   </div>

//                   <div className="mt-3 divide-y divide-gray-100">
//                     {items.map((item) => (
//                       <div
//                         key={item.orderItemId}
//                         className="py-3 flex items-start justify-between gap-3"
//                       >
//                         <div className="min-w-0">
//                           <p className="text-sm font-bold text-gray-900 truncate">
//                             {item.menuItem.itemName}
//                           </p>
//                           <p className="text-xs text-gray-500 mt-0.5">
//                             Qty: {item.quantity} • ₹{item.menuItem.itemPrice}{" "}
//                             each
//                           </p>
//                         </div>

//                         <p className="text-sm font-extrabold text-gray-900 whitespace-nowrap">
//                           ₹{item.quantity * item.menuItem.itemPrice}
//                         </p>
//                       </div>
//                     ))}
//                   </div>

//                   <div className="mt-3 pt-3 border-t border-gray-100 flex justify-between items-center">
//                     <span className="text-sm font-semibold text-gray-600">
//                       Order Total
//                     </span>
//                     <span className="text-sm font-extrabold text-gray-900">
//                       ₹{orderTotal}
//                     </span>
//                   </div>
//                 </div>
//               );
//             })}
//           </div>

//           {/* Note */}
//           <div className="mt-5 rounded-2xl border border-orange-100 bg-orange-50 px-4 py-3">
//             <div className="text-xs font-bold text-orange-700">Tip</div>
//             <div className="text-[11px] text-orange-700/80 mt-1">
//               If UPI fails, retry once or choose Cash and inform staff.
//             </div>
//           </div>

//           <div className="h-2" />
//         </div>

//         {/* Fixed Payment Buttons (kept structure; only green styling for UPI) */}
//         <div className="flex-shrink-0 border-t border-gray-200 bg-white p-5 rounded-b-3xl">
//           <div className="flex gap-3">
//             <button
//               type="button"
//               onClick={() => handlePaymentClick("CASH")}
//               className="flex-1 h-12 rounded-2xl border border-gray-200 bg-white text-sm font-extrabold text-gray-800
//                              hover:bg-gray-50 active:scale-95 transition"
//             >
//               <span className="inline-flex items-center justify-center gap-1">
//                 <IndianRupee size={16} strokeWidth={2.5} />
//                 Cash
//               </span>
//             </button>

//             <button
//               type="button"
//               onClick={() => handlePaymentClick("UPI")}
//               className="flex-1 h-12 rounded-2xl bg-gradient-to-r from-green-600 to-green-700 text-white
//                              text-sm font-extrabold shadow-2xl cursor-pointer
//                              hover:shadow-[0_8px_30px_rgba(34,197,94,0.4)]
//                              active:scale-95 transition"
//             >
//               <span className="inline-flex items-center justify-center gap-2">
//                 <CreditCard size={16} strokeWidth={2.5} />
//                 Pay with UPI
//               </span>
//             </button>
//           </div>

//           <p className="text-[11px] text-gray-500 mt-2">
//             Payment can be completed anytime before leaving the table.
//           </p>
//         </div>
//       </div>
//     </div>
//   );
// };

// export default OrdersSummary;

import React from "react";
import { X, Receipt, IndianRupee, CreditCard, Wallet, ChevronDown, ShieldCheck } from "lucide-react";
import { useSelector, useDispatch } from "react-redux";
import { closeOrderAndPaymentDetails } from "../../store/uiSlice";
import { useNavigate } from "react-router-dom";
import { getKeycloak } from "../../service/keycloak";
import { initializePayment, payInCash } from "../../service/PaymentService";
import Footer from "../landing_page/Footer";

const OrdersSummary = ({ groupedOrders }) => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const keycloak = getKeycloak();

  const isOrderAndPaymentDetailsOpen = useSelector(
    (state) => state.ui.isOrderAndPaymentDetailsOpen
  );
  if (!isOrderAndPaymentDetailsOpen) return null;

  // --- Calculations ---
  const subTotal = groupedOrders
    .flatMap((order) => Object.values(order.statusGroups).flat())
    .reduce(
      (total, item) => total + item.quantity * item.menuItem.itemPrice,
      0
    );

  const taxPercent = 8.5;
  const taxAmount = (subTotal * taxPercent) / 100;
  const grandTotal = subTotal + taxAmount;

  // --- Handlers ---
  const handlePaymentClick = async (method) => {
    dispatch(closeOrderAndPaymentDetails());
    const orderIds = groupedOrders.map((o) => o.orderId);
    const restaurantId = 4;
    const currency = "INR";
    const paymentMethod = method;
    const amount = grandTotal;
    const isAuthenticated = keycloak?.authenticated;
    const userId = isAuthenticated ? keycloak.tokenParsed.sub : null;

    if (method === "UPI") {
      const result = await initializePayment({
        restaurantId,
        orderIds,
        currency,
        gatewayName: "STRIPE",
        paymentMethod,
        amount,
        userId,
      });

      if (result.success && result.data?.sessionUrl) {
        window.location.href = result.data.sessionUrl;

        window.addEventListener("message", (event) => {
          if (event.data?.type === "STRIPE_PAYMENT_SUCCESS") {
            const paymentReferenceId = event.data.paymentReferenceId;
            navigate(
              `/payment-success?paymentReferenceId=${paymentReferenceId}`
            );
          }
        });
      }
    }

    if (method === "CASH") {
      const result = await payInCash({
        restaurantId,
        orderIds,
        currency,
        paymentMethod,
        amount,
        userId,
      });

      if (result.success && result.data?.paymentReferenceId) {
        navigate(
          `/payment-success?paymentReferenceId=${result.data.paymentReferenceId}`
        );
      }
    }
  };

  return (
    <div className="h-full w-full flex flex-col justify-end">
      
      {/* --- Close Button (Floating) --- */}
      <div className="flex justify-center mb-4">
        <button
          onClick={() => dispatch(closeOrderAndPaymentDetails())}
          className="h-12 w-12 rounded-full bg-black/80 backdrop-blur-md border border-white/10 flex items-center justify-center transition-transform duration-200 active:scale-90 shadow-xl"
        >
          <X className="text-gray-100" size={24} strokeWidth={2.5} />
        </button>
      </div>

      {/* --- Main Sheet Container --- */}
      <div className="w-full bg-white rounded-t-[2.5rem] shadow-2xl overflow-hidden flex flex-col max-h-[75vh]">
        
        {/* Drag Handle */}
        <div className="w-full flex justify-center pt-3 pb-1">
          <div className="w-12 h-1.5 bg-gray-200 rounded-full"></div>
        </div>

        {/* --- Header --- */}
        <div className="px-6 pt-4 pb-6">
          <div className="flex items-center gap-3">
            <div className="h-10 w-10 rounded-2xl bg-orange-50 flex items-center justify-center border border-orange-100">
              <Receipt size={20} className="text-orange-600" strokeWidth={2.5} />
            </div>
            <div>
              <h2 className="text-xl font-black text-gray-900 leading-none">Order Summary</h2>
              <p className="text-xs text-gray-400 font-medium mt-1">Review items and complete payment</p>
            </div>
            <div className="ml-auto">
                <p className="px-3 py-1 rounded-full bg-gray-50 border border-gray-100 text-xl font-bold text-gray-600">
                    {groupedOrders.length} 
                </p>
            </div>
          </div>
        </div>

        {/* --- Scrollable Content --- */}
        <div className="flex-1 overflow-y-auto px-6 pb-4 space-y-6 bg-white">
          
          {/* 1. PAYABLE AMOUNT CARD */}
          <div className="relative overflow-hidden rounded-3xl border border-green-100 bg-gradient-to-b from-green-50/50 to-white shadow-sm">
            <div className="p-5">
              <div className="flex justify-between items-start mb-2">
                <span className="text-xs font-bold text-gray-500 uppercase tracking-wider">Payable Amount</span>
                <div className="flex items-center gap-1 bg-green-100/80 px-2 py-0.5 rounded text-[10px] font-bold text-green-800 border border-green-200">
                    <ShieldCheck size={10} />
                    Secure Payment
                </div>
              </div>
              
              <div className="flex items-baseline gap-1">
                <span className="text-3xl font-black text-gray-900">₹{grandTotal.toFixed(2)}</span>
              </div>

              {/* Progress Line */}
              <div className="mt-4 h-1.5 w-full bg-gray-100 rounded-full overflow-hidden">
                <div className="h-full w-2/3 bg-gradient-to-r from-orange-400 to-green-500 rounded-full"></div>
              </div>

              {/* Tax Details Accordion-like look */}
              <div className="mt-4 pt-3 border-t border-dashed border-green-200/50 flex flex-col gap-1.5">
                 <div className="flex justify-between text-xs text-gray-500 font-medium">
                    <span>Item Total</span>
                    <span>₹{subTotal.toFixed(2)}</span>
                 </div>
                 <div className="flex justify-between text-xs text-gray-500 font-medium">
                    <span>Taxes & Charges ({taxPercent}%)</span>
                    <span>₹{taxAmount.toFixed(2)}</span>
                 </div>
              </div>
            </div>
          </div>

          {/* 2. ORDER LIST */}
          <div className="space-y-4">
             <h3 className="text-sm font-black text-gray-900 flex items-center gap-2">
                Your Items
                <div className="h-[1px] flex-1 bg-gray-100"></div>
             </h3>

            {groupedOrders.map((order, idx) => {
              const items = Object.values(order.statusGroups).flat();
              
              return (
                <div key={order.orderId} className="group">
                  {/* Order Header */}
                  <div className="flex items-center gap-2 mb-2">
                     <span className="h-2 w-2 rounded-full bg-orange-500"></span>
                     <span className="text-xs font-extrabold text-gray-700">Order #{order.orderId}</span>
                  </div>

                  {/* Items Container */}
                  <div className="bg-gray-50 rounded-2xl p-4 border border-gray-100">
                    <div className="space-y-3">
                        {items.map((item) => (
                            <div key={item.orderItemId} className="flex justify-between items-start">
                                <div className="flex gap-3">
                                    <div className="mt-1 text-[10px] font-bold text-gray-400 border border-gray-200 rounded px-1.5 h-fit">
                                        {item.quantity}x
                                    </div>
                                    <div className="flex flex-col">
                                        <span className="text-[13px] font-bold text-gray-800 leading-tight">
                                            {item.menuItem.itemName}
                                        </span>
                                        <span className="text-[11px] text-gray-400 font-medium">
                                            ₹{item.menuItem.itemPrice} per item
                                        </span>
                                    </div>
                                </div>
                                <span className="text-[13px] font-black text-gray-900">
                                    ₹{item.quantity * item.menuItem.itemPrice}
                                </span>
                            </div>
                        ))}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>

          {/* Spacer for fixed bottom */}
          
          <Footer />
        </div>

        

        {/* --- Footer Actions --- */}
        <div className="px-6 py-5 bg-white border-t border-gray-100 shadow-[0_-10px_40px_rgba(0,0,0,0.03)] z-10">
          
          <div className="flex gap-3">
            {/* CASH BUTTON */}
            <button
                onClick={() => handlePaymentClick("CASH")}
                className="flex-1 flex items-center justify-center gap-2 py-3.5 rounded-2xl border border-gray-200 bg-white text-gray-700 font-bold text-[13px] hover:bg-gray-50 active:scale-95 transition-all"
            >
                <IndianRupee size={16} />
                Cash
            </button>

            {/* UPI BUTTON (Primary) */}
            <button
                onClick={() => handlePaymentClick("UPI")}
                className="flex-[1.5] flex flex-col items-center justify-center py-2 rounded-2xl bg-gradient-to-r from-green-600 to-green-700 text-white shadow-lg shadow-green-200 hover:shadow-green-300 active:scale-95 transition-all relative overflow-hidden group"
            >
                <div className="flex items-center gap-2">
                    <span className="text-[14px] font-black tracking-wide">Pay via UPI</span>
                    <CreditCard size={16} strokeWidth={2.5} />
                </div>
                <span className="text-[10px] font-medium opacity-90">Recommended</span>
                
                {/* Shine Effect */}
                <div className="absolute inset-0 bg-white/20 translate-x-[-100%] skew-x-12 group-hover:translate-x-[100%] transition-transform duration-700" />
            </button>
          </div>
          
          <p className="text-[10px] text-center text-gray-400 mt-3 font-medium">
            Payments are secured by Stripe & standard encryption
          </p>
        </div>
      </div>
    </div>
  );
};

export default OrdersSummary;