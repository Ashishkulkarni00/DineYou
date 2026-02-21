// src/service/CartService.js
import axios from "axios";
import { getHeaders } from "./HeadersUtil";

const cartServiceHost = import.meta.env.VITE_CART_SERVICE_HOST;

export const fetchCartItems = async (userId) => {
  const baseHeaders = getHeaders();
  const accessToken = localStorage.getItem("kc_token");

  const headers = {
    ...baseHeaders,
    "Content-Type": "application/json",
    Authorization: accessToken ? `Bearer ${accessToken}` : "",
  };

  try {
    const response = await axios.get(
      `${cartServiceHost}/api/v1/cart/${userId}`,
      { headers }
    );

    return {
      success: true,
      data: response.data,
      statusCode: response.status,
    };
  } catch (err) {
    return {
      success: false,
      data: null,
      error: err?.response?.data || err.message,
      statusCode: err?.response?.status ?? 500,
    };
  }
};

export const calculateCartTotal = async (cartId) => {
  try {
    const baseHeaders = getHeaders();
    const accessToken = localStorage.getItem("kc_token");

    const headers = {
      ...baseHeaders,
      "Content-Type": "application/json",
      Authorization: accessToken ? `Bearer ${accessToken}` : "",
    };

    const response = await axios.get(
      `${cartServiceHost}/api/v1/cart/calculateTotal/${cartId}`,
      {
        headers,
      }
    );

    return {
      success: response.data.success,
      data: response.data.data,
      status: response.status,
    };
  } catch (error) {
    return {
      success: false,
      data: null,
      error,
    };
  }
};
