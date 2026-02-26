"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      content: "",
      serverImages: [],
      config: utils_config.config
    };
  },
  methods: {
    // 预览图片
    previewImg(index) {
      const urls = this.serverImages.map((img) => utils_config.config.baseUrl + img);
      common_vendor.index.previewImage({
        current: urls[index],
        urls
      });
    },
    chooseAndUploadImage() {
      common_vendor.index.chooseImage({
        count: 5 - this.serverImages.length,
        sizeType: ["compressed"],
        success: (res) => {
          res.tempFilePaths.forEach((path) => {
            this.uploadSingleFile(path);
          });
        }
      });
    },
    uploadSingleFile(filePath) {
      common_vendor.index.showLoading({ title: "正在上传...", mask: true });
      common_vendor.index.uploadFile({
        url: utils_config.config.baseUrl + "/upload/image",
        filePath,
        name: "file",
        success: (res) => {
          const url = res.data.replace(/\"/g, "");
          this.serverImages.push(url);
        },
        fail: () => {
          common_vendor.index.showToast({ title: "上传失败", icon: "none" });
        },
        complete: () => {
          common_vendor.index.hideLoading();
        }
      });
    },
    removeImg(index) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要删除这张图片吗？",
        success: (res) => {
          if (res.confirm) {
            this.serverImages.splice(index, 1);
          }
        }
      });
    },
    submitPost() {
      if (!this.content && this.serverImages.length === 0) {
        return common_vendor.index.showToast({ title: "写点什么吧", icon: "none" });
      }
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user || !user.id) {
        return common_vendor.index.showToast({ title: "请先登录", icon: "none" });
      }
      common_vendor.index.showLoading({ title: "正在发布...", mask: true });
      const imageStr = this.serverImages.join(",");
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/post/add",
        method: "POST",
        data: {
          userId: user.id,
          content: this.content,
          image: imageStr,
          likes: 0
        },
        success: (res) => {
          common_vendor.index.hideLoading();
          common_vendor.index.showToast({ title: "发布成功", icon: "success" });
          setTimeout(() => {
            common_vendor.index.navigateBack();
          }, 1500);
        },
        fail: (err) => {
          common_vendor.index.hideLoading();
          common_vendor.index.showToast({ title: "发布失败", icon: "none" });
        }
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.content,
    b: common_vendor.o(($event) => $data.content = $event.detail.value),
    c: common_vendor.t($data.content.length),
    d: common_vendor.f($data.serverImages, (img, index, i0) => {
      return {
        a: $data.config.baseUrl + img,
        b: common_vendor.o(($event) => $options.previewImg(index), index),
        c: common_vendor.o(($event) => $options.removeImg(index), index),
        d: index
      };
    }),
    e: $data.serverImages.length < 5
  }, $data.serverImages.length < 5 ? {
    f: common_vendor.o((...args) => $options.chooseAndUploadImage && $options.chooseAndUploadImage(...args))
  } : {}, {
    g: !$data.content && $data.serverImages.length === 0,
    h: common_vendor.o((...args) => $options.submitPost && $options.submitPost(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/post-add/post-add.js.map
