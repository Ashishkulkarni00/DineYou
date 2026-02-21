import React, { useEffect, useMemo } from "react";
import {
  X,
  PhoneCall,
  MessageCircle,
  XCircle,
  CreditCard,
  IndianRupee,
  Clock,
  Receipt,
  CheckCircle2,
  UtensilsCrossed,
} from "lucide-react";
import Footer from "../landing_page/Footer";

// --- HELPERS & CONSTANTS ---

const statusBadgeStyles = {
  PLACED: "bg-orange-50 text-orange-700 border border-orange-100",
  PREPARING: "bg-blue-50 text-blue-700 border border-blue-100",
  READY: "bg-purple-50 text-purple-700 border border-purple-100",
  PARTIALLY_SERVED: "bg-teal-50 text-teal-700 border border-teal-100",
  SERVED: "bg-green-50 text-green-700 border border-green-100",
  CANCELLATION_REQUESTED: "bg-red-50 text-red-700 border border-red-100",
  CANCELLED: "bg-red-50 text-red-700 border border-red-100",
};

const TRACK_STEPS = ["PLACED", "PREPARING", "READY", "SERVED"];

const normalizeStatus = (s) =>
  (s || "").replace("ORDER_", "").replace("PENDING", "PLACED");

const prettify = (s) =>
  normalizeStatus(s)
    .replaceAll("_", " ")
    .toLowerCase()
    .replace(/(^|\s)\S/g, (t) => t.toUpperCase());

const getProgress = (status) => {
  const s = normalizeStatus(status);
  if (s === "CANCELLED") return 0;
  if (s === "CANCELLATION_REQUESTED") return 10;
  const i = TRACK_STEPS.indexOf(s);
  if (i === -1) return 25;
  return Math.round(((i + 1) / TRACK_STEPS.length) * 100);
};

const getDominantStatus = (itemsByStatus) => {
  const keys = Object.keys(itemsByStatus || {});
  if (!keys.length) return "PLACED";

  const priority = [
    "CANCELLED",
    "CANCELLATION_REQUESTED",
    "READY",
    "PREPARING",
    "PLACED",
    "SERVED",
  ];
  const rank = (k) => {
    const s = normalizeStatus(k);
    const i = priority.indexOf(s);
    return i === -1 ? 999 : i;
  };

  return keys.reduce(
    (best, curr) => (rank(curr) < rank(best) ? curr : best),
    keys[0],
  );
};

// --- COMPONENT ---

