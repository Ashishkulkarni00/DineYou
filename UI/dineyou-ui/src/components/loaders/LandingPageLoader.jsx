export default function LandingPageLoader({ children, loading }) {
  if (!loading) return children;

  return (
    <div className="relative min-h-[calc(100vh-56px)]">
      {/* Skeleton content shaped like Landing page */}
      <div className="px-4 mt-[6rem] mb-24 animate-pulse">
        {/* Greeting Section skeleton */}
        <div className="mb-3">
          <div className="h-3 w-56 bg-gray-200 rounded-full" />
          <div className="mt-2 h-7 w-64 bg-gray-200 rounded-xl" />
          <div className="mt-2 h-4 w-72 bg-gray-100 rounded-full" />
        </div>

        {/* Search input skeleton */}
        <div className="h-12 w-full bg-gray-200 rounded-2xl" />

        {/* Hero Carousel skeleton */}
        <div className="mt-3 rounded-3xl overflow-hidden border border-gray-200 shadow-[0_18px_45px_rgba(17,24,39,0.14)] bg-white">
          <div className="h-44 bg-gray-200" />
          <div className="px-4 pt-3 pb-4">
            <div className="h-6 w-56 bg-gray-200 rounded-lg" />
            <div className="mt-2 h-4 w-72 bg-gray-100 rounded-full" />

            <div className="mt-3 flex items-center gap-2">
              <div className="h-9 w-28 bg-gray-200 rounded-xl" />
              <div className="h-9 w-28 bg-gray-200 rounded-xl" />
            </div>
          </div>
        </div>

        {/* Popular Items header skeleton */}
        <div className="mt-6">
          <div className="flex justify-between items-center mb-4">
            <div className="h-4 w-32 bg-gray-200 rounded-full" />
            <div className="h-4 w-16 bg-gray-100 rounded-full" />
          </div>
        </div>

        {/* Popular Items swiper skeleton */}
        <div className="-mx-4 px-4">
          <div className="flex gap-3 overflow-hidden">
            {Array.from({ length: 3 }).map((_, i) => (
              <div
                key={i}
                className="w-[44%] min-w-[44%] rounded-2xl overflow-hidden bg-white border border-gray-100 shadow-md"
              >
                <div className="relative h-32 bg-gray-200" />

                <div className="p-3">
                  <div className="h-4 w-24 bg-gray-200 rounded-full" />
                  <div className="mt-2 h-5 w-16 bg-gray-200 rounded-lg" />
                  <div className="mt-2 h-3 w-28 bg-gray-100 rounded-full" />
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Spinner overlay centered in viewport */}
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
    </div>
  );
}
