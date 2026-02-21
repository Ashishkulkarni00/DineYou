// OrderLoader.jsx
import React from "react";

export default function OrderLoader({ children, loading }) {
  if (!loading) return children;

  return (
    <>
      {/* Skeleton content shaped like Orders page */}
      <div className="mt-[5.5rem] mb-[12rem] px-4 animate-pulse">
        {/* Header Section */}
        <div className="mb-6">
          <div className="flex items-end justify-between gap-3">
            <div>
              <div className="h-7 w-48 bg-gray-200 rounded-lg mb-2" />
              <div className="h-3 w-32 bg-gray-200 rounded-full" />
            </div>
            <div className="h-6 w-20 bg-gray-200 rounded-full" />
          </div>
        </div>

        {/* Order Cards */}
        {Array.from({ length: 3 }).map((_, idx) => (
          <div
            key={idx}
            className="mb-4 bg-white rounded-2xl border border-gray-100 shadow-sm overflow-hidden"
          >
            {/* Order Header */}
            <div className="p-4 pb-3">
              <div className="flex flex-col gap-3">
                <div className="flex items-center gap-3 flex-wrap">
                  <div className="h-2 w-2 bg-gray-200 rounded-full" />
                  <div className="h-5 w-36 bg-gray-200 rounded-lg" />
                  <div className="h-5 w-16 bg-gray-200 rounded-full" />
                  <div className="h-6 w-24 bg-gray-200 rounded-full" />
                </div>
                <div className="h-3 w-48 bg-gray-200 rounded-full" />
              </div>

              {/* Progress Tracker */}
              <div className="mt-4">
                <div className="grid grid-cols-4 text-xs gap-2 mb-2">
                  <div className="h-3 w-12 bg-gray-200 rounded-full mx-auto" />
                  <div className="h-3 w-12 bg-gray-200 rounded-full mx-auto" />
                  <div className="h-3 w-12 bg-gray-200 rounded-full mx-auto" />
                  <div className="h-3 w-12 bg-gray-200 rounded-full mx-auto" />
                </div>
                
              </div>
            </div>

            {/* Items Section */}
            <div className="px-4 pb-4">
              <div className="rounded-2xl border border-gray-100 bg-gray-50 p-3">
                <div className="space-y-3">
                  {Array.from({ length: 2 }).map((_, i) => (
                    <div key={i} className="flex items-center gap-3">
                      <div className="h-12 w-12 bg-gray-200 rounded-xl" />
                      <div className="flex-1 min-w-0">
                        <div className="h-4 w-32 bg-gray-200 rounded mb-1" />
                        <div className="h-3 w-24 bg-gray-200 rounded-full" />
                      </div>
                      <div className="h-5 w-16 bg-gray-200 rounded-lg" />
                    </div>
                  ))}
                  <div className="h-4 w-20 bg-gray-200 rounded-full" />
                </div>
              </div>
            </div>

            {/* Footer */}
            <div className="px-4 py-3 border-t border-gray-100 bg-white">
              <div className="flex items-baseline justify-between mb-3">
                <div className="h-4 w-16 bg-gray-200 rounded" />
                <div className="h-6 w-20 bg-gray-200 rounded-lg" />
              </div>
              <div className="flex gap-2">
                <div className="h-10 px-4 bg-gray-200 rounded-xl flex-1 flex items-center justify-center" />
                <div className="h-10 px-5 bg-gray-200 rounded-xl" />
              </div>
            </div>
          </div>
        ))}

        <div className="mt-4 h-16 bg-gray-200 rounded-2xl" />
      </div>

    
    </>
  );
}
