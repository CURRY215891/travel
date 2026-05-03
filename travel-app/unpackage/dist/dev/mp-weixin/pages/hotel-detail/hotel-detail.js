"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      info: {},
      imgList: [],
      isFav: false,
      roomList: [],
      distance: ""
    };
  },
  onLoad(options) {
    this.getDetail(options.id);
    this.checkFav(options.id);
    this.getRooms(options.id);
  },
  methods: {
    formatImg(url) {
      return utils_config.config.getImgUrl(url);
    },
    getRooms(hotelId) {
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/hotel-room/list",
        data: { hotelId },
        success: (res) => {
          this.roomList = res.data.list || res.data || [];
        }
      });
    },
    goRoomDetail(roomId) {
      common_vendor.index.navigateTo({ url: "/pages/room-detail/room-detail?id=" + roomId });
    },
    calculateDistance() {
      common_vendor.index.getLocation({
        type: "wgs84",
        success: (res) => {
          if (this.info.latitude && this.info.longitude) {
            const dist = this.getDistance(
              res.latitude,
              res.longitude,
              parseFloat(this.info.latitude),
              parseFloat(this.info.longitude)
            );
            this.distance = dist > 1 ? dist.toFixed(1) + "km" : (dist * 1e3).toFixed(0) + "m";
          }
        }
      });
    },
    getDistance(lat1, lng1, lat2, lng2) {
      const radLat1 = lat1 * Math.PI / 180;
      const radLat2 = lat2 * Math.PI / 180;
      const a = radLat1 - radLat2;
      const b = lng1 * Math.PI / 180 - lng2 * Math.PI / 180;
      let s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
      s = s * 6378.137;
      return s;
    },
    openMap() {
      if (!this.info.latitude || !this.info.longitude) {
        common_vendor.index.showToast({ title: "暂无位置信息", icon: "none" });
        return;
      }
      common_vendor.index.openLocation({
        latitude: parseFloat(this.info.latitude),
        longitude: parseFloat(this.info.longitude),
        name: this.info.name,
        address: this.info.address
      });
    },
    checkFav(id) {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user)
        return;
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/favorite/check",
        data: { userId: user.id, targetId: id, type: 3 },
        success: (res) => {
          this.isFav = res.data;
        }
      });
    },
    toggleFav() {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user) {
        common_vendor.index.showToast({ title: "请先登录", icon: "none" });
        return;
      }
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/favorite/toggle",
        method: "POST",
        data: {
          userId: user.id,
          targetId: this.info.id,
          type: 3
        },
        success: (res) => {
          if (res.data) {
            this.isFav = !this.isFav;
            common_vendor.index.showToast({ title: this.isFav ? "收藏成功" : "已取消收藏" });
          }
        }
      });
    },
    getDetail(id) {
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/hotel/" + id,
        success: (res) => {
          this.info = res.data;
          if (this.info.images) {
            this.imgList = this.info.images.split(",");
          } else {
            this.imgList = [this.info.mainImage];
          }
          this.calculateDistance();
        }
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.f($data.imgList, (img, index, i0) => {
      return {
        a: $options.formatImg(img),
        b: index
      };
    }),
    b: common_vendor.t($data.info.name),
    c: common_vendor.t($data.isFav ? "❤️ 已收藏" : "🤍 收藏"),
    d: $data.isFav ? "#ff5a5f" : "#ccc",
    e: common_vendor.o((...args) => $options.toggleFav && $options.toggleFav(...args)),
    f: common_vendor.t($data.info.minPrice || $data.info.price || 0),
    g: $data.info.tags
  }, $data.info.tags ? {
    h: common_vendor.f($data.info.tags.split(","), (tag, index, i0) => {
      return {
        a: common_vendor.t(tag),
        b: index
      };
    })
  } : {}, {
    i: $data.info.starLevel
  }, $data.info.starLevel ? {
    j: common_vendor.t($data.info.starLevel)
  } : {}, {
    k: $data.info.facilities
  }, $data.info.facilities ? {
    l: common_vendor.t($data.info.facilities)
  } : {}, {
    m: common_vendor.t($data.info.address || "查看地图"),
    n: $data.distance
  }, $data.distance ? {
    o: common_vendor.t($data.distance)
  } : {}, {
    p: common_vendor.o((...args) => $options.openMap && $options.openMap(...args)),
    q: common_vendor.t($data.info.description),
    r: common_vendor.f($data.roomList, (room, k0, i0) => {
      return {
        a: $options.formatImg(room.image),
        b: common_vendor.t(room.name),
        c: common_vendor.t(room.description),
        d: common_vendor.t(room.price),
        e: room.id,
        f: common_vendor.o(($event) => $options.goRoomDetail(room.id), room.id)
      };
    }),
    s: $data.roomList.length === 0
  }, $data.roomList.length === 0 ? {} : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/hotel-detail/hotel-detail.js.map
