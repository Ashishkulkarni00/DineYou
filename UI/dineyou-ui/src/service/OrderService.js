import axios from "axios";
import { getHeaders } from "./HeadersUtil";
const orderServiceHost = import.meta.env.VITE_ORDER_SERVICE_HOST;

export const fetchActiveOrders = async (userId) => {
  
  const baseHeaders = getHeaders();
  const accessToken = localStorage.getItem("kc_token");

  const headers = {
    ...baseHeaders,
    "Content-Type": "application/json",
    Authorization: accessToken ? `Bearer ${accessToken}` : "",
  };

  try {
    const response = await axios.get(
      `${orderServiceHost}/api/v1/order/getOrders/${userId}`,
      { headers }
    );

    return {
      success: true,
      data: response.data?.data || [],
      statusCode: response.status,
    };
  } catch (error) {
    return {
      success: false,
      data: [],
      error,
    };
  }
};

export const placeOrderAPI = async (orderRequest) => {
  try {
    const baseHeaders = getHeaders();
    const accessToken = localStorage.getItem("kc_token");

    const headers = {
      ...baseHeaders,
      "Content-Type": "application/json",
      Authorization: accessToken ? `Bearer ${accessToken}` : "",
    };

    const response = await axios.post(
      `${orderServiceHost}/api/v1/order/place`,
      orderRequest,
      { headers }
    );

    return {
      success: response.data.success,
      data: response.data.data || null,
      error: null,
      statusCode: response.status,
    };
  } catch (error) {
    console.log(error);
    return {
      success: false,
      data: null,
      error,
      statusCode: error?.response?.status || 500,
    };
  }
};
