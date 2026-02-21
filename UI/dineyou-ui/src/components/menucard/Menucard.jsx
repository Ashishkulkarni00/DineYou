import Input from "../input/Input";
import React, { useEffect, useState, useRef } from "react";

import {
  Plus,
  Minus,
  Star,
  Bookmark,
  Share2,
  ChevronDown,
  Search,
  SlidersHorizontal,
  Flame,
  MapPin,
} from "lucide-react";
import { Swiper, SwiperSlide } from "swiper/react";
import { useDispatch, useSelector } from "react-redux";
import {
  clearSearchedMenuItem,
  closeItemCategories,
  openItemDetails,
  setCurrentPageId,
  setMenucardCategory,
  setMenucardFilters,
} from "../../store/uiSlice";
import { setMenucard } from "../../store/MenucardSlice";
import { setPopularItems } from "../../store/PopularItemsSlice";
import { setCurrentlyClickedMenuItem } from "../../store/CurrentlyClickedMenuItemSlice";
import "swiper/css";
import { removeFromCart, updateQuantity, setCart } from "../../store/CartSlice";
import ItemDetails from "./ItemDetails";
import ItemCategories from "./ItemCategories";
import CartFooter from "../cart/CartFooter";
import { getKeycloak } from "../../service/keycloak";
import {
  fetchMenuCard,
  fetchPopularItems,
} from "../../service/MenucardService";
import { fetchCartItems } from "../../service/CartService";
import FilterBottomSheet from "./FilterBottomSheet";
import { filterMenucardData } from "../util/FilterMenucardData";
import FilterBar from "./FilterBar";
import MenucardLoader from "../loaders/MenucardLoader";
const menucardServiceHost = import.meta.env.VITE_MENUCARD_SERVICE_HOST;

