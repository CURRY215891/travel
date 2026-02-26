"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  __name: "my-posts",
  setup(__props) {
    const list = common_vendor.ref([]);
    const goDetail = (id) => {
      common_vendor.index.navigateTo({ url: `/pages/social/post-detail/post-detail?id=${id}` });
    };
    const handleDelete = (id) => {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要删除这条动态吗？删除后评论和收藏也将一并清除。",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.request({
              url: `${utils_config.config.baseUrl}/post/${id}`,
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
    const loadList = () => {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (user) {
        common_vendor.index.request({
          url: utils_config.config.baseUrl + "/post/myList",
          data: { userId: user.id },
          success: (res) => {
            list.value = res.data;
          }
        });
      }
    };
    common_vendor.onShow(() => {
      loadList();
    });
    return (_ctx, _cache) => {
      return common_vendor.e({
        a: common_vendor.f(list.value, (item, k0, i0) => {
          return {
            a: common_vendor.t(item.content),
            b: common_vendor.t(item.createTime),
            c: common_vendor.t(item.likes || 0),
            d: common_vendor.o(($event) => handleDelete(item.id), item.id),
            e: item.id,
            f: common_vendor.o(($event) => goDetail(item.id), item.id)
          };
        }),
        b: list.value.length === 0
      }, list.value.length === 0 ? {} : {});
    };
  }
};
wx.createPage(_sfc_main);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/my-posts/my-posts.js.map