export default function OrderDetailsSheet({
  open,
  order,
  isPaid,
  menucardServiceHost,
  onClose,
  onCancelOrder,
  onHelp,
  onCallStaff,
  onPayCash,
  onPayUpi,
}) {
  useEffect(() => {
    if (!open) return;
    const onKey = (e) => e.key === "Escape" && onClose?.();
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open, onClose]);

  // --- Calculations ---
  const itemsByStatus = useMemo(() => {
    const grouped = {};
    Object.entries(order?.statusGroups || {}).forEach(([status, items]) => {
      grouped[status] = items || [];
    });
    return grouped;
  }, [order]);

  const dominantStatus = useMemo(
    () => getDominantStatus(itemsByStatus),
    [itemsByStatus],
  );
  const progress = useMemo(() => getProgress(dominantStatus), [dominantStatus]);
  const normalizedStatus = normalizeStatus(dominantStatus);

  const allItems = useMemo(
    () => Object.values(itemsByStatus).flat(),
    [itemsByStatus],
  );

  const total = useMemo(
    () =>
      allItems.reduce(
        (sum, item) =>
          sum + (item?.menuItem?.itemPrice || 0) * (item?.quantity || 0),
        0,
      ),
    [allItems],
  );

  const canCancel =
    !isPaid &&
    ["PLACED", "PREPARING", "CANCELLATION_REQUESTED"].includes(
      normalizedStatus,
    );

  if (!open) return null;

  return (
    <div className="h-full w-full flex flex-col justify-end">
      {/* --- Floating Close Button --- */}
      <div className="flex justify-center mb-4">
        <button
          onClick={onClose}
          className="h-12 w-12 rounded-full bg-black/80 backdrop-blur-md border border-white/10 flex items-center justify-center transition-transform duration-200 active:scale-90 shadow-xl"
        >
          <X className="text-gray-100" size={24} strokeWidth={2.5} />
        </button>
      </div>

      {/* --- Main Sheet --- */}
      <div className="w-full bg-white rounded-t-[2.5rem] shadow-2xl overflow-hidden flex flex-col max-h-[75vh] z-60">
        {/* Drag Handle */}
        <div className="w-full flex justify-center pt-3 pb-1">
          <div className="w-12 h-1.5 bg-gray-200 rounded-full"></div>
        </div>

        {/* --- Header --- */}
        <div className="px-6 pt-4 pb-6">
          <div className="flex items-center justify-between gap-3">
            <div className="flex items-center gap-3">
              <div className="h-10 w-10 rounded-2xl bg-orange-50 flex items-center justify-center border border-orange-100">
                <UtensilsCrossed
                  size={20}
                  className="text-orange-600"
                  strokeWidth={2.5}
                />
              </div>
              <div>
                <h2 className="text-xl font-black text-gray-900 leading-none">
                  Order Details
                </h2>
                <p className="text-xs text-gray-400 font-medium mt-1">
                  #{order?.orderId}
                </p>
              </div>
            </div>

            <span
              className={`px-3 py-1 rounded-full text-[10px] font-black tracking-wide border shadow-sm uppercase ${
                statusBadgeStyles[normalizedStatus] ||
                "bg-gray-50 text-gray-500 border-gray-100"
              }`}
            >
              {prettify(dominantStatus)}
            </span>
          </div>
        </div>

        {/* --- Scrollable Content --- */}
        <div className="flex-1 overflow-y-auto px-6 pb-4 space-y-6 bg-white">
          {/* 1. STATUS & TRACKER CARD */}
          <div className="rounded-3xl border border-gray-100 p-5 shadow-sm bg-white">
            {/* Progress Bar */}
            <div className="mb-4">
              <div className="h-1.5 w-full bg-gray-100 rounded-full overflow-hidden">
                <div
                  className="h-full bg-gradient-to-r from-orange-400 via-red-500 to-purple-500 rounded-full transition-all duration-500"
                  style={{ width: `${progress}%` }}
                />
              </div>
              <div className="flex justify-between mt-2">
                <span
                  className={`text-[10px] font-bold ${normalizedStatus === "PLACED" ? "text-orange-600" : "text-gray-300"}`}
                >
                  Placed
                </span>
                <span
                  className={`text-[10px] font-bold ${normalizedStatus === "PREPARING" ? "text-orange-600" : "text-gray-300"}`}
                >
                  Cooking
                </span>
                <span
                  className={`text-[10px] font-bold ${normalizedStatus === "READY" ? "text-orange-600" : "text-gray-300"}`}
                >
                  Ready
                </span>
                <span
                  className={`text-[10px] font-bold ${normalizedStatus === "SERVED" ? "text-green-600" : "text-gray-300"}`}
                >
                  Served
                </span>
              </div>
            </div>

            {/* Info Grid */}
            <div className="grid grid-cols-2 gap-4 pt-4 border-t border-dashed border-gray-100">
              <div>
                <span className="text-xs text-gray-400 font-bold uppercase tracking-wider">
                  Total Bill
                </span>
                <div className="text-2xl font-black text-gray-900 mt-0.5">
                  ₹{total}
                </div>
              </div>
              <div className="flex flex-col items-end">
                <span className="text-xs text-gray-400 font-bold uppercase tracking-wider flex items-center gap-1">
                  <Clock size={10} /> Time
                </span>
                <div className="text-sm font-bold text-gray-700 mt-1">
                  {order?.createdAt
                    ? new Date(order.createdAt).toLocaleTimeString([], {
                        hour: "2-digit",
                        minute: "2-digit",
                      })
                    : "-"}
                </div>
              </div>
            </div>

            {/* Ready Notification */}
            {normalizedStatus === "READY" && (
              <div className="mt-4 bg-green-50 border border-green-100 rounded-xl p-3 flex items-start gap-3">
                <CheckCircle2 size={16} className="text-green-600 mt-0.5" />
                <p className="text-xs font-bold text-green-800 leading-snug">
                  Your order is ready! Please request serving at your table.
                </p>
              </div>
            )}
          </div>

          {/* 2. ACTION GRID */}
          <div className="grid grid-cols-2 gap-3">
            <button
              onClick={onCallStaff}
              className="h-11 rounded-2xl bg-gray-50 border border-gray-100 flex items-center justify-center gap-2 text-[13px] font-bold text-gray-700 hover:bg-gray-100 transition active:scale-95"
            >
              <PhoneCall size={16} className="text-gray-400" />
              Call Staff
            </button>
            <button
              onClick={onHelp}
              className="h-11 rounded-2xl bg-gray-50 border border-gray-100 flex items-center justify-center gap-2 text-[13px] font-bold text-gray-700 hover:bg-gray-100 transition active:scale-95"
            >
              <MessageCircle size={16} className="text-gray-400" />
              Support
            </button>

            {canCancel && (
              <button
                onClick={onCancelOrder}
                className="col-span-2 h-11 rounded-2xl bg-red-50 border border-red-100 flex items-center justify-center gap-2 text-[13px] font-bold text-red-600 hover:bg-red-100 transition active:scale-95"
              >
                <XCircle size={16} strokeWidth={2.5} />
                Cancel Order
              </button>
            )}
          </div>

          {/* 3. ITEMS LIST */}
          <div className="space-y-6">
            {Object.entries(itemsByStatus).map(([status, items]) => (
              <div key={status} className="space-y-3">
                {/* Status Header */}
                <div className="flex items-center gap-2">
                  <span className="h-1.5 w-1.5 rounded-full bg-gray-300"></span>
                  <span className="text-xs font-extrabold text-gray-400 uppercase tracking-widest">
                    {prettify(status)}
                  </span>
                </div>

                {/* Items Group */}
                <div className="bg-gray-50 rounded-2xl p-4 border border-gray-100 space-y-4">
                  {items.map((item) => (
                    <div key={item.orderItemId} className="flex gap-4">
                      {/* Image */}
                      <div className="h-14 w-14 flex-shrink-0">
                        <img
                          src={item.menuItem.imagePath
                            .replace(
                              "http://localhost:8081",
                              menucardServiceHost,
                            )
                            .replace(".jpg", ".webp")}
                          className="h-full w-full rounded-2xl object-cover border border-white shadow-sm"
                          alt={item.menuItem.itemName}
                        />
                      </div>

                      {/* Text */}
                      <div className="flex-1 min-w-0 flex flex-col justify-center">
                        <h4 className="text-[13px] font-bold text-gray-900 leading-tight mb-1">
                          {item.menuItem.itemName}
                        </h4>
                        <div className="flex items-center justify-between">
                          <span className="text-[11px] font-bold text-gray-400 bg-white px-1.5 py-0.5 rounded border border-gray-200">
                            Qty: {item.quantity}
                          </span>
                          <span className="text-[13px] font-black text-gray-900">
                            ₹{item.menuItem.itemPrice}
                          </span>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            ))}
          </div>
          <div className="h-4"></div>
          <Footer />
        </div>

        {/* --- Footer (Payment) --- */}

        {isPaid && (
          <div className="w-full h-14 rounded-2xl bg-green-50 border border-green-100 flex items-center justify-center gap-2 text-green-700">
            <div className="p-1 rounded-full bg-green-600 text-white">
              <Receipt size={14} strokeWidth={3} />
            </div>
            <span className="text-sm font-black tracking-wide">
              Payment Completed
            </span>
          </div>
        )}

        {!isPaid && (
          <p className="text-[10px] text-center text-gray-400 mt-3 font-medium">
            Payment can be completed anytime before leaving.
          </p>
        )}
      </div>
    </div>
  );
}
