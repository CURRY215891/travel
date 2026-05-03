"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
if (!Math) {
  "./pages/index/index.js";
  "./pages/social/social.js";
  "./pages/me/me.js";
  "./pages/detail/detail.js";
  "./pages/post-add/post-add.js";
  "./pages/social/post-detail/post-detail.js";
  "./pages/my-favorites/my-favorites.js";
  "./pages/my-posts/my-posts.js";
  "./pages/my-comment/my-comment.js";
  "./pages/category-list/category-list.js";
  "./pages/hotel-detail/hotel-detail.js";
  "./pages/food-detail/food-detail.js";
  "./pages/room-detail/room-detail.js";
  "./pages/my-bookings/my-bookings.js";
  "./pages/booking-order/booking-order.js";
  "./pages/payment/payment.js";
  "./pages/hotel-review/hotel-review.js";
}
const _sfc_main = {
  onLaunch: function() {
    common_vendor.index.__f__("log", "at App.vue:4", "App Launch");
  },
  onShow: function() {
    common_vendor.index.__f__("log", "at App.vue:7", "App Show");
  },
  onHide: function() {
    common_vendor.index.__f__("log", "at App.vue:10", "App Hide");
  }
};
function createApp() {
  const app = common_vendor.createSSRApp(_sfc_main);
  return {
    app
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
//# sourceMappingURL=../.sourcemap/mp-weixin/app.js.map
