"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      roomId: "",
      roomInfo: {
        id: "",
        name: "",
        image: "",
        hotelName: "",
        price: 0
      },
      bookingForm: {
        checkInDate: "",
        checkOutDate: "",
        userName: "",
        userPhone: ""
      },
      totalPrice: "0.00",
      days: 0,
      startDate: ""
    };
  },
  onLoad(options) {
    common_vendor.index.__f__("log", "at pages/booking-order/booking-order.vue:86", "booking-order onLoad:", options);
    if (options.roomId) {
      this.roomId = options.roomId;
      this.getRoomDetail();
    }
    const today = /* @__PURE__ */ new Date();
    const year = today.getFullYear();
    const month = (today.getMonth() + 1).toString().padStart(2, "0");
    const day = today.getDate().toString().padStart(2, "0");
    this.startDate = `${year}-${month}-${day}`;
  },
  methods: {
    formatImg(url) {
      return utils_config.config.getImgUrl(url);
    },
    getRoomDetail() {
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/hotel-room/" + this.roomId,
        success: (res) => {
          common_vendor.index.__f__("log", "at pages/booking-order/booking-order.vue:106", "room detail:", res.data);
          if (res.data) {
            this.roomInfo = res.data;
            this.totalPrice = Number(res.data.price).toFixed(2);
          }
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at pages/booking-order/booking-order.vue:113", "getRoomDetail fail:", err);
        }
      });
    },
    onCheckInChange(e) {
      this.bookingForm.checkInDate = e.detail.value;
      this.calculateTotal();
    },
    onCheckOutChange(e) {
      this.bookingForm.checkOutDate = e.detail.value;
      this.calculateTotal();
    },
    calculateTotal() {
      if (this.bookingForm.checkInDate && this.bookingForm.checkOutDate) {
        const start = new Date(this.bookingForm.checkInDate);
        const end = new Date(this.bookingForm.checkOutDate);
        const diffTime = end - start;
        const diffDays = Math.ceil(diffTime / (1e3 * 60 * 60 * 24));
        if (diffDays > 0) {
          this.days = diffDays;
          const price = Number(this.roomInfo.price) || 0;
          this.totalPrice = (this.days * price).toFixed(2);
        } else {
          this.days = 0;
          this.totalPrice = Number(this.roomInfo.price).toFixed(2);
          if (this.bookingForm.checkOutDate) {
            common_vendor.index.showToast({ title: "退房日期需晚于入住日期", icon: "none" });
            this.bookingForm.checkOutDate = "";
          }
        }
      }
    },
    submitBooking() {
      if (!this.bookingForm.checkInDate || !this.bookingForm.checkOutDate) {
        return common_vendor.index.showToast({ title: "请选择预订日期", icon: "none" });
      }
      if (!this.bookingForm.userName || !this.bookingForm.userName.trim()) {
        return common_vendor.index.showToast({ title: "请输入姓名", icon: "none" });
      }
      if (!this.bookingForm.userPhone || !this.bookingForm.userPhone.trim()) {
        return common_vendor.index.showToast({ title: "请输入联系电话", icon: "none" });
      }
      if (!/^[0-9]{11}$/.test(this.bookingForm.userPhone)) {
        return common_vendor.index.showToast({ title: "联系电话必须为11位数字", icon: "none" });
      }
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user || !user.id) {
        return common_vendor.index.showToast({ title: "请先登录", icon: "none" });
      }
      common_vendor.index.showLoading({ title: "提交中..." });
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/booking/add",
        method: "POST",
        data: {
          userId: user.id,
          hotelId: this.roomInfo.hotelId,
          roomId: this.roomInfo.id,
          checkInDate: this.bookingForm.checkInDate,
          checkOutDate: this.bookingForm.checkOutDate,
          totalPrice: this.totalPrice,
          userName: this.bookingForm.userName,
          userPhone: this.bookingForm.userPhone,
          status: 1
        },
        success: (res) => {
          common_vendor.index.hideLoading();
          if (res.data === true) {
            common_vendor.index.showToast({ title: "预订成功" });
            setTimeout(() => {
              common_vendor.index.navigateTo({ url: "/pages/my-bookings/my-bookings" });
            }, 1500);
          } else {
            common_vendor.index.showToast({ title: "该时间段房间已被预订", icon: "none" });
          }
        },
        fail: () => {
          common_vendor.index.hideLoading();
          common_vendor.index.showToast({ title: "提交失败", icon: "none" });
        }
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $options.formatImg($data.roomInfo.image),
    b: common_vendor.t($data.roomInfo.name),
    c: common_vendor.t($data.roomInfo.hotelName || "酒店房间"),
    d: common_vendor.t($data.roomInfo.price),
    e: common_vendor.t($data.bookingForm.checkInDate || "请选择入住日期"),
    f: $data.startDate,
    g: common_vendor.o((...args) => $options.onCheckInChange && $options.onCheckInChange(...args)),
    h: common_vendor.t($data.bookingForm.checkOutDate || "请选择退房日期"),
    i: $data.bookingForm.checkInDate || $data.startDate,
    j: common_vendor.o((...args) => $options.onCheckOutChange && $options.onCheckOutChange(...args)),
    k: $data.days > 0
  }, $data.days > 0 ? {
    l: common_vendor.t($data.days)
  } : {}, {
    m: $data.bookingForm.userName,
    n: common_vendor.o(($event) => $data.bookingForm.userName = $event.detail.value),
    o: $data.bookingForm.userPhone,
    p: common_vendor.o(($event) => $data.bookingForm.userPhone = $event.detail.value),
    q: common_vendor.t($data.totalPrice),
    r: common_vendor.o((...args) => $options.submitBooking && $options.submitBooking(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/booking-order/booking-order.js.map
