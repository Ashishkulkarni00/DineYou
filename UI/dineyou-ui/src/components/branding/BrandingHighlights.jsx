const BrandHighlights = ({ brandData }) => {
  const features = brandData || [
    {
      title: "The Vibe",
      desc: "Warm lights, cozy corners, and stories that last longer than the food.",
      icon: "✨",
      color: "from-orange-100 to-orange-50",
    },
    {
      title: "Freshly Made",
      desc: "No shortcuts. Every plate cooked fresh, every single time.",
      icon: "🔥",
      color: "from-green-100 to-green-50",
    },
    {
      title: "Community",
      desc: "A place where friends meet, families gather, and memories happen.",
      icon: "🤍",
      color: "from-blue-100 to-blue-50",
    }
  ];

  return (
    <section className="mt-2">
      {/* Header */}
      <div className="mb-4">
        <h2 className="text-2xl font-black text-gray-900">
          More Than Just Food
        </h2>
        <p className="text-sm text-gray-500 mt-1 max-w-xs">
          Every visit to Two Stories is meant to feel special — from the first bite to the last laugh.
        </p>
        <div className="h-1 w-14 bg-orange-500 rounded-full mt-3" />
      </div>

      {/* Cards */}
      <div className="flex flex-col gap-2">
        {features.map((item, i) => (
          <div
            key={i}
            className="group relative overflow-hidden rounded-3xl border border-gray-100 bg-white p-4 transition-all active:scale-[0.98]"
          >
            {/* Glow */}
            <div
              className={`absolute inset-0 opacity-0 group-hover:opacity-100 transition-opacity bg-gradient-to-br ${item.color}`}
            />

            <div className="relative flex gap-4 items-start">
              <div className="w-12 h-12 rounded-2xl bg-white shadow-sm grid place-items-center text-xl">
                {item.icon}
              </div>

              <div>
                <h4 className="font-bold text-sm text-gray-900">
                  {item.title}
                </h4>
                <p className="text-xs text-gray-600 mt-1 leading-relaxed">
                  {item.desc}
                </p>
              </div>
            </div>
          </div>
        ))}
      </div>
    </section>
  );
};

export default BrandHighlights;
