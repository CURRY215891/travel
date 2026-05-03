"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      bannerList: [
        "/static/static-iamge/index1.jpg",
        "/static/static-iamge/index2.jpg",
        "/static/static-iamge/index3.jpg"
      ],
      categories: [
        { id: 3, name: "自然风光", icon: "https://cdn-icons-png.flaticon.com/512/2913/2913520.png" },
        { id: 1, name: "历史名胜", icon: "https://cdn-icons-png.flaticon.com/512/2680/2680894.png" },
        { id: 4, name: "美食品尝", icon: "https://cdn-icons-png.flaticon.com/512/706/706164.png" },
        { id: 2, name: "酒店住宿", icon: "https://cdn-icons-png.flaticon.com/512/2983/2983787.png" }
      ],
      attractions: [],
      currentCity: "定位中...",
      searchKeyword: "",
      searchResults: [],
      showSuggestions: false,
      currentLat: null,
      currentLng: null
    };
  },
  onLoad() {
    this.getAttractions();
    this.getLocation();
  },
  methods: {
    getPriceUnit(categoryId) {
      if (categoryId === 4)
        return " /份起";
      if (categoryId === 2)
        return " /晚起";
      return " /人起";
    },
    formatImg(url) {
      return utils_config.config.getImgUrl(url);
    },
    getLocation() {
      common_vendor.index.getLocation({
        type: "wgs84",
        success: (res) => {
          common_vendor.index.__f__("log", "at pages/index/index.vue:111", "获取当前位置成功:", res);
          this.currentLat = res.latitude;
          this.currentLng = res.longitude;
          setTimeout(() => {
            this.currentCity = "石家庄市";
          }, 1e3);
          this.calculateAllDistances();
        },
        fail: (err) => {
          this.currentCity = "石家庄市";
          common_vendor.index.__f__("log", "at pages/index/index.vue:127", "定位失败", err);
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
    calculateAllDistances() {
      if (!this.currentLat || !this.currentLng)
        return;
      this.attractions = this.attractions.map((item) => {
        if (item.latitude && item.longitude) {
          const dist = this.getDistance(
            this.currentLat,
            this.currentLng,
            parseFloat(item.latitude),
            parseFloat(item.longitude)
          );
          item.distance = dist > 1 ? dist.toFixed(1) + "km" : (dist * 1e3).toFixed(0) + "m";
        }
        return item;
      });
    },
    getAttractions() {
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/attraction/list",
        data: {
          categoryIds: "1,3"
          // 1-历史名胜, 3-自然风光
        },
        success: (res) => {
          this.attractions = res.data.map((item) => {
            return {
              ...item,
              price: item.price || 0,
              mainImage: item.mainImage || item.main_image,
              categoryId: item.categoryId || 1
              // 默认景点分类
            };
          });
          this.calculateAllDistances();
        }
      });
    },
    goDetail(id) {
      const item = this.attractions.find((a) => a.id === id);
      let url = "/pages/detail/detail?id=" + id;
      if (item && item.categoryId == 2) {
        url = "/pages/hotel-detail/hotel-detail?id=" + id;
      } else if (item && item.categoryId == 4) {
        url = "/pages/food-detail/food-detail?id=" + id;
      }
      common_vendor.index.navigateTo({ url });
    },
    goCategory(category) {
      common_vendor.index.navigateTo({
        url: `/pages/category-list/category-list?id=${category.id}&name=${category.name}`
      });
    },
    goSuggestionDetail(item) {
      this.closeSuggestions();
      let url = "/pages/detail/detail?id=" + item.id;
      if (item.categoryId == 2) {
        url = "/pages/hotel-detail/hotel-detail?id=" + item.id;
      } else if (item.categoryId == 4) {
        url = "/pages/food-detail/food-detail?id=" + item.id;
      }
      common_vendor.index.navigateTo({ url });
    },
    onFocus() {
      if (this.searchKeyword.trim()) {
        this.showSuggestions = true;
      }
    },
    onInput(e) {
      const value = e.detail.value;
      if (!value.trim()) {
        this.searchResults = [];
        this.showSuggestions = false;
        return;
      }
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/attraction/search",
        data: { keyword: value },
        success: (res) => {
          this.searchResults = res.data;
          this.showSuggestions = true;
        }
      });
    },
    onSearch() {
      if (!this.searchKeyword.trim()) {
        return;
      }
      this.onInput({ detail: { value: this.searchKeyword } });
    },
    clearSearch() {
      this.searchKeyword = "";
      this.searchResults = [];
      this.showSuggestions = false;
    },
    closeSuggestions() {
      this.showSuggestions = false;
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.t($data.currentCity),
    b: common_vendor.o((...args) => $options.getLocation && $options.getLocation(...args)),
    c: common_vendor.o((...args) => $options.onSearch && $options.onSearch(...args)),
    d: common_vendor.o([($event) => $data.searchKeyword = $event.detail.value, (...args) => $options.onInput && $options.onInput(...args)]),
    e: common_vendor.o((...args) => $options.onFocus && $options.onFocus(...args)),
    f: $data.searchKeyword,
    g: $data.searchKeyword
  }, $data.searchKeyword ? {
    h: common_vendor.o((...args) => $options.clearSearch && $options.clearSearch(...args))
  } : {}, {
    i: $data.showSuggestions && $data.searchResults.length > 0
  }, $data.showSuggestions && $data.searchResults.length > 0 ? {
    j: common_vendor.f($data.searchResults, (item, k0, i0) => {
      return {
        a: common_vendor.t(item.name),
        b: item.id,
        c: common_vendor.o(($event) => $options.goSuggestionDetail(item), item.id)
      };
    })
  } : {}, {
    k: $data.showSuggestions
  }, $data.showSuggestions ? {
    l: common_vendor.o((...args) => $options.closeSuggestions && $options.closeSuggestions(...args))
  } : {}, {
    m: common_vendor.f($data.bannerList, (img, index, i0) => {
      return {
        a: img,
        b: index
      };
    }),
    n: common_vendor.f($data.categories, (item, index, i0) => {
      return {
        a: item.icon,
        b: common_vendor.t(item.name),
        c: index,
        d: common_vendor.o(($event) => $options.goCategory(item), index)
      };
    }),
    o: common_vendor.f($data.attractions, (item, k0, i0) => {
      return common_vendor.e({
        a: $options.formatImg(item.mainImage) || "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=400",
        b: common_vendor.t(item.name),
        c: item.distance
      }, item.distance ? {
        d: common_vendor.t(item.distance)
      } : {}, {
        e: common_vendor.t(item.price || "0"),
        f: common_vendor.t($options.getPriceUnit(item.categoryId)),
        g: item.id,
        h: common_vendor.o(($event) => $options.goDetail(item.id), item.id)
      });
    })
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
