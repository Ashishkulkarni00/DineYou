// paymentService.js
import axios from "axios";
import { getHeaders } from "./HeadersUtil";
const paymentServiceHost = import.meta.env.VITE_PAYMENT_SERVICE_HOST;

export const fetchPaymentStatusByOrderIdsAPI = async (orderIds = []) => {
  if (orderIds.length === 0) return { success: true, data: [] };

  try {
    const ids = orderIds.join(",");
    const baseHeaders = getHeaders();
    const accessToken = localStorage.getItem("kc_token");

    const headers = {
      ...baseHeaders,
      "Content-Type": "application/json",
      Authorization: accessToken ? `Bearer ${accessToken}` : "",
    };

    const response = await axios.get(
      `${paymentServiceHost}/api/v1/payment/getPaymentStatus/by-order-ids/${ids}`,
      { headers }
    );

    const statuses = response.data.data.map((p) => ({
      orderId: p.orderId,
      paymentReferenceId: p.paymentReferenceId,
      paidAmount: p.paidAmount,
      status: p.paymentStatus,
    }));

    return { success: true, data: statuses };
  } catch (error) {
    console.error("Error fetching payment statuses:", error);
    return { success: false, data: [], error };
  }
};

export const initializePayment = async ({
  restaurantId,
  orderIds,
  currency,
  gatewayName,
  paymentMethod,
  amount,
  userId,
}) => {
  try {
    const baseHeaders = getHeaders();
    const accessToken = localStorage.getItem("kc_token");

    const headers = {
      ...baseHeaders,
      "Content-Type": "application/json",
      Authorization: accessToken ? `Bearer ${accessToken}` : "",
    };

    const response = await axios.post(
      `${paymentServiceHost}/api/v1/payment/initialize`,
      {
        restaurantId,
        orderIds,
        currency,
        gatewayName,
        paymentMethod,
        amount,
        userId,
      },
      {
        headers,
      }
    );

    return { success: true, data: response.data.data };
  } catch (error) {
    console.error("❌ Error initializing payment:", error);
    return { success: false, error };
  }
};

// Pay in cash
export const payInCash = async ({
  restaurantId,
  orderId,
  currency,
  paymentMethod,
  amount,
  userId,
}) => {
  try {
    const baseHeaders = getHeaders();
    const accessToken = localStorage.getItem("kc_token");

    const headers = {
      ...baseHeaders,
      "Content-Type": "application/json",
      Authorization: accessToken ? `Bearer ${accessToken}` : "",
    };
    const response = await axios.post(
      `${paymentServiceHost}/api/v1/payment/pay-in-cash`,
      {
        restaurantId,
        orderId,
        currency,
        gatewayName: "CASH",
        paymentMethod,
        amount,
        userId,
      },
      { headers }
    );

    return { success: true, data: response.data.data };
  } catch (error) {
    console.error("❌ Error making cash payment:", error);
    return { success: false, error };
  }
};

export const fetchPaymentStatusByReferenceIdAPI = async (paymentReferenceId) => {
  try {
    const baseHeaders = getHeaders();
    const accessToken = localStorage.getItem("kc_token");

    const headers = {
      ...baseHeaders,
      "Content-Type": "application/json",
      Authorization: accessToken ? `Bearer ${accessToken}` : "",
    };

    const response = await axios.get(
      `${paymentServiceHost}/api/v1/payment/getPaymentStatus/${paymentReferenceId}`,
      { headers }  // ✅ Correct
    );

    return {
      success: true,
      data: response.data.data || null,
    };
  } catch (error) {
    console.error("❌ Failed to fetch payment status by reference id:", error);
    return {
      success: false,
      data: null,
      error,
    };
  }
};
