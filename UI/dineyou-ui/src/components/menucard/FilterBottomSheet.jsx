
import React, { useEffect, useMemo, useState } from "react";
import { X, SlidersHorizontal, Check, Flame, Clock, IndianRupee, Star } from "lucide-react";
import { clearMenucardFilters, setMenucardFilters } from "../../store/uiSlice";
import { useDispatch } from "react-redux";

// --- STYLES ---

// A premium neutral active state (Soft Black)
const NEUTRAL_ACTIVE_STYLE = "bg-gray-900 text-white border-transparent shadow-md";
const NEUTRAL_INACTIVE_STYLE = "bg-white border-gray-200 text-gray-600 hover:bg-gray-50 hover:border-gray-300";

const Chip = ({ active, onClick, children, icon: Icon }) => (
  <button
    type="button"
    onClick={onClick}
    className={[
      "px-4 py-2.5 rounded-2xl text-[13px] font-bold border transition-all duration-200 active:scale-95 flex items-center gap-2",
      active ? NEUTRAL_ACTIVE_STYLE : NEUTRAL_INACTIVE_STYLE,
    ].join(" ")}
  >
    {Icon && (
      <Icon 
        size={14} 
        strokeWidth={2.5} 
        className={active ? "text-gray-300" : "text-gray-400"} 
      />
    )}
    {children}
  </button>
);

const SectionTitle = ({ children }) => (
  <h3 className="text-xs font-black text-gray-400 uppercase tracking-widest flex items-center gap-2 mb-3 pl-1">
    {children}
  </h3>
);

const Divider = () => <div className="border-t border-dashed border-gray-100 my-1" />;

const ToggleRow = ({ label, checked, onChange }) => (
  <div
    onClick={() => onChange(!checked)}
    className={`
      w-full flex items-center justify-between
      px-4 py-3.5 rounded-2xl border cursor-pointer transition-all duration-200
      ${checked ? "bg-gray-50 border-gray-300" : "bg-white border-gray-200 hover:bg-gray-50"}
    `}
  >
    <span className={`text-sm font-bold ${checked ? "text-gray-900" : "text-gray-600"}`}>
        {label}
    </span>

    {/* Neutral Switch */}
    <div
      className={[
        "h-6 w-11 rounded-full p-1 transition-colors duration-300 ease-in-out",
        checked ? "bg-gray-900" : "bg-gray-200",
      ].join(" ")}
    >
      <div
        className={[
          "h-4 w-4 rounded-full bg-white shadow-sm transition-transform duration-300",
          checked ? "translate-x-5" : "translate-x-0",
        ].join(" ")}
      />
    </div>
  </div>
);

