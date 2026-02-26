"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      categoryId: null,
      categoryName: "",
      attractions: [],
      searchKeyword: "",
      searchResults: [],
      showSuggestions: false
    };
  },
  onLoad(options) {
    this.categoryId = options.id;
    this.categoryName = options.name || "景点列表";
    common_vendor.index.setNavigationBarTitle({
      title: this.categoryName
    });
    this.getAttractions();
  },
  methods: {
    getPriceUnit(categoryId) {
      if (categoryId === 4)
        return " /份起";
      if (categoryId === 2)
        return " /晚起";
      return " /人起";
    },
    getTags(item) {
      if (item.categoryId === 4)
        return ["老字号", "必吃榜"];
      if (item.categoryId === 2)
        return ["交通便利", "环境舒适"];
      if (item.categoryId === 1)
        return ["历史名胜", "文化底蕴"];
      if (item.categoryId === 3)
        return ["自然风光", "避暑胜地"];
      return ["推荐"];
    },
    formatImg(url) {
      return utils_config.config.getImgUrl(url);
    },
    getAttractions() {
      common_vendor.index.showLoading({ title: "加载中..." });
      let apiUrl = utils_config.config.baseUrl + "/attraction/listByCategory";
      let requestData = { categoryId: this.categoryId };
      if (this.categoryId == 2) {
        apiUrl = utils_config.config.baseUrl + "/hotel/list";
        requestData = {};
      } else if (this.categoryId == 4) {
        apiUrl = utils_config.config.baseUrl + "/food/list";
        requestData = {};
      }
      common_vendor.index.request({
        url: apiUrl,
        data: requestData,
        success: (res) => {
          this.attractions = res.data.map((item) => {
            return {
              ...item,
              price: item.price || item.minPrice || item.avgPrice || 0,
              mainImage: item.mainImage || item.main_image,
              categoryId: parseInt(this.categoryId)
              // 强制设置分类ID
            };
          });
        },
        fail: () => {
          common_vendor.index.showToast({ title: "加载失败", icon: "none" });
        },
        complete: () => {
          common_vendor.index.hideLoading();
        }
      });
    },
    goDetail(id) {
      let url = "/pages/detail/detail?id=" + id;
      if (this.categoryId == 2) {
        url = "/pages/hotel-detail/hotel-detail?id=" + id;
      } else if (this.categoryId == 4) {
        url = "/pages/food-detail/food-detail?id=" + id;
      }
      common_vendor.index.navigateTo({ url });
    },
    goSuggestionDetail(item) {
      this.searchKeyword = item.name;
      this.attractions = [item];
      this.closeSuggestions();
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
        data: {
          keyword: value,
          categoryId: this.categoryId
        },
        success: (res) => {
          this.searchResults = res.data;
          this.showSuggestions = true;
        }
      });
    },
    onSearch() {
      if (!this.searchKeyword.trim()) {
        this.getAttractions();
        return;
      }
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/attraction/search",
        data: {
          keyword: this.searchKeyword,
          categoryId: this.categoryId
        },
        success: (res) => {
          this.attractions = res.data;
          this.closeSuggestions();
        }
      });
    },
    clearSearch() {
      this.searchKeyword = "";
      this.searchResults = [];
      this.showSuggestions = false;
      this.getAttractions();
    },
    closeSuggestions() {
      this.showSuggestions = false;
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: "搜索" + $data.categoryName,
    b: common_vendor.o((...args) => $options.onSearch && $options.onSearch(...args)),
    c: common_vendor.o([($event) => $data.searchKeyword = $event.detail.value, (...args) => $options.onInput && $options.onInput(...args)]),
    d: common_vendor.o((...args) => $options.onFocus && $options.onFocus(...args)),
    e: $data.searchKeyword,
    f: $data.searchKeyword
  }, $data.searchKeyword ? {
    g: common_vendor.o((...args) => $options.clearSearch && $options.clearSearch(...args))
  } : {}, {
    h: $data.showSuggestions && $data.searchResults.length > 0
  }, $data.showSuggestions && $data.searchResults.length > 0 ? {
    i: common_vendor.f($data.searchResults, (item, k0, i0) => {
      return {
        a: common_vendor.t(item.name),
        b: item.id,
        c: common_vendor.o(($event) => $options.goSuggestionDetail(item), item.id)
      };
    })
  } : {}, {
    j: $data.showSuggestions
  }, $data.showSuggestions ? {
    k: common_vendor.o((...args) => $options.closeSuggestions && $options.closeSuggestions(...args))
  } : {}, {
    l: $data.attractions.length > 0
  }, $data.attractions.length > 0 ? {
    m: common_vendor.f($data.attractions, (item, k0, i0) => {
      return {
        a: $options.formatImg(item.mainImage) || "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=400",
        b: common_vendor.t(item.name),
        c: common_vendor.f($options.getTags(item), (tag, tIdx, i1) => {
          return {
            a: common_vendor.t(tag),
            b: tIdx
          };
        }),
        d: common_vendor.t(item.price || "0"),
        e: common_vendor.t($options.getPriceUnit(item.categoryId)),
        f: item.id,
        g: common_vendor.o(($event) => $options.goDetail(item.id), item.id)
      };
    })
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/category-list/category-list.js.map
