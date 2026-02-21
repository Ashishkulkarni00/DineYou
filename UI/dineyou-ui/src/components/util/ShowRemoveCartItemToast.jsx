import React from "react";
import { toast } from "react-hot-toast";
import { useDispatch } from "react-redux";
import { removeFromCart } from "../../store/CartSlice";

const ConfirmRemoveToast = ({ t, menuItem }) => {
  const dispatch = useDispatch();
  return (
    <div className="fixed bottom-20 left-1/2 -translate-x-1/2 z-[99] max-w-md w-full">
      <div className="max-w-md w-full bg-white shadow-lg rounded-lg pointer-events-auto flex flex-col ring-1 ring-gray-500 ring-opacity-5">
        <div className="flex-1 w-full p-4">
          <p className="text-sm font-medium text-gray-900">Remove Item</p>
          <p className="mt-1 text-sm text-gray-500">
            Do you really want to remove{" "}
            <span className="font-semibold">{menuItem.itemName}</span> from
            cart?
          </p>
        </div>
        <div className="flex border-t border-gray-200">
          <button
            onClick={() => {
              toast.remove(t.id);
              dispatch(removeFromCart(menuItem.itemId));
            }}
            className="w-1/2 border border-transparent rounded-bl-lg p-3 flex items-center justify-center text-sm font-medium text-red-600 hover:bg-red-50"
          >
            Yes, Remove
          </button>
          <button
            onClick={() => {
              toast.remove(t.id);
            }}
            className="w-1/2 border border-transparent rounded-br-lg p-3 flex items-center justify-center text-sm font-medium text-gray-600 hover:bg-gray-50"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

export const showRemoveCartItemToast = (menuItem) => {
  toast.custom((t) => <ConfirmRemoveToast t={t} menuItem={menuItem} />, {
    duration: Infinity,
    className: "animate-none",
  });
};
