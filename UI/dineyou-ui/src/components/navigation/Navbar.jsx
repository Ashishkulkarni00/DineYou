// import React, { useEffect, useState } from "react";
// import { useSelector } from "react-redux";
// import { useNavigate } from "react-router-dom";
// import { getKeycloak } from "../../service/keycloak";
// import { MapPin, User } from "lucide-react";

// const Navbar = () => {
//   const restaurantDetails = useSelector((state) => state.restaurant);
//   const [isLoggedIn, setIsLoggedIn] = useState(false);
//   const navigate = useNavigate();
//   const [displayName, setDisplayName] = useState("User");
//   useEffect(() => {
//     const kc = getKeycloak();
//     setIsLoggedIn(kc?.authenticated ?? false);
//     const displayName =
//       kc?.tokenParsed?.given_name ||
//       kc?.tokenParsed?.name ||
//       kc?.tokenParsed?.preferred_username ||
//       "User";
//     setDisplayName(displayName);
//   }, []);

//   const initial = displayName.trim().charAt(0).toUpperCase();

//   return (
//     <nav
//       className="
//         fixed top-0 left-0 right-0 z-20
//         bg-inherit backdrop-blur-[1200px]
//         border-b border-gray-50
//         rounded-b-2xl
//       "
//     >

//       <div className="px-4 pt-4 pb-4 flex items-center justify-between">
//         {/* Left: Location */}
//         <div className="min-w-0">
//           <div className="flex items-center gap-3">
// <div className="h-10 w-10 rounded-xl bg-orange-50 border border-orange-100 flex items-center justify-center">
//   <MapPin size={20} className="text-[#FF6B35]" strokeWidth={2.5} />
// </div>

//             <div className="min-w-0">
//               <h2 className="text-[16px] text-gray-900 font-extrabold leading-tight truncate">
//                 {restaurantDetails?.restaurantName || "Restaurant"}
//               </h2>

//               <p className="text-xs text-gray-400 font-medium leading-relaxed truncate">
//                 {restaurantDetails?.address || "Address"}
//               </p>
//             </div>
//           </div>
//         </div>

//         {/* Right: Profile/Login */}
//         <div className="flex items-center gap-2">
//           {isLoggedIn ? (
//             <div
//               onClick={() => navigate("/profile")}
//               className="flex items-center gap-2 cursor-pointer select-none group"
//               aria-label="Open profile"
//               role="button"
//               tabIndex={0}
//               onKeyDown={(e) => e.key === "Enter" && navigate("/profile")}
//             >
//               {/* Avatar (subtle) */}
          //     <div className="relative">
          //       <div
          //         className="
          //   h-9 w-9 rounded-xl
          //   bg-white border border-gray-200
          //   flex items-center justify-center
          //   shadow-sm group-hover:shadow-md
          //   transition-all duration-200
          // "
          //       >
          //         <span className="text-gray-800 text-sm font-bold">
          //           {initial}
          //         </span>
          //       </div>

          //       {/* Orange accent dot (theme) + small green status */}
          //       <span className="absolute -bottom-1 -right-1 h-3.5 w-3.5 rounded-full bg-white border border-gray-200 flex items-center justify-center shadow-sm">
          //         <span className="h-2 w-2 rounded-full bg-gradient-to-r from-orange-500 to-red-500" />
          //       </span>
          //       {/* <span className="absolute -top-1 -right-1 h-2.5 w-2.5 rounded-full bg-green-500 border-2 border-white" /> */}
          //     </div>

//               {/* Text (optional, still subtle) */}
//               <div className="hidden sm:flex flex-col leading-none">
//                 <span className="text-[11px] font-semibold text-gray-800">
//                   {displayName.split(" ")[0]}
//                 </span>
//                 <span className="text-[10px] font-medium text-gray-500">
//                   Profile
//                 </span>
//               </div>
//             </div>
//           ) : (
//             <button
//               onClick={() => getKeycloak().login()}
//               className="
//         px-3.5 py-2 rounded-xl
//         text-sm font-semibold text-gray-700
//         bg-white border border-gray-200
//         shadow-sm hover:shadow-md
//         hover:border-gray-300 hover:bg-gray-50
//         transition-all duration-200 active:scale-95
//       "
//             >
//               Login
//             </button>
//           )}
//         </div>
//       </div>
//     </nav>
//   );
// };

