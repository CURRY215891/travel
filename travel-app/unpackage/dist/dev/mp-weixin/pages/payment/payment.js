"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      bookingId: null,
      roomName: "",
      checkInDate: "",
      checkOutDate: "",
      userName: "",
      totalPrice: "0.00",
      payMethod: "wechat",
      paying: false
    };
  },
  onLoad(options) {
    this.bookingId = options.bookingId;
    this.roomName = decodeURIComponent(options.roomName || "");
    this.checkInDate = options.checkInDate || "";
    this.checkOutDate = options.checkOutDate || "";
    this.userName = decodeURIComponent(options.userName || "");
    this.totalPrice = options.totalPrice || "0.00";
  },
  methods: {
    handlePay() {
      if (this.paying)
        return;
      this.paying = true;
      setTimeout(() => {
        common_vendor.index.request({
          url: utils_config.config.baseUrl + "/booking/pay",
          method: "POST",
          data: { id: this.bookingId },
          success: (res) => {
            this.paying = false;
            if (res.data && res.data.success) {
              common_vendor.index.showToast({ title: "支付成功", icon: "success" });
              setTimeout(() => {
                common_vendor.index.redirectTo({ url: "/pages/my-bookings/my-bookings" });
              }, 1500);
            } else {
              common_vendor.index.showToast({ title: res.data.message || "支付失败", icon: "none" });
            }
          },
          fail: () => {
            this.paying = false;
            common_vendor.index.showToast({ title: "网络错误", icon: "none" });
          }
        });
      }, 1e3);
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_vendor.t($data.roomName),
    b: common_vendor.t($data.checkInDate),
    c: common_vendor.t($data.checkOutDate),
    d: common_vendor.t($data.userName),
    e: common_vendor.t($data.totalPrice),
    f: $data.payMethod === "wechat" ? 1 : "",
    g: $data.payMethod === "wechat" ? 1 : "",
    h: common_vendor.o(($event) => $data.payMethod = "wechat"),
    i: $data.payMethod === "alipay" ? 1 : "",
    j: $data.payMethod === "alipay" ? 1 : "",
    k: common_vendor.o(($event) => $data.payMethod = "alipay"),
    l: common_vendor.t($data.totalPrice),
    m: common_vendor.t($data.paying ? "支付中..." : "确认支付"),
    n: common_vendor.o((...args) => $options.handlePay && $options.handlePay(...args)),
    o: $data.paying ? 1 : ""
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/payment/payment.js.map