const Menucard = () => {
  const dispatch = useDispatch();
  const keycloak = getKeycloak();

  const [filtersOpen, setFiltersOpen] = useState(false);
  const [loading, setLoading] = useState(true);

  const menucardFilters = useSelector((state) => state.ui.menucardFilters);

  const isItemDetailsOpen = useSelector((state) => state.ui.isItemDetailsOpen);

  const selectedMenucardCategory = useSelector(
    (state) => state.ui.selectedMenucardCategory,
  );

  const searchedMenuItem = useSelector((state) => state.ui.searchedMenuItem);
  const itemRefs = useRef({});

  const cart = useSelector((state) => state.cart);

  const [openCategories, setOpenCategories] = useState({});

  const categoryRefs = useRef({});

  useEffect(() => {
    dispatch(setCurrentPageId(1));
  }, [dispatch]);

  useEffect(() => {
    if (searchedMenuItem?.itemId) {
      const el = itemRefs.current[searchedMenuItem.itemId];
      if (el) {
        el.classList.remove("highlight");
        void el.offsetWidth;
        el.classList.add("highlight");
      }
    }
  }, [searchedMenuItem]);

  useEffect(() => {
    if (
      selectedMenucardCategory &&
      categoryRefs.current[selectedMenucardCategory]
    ) {
      setOpenCategories((prev) => ({
        ...prev,
        [selectedMenucardCategory]: true,
      }));

      categoryRefs.current[selectedMenucardCategory].scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    }
  }, [selectedMenucardCategory]);

  const toggleCategory = (categoryName) => {
    setOpenCategories((prev) => ({
      ...prev,
      [categoryName]: !(prev[categoryName] ?? true),
    }));
  };

  const areItemCategoriesOpen = useSelector(
    (state) => state.ui.areItemCategoriesOpen,
  );

  const [currentClickedItem, setCurrentClickedItem] = useState(null);

  const [menucardData, setMenucardData] = useState(null);
  const [menucardDataFiltered, setMenucardDataFiltered] = useState(null);

  useEffect(() => {
    if (searchedMenuItem && menucardData) {
      // 1. Open the category
      setOpenCategories((prev) => ({
        ...prev,
        [searchedMenuItem.categoryName]: true,
      }));

      // 2. Scroll category into view (you already do this)
      setMenucardCategory(searchedMenuItem.categoryName);

      // 3. Scroll to the actual item
      setTimeout(() => {
        const itemDiv = itemRefs.current[searchedMenuItem.itemId];
        if (itemDiv) {
          itemDiv.scrollIntoView({
            behavior: "smooth",
            block: "center",
          });
        }
      }, 200);

      // 4. Clear the state so next time doesn't auto-scroll again
      dispatch(clearSearchedMenuItem());
    }
  }, [searchedMenuItem, menucardData]);

  useEffect(() => {
    let cancelled = false;

    const fetchData = async () => {
      try {
        setLoading(true);

        const restaurantId = 4;
        const isAuthenticated = keycloak?.authenticated;
        const userId = isAuthenticated ? keycloak.tokenParsed.sub : null;

        const promiseList = [
          fetchMenuCard(restaurantId),
          fetchPopularItems(restaurantId),
        ];
        if (isAuthenticated) promiseList.push(fetchCartItems(userId));

        const [menuCardResult, popularItemsResult, cartResult] =
          await Promise.allSettled(promiseList);

        if (cancelled) return;

        if (
          menuCardResult.status === "fulfilled" &&
          menuCardResult.value.success
        ) {
          const menuCardData = menuCardResult.value.data.data;
          dispatch(setMenucard(menuCardData));
          setMenucardData(menuCardData);
        }

        if (
          popularItemsResult.status === "fulfilled" &&
          popularItemsResult.value.success
        ) {
          dispatch(setPopularItems(popularItemsResult.value.data.data));
        }

        if (
          isAuthenticated &&
          cartResult?.status === "fulfilled" &&
          cartResult.value.success
        ) {
          const rawItems = cartResult.value.data.data.cartItemList;

          const cartItems = Array.from(
            new Map(
              rawItems.map((item) => [
                item.menuItemId,
                {
                  cartItemId: item.cartItemId,
                  menuItemId: item.menuItemId,
                  quantity: item.quantity,
                },
              ]),
            ).values(),
          );

          dispatch(
            setCart({
              cartId: cartResult.value.data.data.cartId,
              items: cartItems,
            }),
          );
        }
      } catch (err) {
        if (!cancelled) console.error("Unexpected error in fetchData:", err);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    fetchData();
    return () => {
      cancelled = true;
    };
  }, [keycloak?.authenticated, dispatch]);

  useEffect(() => {
    setMenucardDataFiltered(filterMenucardData(menucardData, menucardFilters));
  }, [menucardData, menucardFilters]);

  function renderRatingStars(rating) {
    const stars = [];

    for (let i = 1; i <= 5; i++) {
      if (rating >= i) {
        // Full star
        stars.push(
          <Star
            key={i}
            fill="#ffd300"
            height={18}
            width={18}
            strokeWidth={0}
          />,
        );
      } else if (rating >= i - 0.5) {
        // Half star
        stars.push(
          <Star
            key={i}
            fill="url(#half)"
            height={18}
            width={18}
            strokeWidth={0}
          />,
        );
      } else {
        // Empty star
        stars.push(
          <Star
            key={i}
            fill="none"
            height={18}
            width={18}
            stroke="#ffd300"
            strokeWidth={1}
          />,
        );
      }
    }
    return stars;
  }

  const handleMenucarImageClick = (menuItem) => {
    dispatch(setCurrentlyClickedMenuItem(menuItem));
    dispatch(openItemDetails());
  };

  return (
    <MenucardLoader loading={loading}>
      <div className={`min-h-screen bg-white pb-32 pt-20 relative`}>
        <FilterBottomSheet
          open={filtersOpen}
          onClose={() => setFiltersOpen(false)}
          initialValue={menucardFilters}
          onApply={(v) => dispatch(setMenucardFilters(v))}
        />

        {/* Search Input */}
        <div className="px-4 mb-3">
          <Input />
        </div>

        {/* Filter Bar */}
        <div className="mb-4 pl-1">
          <FilterBar
            filters={menucardFilters}
            onChangeFilters={(next) => dispatch(setMenucardFilters(next))}
            onOpenFilters={() => setFiltersOpen(true)}
          />
        </div>

        {/* Popular Items */}
        <div className="mb-6">
          <PopularItemsScroller />
        </div>

        {/* Section Divider */}
        <div className="h-2 bg-gray-50 w-full mb-4"></div>

        {/* ==============================================
            MENU CATEGORIES
           ============================================== */}
        <div className="flex flex-col gap-2">
          {menucardDataFiltered?.categories?.map((category) => {
            const isOpen = openCategories[category.categoryName] ?? true;

            return (
              <div key={category.categoryName} className="bg-white">
                {/* Sticky Header */}
                {menucardFilters.sort !== "priceLow" &&
                menucardFilters.sort !== "priceHigh" ? (
                  <div
                    className="sticky top-16 z-16 bg-white/95 backdrop-blur-md px-4 py-3.5 flex justify-between items-center cursor-pointer border-b border-gray-50 transition-shadow duration-300 shadow-[0_4px_10px_rgba(0,0,0,0.02)]"
                    onClick={() => toggleCategory(category.categoryName)}
                    ref={(el) =>
                      (categoryRefs.current[category.categoryName] = el)
                    }
                  >
                    <h2 className="text-lg font-black text-gray-900 flex items-center gap-2">
                      <span className="w-1.5 h-1.5 rounded-full bg-orange-500 shadow-[0_0_8px_rgba(249,115,22,0.6)]"></span>
                      {category.categoryName}
                    </h2>

                    <ChevronDown
                      size={20}
                      className={`text-gray-400 transition-transform duration-300 ${isOpen ? "rotate-180" : "rotate-0"}`}
                    />
                  </div>
                ) : null}

                {/* ITEMS LIST */}
                {isOpen && (
                  <div className="pt-2">
                    {category?.menuItemList?.map((menuItem, index) => (
                      <div
                        key={menuItem.itemId}
                        ref={(el) => (itemRefs.current[menuItem.itemId] = el)}
                        className="relative"
                      >
                        <div className="px-4 py-4 flex items-start gap-4">
                          {/* --- ITEM DETAILS (Left) --- */}
                          <div className="flex-1 min-w-0 pt-1 pb-2">
                            <h3 className="text-[1.05rem] font-bold text-gray-900 mb-1 leading-tight">
                              {menuItem.itemName}
                            </h3>

                            {/* Stars */}
                            <div className="flex items-center gap-1 mb-1.5">
                              <div className="flex items-center gap-0.5 bg-green-50 px-1.5 py-[2px] rounded border border-green-100">
                                <Star
                                  size={8}
                                  className="fill-green-700 text-green-700"
                                />
                                <span className="text-[10px] font-bold text-green-700">
                                  {menuItem.rating}
                                </span>
                              </div>
                            </div>

                            {/* Price */}
                            <div className="flex items-center gap-2 mb-2">
                              <span className="text-[0.95rem] font-black text-gray-900">
                                ₹{menuItem.itemPrice}
                              </span>
                              {menuItem.discountPercentage > 0 && (
                                <span className="text-[9px] font-bold text-orange-700 bg-orange-50 px-1.5 py-0.5 rounded border border-orange-100">
                                  {menuItem.discountPercentage}% OFF
                                </span>
                              )}
                            </div>

                            <p className="text-xs text-gray-500 font-medium leading-relaxed line-clamp-2">
                              {menuItem.itemDescription}
                            </p>

                            {/* Actions */}
                            <div className="flex gap-2 mt-4">
                              <button className="p-2 rounded-xl bg-gray-50 text-gray-400 hover:text-orange-500 hover:bg-orange-50 transition-colors active:scale-95">
                                <Bookmark size={18} strokeWidth={2} />
                              </button>
                              <button className="p-2 rounded-xl bg-gray-50 text-gray-400 hover:text-orange-500 hover:bg-orange-50 transition-colors active:scale-95">
                                <Share2 size={18} strokeWidth={2} />
                              </button>
                            </div>
                          </div>

                          {/* --- IMAGE & BUTTON (Right) --- */}
                          {/* Increased Width to 140px and Height to 125px */}
                          <div className="relative w-[140px] flex-shrink-0 flex flex-col items-center pt-1">
                            <div
                              className="w-full h-[125px] rounded-2xl overflow-hidden shadow-sm border border-gray-100 bg-gray-50 relative group cursor-pointer"
                              onClick={() => handleMenucarImageClick(menuItem)}
                            >
                              <img
                                src={menuItem.imagePath
                                  .replace(
                                    "http://localhost:8081",
                                    menucardServiceHost,
                                  )
                                  .replace(".jpg", ".webp")}
                                alt={menuItem.itemName}
                                loading="lazy"
                                className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-105"
                              />
                              {/* Dark overlay on hover */}
                              <div className="absolute inset-0 bg-black/5 opacity-0 group-hover:opacity-100 transition-opacity" />
                            </div>

                            <div className="absolute -bottom-4 w-[85%] z-10">
                              <AddButton menuItem={menuItem} />
                            </div>
                          </div>
                        </div>

                        {/* Dashed Line Separator */}
                        {index !== category.menuItemList.length - 1 && (
                          <div className="mx-4 my-1 border-b border-dashed border-gray-200"></div>
                        )}
                      </div>
                    ))}
                  </div>
                )}
              </div>
            );
          })}
        </div>

        {/* Modals */}
        {isItemDetailsOpen && (
          <div className="fixed inset-0 z-60 bg-black/50 backdrop-blur-sm flex justify-center items-end">
            <ItemDetails menuItem={currentClickedItem} />
          </div>
        )}

        <div className="relative">  
          {areItemCategoriesOpen && (
            <div className="fixed inset-0 z-20 bg-black/50 backdrop-blur-sm flex justify-between items-center">
              <div
                className="w-full h-full"
                onClick={() => dispatch(closeItemCategories())}
              />
              <ItemCategories />
            </div>
          )}
        </div>

        {/* Cart Footer */}
        {cart?.items.length > 0 &&
          !isItemDetailsOpen &&
          !areItemCategoriesOpen && <CartFooter />}
      </div>
    </MenucardLoader>
  );
};

const AddButton = ({ menuItem }) => {
  const keycloak = getKeycloak();
  const cart = useSelector((state) => state.cart);
  const dispatch = useDispatch();

  const existingItem = cart?.items.find(
    (item) => item.menuItemId === menuItem.itemId,
  );

  const handleAddButtonClick = async () => {
    if (!existingItem) dispatch(openItemDetails());
    dispatch(setCurrentlyClickedMenuItem(menuItem));
  };

  const handleMinus = async () => {
    if (existingItem.quantity === 1) {
      dispatch(removeFromCart(menuItem.itemId));
    } else {
      dispatch(
        updateQuantity({
          menuItemId: menuItem.itemId,
          quantity: existingItem.quantity - 1,
        }),
      );
    }
  };

  const handlePlus = async () => {
    dispatch(
      updateQuantity({
        menuItemId: menuItem.itemId,
        quantity: existingItem.quantity + 1,
      }),
    );
  };

  return (
    <div className="w-full h-11 relative">
      {!existingItem ? (
        <button
          onClick={handleAddButtonClick}
          className="
        group w-full h-full
        bg-white 
        border border-green-500
        text-green-700
        rounded-xl
        font-extrabold text-sm tracking-wide
        shadow-sm hover:shadow-md
        hover:bg-green-50 hover:border-green-200
        active:scale-95 transition-all duration-200
        flex items-center justify-center gap-1.5
      "
        >
          ADD
          <Plus size={14} strokeWidth={4} className="text-green-700" />
        </button>
      ) : (
        // STATE 2: ACTIVE QUANTITY CONTROLS
        <div
          className="
        flex items-center justify-around w-full h-11 px-1
        bg-white 
        border border-green-500
        rounded-xl
        shadow-sm 
        animate-in fade-in zoom-in-95 duration-200
      "
        >
          {/* Decrease */}
          <button
            onClick={handleMinus}
            className="
          w-8 h-full flex items-center justify-center 
          text-gray-400 hover:text-green-700 hover:bg-green-50 
          rounded transition-colors active:scale-90
        "
          >
            <Minus size={16} strokeWidth={3} />
          </button>

          {/* Count */}
          <span className="text-sm font-black text-green-700 min-w-[1rem] text-center select-none">
            {existingItem.quantity}
          </span>

          {/* Increase */}
          <button
            onClick={handlePlus}
            className="
          w-8 h-full flex items-center justify-center 
          text-green-600 hover:text-green-800 hover:bg-green-50 
          rounded transition-colors active:scale-90
        "
          >
            <Plus size={16} strokeWidth={3} />
          </button>
        </div>
      )}
    </div>
  );
};

const PopularItemsScroller = () => {
  const dispatch = useDispatch();

  const handlePopularItemClick = (item) => {
    dispatch(openItemDetails());
    dispatch(setCurrentlyClickedMenuItem(item));
  };

  const popularItems = useSelector((state) => state.popularItems);
  return (
    <div className="pl-4 overflow-hidden w-full">
      <Swiper
        slidesPerView={4.2}
        spaceBetween={12}
        /* 🔥 SMOOTH SCROLLING FIXES */
        speed={450}
        touchRatio={1.3}
        resistanceRatio={0.6}
        threshold={4}
        followFinger={true}
        grabCursor={true}
        watchSlidesProgress={true}
        updateOnWindowResize={true}
        touchEventsTarget="container"
        className="!overflow-visible py-2"
      >
        {popularItems?.map((item) => (
          <SwiperSlide key={item.itemId}>
            <div
              className="group flex flex-col items-center gap-2 cursor-pointer transition-transform duration-300 active:scale-95 will-change-transform"
              onClick={() => handlePopularItemClick(item)}
            >
              {/* Ring Container */}
              <div className="relative">
                {/* Gradient Border Ring */}
                <div
                  className="
                w-[76px] h-[76px] rounded-full p-[2px]
                bg-gradient-to-tr from-orange-400 via-red-500 to-purple-500
                shadow-sm group-hover:shadow-md transition-shadow
              "
                >
                  {/* White Border inside Ring */}
                  <div className="w-full h-full rounded-full border-[2px] border-white bg-white overflow-hidden">
                    <img
                      src={item.imagePath
                        ?.replace("http://localhost:8081", menucardServiceHost)
                        .replace(".jpg", ".webp")}
                      alt={item.itemName}
                      loading="lazy"
                      draggable={false}
                      className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110 transform-gpu"
                    />
                  </div>
                </div>
              </div>

              {/* Text Label */}
              <p className="text-[10px] font-bold text-gray-700 text-center capitalize leading-tight group-hover:text-orange-600 transition-colors">
                {item.itemName?.split(" ")[0]}
              </p>
            </div>
          </SwiperSlide>
        ))}
      </Swiper>
    </div>
  );
};

export default Menucard;
