import React from "react";
import { useSelector, useDispatch } from "react-redux";
import {
  closeItemCategories,
  setMenucardCategory,
  toggleItemCategories,
} from "../../store/uiSlice";
import { Utensils } from "lucide-react";

const ItemCategories = () => {
  const menuCardData = useSelector((state) => state.menucard); // Adjusted to match your previous slice name
  const currentMenucardCategory = useSelector(
    (state) => state.ui.selectedMenucardCategory // Adjust based on your actual state path
  );
  const dispatch = useDispatch();

  const handleCategoryChange = (category) => {
    dispatch(closeItemCategories());
    dispatch(setMenucardCategory(category.categoryName));
  };

  return (
    <div
      className="
        absolute z-50 right-4 bottom-24 
        w-64 max-h-[24rem] 
        bg-white rounded-3xl 
        shadow-[0_10px_40px_-10px_rgba(0,0,0,0.15)] 
        border border-gray-100 
        overflow-hidden flex flex-col
        animate-in fade-in slide-in-from-bottom-4 duration-200     
      "
      onClick={(e) => e.stopPropagation()}
    >
      {/* --- HEADER --- */}
      <div className="flex-shrink-0 bg-white/95 backdrop-blur-sm border-b border-gray-100 px-5 py-4 flex items-center gap-2">
        <div className="p-1.5 bg-orange-50 rounded-lg">
            <Utensils size={14} className="text-orange-600" />
        </div>
        <h2 className="text-sm font-black text-gray-900 uppercase tracking-wider">
          Menu Categories
        </h2>
      </div>

      {/* --- SCROLLABLE LIST --- */}
      {/* Custom scrollbar styling for webkit browsers */}
      <div className="
        overflow-y-auto flex-1 
        scrollbar-thin scrollbar-thumb-gray-200 scrollbar-track-transparent
      ">
        {menuCardData?.categories?.map((category, index) => {
          const isHighlighted =
            (currentMenucardCategory === null && index === 0) ||
            currentMenucardCategory === category.categoryName;

          return (
            <div
              key={category.categoryName}
              onClick={(e) => {
                e.stopPropagation();
                handleCategoryChange(category);
              }}
              className={`
                group flex justify-between items-center px-5 py-3.5 
                cursor-pointer transition-all duration-200 
                border-b border-dashed border-gray-50 last:border-b-0
                ${isHighlighted ? "bg-orange-50/50" : "hover:bg-gray-50"}
              `}
            >
              {/* Left: Indicator & Name */}
              <div className="flex items-center gap-3">
                {/* Active Indicator Dot */}
                <div className={`
                  h-2 w-2 rounded-full shadow-sm transition-all duration-300
                  ${isHighlighted 
                    ? "bg-gradient-to-r from-orange-500 to-red-600 scale-110" 
                    : "bg-gray-200 scale-100 group-hover:bg-gray-300"}
                `} />
                
                <h3
                  className={`text-sm transition-colors duration-200 ${
                    isHighlighted 
                      ? "font-bold text-gray-900" 
                      : "font-medium text-gray-500 group-hover:text-gray-700"
                  }`}
                >
                  {category.categoryName}
                </h3>
              </div>

              {/* Right: Count Badge */}
              <span
                className={`
                  text-[10px] font-bold px-2 py-0.5 rounded-md border transition-colors
                  ${isHighlighted
                    ? "bg-white text-orange-600 border-orange-100 shadow-sm"
                    : "bg-gray-100 text-gray-400 border-transparent group-hover:bg-gray-200"}
                `}
              >
                {category.menuItemList.length}
              </span>
            </div>
          );
        })}
        
        {/* Bottom padding for scrolling comfort */}
        <div className="h-2 w-full"></div>
      </div>
    </div>
  );
};

export default ItemCategories;