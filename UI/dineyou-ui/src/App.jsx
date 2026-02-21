import { useState } from "react";
import BottomNavigation from "./components/navigation/BottomNavigation";
import Navbar from "./components/navigation/Navbar";
import Hero from "./components/landing_page/Hero";
import Menucard from "./components/menucard/Menucard";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Cart from "./components/cart/Cart";
import Checkout from "./components/checkout/Checkout";
import { Toaster } from "react-hot-toast";
import Order from "./components/order/Order";
import PaymentSuccess from "./components/payment/PaymentSuccess";
import PaymentFailed from "./components/payment/PaymentFailed";

function App() {
  return (
    <div className="max-w-md mx-auto relative overflow-hidden">
      <Router>
        <Navbar />
        <Toaster position="bottom-center" reverseOrder={false} />
        <Routes>
          <Route path="/" element={<Hero />} />
          <Route path="/menucard" element={<Menucard />} />

          <Route path="/cart" element={<Cart />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/order" element={<Order />} />
          <Route path="/payment-success" element={<PaymentSuccess />} />

          <Route path="/payment-failed" element={<PaymentFailed />} />
        </Routes>
        <BottomNavigation />
      </Router>
    </div>
  );
}

export default App;
