// MenucardLoader.jsx
import React from "react";

export default function MenucardLoader({ children, loading }) {
  if (!loading) return children;

  return (
    <>
      {/* Skeleton content shaped like Menucard */}
      <div className="mt-[5.8rem] mb-[6rem]">
        {/* Search Bar */}
        <div className="px-4 w-full -mt-1 animate-pulse">
          <div className="h-12 w-full bg-gray-200 rounded-2xl" />
        </div>

        {/* Filter bar */}
        <div className="px-4 mt-3 animate-pulse flex gap-2">
          <div className="h-10 w-24 bg-gray-200 rounded-2xl" />
          <div className="h-10 w-24 bg-gray-200 rounded-2xl" />
        </div>

        {/* Popular Items Scroller (circles) */}
        <div className="mx-4 mt-3 pb-1 animate-pulse">
          <div className="flex gap-3 overflow-hidden py-3">
            {Array.from({ length: 6 }).map((_, i) => (
              <div key={i} className="flex flex-col items-center gap-2 min-w-[72px]">
                <div className="h-[72px] w-[72px] rounded-full bg-gray-200" />
                <div className="h-3 w-12 bg-gray-100 rounded-full" />
              </div>
            ))}
          </div>
        </div>

        {/* Divider */}
        <div className="mt-2 mx-4 border-t border-gray-200" />

        {/* Categories + Items */}
        <div className="flex flex-col gap-2 bg-gray-50 mt-2 pb-6">
          {Array.from({ length: 3 }).map((_, cIdx) => (
            <div
              key={cIdx}
              className="bg-white rounded-xl mx-2 shadow-sm overflow-hidden animate-pulse"
            >
              {/* Category header skeleton */}
              <div className="flex justify-between items-center px-4 py-4">
                <div className="flex items-center gap-2">
                  <div className="h-2 w-2 rounded-full bg-gray-200" />
                  <div className="h-5 w-40 bg-gray-200 rounded-lg" />
                </div>
                <div className="h-5 w-5 bg-gray-200 rounded-md" />
              </div>

              {/* 2 item rows skeleton */}
              {Array.from({ length: 2 }).map((_, iIdx) => (
                <div key={iIdx}>
                  <div className="px-4 pb-5 pt-0 flex items-start gap-4">
                    {/* Left - text */}
                    <div className="flex-1 min-w-0">
                      <div className="h-5 w-36 bg-gray-200 rounded-lg mb-1" />
                      <div className="flex gap-1 mb-2">
                        {Array.from({ length: 5 }).map((__, s) => (
                          <div key={s} className="h-4 w-4 mt-1 bg-gray-200 rounded" />
                        ))}
                      </div>
                      <div className="flex items-center gap-2 mb-2">
                        <div className="h-6 w-20 bg-gray-200 rounded-lg" />
                        <div className="h-6 w-16 bg-gray-100 rounded-full" />
                      </div>
                      <div className="space-y-2 mb-3">
                        <div className="h-3 w-full bg-gray-200 rounded-full" />
                        <div className="h-3 w-3/4 bg-gray-100 rounded-full" />
                      </div>
                      <div className="flex gap-2">
                        <div className="h-10 w-10 bg-gray-200 rounded-xl" />
                        <div className="h-10 w-10 bg-gray-200 rounded-xl" />
                      </div>
                    </div>

                    {/* Right - image + add button */}
                    <div className="relative flex flex-col items-center w-[140px]">
                      <div className="w-full aspect-square rounded-2xl bg-gray-200 ring-1 ring-gray-100" />
                    </div>
                  </div>
                  {iIdx !== 1 && <div className="mx-4 mb-4 border-t border-gray-100" />}
                </div>
              ))}
            </div>
          ))}
        </div>
      </div>

      {/* **SPINNER OVERLAY** - This was missing! */}
      {/* <div className="fixed inset-0 z-[9999] grid place-items-center bg-white/35 backdrop-blur-[1px]">
        <div className="h-12 w-12 sm:h-14 sm:w-14">
          <svg
            aria-hidden="true"
            className="w-full h-full animate-spin text-gray-200 fill-gray-600"
            viewBox="0 0 100 101"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
          >
            <path
              d="M100 50.5908C100 78.2051 77.6142 100.591 50 100.591C22.3858 100.591 0 78.2051 0 50.5908C0 22.9766 22.3858 0.59082 50 0.59082C77.6142 0.59082 100 22.9766 100 50.5908ZM9.08144 50.5908C9.08144 73.1895 27.4013 91.5094 50 91.5094C72.5987 91.5094 90.9186 73.1895 90.9186 50.5908C90.9186 27.9921 72.5987 9.67226 50 9.67226C27.4013 9.67226 9.08144 27.9921 9.08144 50.5908Z"
              fill="currentColor"
            />
            <path
              d="M93.9676 39.0409C96.393 38.4038 97.8624 35.9116 97.0079 33.5539C95.2932 28.8227 92.871 24.3692 89.8167 20.348C85.8452 15.1192 80.8826 10.7238 75.2124 7.41289C69.5422 4.10194 63.2754 1.94025 56.7698 1.05124C51.7666 0.367541 46.6976 0.446843 41.7345 1.27873C39.2613 1.69328 37.813 4.19778 38.4501 6.62326C39.0873 9.04874 41.5694 10.4717 44.0505 10.1071C47.8511 9.54855 51.7191 9.52689 55.5402 10.0491C60.8642 10.7766 65.9928 12.5457 70.6331 15.2552C75.2735 17.9648 79.3347 21.5619 82.5849 25.841C84.9175 28.9121 86.7997 32.2913 88.1811 35.8758C89.083 38.2158 91.5421 39.6781 93.9676 39.0409Z"
              fill="currentFill"
            />
          </svg>
          <span className="sr-only">Loading...</span>
        </div>
      </div> */}
    </>
  );
}
