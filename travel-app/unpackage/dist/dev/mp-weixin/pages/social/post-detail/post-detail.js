"use strict";
const common_vendor = require("../../../common/vendor.js");
const utils_config = require("../../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      postId: "",
      post: {},
      commentList: [],
      myComment: "",
      isLiked: false,
      isFaved: false
    };
  },
  onLoad(options) {
    common_vendor.index.__f__("log", "at pages/social/post-detail/post-detail.vue:80", "详情页收到的原始参数:", options);
    if (options.id && options.id !== "undefined") {
      this.postId = options.id;
      const userInfo = common_vendor.index.getStorageSync("userInfo");
      this.loadPostDetail();
      this.loadComments();
      if (userInfo && userInfo.id) {
        this.checkUserLike(userInfo.id);
        this.checkFavorite(userInfo.id);
      }
    } else {
      common_vendor.index.__f__("error", "at pages/social/post-detail/post-detail.vue:97", "错误：未检测到有效的 postId");
      common_vendor.index.showToast({
        title: "动态参数丢失",
        icon: "none"
      });
    }
  },
  methods: {
    // 预览图片逻辑
    previewDetailImg(urls, currentIndex) {
      const fullUrls = urls.map((url) => this.formatImg(url));
      common_vendor.index.previewImage({
        urls: fullUrls,
        current: fullUrls[currentIndex]
      });
    },
    formatImg(url) {
      return utils_config.config.getImgUrl(url);
    },
    loadPostDetail() {
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/post/detail/" + this.postId,
        success: (res) => {
          this.post = res.data;
        }
      });
    },
    loadComments() {
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/post-comment/list/" + this.postId,
        success: (res) => {
          this.commentList = res.data;
        }
      });
    },
    checkUserLike(userId) {
      common_vendor.index.request({
        url: `${utils_config.config.baseUrl}/post/like/check/${this.postId}/${userId}`,
        method: "GET",
        success: (res) => {
          this.isLiked = res.data;
        }
      });
    },
    handleLike() {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user)
        return common_vendor.index.showToast({ title: "请先登录", icon: "none" });
      common_vendor.index.request({
        url: `${utils_config.config.baseUrl}/post/like/${this.postId}/${user.id}`,
        method: "POST",
        success: (res) => {
          if (res.data) {
            this.isLiked = !this.isLiked;
            if (this.isLiked) {
              this.post.likes = (this.post.likes || 0) + 1;
            } else {
              this.post.likes = Math.max(0, this.post.likes - 1);
            }
          }
        }
      });
    },
    checkFavorite(userId) {
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/favorite/check",
        data: { userId, targetId: this.postId, type: 1 },
        success: (res) => {
          this.isFaved = res.data;
        }
      });
    },
    handleFavorite() {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user)
        return common_vendor.index.showToast({ title: "请先登录", icon: "none" });
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/favorite/toggle",
        method: "POST",
        data: { userId: user.id, targetId: this.postId, type: 1 },
        success: (res) => {
          if (res.data) {
            this.isFaved = !this.isFaved;
            common_vendor.index.showToast({ title: this.isFaved ? "收藏成功" : "取消收藏", icon: "none" });
          }
        }
      });
    },
    submitComment() {
      if (!this.myComment.trim())
        return;
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user)
        return common_vendor.index.showToast({ title: "请先登录", icon: "none" });
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/post-comment/add",
        method: "POST",
        data: { postId: this.postId, userId: user.id, content: this.myComment },
        success: () => {
          this.myComment = "";
          this.loadComments();
          common_vendor.index.showToast({ title: "评论成功" });
        }
      });
    },
    formatTime(timeStr) {
      if (!timeStr)
        return "";
      return timeStr.replace("T", " ").substring(5, 16);
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.post.id
  }, $data.post.id ? common_vendor.e({
    b: $options.formatImg($data.post.avatar) || "/static/logo.png",
    c: common_vendor.t($data.post.nickname || "游客"),
    d: common_vendor.t($options.formatTime($data.post.createTime)),
    e: common_vendor.t($data.post.content),
    f: $data.post.image
  }, $data.post.image ? {
    g: common_vendor.f($data.post.image.split(","), (src, index, i0) => {
      return {
        a: index,
        b: $options.formatImg(src),
        c: common_vendor.o(($event) => $options.previewDetailImg($data.post.image.split(","), index), index)
      };
    })
  } : {}, {
    h: common_vendor.t($data.isLiked ? "❤️" : "🤍"),
    i: common_vendor.t($data.post.likes || 0),
    j: common_vendor.o((...args) => $options.handleLike && $options.handleLike(...args)),
    k: $data.isLiked ? 1 : "",
    l: common_vendor.t($data.isFaved ? "⭐" : "☆"),
    m: common_vendor.t($data.isFaved ? "已收藏" : "收藏"),
    n: common_vendor.o((...args) => $options.handleFavorite && $options.handleFavorite(...args)),
    o: $data.isFaved ? 1 : "",
    p: common_vendor.t($data.commentList.length),
    q: $data.commentList.length === 0
  }, $data.commentList.length === 0 ? {} : {}, {
    r: common_vendor.f($data.commentList, (c, index, i0) => {
      return {
        a: $options.formatImg(c.avatar) || "/static/logo.png",
        b: common_vendor.t(c.nickname || "游客"),
        c: common_vendor.t($options.formatTime(c.createTime)),
        d: common_vendor.t(c.content),
        e: index
      };
    }),
    s: $data.myComment,
    t: common_vendor.o(($event) => $data.myComment = $event.detail.value),
    v: common_vendor.o((...args) => $options.submitComment && $options.submitComment(...args))
  }) : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../../.sourcemap/mp-weixin/pages/social/post-detail/post-detail.js.map