export default function FilterBottomSheet({
  open,
  onClose,
  initialValue,
  onApply,
}) {

  const dispatch = useDispatch();

  const [value, setValue] = useState(
    initialValue || {
      sort: "recommended",
      vegOnly: false,
      nonVegOnly: false,
      eggOnly: false,
      offerOnly: false,
      ratingMin: 0,
      timeMax: 0,
      priceRange: "all",
    }
  );

  useEffect(() => {
    if (open) setValue(initialValue || value);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [open, initialValue]);

  const appliedCount = useMemo(() => {
    let c = 0;
    if (value.sort !== "recommended") c++;
    if (value.vegOnly) c++;
    if (value.nonVegOnly) c++;
    if (value.eggOnly) c++;
    if (value.offerOnly) c++;
    if (value.ratingMin > 0) c++;
    if (value.timeMax > 0) c++;
    if (value.priceRange !== "all") c++;
    return c;
  }, [value]);

  const clearAll = () => {
    setValue({
      sort: "recommended",
      vegOnly: false,
      nonVegOnly: false,
      eggOnly: false,
      offerOnly: false,
      ratingMin: 0,
      timeMax: 0,
      priceRange: "all",
    });
    dispatch(clearMenucardFilters())
  };

  const apply = () => {
    onApply?.(value);
    onClose?.();
    dispatch(setMenucardFilters(value));
  };

  if (!open) return null;

  return (
    <div className="fixed inset-0 z-60 flex justify-center items-end">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/40 backdrop-blur-sm transition-opacity"
        onClick={onClose}
      />

      {/* --- Content Wrapper --- */}
      <div className="relative w-full max-w-md h-full flex flex-col justify-end pointer-events-none">
        
        {/* Floating Close Button */}
        <div className="flex justify-center mb-4 pointer-events-auto">
          <button
            onClick={onClose}
            className="h-12 w-12 rounded-full bg-white/10 backdrop-blur-md border border-white/20 flex items-center justify-center text-white shadow-lg active:scale-95 transition-all"
          >
            <X size={24} strokeWidth={2.5} />
          </button>
        </div>

        {/* --- Sheet --- */}
        <div className="bg-white rounded-t-[2.5rem] shadow-2xl overflow-hidden flex flex-col max-h-[75vh] pointer-events-auto">
          
          {/* Drag Handle */}
          <div className="w-full flex justify-center pt-3 pb-1">
            <div className="w-12 h-1.5 bg-gray-200 rounded-full"></div>
          </div>

          {/* --- Header --- */}
          <div className="px-6 pt-2 pb-2 flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="h-10 w-10 rounded-2xl bg-gray-100 flex items-center justify-center border border-gray-200">
                <SlidersHorizontal
                  size={20}
                  className="text-gray-900"
                  strokeWidth={2.5}
                />
              </div>
              <div>
                <h2 className="text-xl font-black text-gray-900 leading-none">
                  Filters
                </h2>
                <p className="text-xs text-gray-400 font-medium mt-1">
                  {appliedCount > 0
                    ? `${appliedCount} preferences applied`
                    : "Refine your search"}
                </p>
              </div>
            </div>

            {appliedCount > 0 && (
              <button
                type="button"
                onClick={clearAll}
                className="px-3 py-1.5 rounded-xl text-[11px] font-bold text-gray-500 bg-gray-50 border border-gray-200 hover:bg-gray-100 transition-colors"
              >
                Clear All
              </button>
            )}
          </div>

          {/* --- Scrollable Body --- */}
          <div className="flex-1 overflow-y-auto px-6 py-4 space-y-6 bg-white">
            
            {/* SORT */}
            <div>
              <SectionTitle>Sort By</SectionTitle>
              <div className="flex flex-wrap gap-2">
                <Chip
                  active={value.sort === "recommended"}
                  onClick={() =>
                    setValue((v) => ({ ...v, sort: "recommended" }))
                  }
                >
                  Recommended
                </Chip>
                <Chip
                  active={value.sort === "rating"}
                  icon={Star}
                  onClick={() => setValue((v) => ({ ...v, sort: "rating" }))}
                >
                  Top Rated
                </Chip>
                <Chip
                  active={value.sort === "time"}
                  icon={Clock}
                  onClick={() => setValue((v) => ({ ...v, sort: "time" }))}
                >
                  Fastest
                </Chip>
                <Chip
                  active={value.sort === "priceLow"}
                  icon={IndianRupee}
                  onClick={() => setValue((v) => ({ ...v, sort: "priceLow" }))}
                >
                  Low to High
                </Chip>
                <Chip
                  active={value.sort === "priceHigh"}
                  icon={IndianRupee}
                  onClick={() => setValue((v) => ({ ...v, sort: "priceHigh" }))}
                >
                  High to Low
                </Chip>
              </div>
            </div>

            <Divider />

            {/* DIETARY */}
            <div>
              <SectionTitle>Dietary Preference</SectionTitle>
              <div className="grid grid-cols-1 gap-2.5">
                <ToggleRow
                  label="Veg Only"
                  checked={value.vegOnly}
                  onChange={(checked) =>
                    setValue((v) => ({
                      ...v,
                      vegOnly: checked,
                      ...(checked ? { nonVegOnly: false } : {}),
                    }))
                  }
                />
                <ToggleRow
                  label="Non-Veg Only"
                  checked={value.nonVegOnly}
                  onChange={(checked) =>
                    setValue((v) => ({
                      ...v,
                      nonVegOnly: checked,
                      ...(checked ? { vegOnly: false } : {}),
                    }))
                  }
                />
                <ToggleRow
                  label="Contains Egg"
                  checked={value.eggOnly}
                  onChange={(checked) =>
                    setValue((v) => ({ ...v, eggOnly: checked }))
                  }
                />
                <ToggleRow
                  label="Show Offers Only"
                  checked={value.offerOnly}
                  onChange={(checked) =>
                    setValue((v) => ({ ...v, offerOnly: checked }))
                  }
                />
              </div>
            </div>

            <Divider />

            {/* RATING */}
            <div>
              <SectionTitle>Minimum Rating</SectionTitle>
              <div className="flex flex-wrap gap-2">
                <Chip
                  active={value.ratingMin === 0}
                  onClick={() => setValue((v) => ({ ...v, ratingMin: 0 }))}
                >
                  Any
                </Chip>
                <Chip
                  active={value.ratingMin === 4}
                  icon={Star}
                  onClick={() => setValue((v) => ({ ...v, ratingMin: 4 }))}
                >
                  4.0+
                </Chip>
                <Chip
                  active={value.ratingMin === 4.5}
                  icon={Star}
                  onClick={() => setValue((v) => ({ ...v, ratingMin: 4.5 }))}
                >
                  4.5+
                </Chip>
              </div>
            </div>

            <Divider />

            {/* PRICE RANGE */}
            <div>
              <SectionTitle>Price Range</SectionTitle>
              <div className="flex flex-wrap gap-2">
                <Chip
                  active={value.priceRange === "all"}
                  onClick={() => setValue((v) => ({ ...v, priceRange: "all" }))}
                >
                  All
                </Chip>
                <Chip
                  active={value.priceRange === "under200"}
                  onClick={() =>
                    setValue((v) => ({ ...v, priceRange: "under200" }))
                  }
                >
                  Under ₹200
                </Chip>
                <Chip
                  active={value.priceRange === "200to400"}
                  onClick={() =>
                    setValue((v) => ({ ...v, priceRange: "200to400" }))
                  }
                >
                  ₹200 – ₹400
                </Chip>
                <Chip
                  active={value.priceRange === "400plus"}
                  onClick={() =>
                    setValue((v) => ({ ...v, priceRange: "400plus" }))
                  }
                >
                  ₹400+
                </Chip>
              </div>
            </div>

            {/* Bottom Padding for scroll */}
            <div className="h-6"></div>
          </div>

          {/* --- Footer Action --- */}
          <div className="px-6 py-5 bg-white border-t border-gray-100 shadow-[0_-10px_40px_rgba(0,0,0,0.03)] z-10">
            <button
              type="button"
              onClick={apply}
              className={`
                w-full py-3.5 rounded-2xl 
                bg-gray-900 text-white
                font-bold text-sm tracking-wide
                shadow-lg shadow-gray-200
                hover:bg-black hover:shadow-xl hover:scale-[1.01] 
                active:scale-[0.98] 
                transition-all duration-300
                flex items-center justify-center gap-2
              `}
            >
              Apply Filters
              {appliedCount > 0 && (
                <span className="bg-white/20 px-2 py-0.5 rounded-lg text-[11px] font-bold text-white">
                  {appliedCount}
                </span>
              )}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}