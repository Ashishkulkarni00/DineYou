// src/service/MenucardService.js
import axios from "axios";
import { getHeaders } from "./HeadersUtil";

const menuCardServiceHost = import.meta.env.VITE_MENUCARD_SERVICE_HOST;

// Get full menu card of a restaurant
export const fetchMenuCard = async (restaurantId) => {
  const headers = getHeaders();

  try {
    const response = await axios.get(
      `${menuCardServiceHost}/api/v1/menucard/restaurant/${restaurantId}`,
      { headers }
    );

    return {
      success: true,
      data: response.data,
      statusCode: response.status
    };
  } catch (err) {
    return {
      success: false,
      data: null,
      error: err?.response?.data || err.message,
      statusCode: err?.response?.status ?? 500
    };
  }
};

// Get ONLY popular items
export const fetchPopularItems = async (restaurantId) => {
  const headers = getHeaders();

  try {
    const response = await axios.get(
      `${menuCardServiceHost}/api/v1/menucardItem/getPopularItems/${restaurantId}`,
      { headers }
    );

    return {
      success: true,
      data: response.data,
      statusCode: response.status
    };
  } catch (err) {
    return {
      success: false,
      data: null,
      error: err?.response?.data || err.message,
      statusCode: err?.response?.status ?? 500
    };
  }
};
