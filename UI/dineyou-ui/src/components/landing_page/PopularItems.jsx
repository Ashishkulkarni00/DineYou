import { Swiper, SwiperSlide } from "swiper/react";
const menucardServiceHost = import.meta.env.VITE_MENUCARD_SERVICE_HOST;
import { openItemDetails } from "../../store/uiSlice";
import { useNavigate } from "react-router-dom";
import { closeItemCategories } from "../../store/uiSlice";
import { useDispatch, useSelector } from "react-redux";
import { setCurrentlyClickedMenuItem } from "../../store/CurrentlyClickedMenuItemSlice";
import { Plus } from "lucide-react";

export default function PopularItems({ popularItemsData }) {
  const currentPageId = useSelector((state) => state.currentPageId);
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const handlePopularItemClick = (menuItem) => {
    dispatch(setCurrentlyClickedMenuItem(menuItem));

    if (currentPageId !== 2) {
      navigate("/menucard");
      dispatch(closeItemCategories());
      dispatch(openItemDetails());
    } else {
      dispatch(openItemDetails());
    }
  };

  return (
    <div className="mt-4 -mx-4 px-4 overflow-hidden">
      <Swiper
        slidesPerView={2.2}
        spaceBetween={12}
        /* 🔥 SMOOTH SCROLLING FIXES */
        speed={500}
        touchRatio={1.25}
        resistanceRatio={0.6}
        threshold={4}
        followFinger={true}
        grabCursor={true}
        watchSlidesProgress={true}
        updateOnWindowResize={true}
        touchEventsTarget="container"
        className="!pb-6"
      >
        {popularItemsData?.map((item, index) => (
          <SwiperSlide key={index} onClick={() => handlePopularItemClick(item)}>
            <div
              className="
            group
            relative
            rounded-2xl
            bg-white
            border border-gray-100
            cursor-pointer
            overflow-hidden
            shadow-sm
            transition-shadow duration-300
            hover:shadow-md
            will-change-transform
          "
            >
              <div className="absolute top-0 left-0 bg-white/90 backdrop-blur-sm px-4 py-1.5 rounded-br-xl border-r border-b border-gray-100 shadow-sm z-10">
                <p className="text-[10px] font-bold text-orange-600 leading-none">
                  ₹{Math.ceil((item.itemPrice * item.discountPercentage) / 100)}{" "}
                  OFF
                </p>
              </div>

              {/* Image Container */}
              <div className="relative h-32 bg-gray-50">
                <img
                  src={item.imagePath.replace(
                    "http://localhost:8081",
                    menucardServiceHost,
                  )}
                  alt={item.itemName}
                  loading="lazy"
                  draggable={false}
                  className="
                absolute inset-0
                w-full h-full
                object-cover
                transform-gpu
                transition-transform duration-500
                group-hover:scale-105
                pointer-events-none
              "
                />
              </div>

              {/* Content */}
              <div className="p-3 space-y-1">
                <h3 className="font-bold text-xs text-gray-900 line-clamp-1">
                  {item.itemName}
                </h3>

                <p className="text-sm font-bold text-gray-900">
                  ₹{item.itemPrice}
                </p>

                <div className="flex items-center justify-between mt-1.5 pt-1 border-t border-gray-50">
                  <div className="flex items-center gap-1 text-xs text-gray-500">
                    <span className="font-medium">
                      {item.minimumPreparationTime}m
                    </span>
                    <span className="text-orange-400">•</span>
                    <span>{item.soldCount}+ sold</span>
                  </div>
                </div>
              </div>
            </div>
          </SwiperSlide>
        ))}
      </Swiper>
    </div>
  );

  // return (
  //   <div className="-mx-4 px-4 overflow-visible">
  //     {/* overflow-visible allows shadows to not be clipped */}
  //     <Swiper slidesPerView={2.1} spaceBetween={14} className="!pb-8 !pt-2">
  //       {popularItemsData?.map((item, index) => (
  //         <SwiperSlide key={index}>
  //           <div
  //             onClick={() => handlePopularItemClick(item)}
  //             className="
  //               group relative flex flex-col
  //               h-full w-full
  //               bg-white rounded-2xl
  //               shadow-[0_2px_12px_rgb(0,0,0,0.04)]
  //               border border-gray-100
  //               overflow-hidden
  //               active:scale-[0.97] transition-all duration-200
  //             "
  //           >
  //             {/* Image */}
  //             <div className="relative aspect-[4/3] w-full bg-gray-100 overflow-hidden">
  //               <img
  //                 src={item.imagePath.replace(
  //                   "http://localhost:8081",
  //                   menucardServiceHost,
  //                 )}
  //                 alt={item.itemName}
  //                 className="w-full h-full object-cover transition-transform duration-500 group-hover:scale-110"
  //                 loading="lazy"
  //               />

  //               {/* Modern Glass Badge */}
  //               <div className="absolute top-0 left-0 bg-white/90 backdrop-blur-sm px-2 py-1 rounded-br-xl border-r border-b border-gray-100 shadow-sm z-10">
  //                 <p className="text-[10px] font-bold text-orange-600 leading-none">
  //                   ₹
  //                   {Math.ceil(
  //                     (item.itemPrice * item.discountPercentage) / 100,
  //                   )}{" "}
  //                   OFF
  //                 </p>
  //               </div>
  //             </div>

  //             {/* Info */}
  //             <div className="p-3 flex flex-col gap-1">
  //               <h3 className="font-bold text-xs text-gray-800 line-clamp-1 group-hover:text-orange-600 transition-colors">
  //                 {item.itemName}
  //               </h3>

  //               <div className="flex items-center justify-between mt-1">
  //                 <div className="flex flex-col">
  //                   <span className="text-[10px] text-gray-400 line-through decoration-gray-300">
  //                     ₹
  //                     {Math.ceil(
  //                       item.itemPrice * (1 + item.discountPercentage / 100),
  //                     )}
  //                   </span>
  //                   <span className="text-sm font-black text-gray-900">
  //                     ₹{item.itemPrice}
  //                   </span>
  //                 </div>

  //                 {/* Mini Add Button visual cue */}
  //                 <div className="w-6 h-6 rounded-full bg-gray-50 border border-gray-200 flex items-center justify-center group-hover:bg-orange-500 group-hover:border-orange-500 transition-colors">
  //                   <Plus
  //                     size={12}
  //                     className="text-gray-400 group-hover:text-white"
  //                   />
  //                 </div>
  //               </div>
  //             </div>
  //           </div>
  //         </SwiperSlide>
  //       ))}
  //     </Swiper>
  //   </div>
  // );
}
