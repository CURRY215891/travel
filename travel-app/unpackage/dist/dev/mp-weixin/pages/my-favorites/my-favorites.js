"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  __name: "my-favorites",
  setup(__props) {
    const tabIndex = common_vendor.ref(1);
    const postList = common_vendor.ref([]);
    const attrList = common_vendor.ref([]);
    const formatImg = (url) => {
      return utils_config.config.getImgUrl(url);
    };
    common_vendor.onShow(() => {
      loadFavorites();
    });
    const switchTab = (index) => {
      tabIndex.value = index;
      loadFavorites();
    };
    const loadFavorites = () => {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user || !user.id)
        return;
      let url = utils_config.config.baseUrl + "/favorite/myList";
      let data = { userId: user.id };
      if (tabIndex.value !== 1) {
        url = utils_config.config.baseUrl + "/favorite/myAttractions";
        if (tabIndex.value === 2) {
          data.categoryIds = "1,3";
        } else if (tabIndex.value === 4) {
          data.categoryIds = "4";
        } else if (tabIndex.value === 3) {
          data.categoryIds = "2";
        }
      }
      common_vendor.index.request({
        url,
        method: "GET",
        data,
        success: (res) => {
          if (tabIndex.value === 1) {
            postList.value = res.data;
          } else {
            attrList.value = res.data.map((item) => ({
              ...item,
              mainImage: item.mainImage || item.main_image,
              price: item.price || item.minPrice || item.avgPrice || 0
            }));
          }
        }
      });
    };
    const goPostDetail = (id) => {
      common_vendor.index.navigateTo({
        url: `/pages/social/post-detail/post-detail?id=${id}`
      });
    };
    const goAttrDetail = (id) => {
      let url = `/pages/detail/detail?id=${id}`;
      if (tabIndex.value === 3) {
        url = `/pages/hotel-detail/hotel-detail?id=${id}`;
      } else if (tabIndex.value === 4) {
        url = `/pages/food-detail/food-detail?id=${id}`;
      }
      common_vendor.index.navigateTo({ url });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: tabIndex.value === 1 ? 1 : "",
        b: common_vendor.o(($event) => switchTab(1)),
        c: tabIndex.value === 2 ? 1 : "",
        d: common_vendor.o(($event) => switchTab(2)),
        e: tabIndex.value === 4 ? 1 : "",
        f: common_vendor.o(($event) => switchTab(4)),
        g: tabIndex.value === 3 ? 1 : "",
        h: common_vendor.o(($event) => switchTab(3)),
        i: tabIndex.value === 1
      }, tabIndex.value === 1 ? common_vendor.e({
        j: common_vendor.f(postList.value, (item, k0, i0) => {
          return {
            a: common_vendor.t(item.content),
            b: common_vendor.t(item.nickname || "用户"),
            c: common_vendor.t(item.likes || 0),
            d: item.id,
            e: common_vendor.o(($event) => goPostDetail(item.id), item.id)
          };
        }),
        k: postList.value.length === 0
      }, postList.value.length === 0 ? {} : {}) : {}, {
        l: tabIndex.value !== 1
      }, tabIndex.value !== 1 ? common_vendor.e({
        m: common_vendor.f(attrList.value, (item, k0, i0) => {
          return {
            a: formatImg(item.mainImage),
            b: common_vendor.t(item.name),
            c: common_vendor.t(item.address),
            d: common_vendor.t(item.price),
            e: item.id,
            f: common_vendor.o(($event) => goAttrDetail(item.id), item.id)
          };
        }),
        n: common_vendor.t(tabIndex.value === 4 ? "份" : "人"),
        o: attrList.value.length === 0
      }, attrList.value.length === 0 ? {
        p: common_vendor.t(tabIndex.value === 4 ? "🍱" : tabIndex.value === 3 ? "🏨" : "📍"),
        q: common_vendor.t(tabIndex.value === 4 ? "美食" : tabIndex.value === 3 ? "酒店" : "景点")
      } : {}) : {});
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/my-favorites/my-favorites.js.map
