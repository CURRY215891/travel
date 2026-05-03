"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      roomId: null,
      roomInfo: {},
      bookingForm: {
        checkInDate: "",
        checkOutDate: ""
      },
      totalPrice: 0,
      commentList: [],
      config: utils_config.config
    };
  },
  onLoad(options) {
    this.roomId = options.id;
    this.getRoomDetail();
    this.getComments();
  },
  methods: {
    getImgUrl(url) {
      return utils_config.config.getImgUrl(url);
    },
    formatTime(time) {
      if (!time)
        return "";
      return time.replace("T", " ").substring(0, 16);
    },
    splitImages(images) {
      if (!images)
        return [];
      return images.split(",");
    },
    previewImgs(imagesStr, current) {
      const urls = this.splitImages(imagesStr).map((img) => this.getImgUrl(img));
      common_vendor.index.previewImage({
        current: urls[current],
        urls
      });
    },
    getComments() {
      common_vendor.index.__f__("log", "at pages/room-detail/room-detail.vue:147", "Fetching comments for roomId:", this.roomId);
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/comment/attr/" + this.roomId + "?type=3",
        success: (res) => {
          common_vendor.index.__f__("log", "at pages/room-detail/room-detail.vue:151", "Comments API response:", res);
          this.commentList = res.data || [];
          common_vendor.index.__f__("log", "at pages/room-detail/room-detail.vue:153", "commentList set to:", this.commentList);
        },
        fail: (err) => {
          common_vendor.index.__f__("error", "at pages/room-detail/room-detail.vue:156", "Failed to fetch comments:", err);
        }
      });
    },
    formatImg(url) {
      if (!url)
        return "";
      return utils_config.config.getImgUrl(url);
    },
    getRoomDetail() {
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/hotel-room/" + this.roomId,
        success: (res) => {
          this.roomInfo = res.data;
          this.totalPrice = res.data.price;
        }
      });
    },
    handleBook() {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user) {
        common_vendor.index.showToast({ title: "请先登录", icon: "none" });
        return;
      }
      common_vendor.index.navigateTo({
        url: `/pages/booking-order/booking-order?roomId=${this.roomId}`
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $options.formatImg($data.roomInfo.image),
    b: common_vendor.t($data.roomInfo.name),
    c: common_vendor.t($data.roomInfo.price),
    d: $data.roomInfo.bedType || $data.roomInfo.area || $data.roomInfo.window
  }, $data.roomInfo.bedType || $data.roomInfo.area || $data.roomInfo.window ? common_vendor.e({
    e: $data.roomInfo.bedType
  }, $data.roomInfo.bedType ? {
    f: common_vendor.t($data.roomInfo.bedType)
  } : {}, {
    g: $data.roomInfo.area
  }, $data.roomInfo.area ? {
    h: common_vendor.t($data.roomInfo.area)
  } : {}, {
    i: $data.roomInfo.window
  }, $data.roomInfo.window ? {
    j: common_vendor.t($data.roomInfo.window)
  } : {}) : {}, {
    k: $data.roomInfo.facilities
  }, $data.roomInfo.facilities ? {
    l: common_vendor.f($data.roomInfo.facilities.split(","), (item, index, i0) => {
      return {
        a: common_vendor.t(item),
        b: index
      };
    })
  } : {}, {
    m: common_vendor.t($data.roomInfo.description || "暂无详细介绍"),
    n: $data.commentList.length
  }, $data.commentList.length ? {
    o: common_vendor.t($data.commentList.length)
  } : {}, {
    p: !$data.commentList.length
  }, !$data.commentList.length ? {} : {
    q: common_vendor.f($data.commentList, (item, index, i0) => {
      return common_vendor.e({
        a: $options.formatImg(item.avatar) || "/static/default-avatar.png",
        b: common_vendor.t(item.nickname || "匿名用户"),
        c: common_vendor.t($options.formatTime(item.createTime)),
        d: common_vendor.f(5, (s, k1, i1) => {
          return {
            a: s,
            b: s <= item.star ? 1 : ""
          };
        }),
        e: common_vendor.t(item.hygieneScore || 5),
        f: common_vendor.t(item.environmentScore || 5),
        g: common_vendor.t(item.serviceScore || 5),
        h: common_vendor.t(item.facilityScore || 5),
        i: common_vendor.t(item.content),
        j: item.images
      }, item.images ? {
        k: common_vendor.f($options.splitImages(item.images), (img, i, i1) => {
          return {
            a: i,
            b: $options.getImgUrl(img),
            c: common_vendor.o(($event) => $options.previewImgs(item.images, i), i)
          };
        })
      } : {}, {
        l: index
      });
    })
  }, {
    r: common_vendor.t($data.roomInfo.price),
    s: common_vendor.o((...args) => $options.handleBook && $options.handleBook(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/room-detail/room-detail.js.map
