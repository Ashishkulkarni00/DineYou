import React, { useEffect, useState } from "react";
import HeroCarousel from "./HeroCarousel";
import PopularItems from "./PopularItems";
import Input from "../input/Input";
import { useDispatch } from "react-redux";
import { setRestaurant } from "../../store/RestaurantSlice";
import { setPopularItems } from "../../store/PopularItemsSlice";
import { useNavigate } from "react-router-dom";
import { setCurrentPageId } from "../../store/uiSlice";
import { fetchLandingPage } from "../../service/RestaurantService";
import { getKeycloak } from "../../service/keycloak";
import { fetchActiveOrders } from "../../service/OrderService";
import { setOrder } from "../../store/OrderSlice";
import LandingPageLoader from "../loaders/LandingPageLoader";
import { ChevronRight, MapPin } from "lucide-react";
import BrandHighlights from "../branding/BrandingHighlights";
import Footer from "./Footer";

const Hero = () => {
  const dispatch = useDispatch();
  const [landingPageData, setLandingPageData] = useState();
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();
  const keycloak = getKeycloak();

  useEffect(() => {
    dispatch(setCurrentPageId(0));
  }, [dispatch]);

  const loadOrders = async () => {
    try {
      const isAuthenticated = keycloak?.authenticated;
      const userId = isAuthenticated ? keycloak.tokenParsed.sub : null;
      if (isAuthenticated) {
        const orders = await fetchActiveOrders(userId);
        dispatch(setOrder(orders.data));
      }
    } catch (e) {
      console.error("Error fetching orders:", e);
    }
  };
  useEffect(() => {
    loadOrders();
  }, []);

  useEffect(() => {
    let cancelled = false;

    const loadLandingPage = async () => {
      setLoading(true);
      const restaurantId = 4;

      try {
        const result = await fetchLandingPage(restaurantId);

        if (cancelled) return;

        if (result.success && result.data?.data) {
          const data = result.data.data;

          dispatch(
            setRestaurant({
              restaurantId: data.restaurantId,
              restaurantName: data.restaurantName,
              location: data.location,
              address: data.address,
              averageRating: data.averageRating,
              totalReviews: data.totalReviews,
              logoImagePath: data.logoImagePath,
              lastUpdated: data.lastUpdated,
            }),
          );

          dispatch(setPopularItems(data.popularItems));
          setLandingPageData(data);
        } else {
          console.error("loadLandingPage: condition failed", {
            success: result.success,
            data: result.data,
          });
        }
      } catch (e) {
        if (!cancelled) console.error("loadLandingPage crashed", e);
      } finally {
        if (!cancelled) setLoading(false);
      }
    };

    loadLandingPage();

    return () => {
      cancelled = true;
    };
  }, [dispatch]);

  return (
    <LandingPageLoader loading={loading}>
      <div className="px-4 mt-22 w-full mb-16 animate-in fade-in duration-200">
        {/* --- GREETING SECTION --- */}
        <header className="mb-3">
          <div className="flex items-center gap-2 mb-1">
            <span className="h-[2px] w-4 bg-orange-500 rounded-full" />
            <p className="text-[10px] font-black tracking-[0.2em] uppercase text-orange-600/80">
              Chhatrapati Sambhajinagar
            </p>
          </div>

          <h2 className="text-[1.7rem] leading-[1.1] font-black text-gray-900 tracking-tight">
            Good Evening,{" "}
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-orange-500 to-red-600">
              Foodie!
            </span>
          </h2>

          <p className="mt-1.5 text-xs text-gray-400 font-medium leading-relaxed">
            Start your next story with{" "}
            <span className="text-gray-900 font-bold">Two Stories!</span>
          </p>
        </header>

        {/* --- SEARCH SECTION (Enhanced) --- */}
        <section className="relative group mt-2">
          <div className="absolute -inset-1 bg-gradient-to-r from-orange-100 to-orange-50 rounded-3xl blur opacity-25 group-focus-within:opacity-100 transition duration-500"></div>
          <div className="relative">
            <Input /> {/* Your refined input component */}
          </div>
        </section>

        {/* --- HERO CAROUSEL --- */}
        <section className="mt-3">
          {landingPageData && (
            <HeroCarousel landingPageData={landingPageData} />
          )}
        </section>

        {/* --- POPULAR ITEMS SECTION --- */}
        <section className="mt-3">
          <div className="flex justify-between items-start mb-5">
            <div>
              <h2 className="text-gray-900 text-lg font-black tracking-tight">
                Fan Favorites
              </h2>
              <p className="text-[10px] text-gray-400 font-bold uppercase tracking-widest">
                Most ordered this week
              </p>
            </div>

            <button
              onClick={() => navigate("/menucard")}
              className="mt-2 flex items-center gap-1 px-3 py-1.5 bg-orange-50 text-[#FF6B35] rounded-full text-xs font-bold active:scale-95 transition-all"
            >
              View all
              <ChevronRight size={14} />
            </button>
          </div>

          {landingPageData && (
            <PopularItems popularItemsData={landingPageData.popularItems} />
          )}
        </section>

        <section className="">
          <BrandHighlights />
        </section>

        <Footer />
      </div>
    </LandingPageLoader>
  );

  // return (
  //   <LandingPageLoader loading={loading}>
  //     <div className="w-full min-h-screen bg-gray-50/50 pb-24">
  //       {/* --- HEADER SECTION --- */}
  //       <div className="px-5 pt-6 pb-2 sticky top-0 z-40 border-b border-gray-100/50 backdrop-blur-xl bg-white/80">
  //         {/* Location Pill */}
  //         <div className="flex items-center gap-1.5 mb-3 w-max px-2 py-1 rounded-full bg-orange-50 border border-orange-100">
  //           <MapPin size={12} className="text-orange-500 fill-orange-500" />
  //           <span className="text-[10px] font-bold tracking-wide uppercase text-orange-700">
  //             Chhatrapati Sambhajinagar
  //           </span>
  //         </div>

  //         <div className="flex justify-between items-end">
  //           <div>
  //             <h2 className="text-2xl font-black text-gray-900 leading-none tracking-tight">
  //               Good Evening, <br />
  //               <span className="text-transparent bg-clip-text bg-gradient-to-r from-orange-500 to-red-600">
  //                 Foodie!
  //               </span>
  //             </h2>
  //             <p className="mt-1 text-xs text-gray-400 font-medium">
  //               What are we craving today?
  //             </p>
  //           </div>
  //           {/* Profile/Menu Icon placeholder if needed */}
  //         </div>
  //       </div>

  //       <div className="px-4 mt-4 max-w-md mx-auto animate-in fade-in duration-300">
  //         {/* --- SEARCH --- */}
  //         {/* Removed the heavy blur glow for a cleaner look */}
  //         <div className="relative mb-6">
  //           <Input />
  //         </div>

  //         {/* --- HERO CAROUSEL --- */}
  //         <section className="mb-8">
  //           {landingPageData && (
  //             <HeroCarousel landingPageData={landingPageData} />
  //           )}
  //         </section>

  //         {/* --- POPULAR ITEMS --- */}
  //         <section>
  //           <div className="flex justify-between items-center mb-4 px-1">
  //             <div>
  //               <h2 className="text-lg font-black text-gray-900 leading-none">
  //                 Fan Favorites
  //               </h2>
  //               <span className="text-[10px] text-gray-400 font-bold uppercase tracking-wider">
  //                 Top picks this week
  //               </span>
  //             </div>

  //             <button
  //               onClick={() => navigate("/menucard")}
  //               className="group flex items-center gap-1 pl-3 pr-2 py-1.5 bg-white border border-gray-200 text-gray-600 rounded-full text-xs font-bold active:scale-95 transition-all shadow-sm"
  //             >
  //               See all
  //               <div className="bg-orange-100 rounded-full p-0.5 group-hover:bg-orange-500 transition-colors">
  //                 <ChevronRight
  //                   size={12}
  //                   className="text-orange-600 group-hover:text-white transition-colors"
  //                 />
  //               </div>
  //             </button>
  //           </div>

  //           {landingPageData && (
  //             <PopularItems popularItemsData={landingPageData.popularItems} />
  //           )}
  //         </section>

  //         {/* --- BRAND HIGHLIGHTS & FOOTER --- */}
  //         <section className="mt-8">
  //           <BrandHighlights />
  //         </section>

  //         <footer className="mt-12 text-center opacity-60">
  //           <div className="flex justify-center items-center gap-2 mb-1">
  //             <span className="font-black text-sm tracking-widest text-gray-300">
  //               TWO STORIES
  //             </span>
  //           </div>
  //           <p className="text-[10px] text-gray-400">
  //             Made with ❤️ for foodies
  //           </p>
  //         </footer>
  //       </div>
  //     </div>
  //   </LandingPageLoader>
  // );
};
export default Hero;
