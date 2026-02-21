import React from "react";
import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useLocation, useNavigate } from "react-router-dom";
import { setPaymentDetails } from "../../store/PaymentSlice";
import { fetchPaymentStatusByReferenceIdAPI } from "../../service/PaymentService";

const PaymentSuccess = () => {
  const location = useLocation();
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    const fetchStatus = async () => {

      // CHECKS STATUS ONLY OF SINGLE PAYMENT WHICH IS MADE

      const urlParams = new URLSearchParams(location.search);
      const paymentReferenceId = urlParams.get("paymentReferenceId");

      if (!paymentReferenceId) return;

      const result = await fetchPaymentStatusByReferenceIdAPI(
        paymentReferenceId
      );

      if (result.success && result.data) {
        dispatch(setPaymentDetails(result.data));
      }

      navigate("/order");
    };

    fetchStatus();
  }, [location.search, dispatch, navigate]);

  return <div>Payement Succeeded</div>;
};

export default PaymentSuccess;
