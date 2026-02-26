"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      info: {},
      imgList: [],
      isFav: false,
      commentList: [],
      structuredComments: [],
      commentText: "",
      userStar: 5,
      hasRated: false,
      expandedMap: {},
      distance: ""
    };
  },
  onLoad(options) {
    this.getDetail(options.id);
    this.checkFav(options.id);
    this.getComments(options.id);
    this.checkRatedStatus(options.id);
  },
  methods: {
    formatImg(url) {
      return utils_config.config.getImgUrl(url);
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
    checkRatedStatus(id) {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user)
        return;
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/comment/check",
        data: { userId: user.id, attrId: id, type: 2 },
        success: (res) => {
          this.hasRated = res.data === true;
        }
      });
    },
    formatTime(time) {
      if (!time)
        return "";
      return time.replace("T", " ").substring(0, 16);
    },
    isMyComment(userId) {
      const user = common_vendor.index.getStorageSync("userInfo");
      return user && user.id === userId;
    },
    getComments(id) {
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/comment/attr/" + id + "?type=2",
        success: (res) => {
          if (res.statusCode === 200 && Array.isArray(res.data)) {
            this.commentList = res.data;
            this.buildStructuredComments();
          } else {
            this.commentList = [];
            this.structuredComments = [];
          }
        }
      });
    },
    buildStructuredComments() {
      const list = this.commentList;
      if (!Array.isArray(list))
        return;
      const parents = list.filter((c) => c.parentId === 0);
      const children = list.filter((c) => c.parentId !== 0);
      this.structuredComments = parents.map((p) => ({
        ...p,
        replies: children.filter((c) => c.parentId === p.id)
      }));
    },
    toggleExpand(parentId) {
      this.$set(this.expandedMap, parentId, !this.expandedMap[parentId]);
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
    getVisibleReplies(replies, parentId) {
      if (this.expandedMap[parentId])
        return replies;
      return replies.slice(0, 2);
    },
    submitComment() {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user) {
        common_vendor.index.showToast({ title: "请先登录", icon: "none" });
        return;
      }
      if (!this.commentText.trim()) {
        common_vendor.index.showToast({ title: "请输入评论内容", icon: "none" });
        return;
      }
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/comment/add",
        method: "POST",
        data: {
          userId: user.id,
          attrId: this.info.id,
          content: this.commentText,
          star: this.userStar,
          type: 2
        },
        success: (res) => {
          common_vendor.index.showToast({ title: "评论成功" });
          this.commentText = "";
          this.getComments(this.info.id);
          this.checkRatedStatus(this.info.id);
        }
      });
    },
    handleDeleteComment(commentId) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要删除这条评论吗？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.request({
              url: utils_config.config.baseUrl + "/comment/" + commentId,
              method: "DELETE",
              success: (res2) => {
                if (res2.data) {
                  common_vendor.index.showToast({ title: "删除成功" });
                  this.getComments(this.info.id);
                }
              }
            });
          }
        }
      });
    },
    checkFav(id) {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user)
        return;
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/favorite/check",
        data: { userId: user.id, targetId: id, type: 4 },
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
          type: 4
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
        url: utils_config.config.baseUrl + "/food/" + id,
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
    f: common_vendor.t($data.info.avgPrice || $data.info.price || 0),
    g: $data.info.openTime
  }, $data.info.openTime ? {
    h: common_vendor.t($data.info.openTime)
  } : {}, {
    i: common_vendor.t($data.info.address || "查看地图"),
    j: $data.distance
  }, $data.distance ? {
    k: common_vendor.t($data.distance)
  } : {}, {
    l: common_vendor.o((...args) => $options.openMap && $options.openMap(...args)),
    m: common_vendor.t($data.info.description),
    n: common_vendor.t($data.commentList.length),
    o: common_vendor.f($data.structuredComments, (item, index, i0) => {
      return common_vendor.e({
        a: $options.formatImg(item.avatar) || "/static/default-avatar.png",
        b: common_vendor.t(item.nickname || "游客"),
        c: common_vendor.t($options.formatTime(item.createTime)),
        d: item.star > 0
      }, item.star > 0 ? {
        e: common_vendor.f(5, (s, k1, i1) => {
          return {
            a: s,
            b: s <= item.star ? 1 : ""
          };
        })
      } : {}, {
        f: common_vendor.t(item.content),
        g: $options.isMyComment(item.userId)
      }, $options.isMyComment(item.userId) ? {
        h: common_vendor.o(($event) => $options.handleDeleteComment(item.id), index)
      } : {}, {
        i: item.replies && item.replies.length > 0
      }, item.replies && item.replies.length > 0 ? common_vendor.e({
        j: common_vendor.f($options.getVisibleReplies(item.replies, item.id), (reply, rIdx, i1) => {
          return common_vendor.e({
            a: common_vendor.t($options.formatTime(reply.createTime)),
            b: common_vendor.t(reply.content),
            c: $options.isMyComment(reply.userId)
          }, $options.isMyComment(reply.userId) ? {
            d: common_vendor.o(($event) => $options.handleDeleteComment(reply.id), rIdx)
          } : {}, {
            e: rIdx
          });
        }),
        k: item.replies.length > 2
      }, item.replies.length > 2 ? {
        l: common_vendor.t($data.expandedMap[item.id] ? "收起评论" : "展开全部 " + item.replies.length + " 条追评"),
        m: common_vendor.o(($event) => $options.toggleExpand(item.id), index)
      } : {}) : {}, {
        n: index
      });
    }),
    p: $data.structuredComments.length === 0
  }, $data.structuredComments.length === 0 ? {} : {}, {
    q: $data.commentText,
    r: common_vendor.o(($event) => $data.commentText = $event.detail.value),
    s: !$data.hasRated
  }, !$data.hasRated ? {
    t: common_vendor.f(5, (s, k0, i0) => {
      return {
        a: s,
        b: s <= $data.userStar ? 1 : "",
        c: common_vendor.o(($event) => $data.userStar = s, s)
      };
    })
  } : {}, {
    v: common_vendor.o((...args) => $options.submitComment && $options.submitComment(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/food-detail/food-detail.js.map
