import React, { useMemo } from "react";
import { SlidersHorizontal, X, TrendingUp } from "lucide-react";

import { Swiper, SwiperSlide } from "swiper/react";
import "swiper/css";

const ORANGE_SOFT_GRAD = "bg-gradient-to-r from-orange-400 to-orange-500";

const Chip = ({ label, onRemove }) => (
  <span className="inline-flex items-center gap-1.5 px-3 py-1.5 rounded-full bg-white border border-gray-200 text-[12px] font-semibold text-gray-700 shadow-sm">
    <span className="whitespace-nowrap">{label}</span>
    <button
      type="button"
      onClick={onRemove}
      className="h-5 w-5 rounded-full flex items-center justify-center hover:bg-gray-100 active:scale-95 transition"
      aria-label={`Remove ${label}`}
    >
      <X size={14} className="text-gray-500" />
    </button>
  </span>
);

function buildFilterChips(filters) {
  const chips = [];

  const sort = filters?.sort ?? "recommended";
  const hideDefaultSortChip = !!filters?.hideDefaultSortChip;

  const map = {
    recommended: "Recommended",
    time: "Fastest",
    priceLow: "Price: Low",
    priceHigh: "Price: High",
    topRated: "Top rated",
    rating: "Top rated",
  };

  // show sort chip unless user chose to hide default one
  const shouldShowSortChip =
    sort !== "recommended" || (sort === "recommended" && !hideDefaultSortChip);

  if (shouldShowSortChip) {
    chips.push({ key: "sort", label: map[sort] || "Sort" });
  }

  if (filters.offerOnly) chips.push({ key: "offerOnly", label: "Offers" });
  if ((filters.ratingMin ?? 0) > 0)
    chips.push({ key: "ratingMin", label: `${filters.ratingMin}+` });
  if ((filters.timeMax ?? 0) > 0)
    chips.push({ key: "timeMax", label: `Under ${filters.timeMax} min` });

  if (filters.priceRange && filters.priceRange !== "all") {
    const map2 = {
      under200: "Under ₹200",
      "200to400": "₹200–₹400",
      "400plus": "₹400+",
    };
    chips.push({
      key: "priceRange",
      label: map2[filters.priceRange] || "Price",
    });
  }

  return chips;
}

export default function FilterBar({ filters, onChangeFilters, onOpenFilters }) {
  const chips = useMemo(() => buildFilterChips(filters), [filters]);

  const removeChip = (key) => {
    const next = { ...filters };

    if (key === "sort") {
      // If current sort is recommended, just hide chip (no ordering change)
      const currentSort = filters?.sort ?? "recommended";
      if (currentSort === "recommended") {
        next.hideDefaultSortChip = true;
      } else {
        // user had selected a non-default sort => reset to recommended
        next.sort = "recommended";
        next.hideDefaultSortChip = false; // show recommended chip again (optional)
      }
    }

    if (key === "offerOnly") next.offerOnly = false;
    if (key === "ratingMin") next.ratingMin = 0;
    if (key === "timeMax") next.timeMax = 0;
    if (key === "priceRange") next.priceRange = "all";

    onChangeFilters(next);
  };

  return (
    <div className="px-3 mt-3">
      {/* key: keep a stable row height + center alignment */}
      <div className="flex items-center gap-2 min-h-[44px]">
        {/* Filters button */}
        <button
          type="button"
          onClick={onOpenFilters}
          className="relative h-10 px-3 rounded-xl border border-gray-200 bg-white hover:bg-gray-50 hover:border-gray-300 transition-all duration-200 shadow-sm active:scale-95 flex items-center gap-2"
        >
          <SlidersHorizontal
            size={14}
            strokeWidth={2.5}
            className="text-green-600"
          />
          <span className="text-sm font-medium text-gray-700">Filters</span>
          {chips.length > 0 && (
            <span
              className={`${ORANGE_SOFT_GRAD} text-white flex items-center justify-center text-[11px] leading-none text-center h-5 w-5 font-extrabold rounded-full absolute top-0.5 right-1 translate-x-1/2 -translate-y-1/2 shadow-sm ring-1 ring-white`}
            >
              {chips.length}
            </span>
          )}
        </button>

        {/* Applied chips as swiper */}
        {chips.length > 0 && (
          <div className="flex-1 min-w-0 mt-1">
            <Swiper
              slidesPerView="auto"
              spaceBetween={8}
              freeMode
              className="!py-1" // tiny vertical padding so shadows never clip
            >
              {chips.map((c) => (
                <SwiperSlide
                  key={c.key}
                  style={{ width: "auto" }}
                  className="!w-auto"
                >
                  <Chip label={c.label} onRemove={() => removeChip(c.key)} />
                </SwiperSlide>
              ))}
              {/* optional end breathing room */}
              <SwiperSlide style={{ width: 4 }} />
            </Swiper>
          </div>
        )}
      </div>
    </div>
  );
}
