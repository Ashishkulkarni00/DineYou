// import React, { useRef, useEffect } from "react";
// import { Search, Mic, Minus } from "lucide-react";
// import { useState } from "react";
// import { useDispatch } from "react-redux";
// import { setSearchedMenuItem } from "../../store/uiSlice";
// import { useNavigate } from "react-router-dom";

// const menucardServiceHost = import.meta.env.VITE_MENUCARD_SERVICE_HOST;

// const Input = () => {
//   const [isFocused, setIsFocused] = useState(false);
//   const [searchValue, setSearchValue] = useState("");
//   const [results, setResults] = useState([]);
//   const [showResults, setShowResults] = useState(false);

//   const containerRef = useRef(null);

//   const dispatch = useDispatch();

//   const navigate = useNavigate();

//   useEffect(() => {
//     // Close dropdown on outside click
//     const handleClickOutside = (e) => {
//       if (containerRef.current && !containerRef.current.contains(e.target)) {
//         setShowResults(false);
//       }
//     };
//     document.addEventListener("mousedown", handleClickOutside);
//     return () => document.removeEventListener("mousedown", handleClickOutside);
//   }, []);

//   // Debouncing
//   useEffect(() => {
//     const handler = setTimeout(() => {
//       if (searchValue.trim() !== "") {
//         searchApiCall(searchValue);
//       } else {
//         setResults([]);
//         setShowResults(false);
//       }
//     }, 300);

//     return () => clearTimeout(handler);
//   }, [searchValue]);

//   const searchApiCall = async (query) => {
//     try {
//       const res = await fetch(
//         `${menucardServiceHost}/api/v1/search/item?itemName=${query}`,
//       );
//       const data = await res.json();

//       if (data?.success && data.data.length > 0) {
//         setResults(data.data);
//         setShowResults(true);
//       } else {
//         setResults([]);
//         setShowResults(true);
//       }
//     } catch (err) {
//       console.error("API error:", err);
//     }
//   };

//   const handleSearchedItemClick = (item) => {
//     // Dispatch the selected item
//     dispatch(
//       setSearchedMenuItem({
//         itemId: item.itemId,
//         categoryName: item.categoryName,
//       }),
//     );

//     setSearchValue("");
//     setResults([]);
//     setShowResults(false);

//     navigate("/menucard");
//   };

//   return (
//     <div ref={containerRef} className="relative mt-2">
//       {/* INPUT */}
//       <Search
//         strokeWidth={2.5}
//         size={18}
//         className={`absolute left-4 top-1/2 -translate-y-1/2 pointer-events-none transition-colors duration-200 ${
//           isFocused ? "text-gray-700" : "text-gray-500"
//         }`}
//       />

//       {/* Modify your Search Input Classes to this: */}
//       <input
//         type="text"
//         className="w-full h-12 pl-12 pr-14 bg-gray-50/50 rounded-2xl
//              text-gray-800 text-sm placeholder:text-gray-400
//              border-none ring-1 ring-gray-200
//              focus:bg-white focus:ring-2 focus:ring-orange-200
//              shadow-sm focus:shadow-orange-100/50
//              transition-all duration-300"
//         placeholder="Search spicy noodles, coffee..."
//         onFocus={() => setIsFocused(true)}
//         onBlur={() => setIsFocused(false)}
//         value={searchValue}
//         onChange={(e) => setSearchValue(e.target.value)}
//       />

//       <div className="absolute right-2 top-1/2 -translate-y-1/2 flex items-center">
//         <div className="h-6 w-px bg-gray-200 mr-1.5" />
//         <button
//           type="button"
//           className="h-9 w-9 rounded-xl grid place-items-center
//                  text-gray-500 hover:text-gray-700 hover:bg-gray-50
//                  active:scale-95 transition"
//           aria-label="Voice search"
//         >
//           <Mic strokeWidth={2} size={18} />
//         </button>
//       </div>

//       {/* SEARCH RESULTS DROPDOWN */}
//       {showResults && (
//         <div className="absolute left-0 right-0 mt-2 bg-white rounded-2xl shadow-lg border border-gray-100 z-50 max-h-80 overflow-y-auto">
//           {results.length === 0 ? (
//             <p className="text-gray-500 text-sm py-4 text-center">
//               No items found
//             </p>
//           ) : (
//             results.map((item, index) => (
//               <div
//                 onClick={() => {
//                   handleSearchedItemClick(item);
//                 }}
//                 key={index}
//                 className="flex items-center gap-3 p-3 hover:bg-gray-50 active:bg-gray-100 cursor-pointer transition"
//               >
//                 <img
//                   src={item.imageUrl
//                     .replace("http://localhost:8081", menucardServiceHost)
//                     .replace(".jpg", ".webp")}
//                   alt={item.itemName}
//                   className="w-12 h-12 rounded-lg object-cover shadow-sm"
//                 />

//                 <div className="flex flex-col">
//                   <span className="font-medium text-gray-800 text-sm">
//                     {item.itemName}
//                   </span>

//                   <span className="text-xs text-gray-500 line-clamp-1">
//                     {item.description}
//                   </span>

//                   <span className="text-sm font-semibold text-orange-600 mt-1">
//                     ₹ {item.itemPrice}
//                   </span>
//                 </div>
//               </div>
//             ))
//           )}
//         </div>
//       )}
//     </div>
//   );
// };

// export default Input;

import React, { useState, useEffect, useRef } from "react";
import { Search, Mic } from "lucide-react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import { setSearchedMenuItem } from "../../store/uiSlice";

const menucardServiceHost = import.meta.env.VITE_MENUCARD_SERVICE_HOST;

