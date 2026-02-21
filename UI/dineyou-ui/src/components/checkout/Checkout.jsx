import React from "react";
import { useSelector } from "react-redux";

const Checkout = () => {
  const cartItems = useSelector((state) => state.cart);
  const menucard = useSelector((state) => state.menucard);

  const allMenuItems = menucard.categories.flatMap((cat) => cat.menuItemList);

  const mappedCartItems = cartItems.map((ci) => {
    const menuItem = allMenuItems.find((mi) => mi.itemId === ci.itemId);
    return {
      ...menuItem, // menu item details
      ...ci, // cart-specific details
      orderedQuantity: ci.quantity, // explicitly add orderedQuantity
    };
  });

  console.log("mapped", mappedCartItems);

  return (
    <div className="top-20 relative px-4">
      <p>Bill Total: {}</p>
    </div>
  );
};

export default Checkout;
