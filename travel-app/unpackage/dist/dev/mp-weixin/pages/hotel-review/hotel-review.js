"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      roomId: "",
      hotelName: "",
      roomName: "",
      star: 5,
      hygieneScore: 5,
      environmentScore: 5,
      serviceScore: 5,
      facilityScore: 5,
      content: "",
      serverImages: [],
      config: utils_config.config
    };
  },
  onLoad(options) {
    this.roomId = options.roomId;
    this.hotelName = options.hotelName || "酒店";
    this.roomName = options.roomName || "房间";
  },
  methods: {
    getImgUrl(url) {
      return utils_config.config.getImgUrl(url);
    },
    previewImg(index) {
      const urls = this.serverImages.map((img) => this.getImgUrl(img));
      common_vendor.index.previewImage({
        current: urls[index],
        urls
      });
    },
    chooseAndUploadImage() {
      common_vendor.index.chooseImage({
        count: 9 - this.serverImages.length,
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
          if (url) {
            this.serverImages.push(url);
          }
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
      this.serverImages.splice(index, 1);
    },
    submitComment() {
      if (!this.content.trim()) {
        return common_vendor.index.showToast({ title: "请输入评价内容", icon: "none" });
      }
      const user = common_vendor.index.getStorageSync("userInfo");
      if (!user) {
        return common_vendor.index.showToast({ title: "请先登录", icon: "none" });
      }
      common_vendor.index.showLoading({ title: "正在发布...", mask: true });
      common_vendor.index.request({
        url: utils_config.config.baseUrl + "/comment/add",
        method: "POST",
        data: {
          userId: user.id,
          attrId: this.roomId,
          type: 3,
          // 酒店房间评论
          content: this.content,
          star: this.star,
          hygieneScore: this.hygieneScore,
          environmentScore: this.environmentScore,
          serviceScore: this.serviceScore,
          facilityScore: this.facilityScore,
          images: this.serverImages.join(",")
        },
        success: (res) => {
          common_vendor.index.hideLoading();
          if (res.data) {
            common_vendor.index.showToast({ title: "评价成功" });
            setTimeout(() => {
              common_vendor.index.navigateBack();
            }, 1500);
          } else {
            common_vendor.index.showToast({ title: "发布失败", icon: "none" });
          }
        },
        fail: () => {
          common_vendor.index.hideLoading();
          common_vendor.index.showToast({ title: "网络请求失败", icon: "none" });
        }
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.t($data.hotelName),
    b: common_vendor.t($data.roomName),
    c: common_vendor.f(5, (s, k0, i0) => {
      return {
        a: s,
        b: s <= $data.star ? 1 : "",
        c: common_vendor.o(($event) => $data.star = s, s)
      };
    }),
    d: common_vendor.f(5, (s, k0, i0) => {
      return {
        a: s,
        b: s <= $data.hygieneScore ? 1 : "",
        c: common_vendor.o(($event) => $data.hygieneScore = s, s)
      };
    }),
    e: common_vendor.f(5, (s, k0, i0) => {
      return {
        a: s,
        b: s <= $data.environmentScore ? 1 : "",
        c: common_vendor.o(($event) => $data.environmentScore = s, s)
      };
    }),
    f: common_vendor.f(5, (s, k0, i0) => {
      return {
        a: s,
        b: s <= $data.serviceScore ? 1 : "",
        c: common_vendor.o(($event) => $data.serviceScore = s, s)
      };
    }),
    g: common_vendor.f(5, (s, k0, i0) => {
      return {
        a: s,
        b: s <= $data.facilityScore ? 1 : "",
        c: common_vendor.o(($event) => $data.facilityScore = s, s)
      };
    }),
    h: $data.content,
    i: common_vendor.o(($event) => $data.content = $event.detail.value),
    j: common_vendor.t($data.content.length),
    k: common_vendor.f($data.serverImages, (img, index, i0) => {
      return {
        a: $options.getImgUrl(img),
        b: common_vendor.o(($event) => $options.previewImg(index), index),
        c: common_vendor.o(($event) => $options.removeImg(index), index),
        d: index
      };
    }),
    l: $data.serverImages.length < 9
  }, $data.serverImages.length < 9 ? {
    m: common_vendor.o((...args) => $options.chooseAndUploadImage && $options.chooseAndUploadImage(...args))
  } : {}, {
    n: common_vendor.o((...args) => $options.submitComment && $options.submitComment(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/hotel-review/hotel-review.js.map