const AnimatedPlaceholder = () => {
  const [index, setIndex] = useState(0);
  const items = [
    "'Spicy Noodles'",
    "'Cold Coffee'",
    "'Sizzling Brownie'",
    "'Schezwan Combo'",
  ];

  useEffect(() => {
    const timer = setInterval(() => {
      setIndex((prev) => (prev + 1) % items.length);
    }, 3000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="absolute left-12 top-0 bottom-0 flex items-center pointer-events-none overflow-hidden h-full">
      {/* Static Part */}
      <span className="text-sm text-gray-900 font-medium mr-1">Search</span>

      {/* Animated Part */}
      <div className="relative h-full flex items-center">
        <AnimatePresence mode="wait">
          <motion.span
            key={items[index]}
            initial={{ y: 15, opacity: 0 }}
            animate={{ y: 0, opacity: 1 }}
            exit={{ y: -15, opacity: 0 }}
            transition={{ duration: 0.4, ease: "easeInOut" }}
            className="text-sm text-gray-900 font-medium whitespace-nowrap"
          >
            {items[index]}
          </motion.span>
        </AnimatePresence>
      </div>
    </div>
  );
};

const Input = () => {
  const [isFocused, setIsFocused] = useState(false);
  const [searchValue, setSearchValue] = useState("");
  const [results, setResults] = useState([]);
  const [showResults, setShowResults] = useState(false);

  const containerRef = useRef(null);
  const dispatch = useDispatch();
  const navigate = useNavigate();

  useEffect(() => {
    const handleClickOutside = (e) => {
      if (containerRef.current && !containerRef.current.contains(e.target)) {
        setShowResults(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  useEffect(() => {
    const handler = setTimeout(() => {
      if (searchValue.trim() !== "") {
        searchApiCall(searchValue);
      } else {
        setResults([]);
        setShowResults(false);
      }
    }, 300);
    return () => clearTimeout(handler);
  }, [searchValue]);

  const searchApiCall = async (query) => {
    try {
      const res = await fetch(
        `${menucardServiceHost}/api/v1/search/item?itemName=${query}`,
      );
      const data = await res.json();
      if (data?.success && data.data.length > 0) {
        setResults(data.data);
      } else {
        setResults([]);
      }
      setShowResults(true);
    } catch (err) {
      console.error("API error:", err);
    }
  };

  const handleSearchedItemClick = (item) => {
    // 1. Dispatch the selection to Redux so MenuCard can react to it
    dispatch(
      setSearchedMenuItem({
        itemId: item.itemId,
        categoryName: item.categoryName,
      }),
    );

    // 2. Clear the search state
    setSearchValue("");
    setResults([]);
    setShowResults(false);

    // 3. Navigate to the menu card
    navigate("/menucard");
  };

  return (
    <div ref={containerRef} className="relative">
      <Search
        strokeWidth={2.5}
        size={18}
        className={`absolute left-4 top-1/2 -translate-y-1/2 z-10 pointer-events-none transition-colors duration-200 ${
          isFocused ? "text-orange-500" : "text-gray-400"
        }`}
      />

      {/* Static "Search" + Animated Items */}
      {!searchValue && !isFocused && <AnimatedPlaceholder />}

      <input
        type="text"
        className="
          w-full h-12 pl-12 pr-14 rounded-2xl
          bg-gray-50 border border-gray-100
          text-sm font-medium text-gray-800 placeholder:text-gray-400

          focus:bg-white
          focus:border-orange-200
          focus:ring-4 focus:ring-orange-50

          outline-none
          transition-all duration-200
        "
        onFocus={() => setIsFocused(true)}
        onBlur={() => setIsFocused(false)}
        value={searchValue}
        onChange={(e) => setSearchValue(e.target.value)}
      />

      <div className="absolute right-2 top-1/2 -translate-y-1/2 flex items-center z-10">
        <div className="h-6 w-px bg-gray-200 mr-1.5" />
        <button
          type="button"
          className="h-9 w-9 rounded-xl grid place-items-center
                     text-gray-500 hover:text-gray-700 hover:bg-gray-50
                     active:scale-95 transition"
        >
          <Mic strokeWidth={2} size={18} />
        </button>
      </div>

      <AnimatePresence>
        {showResults && (
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: 10 }}
            className="absolute left-0 right-0 mt-2 bg-white rounded-2xl shadow-xl border border-gray-100 z-50 max-h-80 overflow-y-auto"
          >
            {results.length === 0 ? (
              <div className="flex flex-col items-center py-6">
                <p className="text-gray-400 text-sm">No items found</p>
              </div>
            ) : (
              results.map((item, index) => (
                <div
                  key={item.itemId || index}
                  onClick={() => handleSearchedItemClick(item)}
                  className="flex items-center gap-3 p-3 hover:bg-orange-50/50 active:bg-orange-50 cursor-pointer transition"
                >
                  <img
                    src={item.imageUrl
                      .replace("http://localhost:8081", menucardServiceHost)
                      .replace(".jpg", ".webp")}
                    alt={item.itemName}
                    className="w-12 h-12 rounded-xl object-cover shadow-sm"
                  />
                  <div className="flex flex-col flex-1">
                    <span className="font-bold text-gray-800 text-sm">
                      {item.itemName}
                    </span>
                    <span className="text-[11px] text-gray-500 line-clamp-1">
                      {item.description}
                    </span>
                    <span className="text-xs font-black text-orange-600 mt-0.5">
                      ₹{item.itemPrice}
                    </span>
                  </div>
                </div>
              ))
            )}
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default Input;
