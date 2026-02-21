import React, { useState } from "react";
import { ArrowRight } from "lucide-react";
import { useSelector, useDispatch } from "react-redux";
import { openOrderDetailsAndPayment } from "../../store/uiSlice";
import OrdersSummary from "./OrdersSummary";

const OrderFooter = ({ groupedOrders }) => {
  
  const dispatch = useDispatch();

  const isOrderAndPaymentDetailsOpen = useSelector(
    (state) => state.ui.isOrderAndPaymentDetailsOpen
  );

  const handleOrderDetailsClick = (e) => {
    e.preventDefault();
    dispatch(openOrderDetailsAndPayment());
  };

  return (
    
    // <div className="fixed bottom-20 left-0 right-0 z-30 flex justify-center px-4">
    //   <div
    //     className="w-full max-w-md rounded-2xl bg-gradient-to-r from-green-600 to-green-700 shadow-2xl cursor-pointer hover:shadow-[0_8px_30px_rgba(34,197,94,0.4)] transition-all duration-300 active:scale-[0.98] border border-green-500/30 overflow-hidden"
    //     onClick={handleOrderDetailsClick}
    //   >
    //     {/* Subtle shimmer effect */}
    //     <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent animate-shimmer rounded-2xl pointer-events-none" />

    //     {/* Content */}
    //     <div className="relative px-5 py-4">
    //       <div className="flex items-center justify-between">
    //         {/* Left side - Text content */}
    //         <div className="flex-1">
    //           <div className="flex items-center gap-2 mb-0.5">
    //             <p className="text-base font-bold text-white">Request a bill</p>
    //             <div className="h-5 w-5 rounded-full bg-white/20 backdrop-blur-sm flex items-center justify-center">
    //               <span className="text-lg">🧾</span>
    //             </div>
    //           </div>
    //           <p className="text-sm font-medium text-green-50">
    //             Always Delicious Food 😋
    //           </p>
    //         </div>

    //         {/* Right side - Arrow button */}
    //         <div className="ml-4 h-10 w-10 rounded-full bg-white flex items-center justify-center shadow-lg transition-transform">
    //           <ArrowRight
    //             size={18}
    //             strokeWidth={2.5}
    //             className="text-green-700"
    //           />
    //         </div>
    //       </div>
    //     </div>
    //   </div>

    //   {/* Modal */}
    //   {isOrderAndPaymentDetailsOpen && (
    //     <div className="fixed inset-0 z-100 bg-black/50 backdrop-blur-sm flex items-end">
    //       <OrdersSummary groupedOrders={groupedOrders} />
    //     </div>
    //   )}
    // </div>

    <>
      {/* ================= FOOTER BAR ================= */}
      <div className="fixed bottom-20 left-0 right-0 z-30 flex justify-center px-4">
        <div
          className="w-full max-w-md rounded-2xl bg-gradient-to-r from-green-600 to-green-700 shadow-2xl cursor-pointer hover:shadow-[0_8px_30px_rgba(34,197,94,0.4)] transition-all duration-300 active:scale-[0.98] border border-green-500/30 overflow-hidden"
          onClick={handleOrderDetailsClick}
        >
          {/* Shimmer */}
          <div className="absolute inset-0 bg-gradient-to-r from-transparent via-white/10 to-transparent animate-shimmer pointer-events-none" />

          {/* Content */}
          <div className="relative px-5 py-4">
            <div className="flex items-center justify-between">
              {/* Left */}
              <div className="flex-1">
                <div className="flex items-center gap-2 mb-0.5">
                  <p className="text-base font-bold text-white">
                    Request a bill
                  </p>
                  <div className="h-5 w-5 rounded-full bg-white/20 flex items-center justify-center">
                    <span className="text-lg">🧾</span>
                  </div>
                </div>

                <p className="text-sm font-medium text-green-50">
                  Always Delicious Food 😋
                </p>
              </div>

              {/* Right */}
              <div className="ml-4 h-10 w-10 rounded-full bg-white flex items-center justify-center shadow-lg">
                <ArrowRight
                  size={18}
                  strokeWidth={2.5}
                  className="text-green-700"
                />
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* ================= MODAL (OUTSIDE FOOTER) ================= */}
      {isOrderAndPaymentDetailsOpen && (
        <div className="fixed inset-0 z-60 bg-black/50 backdrop-blur-sm flex justify-center items-end">
          <OrdersSummary
            groupedOrders={groupedOrders}
            // onClose={closeOrderSummary}
          />
        </div>
      )}
    </>
  );
};

export default OrderFooter;
