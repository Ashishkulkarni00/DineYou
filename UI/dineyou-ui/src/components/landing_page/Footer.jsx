import React from "react";

const Footer = () => {
  return (
    <footer className="mt-8 pb-6 px-4 text-center">
      <div className="h-px w-full bg-gradient-to-r from-transparent via-gray-200 to-transparent mb-4" />

      <p className="text-xs text-gray-500 italic mb-3">
        Good food is best enjoyed with good stories.
      </p>

      <div className="flex justify-center items-center gap-2 mb-2">
        <span className="font-black text-lg tracking-tight text-gray-900">
          TWO STORIES
        </span>
      </div>

      <p className="text-[10px] text-gray-400 uppercase tracking-widest font-semibold">
        Crafting Experiences Since 2024
      </p>
    </footer>
  );
};

export default Footer;
