export const filterMenucardData = (menucardData, menucardFilters) => {
  if (!menucardData?.categories) return menucardData;

  const SOLDCOUNT_TOP_THRESHOLD = 100;

  const matchesOffer = (item) =>
    !menucardFilters.offerOnly || (item.discountPercentage ?? 0) > 0;

  const matchesRating = (item) =>
    (menucardFilters.ratingMin ?? 0) === 0 ||
    (item.rating ?? 0) >= menucardFilters.ratingMin;

  const matchesTime = (item) =>
    (menucardFilters.timeMax ?? 0) === 0 ||
    (item.minimumPreparationTime ?? Infinity) <= menucardFilters.timeMax;

  const matchesPrice = (item) => {
    const p = item.itemPrice ?? 0;
    switch (menucardFilters.priceRange) {
      case "under200":
        return p < 200;
      case "200to400":
        return p >= 200 && p <= 400;
      case "400plus":
        return p > 400;
      default:
        return true;
    }
  };

  const applyAllFilters = (items = []) =>
    items
      .filter(matchesOffer)
      .filter(matchesRating)
      .filter(matchesTime)
      .filter(matchesPrice);

  const sortItems = (items) => {
    const s = menucardFilters.sort;
    const arr = [...items]; // sort() mutates [web:322]

    if (s === "time") {
      return arr.sort(
        (a, b) =>
          (a.minimumPreparationTime ?? Infinity) -
          (b.minimumPreparationTime ?? Infinity)
      );
    }

    if (s === "priceLow") {
      return arr.sort((a, b) => (a.itemPrice ?? 0) - (b.itemPrice ?? 0));
    }

    if (s === "priceHigh") {
      return arr.sort((a, b) => (b.itemPrice ?? 0) - (a.itemPrice ?? 0));
    }

    if (s === "topRated") {
      return arr
        .filter((x) => (x.soldCount ?? 0) >= SOLDCOUNT_TOP_THRESHOLD)
        .sort((a, b) => (b.soldCount ?? 0) - (a.soldCount ?? 0));
    }

    return items; // recommended => keep original order
  };

  const s = menucardFilters.sort;

  // ✅ Global sort mode (ignore categories)
  if (s === "priceLow" || s === "priceHigh") {
    const allFilteredItems = menucardData.categories.flatMap((cat) =>
      applyAllFilters(cat.menuItemList ?? [])
    );

    const globallySorted = sortItems(allFilteredItems);

    return {
      ...menucardData,
      categories: [
        {
          categoryName: "All Items",
          menuItemList: globallySorted,
          // keep any fields your UI expects:
          // categoryId: "ALL",
        },
      ],
    };
  }

  // Normal behavior (keep categories)
  const categories = menucardData.categories
    .map((cat) => {
      const filtered = applyAllFilters(cat.menuItemList ?? []);
      return { ...cat, menuItemList: sortItems(filtered) };
    })
    .filter((c) => (c.menuItemList?.length ?? 0) > 0);

  return { ...menucardData, categories };
};
