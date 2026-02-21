import axios from "axios";
import { getHeaders } from "./HeadersUtil";

const restaurantServiceHost = import.meta.env.VITE_RESTAURANT_SERVICE_HOST;

// Fetch restaurant landing page
export const fetchLandingPage = async (restaurantId) => {
  const headers = getHeaders();

  try {
    const response = await axios.get(
      `${restaurantServiceHost}/api/v1/restaurantLandingPage/getDetails/${restaurantId}`,
      { headers }
    );

    return {
      success: true,
      data: response.data, // <-- do NOT flatten
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

export const fetchRestaurantDetails = async (restaurantId) => {
  // const headers = getHeaders(
  // );

  const headers = {
    "Content-Type": "application/json",
    // remove custom headers temporarily
  };
  try {
    const response = await axios.get(
      `${restaurantServiceHost}/api/v1/restaurant/${restaurantId}`,
      { headers }
    );

    return {
      success: true,
      data: response.data, // KEEP THE ORIGINAL STRUCTURE
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
