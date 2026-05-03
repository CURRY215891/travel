"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      currentStatus: -1,
      statusTabs: [
        { name: "全部", status: -1 },
        { name: "待支付", status: 0 },
        { name: "待确认", status: 1 },
        { name: "待入住", status: 2 },
        { name: "已入住", status: 3 },
        { name: "已完成", status: 4 }
      ],
      bookings: []
    };
  },
  computed: {
    filteredBookings() {
      if (this.currentStatus === -1)
        return this.bookings;
      return this.bookings.filter((b) => b.status === this.currentStatus);
    }
  },
  onShow() {
    this.loadBookings();
  },
  methods: {
    formatImg(url) {
      return utils_config.config.getImgUrl(url);
    },
    formatTime(time) {
      if (!time)
        return "";
      return time.replace("T", " ").substring(0, 16);
    },
    loadBookings() {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user)
        return;
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/booking/user/" + user.id,
        success: (res) => {
          if (res.statusCode === 200 && Array.isArray(res.data)) {
            this.bookings = res.data;
          } else {
            this.bookings = [];
            common_vendor.index.__f__("error", "at pages/my-bookings/my-bookings.vue:104", "加载预订列表失败:", res);
          }
        },
        fail: (err) => {
          this.bookings = [];
          common_vendor.index.showToast({ title: "网络错误", icon: "none" });
        }
      });
    },
    switchStatus(status) {
      this.currentStatus = status;
    },
    getStatusName(status) {
      const names = {
        0: "待支付",
        1: "待确认",
        2: "待入住",
        3: "已入住",
        4: "已完成",
        5: "已取消",
        6: "退款中",
        7: "已退款"
      };
      return names[status] || "未知";
    },
    handlePay(id) {
      const booking = this.bookings.find((b) => b.id === id);
      if (!booking)
        return;
      common_vendor.index.navigateTo({
        url: `/pages/payment/payment?bookingId=${id}&roomName=${encodeURIComponent(booking.roomName)}&checkInDate=${booking.checkInDate}&checkOutDate=${booking.checkOutDate}&userName=${encodeURIComponent(booking.userName)}&totalPrice=${booking.totalPrice}`
      });
    },
    handleCancel(id) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要取消订单吗？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.request({
              url: utils_config.config.baseUrl + "/booking/cancel",
              method: "POST",
              data: { id },
              success: (res2) => {
                if (res2.data && res2.data.success) {
                  common_vendor.index.showToast({ title: "订单已取消" });
                  this.loadBookings();
                } else {
                  common_vendor.index.showToast({ title: res2.data.message || "取消失败", icon: "none" });
                }
              }
            });
          }
        }
      });
    },
    handleApplyRefund(id) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要申请退款吗？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.request({
              url: utils_config.config.baseUrl + "/booking/apply-refund",
              method: "POST",
              data: { id },
              success: (res2) => {
                if (res2.data && res2.data.success) {
                  common_vendor.index.showToast({ title: "已申请退款" });
                  this.loadBookings();
                } else {
                  common_vendor.index.showToast({ title: res2.data.message || "申请失败", icon: "none" });
                }
              }
            });
          }
        }
      });
    },
    handleUserDelete(id) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要删除订单吗？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.request({
              url: utils_config.config.baseUrl + "/booking/user-delete",
              method: "POST",
              data: { id },
              success: (res2) => {
                if (res2.data && res2.data.success) {
                  common_vendor.index.showToast({ title: "删除成功" });
                  this.loadBookings();
                } else {
                  common_vendor.index.showToast({ title: res2.data.message || "删除失败", icon: "none" });
                }
              }
            });
          }
        }
      });
    },
    goRoomDetail(roomId) {
      common_vendor.index.navigateTo({
        url: "/pages/room-detail/room-detail?id=" + roomId
      });
    },
    goComment(item) {
      common_vendor.index.navigateTo({
        url: `/pages/hotel-review/hotel-review?roomId=${item.roomId}&hotelName=${item.hotelName}&roomName=${item.roomName}`
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.f($data.statusTabs, (item, index, i0) => {
      return {
        a: common_vendor.t(item.name),
        b: index,
        c: $data.currentStatus === item.status ? 1 : "",
        d: common_vendor.o(($event) => $options.switchStatus(item.status), index)
      };
    }),
    b: common_vendor.f($options.filteredBookings, (item, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(item.hotelName),
        b: common_vendor.t($options.getStatusName(item.status)),
        c: common_vendor.n("status-" + item.status),
        d: $options.formatImg(item.roomImage),
        e: common_vendor.t(item.roomName),
        f: common_vendor.t(item.checkInDate),
        g: common_vendor.t(item.checkOutDate),
        h: common_vendor.t(item.totalPrice),
        i: common_vendor.o(($event) => $options.goRoomDetail(item.roomId), item.id),
        j: common_vendor.t($options.formatTime(item.createTime)),
        k: item.status === 0
      }, item.status === 0 ? {
        l: common_vendor.o(($event) => $options.handlePay(item.id), item.id)
      } : {}, {
        m: item.status === 0
      }, item.status === 0 ? {
        n: common_vendor.o(($event) => $options.handleCancel(item.id), item.id)
      } : {}, {
        o: item.status === 1 || item.status === 2
      }, item.status === 1 || item.status === 2 ? {
        p: common_vendor.o(($event) => $options.handleCancel(item.id), item.id)
      } : {}, {
        q: item.status === 1 || item.status === 2
      }, item.status === 1 || item.status === 2 ? {
        r: common_vendor.o(($event) => $options.handleApplyRefund(item.id), item.id)
      } : {}, {
        s: item.status === 4
      }, item.status === 4 ? {
        t: common_vendor.o(($event) => $options.goComment(item), item.id)
      } : {}, {
        v: item.status === 4 || item.status === 5 || item.status === 7
      }, item.status === 4 || item.status === 5 || item.status === 7 ? {
        w: common_vendor.o(($event) => $options.handleUserDelete(item.id), item.id)
      } : {}, {
        x: item.id
      });
    }),
    c: $options.filteredBookings.length === 0
  }, $options.filteredBookings.length === 0 ? {
    d: common_assets._imports_0
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/my-bookings/my-bookings.js.map
