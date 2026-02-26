"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      posts: []
    };
  },
  onShow() {
    this.fetchPosts();
  },
  methods: {
    fetchPosts() {
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/post/list",
        method: "GET",
        success: (res) => {
          common_vendor.index.__f__("log", "at pages/social/social.vue:47", "获取动态成功:", res.data);
          this.posts = res.data;
        },
        fail: () => {
          common_vendor.index.showToast({ title: "无法连接服务器", icon: "none" });
        }
      });
    },
    goToDetail(id) {
      common_vendor.index.navigateTo({
        url: "/pages/social/post-detail/post-detail?id=" + id
      });
    },
    formatTime(timeStr) {
      if (!timeStr)
        return "";
      return timeStr.replace("T", " ").substring(5, 16);
    },
    formatImg(url) {
      return utils_config.config.getImgUrl(url);
    },
    goToAdd() {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user || !user.id) {
        common_vendor.index.showToast({ title: "请先登录账号", icon: "none" });
        return;
      }
      common_vendor.index.navigateTo({
        url: "/pages/post-add/post-add"
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_vendor.f($data.posts, (item, index, i0) => {
      return common_vendor.e({
        a: $options.formatImg(item.avatar) || "/static/logo.png",
        b: common_vendor.t(item.nickname || "游客"),
        c: common_vendor.t($options.formatTime(item.createTime)),
        d: common_vendor.t(item.content),
        e: item.image
      }, item.image ? {
        f: $options.formatImg(item.image.split(",")[0])
      } : {}, {
        g: common_vendor.t(item.likes || 0),
        h: common_vendor.t(item.commentCount || 0),
        i: index,
        j: common_vendor.o(($event) => $options.goToDetail(item.id), index)
      });
    }),
    b: common_vendor.o((...args) => $options.goToAdd && $options.goToAdd(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/social/social.js.map
