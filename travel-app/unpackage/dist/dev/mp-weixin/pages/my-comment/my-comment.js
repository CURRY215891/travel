"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  __name: "my-comment",
  setup(__props) {
    const tabIndex = common_vendor.ref(1);
    const postComments = common_vendor.ref([]);
    const attrComments = common_vendor.ref([]);
    const foodComments = common_vendor.ref([]);
    const roomComments = common_vendor.ref([]);
    const formatTime = (time) => {
      if (!time)
        return "";
      return time.replace("T", " ").substring(0, 16);
    };
    const formatImg = (url) => {
      return utils_config.config.getImgUrl(url);
    };
    const switchTab = (index) => {
      tabIndex.value = index;
      loadList();
    };
    const loadList = () => {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user)
        return;
      if (tabIndex.value === 1) {
        common_vendor.index.request({
          url: utils_config.config.baseUrl + "/post-comment/myList",
          data: { userId: user.id },
          success: (res) => {
            postComments.value = res.data;
          }
        });
      } else if (tabIndex.value === 2) {
        common_vendor.index.request({
          url: utils_config.config.baseUrl + "/comment/user/" + user.id,
          data: { type: 1 },
          success: (res) => {
            attrComments.value = res.data;
          }
        });
      } else if (tabIndex.value === 3) {
        common_vendor.index.request({
          url: utils_config.config.baseUrl + "/comment/user/" + user.id,
          data: { type: 2 },
          success: (res) => {
            foodComments.value = res.data;
          }
        });
      } else if (tabIndex.value === 4) {
        common_vendor.index.request({
          url: utils_config.config.baseUrl + "/comment/user/" + user.id,
          data: { type: 3 },
          success: (res) => {
            roomComments.value = res.data;
          }
        });
      }
    };
    common_vendor.onShow(() => {
      loadList();
    });
    const handleDelete = (id, type) => {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要删除这条评论吗？",
        success: (res) => {
          if (res.confirm) {
            const url = type === 1 ? `${utils_config.config.baseUrl}/post-comment/${id}` : `${utils_config.config.baseUrl}/comment/${id}`;
            common_vendor.index.request({
              url,
              method: "DELETE",
              success: (res2) => {
                if (res2.data) {
                  common_vendor.index.showToast({ title: "删除成功" });
                  loadList();
                }
              }
            });
          }
        }
      });
    };
    const goPostDetail = (id) => {
      if (!id)
        return common_vendor.index.showToast({ title: "数据异常", icon: "none" });
      common_vendor.index.navigateTo({
        url: `/pages/social/post-detail/post-detail?id=${id}`
      });
    };
    const goAttrDetail = (item) => {
      if (!item || !item.attrId)
        return common_vendor.index.showToast({ title: "数据异常", icon: "none" });
      let url = `/pages/detail/detail?id=${item.attrId}`;
      if (item.type === 2) {
        url = `/pages/food-detail/food-detail?id=${item.attrId}`;
      } else if (item.type === 3) {
        url = `/pages/room-detail/room-detail?id=${item.attrId}`;
      }
      common_vendor.index.navigateTo({ url });
    };
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: tabIndex.value === 1 ? 1 : "",
        b: common_vendor.o(($event) => switchTab(1)),
        c: tabIndex.value === 2 ? 1 : "",
        d: common_vendor.o(($event) => switchTab(2)),
        e: tabIndex.value === 3 ? 1 : "",
        f: common_vendor.o(($event) => switchTab(3)),
        g: tabIndex.value === 4 ? 1 : "",
        h: common_vendor.o(($event) => switchTab(4)),
        i: tabIndex.value === 1
      }, tabIndex.value === 1 ? common_vendor.e({
        j: common_vendor.f(postComments.value, (item, k0, i0) => {
          return {
            a: common_vendor.t(item.content),
            b: common_vendor.o(($event) => handleDelete(item.id, 1), item.id),
            c: common_vendor.t(item.postContent),
            d: common_vendor.t(item.createTime),
            e: item.id,
            f: common_vendor.o(($event) => goPostDetail(item.post_id), item.id)
          };
        }),
        k: postComments.value.length === 0
      }, postComments.value.length === 0 ? {} : {}) : {}, {
        l: tabIndex.value === 2
      }, tabIndex.value === 2 ? common_vendor.e({
        m: common_vendor.f(attrComments.value, (item, k0, i0) => {
          return common_vendor.e({
            a: item.parentId > 0
          }, item.parentId > 0 ? {} : {}, {
            b: item.star > 0
          }, item.star > 0 ? {
            c: common_vendor.f(5, (s, k1, i1) => {
              return {
                a: s,
                b: s <= item.star ? 1 : ""
              };
            })
          } : {}, {
            d: common_vendor.t(item.content),
            e: common_vendor.o(($event) => handleDelete(item.id, 2), item.id),
            f: formatImg(item.attrImage),
            g: common_vendor.t(item.attrName),
            h: common_vendor.t(formatTime(item.createTime)),
            i: item.id,
            j: common_vendor.o(($event) => goAttrDetail(item), item.id)
          });
        }),
        n: attrComments.value.length === 0
      }, attrComments.value.length === 0 ? {} : {}) : {}, {
        o: tabIndex.value === 3
      }, tabIndex.value === 3 ? common_vendor.e({
        p: common_vendor.f(foodComments.value, (item, k0, i0) => {
          return common_vendor.e({
            a: item.parentId > 0
          }, item.parentId > 0 ? {} : {}, {
            b: item.star > 0
          }, item.star > 0 ? {
            c: common_vendor.f(5, (s, k1, i1) => {
              return {
                a: s,
                b: s <= item.star ? 1 : ""
              };
            })
          } : {}, {
            d: common_vendor.t(item.content),
            e: common_vendor.o(($event) => handleDelete(item.id, 3), item.id),
            f: formatImg(item.attrImage),
            g: common_vendor.t(item.attrName),
            h: common_vendor.t(formatTime(item.createTime)),
            i: item.id,
            j: common_vendor.o(($event) => goAttrDetail(item), item.id)
          });
        }),
        q: foodComments.value.length === 0
      }, foodComments.value.length === 0 ? {} : {}) : {}, {
        r: tabIndex.value === 4
      }, tabIndex.value === 4 ? common_vendor.e({
        s: common_vendor.f(roomComments.value, (item, k0, i0) => {
          return common_vendor.e({
            a: item.parentId > 0
          }, item.parentId > 0 ? {} : {}, {
            b: item.star > 0
          }, item.star > 0 ? {
            c: common_vendor.f(5, (s, k1, i1) => {
              return {
                a: s,
                b: s <= item.star ? 1 : ""
              };
            })
          } : {}, {
            d: common_vendor.t(item.content),
            e: common_vendor.o(($event) => handleDelete(item.id, 4), item.id),
            f: formatImg(item.attrImage),
            g: common_vendor.t(item.attrName),
            h: common_vendor.t(formatTime(item.createTime)),
            i: item.id,
            j: common_vendor.o(($event) => goAttrDetail(item), item.id)
          });
        }),
        t: roomComments.value.length === 0
      }, roomComments.value.length === 0 ? {} : {}) : {});
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/my-comment/my-comment.js.map
