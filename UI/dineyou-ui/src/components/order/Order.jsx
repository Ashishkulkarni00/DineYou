import React, { useCallback, useEffect, useMemo, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { setOrder } from "../../store/OrderSlice";
import OrderFooter from "./OrderFooter";
import { setPaymentDetails } from "../../store/PaymentSlice";
import { setCurrentPageId } from "../../store/uiSlice";
import { fetchActiveOrders } from "../../service/OrderService";
import { getKeycloak } from "../../service/keycloak";
import { fetchPaymentStatusByOrderIdsAPI } from "../../service/PaymentService";
import { RotateCw, ChevronRight, Clock, MapPin, Receipt } from "lucide-react";
import OrderDetailsSheet from "./OrderDetailsSheet";
import OrderLoader from "../loaders/OrderLoader";
import Footer from "../landing_page/Footer";

const menucardServiceHost = import.meta.env.VITE_MENUCARD_SERVICE_HOST;

// --- CONFIGURATION ---

const STATUS_PRIORITY = [
  "CANCELLED",
  "CANCELLATION_REQUESTED",
  "READY",
  "PREPARING",
  "PLACED",
  "PENDING",
  "PARTIALLY_SERVED",
  "SERVED",
  "COMPLETED",
  "PAYMENT_PENDING",
  "PAID",
];

const TRACK_STEPS = ["PLACED", "PREPARING", "READY", "SERVED"];

// Updated Badge Styles to match Menu's softer pastel look
const statusBadgeStyles = {
  PLACED: "bg-orange-50 text-orange-700 border border-orange-100",
  PENDING: "bg-orange-50 text-orange-700 border border-orange-100",
  PREPARING: "bg-blue-50 text-blue-700 border border-blue-100",
  READY: "bg-purple-50 text-purple-700 border border-purple-100",
  PARTIALLY_SERVED: "bg-teal-50 text-teal-700 border border-teal-100",
  SERVED: "bg-green-50 text-green-700 border border-green-100",
  PAYMENT_PENDING: "bg-amber-50 text-amber-700 border border-amber-100",
  PAID: "bg-green-50 text-green-700 border border-green-100",
  CANCELLATION_REQUESTED: "bg-red-50 text-red-700 border border-red-100",
  CANCELLED: "bg-red-50 text-red-700 border border-red-100",
  COMPLETED: "bg-gray-100 text-gray-700 border border-gray-200",
};

// --- HELPER FUNCTIONS ---

function normalizeStatus(status) {
  if (status === "PENDING") return "PLACED";
  return status;
}

function getDominantStatus(itemsByStatus) {
  const statuses = Object.keys(itemsByStatus || {});
  if (statuses.length === 0) return "PLACED";

  const idx = (s) => {
    const i = STATUS_PRIORITY.indexOf(s);
    return i === -1 ? 999 : i;
  };

  return statuses.reduce(
    (best, curr) => (idx(curr) < idx(best) ? curr : best),
    statuses[0]
  );
}

function getProgressPercent(dominantStatus) {
  const s = normalizeStatus(dominantStatus);
  if (s === "CANCELLED") return 0;
  if (s === "CANCELLATION_REQUESTED") return 10;
  const stepIndex = TRACK_STEPS.indexOf(s);
  if (stepIndex === -1) {
    if (s === "PARTIALLY_SERVED") return 90;
    if (s === "SERVED" || s === "COMPLETED") return 100;
    return 25;
  }
  return Math.round(((stepIndex + 1) / TRACK_STEPS.length) * 100);
}

function formatOrderDate(iso) {
  try {
    const date = new Date(iso);
    return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
  } catch {
    return "";
  }
}

// --- COMPONENT ---

const Order = () => {
  const dispatch = useDispatch();
  const ordersState = useSelector((state) => state.orders);
  const paymentState = useSelector((state) => state.paymentDetails);
  const keycloak = getKeycloak();
  const [openDetailsSheet, setOpenDetailsSheet] = useState(false);
  const [selectedOrder, setSelectedOrder] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    dispatch(setCurrentPageId(3));
  }, [dispatch]);

  const loadOrders = useCallback(async () => {
    try {
      setLoading(true);
      const isAuthenticated = keycloak?.authenticated;
      const userId = isAuthenticated ? keycloak.tokenParsed?.sub : null;
      if (!isAuthenticated || !userId) {
        setLoading(false);
        return;
      }
      const res = await fetchActiveOrders(userId);
      dispatch(setOrder(res.data));
    } catch (e) {
      console.error("Error fetching orders:", e);
    } finally {
      setLoading(false);
    }
  }, [dispatch, keycloak]);

  useEffect(() => {
    loadOrders();
  }, [loadOrders]);

  const paymentMap = useMemo(() => {
    const map = {};
    (paymentState?.paymentDetails || []).forEach((p) => {
      map[p.orderId] = p.status;
    });
    return map;
  }, [paymentState]);

  console.log("Payment map", paymentMap);

  useEffect(() => {
    const fetchStatuses = async () => {
      const allOrders = ordersState?.orders || [];
      const orderIds = allOrders.map((o) => o.orderId);
      if (orderIds.length === 0) return;
      const result = await fetchPaymentStatusByOrderIdsAPI(orderIds);
      if (result?.success) {
        dispatch(
          setPaymentDetails({ statuses: result.data, byOrderIds: true })
        );
      }
    };
    if (ordersState?.orders?.length > 0) fetchStatuses();
  }, [ordersState?.orders, dispatch]);

  const orders = ordersState?.orders || [];
  const unpaidOrders = orders.filter(
    (o) => paymentMap[o.orderId] !== "SUCCESS"
  );

  // --- RENDER ---

  if (!loading && (!orders || orders.length === 0)) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen bg-white pb-32 pt-20">
        <div className="w-16 h-16 bg-gray-50 rounded-full flex items-center justify-center mb-4">
          <Receipt size={32} className="text-gray-300" />
        </div>
        <h2 className="text-xl font-black text-gray-900">No Orders Yet</h2>
        <p className="text-sm text-gray-400 mt-2">
          Looks like you haven't ordered anything.
        </p>
      </div>
    );
  }

  return (
    <OrderLoader loading={loading}>
      <div className="min-h-screen bg-white pb-20 pt-20">
        {/* Page Header */}
        <div className="px-5 mb-6">
          <h1 className="text-2xl font-black text-gray-900 flex items-center gap-2">
            <span className="w-2 h-2 rounded-full bg-orange-500 shadow-[0_0_8px_rgba(249,115,22,0.6)]"></span>
            Orders
          </h1>
          <p className="text-xs text-gray-500 font-medium ml-4 mt-1">
            Track your meals in real-time
          </p>
        </div>

        {/* Orders List */}
        <div className="flex flex-col gap-6">
          {orders.map((order) => {
            const isPaid = paymentMap[order.orderId] === "SUCCESS";

            // Status Calculations
            const itemsByStatus = {};
            Object.entries(order.statusGroups || {}).forEach(
              ([status, items]) => {
                itemsByStatus[status] = items || [];
              }
            );
            const dominantStatus = getDominantStatus(itemsByStatus);
            const progress = getProgressPercent(dominantStatus);

            // Total Price
            const total = Object.values(itemsByStatus)
              .flat()
              .reduce(
                (sum, item) =>
                  sum +
                  (item?.menuItem?.itemPrice || 0) * (item?.quantity || 0),
                0
              );

            const statusLabel = dominantStatus.replace("ORDER_", "");
            const normalizedStatus = normalizeStatus(dominantStatus);

            return (
              <div key={order.orderId} className="px-4">
                <div className="group relative bg-white border border-gray-100 rounded-[2rem] shadow-[0_4px_20px_rgb(0,0,0,0.03)] overflow-hidden transition-all duration-300 hover:shadow-sm hover:-translate-y-1">
                  
                  {/* --- CARD HEADER --- */}
                  <div className="px-5 pt-5 pb-3 flex justify-between items-start">
                    <div>
                      <div className="flex items-center gap-2 mb-1">
                        <span className="text-[17px] font-black text-gray-900">
                          #{order.orderId}
                        </span>
                        {isPaid && (
                          <span className="bg-green-50 border border-green-100 text-green-700 text-[9px] font-bold px-1.5 py-0.5 rounded-md">
                            PAID
                          </span>
                        )}
                      </div>
                      <div className="flex items-center gap-1.5 text-xs text-gray-400 font-medium">
                        <Clock size={12} strokeWidth={2.5} />
                        {formatOrderDate(order.createdAt)}
                        <span className="w-1 h-1 bg-gray-300 rounded-full mx-0.5"></span>
                        <MapPin size={12} strokeWidth={2.5} />
                        Table 4
                      </div>
                    </div>

                    <span
                      className={`px-3 py-1.5 rounded-full text-[10px] font-black tracking-wide border shadow-sm uppercase ${
                        statusBadgeStyles[dominantStatus] ||
                        "bg-gray-50 text-gray-500 border-gray-100"
                      }`}
                    >
                      {statusLabel}
                    </span>
                  </div>

                  {/* --- PROGRESS BAR --- */}
                  <div className="px-5 py-2">
                     <div className="h-1.5 w-full bg-gray-100 rounded-full overflow-hidden">
                        <div 
                          className="h-full bg-gradient-to-r from-orange-400 via-red-500 to-purple-500 rounded-full transition-all duration-500 ease-out"
                          style={{ width: `${progress}%` }}
                        />
                     </div>
                     <div className="flex justify-between mt-1.5">
                        <span className={`text-[10px] font-bold ${normalizedStatus === 'PLACED' ? 'text-orange-600' : 'text-gray-300'}`}>Placed</span>
                        <span className={`text-[10px] font-bold ${normalizedStatus === 'PREPARING' ? 'text-orange-600' : 'text-gray-300'}`}>Cooking</span>
                        <span className={`text-[10px] font-bold ${normalizedStatus === 'READY' ? 'text-orange-600' : 'text-gray-300'}`}>Ready</span>
                     </div>
                  </div>

                  {/* --- ITEMS PREVIEW --- */}
                  <div className="px-2 pb-2">
                    <div className="bg-gray-50/80 rounded-3xl p-3 border border-gray-100/50">
                      {Object.values(itemsByStatus)
                        .flat()
                        .slice(0, 2)
                        .map((item, idx) => (
                          <div key={idx}>
                            <div className="flex items-center gap-3 py-2">
                              {/* Item Image */}
                              <div className="relative h-[50px] w-[50px] flex-shrink-0">
                                <img
                                  src={item.menuItem.imagePath
                                    .replace("http://localhost:8081", menucardServiceHost)
                                    .replace(".jpg", ".webp")}
                                  alt={item.menuItem.itemName}
                                  className="h-full w-full rounded-2xl object-cover border border-white shadow-sm"
                                />
                                <div className="absolute -bottom-1 -right-1 bg-white border border-gray-100 text-[10px] font-black text-gray-900 w-5 h-5 flex items-center justify-center rounded-full shadow-sm">
                                  {item.quantity}
                                </div>
                              </div>
                              
                              {/* Details */}
                              <div className="flex-1 min-w-0">
                                <h4 className="text-[13px] font-bold text-gray-900 truncate">
                                  {item.menuItem.itemName}
                                </h4>
                                <p className="text-[11px] font-semibold text-gray-500">
                                  ₹{item.menuItem.itemPrice}
                                </p>
                              </div>

                              {/* Item Total */}
                              <div className="text-[13px] font-black text-gray-900">
                                ₹{item.menuItem.itemPrice * item.quantity}
                              </div>
                            </div>
                            
                            {/* Separator if not last */}
                            {idx === 0 && Object.values(itemsByStatus).flat().length > 1 && (
                                <div className="border-b border-dashed border-gray-200 mx-2"></div>
                            )}
                          </div>
                        ))}

                      {Object.values(itemsByStatus).flat().length > 2 && (
                        <div className="pt-2 text-center">
                            <span className="text-[10px] font-bold text-gray-400 bg-white px-3 py-1 rounded-full border border-gray-100 shadow-sm">
                                + {Object.values(itemsByStatus).flat().length - 2} more items
                            </span>
                        </div>
                      )}
                    </div>
                  </div>

                  {/* --- FOOTER & ACTIONS --- */}
                  <div className="px-5 py-4 flex items-center justify-between gap-4">
                     <div className="flex flex-col">
                        <span className="text-[10px] font-bold text-gray-400 uppercase tracking-wider">Total Amount</span>
                        <span className="text-xl font-black text-gray-900 leading-none">₹{total}</span>
                     </div>

                     <div className="flex gap-2">
                        <button 
                            className="w-10 h-10 flex items-center justify-center rounded-xl bg-gray-50 text-gray-400 border border-gray-200 hover:text-orange-500 hover:bg-orange-50 transition active:scale-95"
                            title="Reorder"
                        >
                            <RotateCw size={18} strokeWidth={2.5} />
                        </button>
                        
                        <button
                            onClick={() => {
                                setSelectedOrder(order);
                                setOpenDetailsSheet(true);
                            }}
                            className="h-10 px-5 rounded-xl bg-gray-900 text-white text-[13px] font-bold flex items-center gap-2 hover:bg-black active:scale-95 transition shadow-lg shadow-gray-200"
                        >
                            Track Order
                            <ChevronRight size={14} strokeWidth={3} />
                        </button>
                     </div>
                  </div>     
                </div>
                <Footer />
              </div>
            );
          })}
        </div>

        {/* DETAILS MODAL */}
        {openDetailsSheet && (
          <div className="fixed inset-0 z-60 bg-black/50 backdrop-blur-sm flex justify-center items-end">
            <OrderDetailsSheet
              open={openDetailsSheet}
              order={selectedOrder}
              isPaid={paymentMap[selectedOrder?.orderId] === "SUCCESS"}
              menucardServiceHost={menucardServiceHost}
              onClose={() => setOpenDetailsSheet(false)}
              onPayCash={() => {}}
              onPayUpi={() => {}}
              onReorder={() => {}}
            />
          </div>
        )}

        {/* FLOATING FOOTER IF UNPAID */}
        {unpaidOrders.length > 0 && !openDetailsSheet && (
          <OrderFooter groupedOrders={unpaidOrders}  />
        )}
      </div>
    </OrderLoader>
  );
};

export default Order;