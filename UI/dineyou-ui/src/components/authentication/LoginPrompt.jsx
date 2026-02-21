import { X, Heart, Zap } from "lucide-react";

const LoginPrompt = ({ isOpen, onClose, onLogin }) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black/50 backdrop-blur-sm flex justify-center items-end z-50">
      <div className="relative w-full max-w-md animate-slideUp">
        {/* Floating Close Button */}
        <button
          onClick={onClose}
          className="absolute left-1/2 -top-14 -translate-x-1/2
                     h-11 w-11 rounded-full bg-gray-900 hover:bg-gray-800
                     flex items-center justify-center transition
                     active:scale-95 shadow-lg z-50"
        >
          <X size={20} className="text-white" />
        </button>

        {/* Modal */}
        <div className="bg-white rounded-t-3xl p-6 shadow-xl">
          {/* Header */}
          <h3 className="text-2xl font-bold text-gray-900 mb-2">
            Almost there! 🍰
          </h3>

          <p className="text-gray-600 text-sm mb-5">
            Login helps us give you a smoother, tastier experience.
          </p>

          {/* Benefits */}
          <div className="space-y-3 mb-6">
            <div className="flex items-center gap-3 bg-orange-50 border border-orange-200 rounded-xl p-3">
              <Heart className="text-orange-500" size={18} />
              <span className="text-sm text-gray-700">
                Save your favorite dishes
              </span>
            </div>

            <div className="flex items-center gap-3 bg-orange-50 border border-orange-200 rounded-xl p-3">
              <Zap className="text-orange-500" size={18} />
              <span className="text-sm text-gray-700">
                Faster checkout & order tracking
              </span>
            </div>
          </div>

          {/* Actions */}
          <div className="flex gap-3">
            <button
              onClick={onClose}
              className="flex-1 py-3 rounded-xl bg-gray-100 text-gray-700
                         text-[15px] font-medium active:scale-[0.98]"
            >
              Maybe Later
            </button>

            <button
              onClick={onLogin}
              className="flex-1 py-3 rounded-xl bg-[#ff6c2f]
                         text-white text-[15px] font-semibold
                         shadow-md hover:shadow-lg active:scale-[0.97]"
            >
              Continue Login
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginPrompt;
