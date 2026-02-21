import { Swiper, SwiperSlide } from "swiper/react";
import { Autoplay, Pagination } from "swiper/modules";
import { LazyLoadImage } from "react-lazy-load-image-component";
import "swiper/css";
import "swiper/css/pagination";
import {
  ArrowRight,
  ChevronRight,
  Clock,
  Flame,
  Star,
  Zap,
} from "lucide-react";

const restaurantServiceHost = import.meta.env.VITE_RESTAURANT_SERVICE_HOST;

const HeroCarousel = ({ landingPageData, onBannerClick }) => {
  const formatReviews = (count) => {
    if (!count) return "0";
    return count >= 1000 ? `${(count / 1000).toFixed(1)}k` : count;
  };

  return (
    // <div className="w-full hero-carousel-wrapper">
    //   <Swiper
    //     modules={[Autoplay, Pagination]}
    //     autoplay={{ delay: 5000, disableOnInteraction: false }}
    //     pagination={{ clickable: true, dynamicBullets: true }}
    //     slidesPerView={1}
    //     spaceBetween={16}
    //     className="w-full !pb-8" // Space for pagination dots
    //   >
    //     {landingPageData.banners.map((banner, index) => {
    //       const imageUrl = banner.imagePath.replace(
    //         "http://localhost:8080",
    //         restaurantServiceHost,
    //       );

    //       return (
    //         <SwiperSlide key={banner.landingPageId} className="!h-auto">
    //           <div
    //             onClick={() => onBannerClick?.(banner)}
    //             className="
    //               group relative flex flex-col w-full
    //               bg-white rounded-3xl overflow-hidden
    //               shadow-[0_8px_30px_rgb(0,0,0,0.06)]
    //               border border-gray-100
    //               active:scale-[0.98] transition-all duration-200
    //             "
    //           >
    //             {/* --- IMAGE SECTION --- */}
    //             <div className="relative h-48 w-full overflow-hidden">
    //               <LazyLoadImage
    //                 alt={banner.title}
    //                 src={imageUrl}
    //                 effect="opacity"
    //                 className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105"
    //                 wrapperClassName="w-full h-full"
    //                 visibleByDefault={index === 0}
    //               />

    //               {/* Gradient for depth */}
    //               <div className="absolute inset-0 bg-gradient-to-t from-black/20 via-transparent to-transparent pointer-events-none" />

    //               {/* Top Left: Discount Badge */}
    //               <div className="absolute top-3 left-3">
    //                 {banner.discountPercentage > 0 && (
    //                   <div className="flex items-center gap-1 px-2.5 py-1 rounded-lg bg-white/95 backdrop-blur shadow-sm border border-white/20">
    //                     <Flame
    //                       size={12}
    //                       className="text-orange-500 fill-orange-500"
    //                     />
    //                     <span className="text-[10px] font-black text-gray-900 leading-none tracking-wide">
    //                       {banner.discountPercentage}% OFF
    //                     </span>
    //                   </div>
    //                 )}
    //               </div>

    //               {/* Bottom Right: High Visibility Prep Time Pill */}
    //               <div className="absolute bottom-3 right-3">
    //                 <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-white text-gray-900 shadow-md border border-gray-100">
    //                   <Clock size={12} className="text-orange-600" />
    //                   <span className="text-[11px] font-black tracking-wide">
    //                     {banner.preparationTime || "20"} min
    //                   </span>
    //                 </div>
    //               </div>
    //             </div>

    //             {/* --- CONTENT SECTION --- */}
    //             <div className="p-4 flex items-center justify-between gap-4">
    //               {/* Left Side: Text Content */}
    //               <div className="flex-1 min-w-0 flex flex-col justify-center">
    //                 {/* 1. Title */}
    //                 <h3 className="text-lg font-black text-gray-900 leading-none truncate mb-1">
    //                   {banner.title}
    //                 </h3>

    //                 {/* 2. Description (Strictly 1 line) */}
    //                 <p className="text-xs text-gray-500 font-medium truncate mb-2">
    //                   {banner.description ||
    //                     "Freshly prepared with special ingredients."}
    //                 </p>

    //                 {/* 3. Rating & Reviews Row */}
    //                 <div className="flex items-center gap-2">
    //                   <div className="flex items-center gap-1 bg-green-50 px-1.5 py-0.5 rounded-md border border-green-100">
    //                     <span className="text-green-700 font-extrabold text-[10px]">
    //                       {banner.rating}
    //                     </span>
    //                     <Star
    //                       size={8}
    //                       className="text-green-600 fill-green-600"
    //                     />
    //                   </div>
    //                   <div className="h-3 w-[1px] bg-gray-200"></div>
    //                   <span className="text-[10px] font-semibold text-gray-400 uppercase tracking-wide">
    //                     {formatReviews(banner.reviewCount)} reviews
    //                   </span>
    //                 </div>
    //               </div>

    //               {/* Right Side: Action Button */}
    //               <button
    //                 className="
    //                 flex-shrink-0
    //                 flex items-center justify-center
    //                 w-10 h-10 rounded-full
    //                 bg-gray-900 text-white
    //                 shadow-lg shadow-gray-200
    //                 group-hover:bg-orange-600 group-hover:scale-110 group-active:scale-95
    //                 transition-all duration-300
    //               "
    //               >
    //                 <ArrowRight size={18} />
    //               </button>
    //             </div>
    //           </div>
    //         </SwiperSlide>
    //       );
    //     })}
    //   </Swiper>

    //   <style>{`
    //     .hero-carousel-wrapper .swiper-pagination-bullet {
    //         width: 5px;
    //         height: 5px;
    //         background: #cbd5e1;
    //         opacity: 1;
    //         transition: all 0.3s ease;
    //     }
    //     .hero-carousel-wrapper .swiper-pagination-bullet-active {
    //         width: 20px;
    //         background: #f97316; /* Orange-500 */
    //         border-radius: 99px;
    //     }
    //   `}</style>
    // </div>

    <div className="w-full hero-carousel-wrapper">
      <Swiper
        modules={[Autoplay, Pagination]}
        slidesPerView={1}
        spaceBetween={16}
        /* 🔥 SMOOTH INTERACTION FIXES */
        speed={600}
        touchRatio={1.2}
        resistanceRatio={0.6}
        threshold={5}
        followFinger={true}
        grabCursor={true}
        watchSlidesProgress={true}
        updateOnWindowResize={true}
        autoplay={{
          delay: 5000,
          disableOnInteraction: false,
          pauseOnMouseEnter: true,
        }}
        pagination={{
          clickable: true,
          dynamicBullets: true,
        }}
        className="w-full !pb-8"
      >
        {landingPageData.banners.map((banner, index) => {
          const imageUrl = banner.imagePath.replace(
            "http://localhost:8080",
            restaurantServiceHost,
          );

          return (
            <SwiperSlide key={banner.landingPageId} className="!h-auto">
              <div
                onClick={() => onBannerClick?.(banner)}
                className="
              group relative flex flex-col w-full 
              bg-white rounded-3xl overflow-hidden 
              shadow-[0_8px_30px_rgb(0,0,0,0.06)] 
              border border-gray-100
              active:scale-[0.98] transition-all duration-200
              will-change-transform
            "
              >
                {/* --- IMAGE SECTION --- */}
                <div className="relative h-48 w-full overflow-hidden">
                  <LazyLoadImage
                    alt={banner.title}
                    src={imageUrl}
                    effect="opacity"
                    className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-105"
                    wrapperClassName="w-full h-full"
                    visibleByDefault={index === 0}
                  />

                  {/* Gradient */}
                  <div className="absolute inset-0 bg-gradient-to-t from-black/20 via-transparent to-transparent pointer-events-none" />

                  {/* Discount Badge */}
                  <div className="absolute top-3 left-3">
                    {banner.discountPercentage > 0 && (
                      <div className="flex items-center gap-1 px-2.5 py-1 rounded-lg bg-white/95 backdrop-blur shadow-sm border border-white/20">
                        <Flame
                          size={12}
                          className="text-orange-500 fill-orange-500"
                        />
                        <span className="text-[10px] font-black text-gray-900 leading-none tracking-wide">
                          {banner.discountPercentage}% OFF
                        </span>
                      </div>
                    )}
                  </div>

                  {/* Prep Time */}
                  <div className="absolute bottom-3 right-3">
                    <div className="flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-white text-gray-900 shadow-md border border-gray-100">
                      <Clock size={12} className="text-orange-600" />
                      <span className="text-[11px] font-black tracking-wide">
                        {banner.preparationTime || "20"} min
                      </span>
                    </div>
                  </div>
                </div>

                {/* --- CONTENT SECTION --- */}
                <div className="p-4 flex items-center justify-between gap-4">
                  <div className="flex-1 min-w-0 flex flex-col justify-center">
                    <h3 className="text-lg font-black text-gray-900 leading-none truncate mb-1">
                      {banner.title}
                    </h3>

                    <p className="text-xs text-gray-500 font-medium truncate mb-2">
                      {banner.description ||
                        "Freshly prepared with special ingredients."}
                    </p>

                    <div className="flex items-center gap-2">
                      <div className="flex items-center gap-1 bg-green-50 px-1.5 py-0.5 rounded-md border border-green-100">
                        <span className="text-green-700 font-extrabold text-[10px]">
                          {banner.rating}
                        </span>
                        <Star
                          size={8}
                          className="text-green-600 fill-green-600"
                        />
                      </div>
                      <div className="h-3 w-[1px] bg-gray-200" />
                      <span className="text-[10px] font-semibold text-gray-400 uppercase tracking-wide">
                        {formatReviews(banner.reviewCount)} reviews
                      </span>
                    </div>
                  </div>

                  <button
                    className="
                  flex-shrink-0
                  flex items-center justify-center
                  w-10 h-10 rounded-full
                  bg-gray-900 text-white
                  shadow-lg shadow-gray-200
                  group-hover:bg-orange-600 group-hover:scale-110 group-active:scale-95
                  transition-all duration-300
                "
                  >
                    <ArrowRight size={18} />
                  </button>
                </div>
              </div>
            </SwiperSlide>
          );
        })}
      </Swiper>

      <style>{`
    .hero-carousel-wrapper .swiper-pagination-bullet {
      width: 5px;
      height: 5px;
      background: #cbd5e1;
      opacity: 1;
      transition: all 0.3s ease;
    }
    .hero-carousel-wrapper .swiper-pagination-bullet-active {
      width: 20px;
      background: #f97316;
      border-radius: 999px;
    }
  `}</style>
    </div>
  );
};

export default HeroCarousel;