// export default Navbar;

import React, { useEffect, useState } from "react";
import { useSelector } from "react-redux";
import { useNavigate } from "react-router-dom";
import { getKeycloak } from "../../service/keycloak";
import { MapPin, User, ChevronDown, LogIn } from "lucide-react";

const Navbar = () => {
  const restaurantDetails = useSelector((state) => state.restaurant);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const navigate = useNavigate();
  const [displayName, setDisplayName] = useState("User");

  useEffect(() => {
    const kc = getKeycloak();
    setIsLoggedIn(kc?.authenticated ?? false);
    const name =
      kc?.tokenParsed?.given_name ||
      kc?.tokenParsed?.name ||
      kc?.tokenParsed?.preferred_username ||
      "User";
    setDisplayName(name);
  }, []);

  const initial = displayName.trim().charAt(0).toUpperCase();

  return (
    <nav
      className="
        fixed top-0 left-0 right-0 z-40
        bg-white/80 backdrop-blur-xl
        border-b border-gray-100/50
        shadow-[0_4px_30px_rgba(0,0,0,0.02)]
        transition-all duration-300
        max-w-md mx-auto
      "
    >
      <div className="px-4 py-3 flex items-center justify-between">
        {/* Left: Location / Restaurant Info */}
        <div className="flex-1 min-w-0 pr-4">
          <div className="flex items-center gap-3">
            {/* Icon Box */}
            <div className="h-10 w-10 rounded-xl bg-orange-50 border border-orange-100 flex items-center justify-center">
              <MapPin size={20} className="text-[#FF6B35]" strokeWidth={2.5} />
            </div>

            {/* Text Details */}
            <div className="min-w-0 flex flex-col justify-center">
              <h2 className="text-[15px] text-gray-900 font-black leading-none truncate mb-1">
                {restaurantDetails?.restaurantName || "Restaurant"}
              </h2>
              <div className="flex items-center gap-1 text-gray-400">
                <p className="text-[11px] font-bold truncate max-w-[150px] leading-none">
                  {restaurantDetails?.address || "Select Location"}
                </p>
                {/* Visual cue that location might be clickable (optional) */}
                <ChevronDown size={10} strokeWidth={3} />
              </div>
            </div>
          </div>
        </div>

        {/* Right: Profile / Login Action */}
        <div className="flex-shrink-0">
          {isLoggedIn ? (
            <div
              onClick={() => navigate("/profile")}
              className="group relative cursor-pointer"
              role="button"
              aria-label="Profile"
            >
              {/* Avatar Container: Changed rounded-full to rounded-2xl */}
               <div className="relative">
                <div
                  className="
            h-9 w-9 rounded-xl
            bg-white border border-gray-200
            flex items-center justify-center
            shadow-sm group-hover:shadow-md
            transition-all duration-200
          "
                >
                  <span className="text-gray-800 text-sm font-bold">
                    {initial}
                  </span>
                </div>

                {/* Orange accent dot (theme) + small green status */}
                <span className="absolute -bottom-0.5 -right-0.5 h-3 w-3 rounded-full bg-white border border-gray-200 flex items-center justify-center shadow-sm">
                  <span className="h-2 w-2 rounded-full bg-gradient-to-r from-green-500 to-green-500" />
                </span>
                {/* <span className="absolute -top-1 -right-1 h-2.5 w-2.5 rounded-full bg-green-500 border-2 border-white" /> */}
              </div>
            </div>
          ) : (
            <button
              onClick={() => getKeycloak().login()}
              className="
                h-10 px-5 rounded-2xl
                bg-gray-900 text-white
                text-xs font-bold tracking-wide
                shadow-md shadow-gray-200
                hover:bg-black hover:shadow-lg hover:scale-[1.02]
                active:scale-[0.98]
                transition-all duration-200
                flex items-center gap-2
              "
            >
              <span>Login</span>
            </button>
          )}
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
