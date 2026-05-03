"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_config = require("../../utils/config.js");
const _sfc_main = {
  data() {
    return {
      isLogin: false,
      showLoginModal: false,
      tempAvatarUrl: "",
      tempNickname: "",
      userInfo: {
        nickname: "",
        avatar: ""
      }
    };
  },
  onShow() {
    this.checkLoginStatus();
  },
  methods: {
    formatImg(url) {
      return utils_config.config.getImgUrl(url);
    },
    checkLoginStatus() {
      const user = common_vendor.index.getStorageSync("userInfo");
      if (user && user.id) {
        this.isLogin = true;
        this.userInfo = user;
      } else {
        this.isLogin = false;
      }
    },
    navigateTo(url) {
      if (!this.isLogin) {
        common_vendor.index.showToast({ title: "请先登录", icon: "none" });
        return;
      }
      common_vendor.index.navigateTo({ url });
    },
    handleLogin() {
      if (this.isLogin)
        return;
      this.showLoginModal = true;
    },
    onChooseAvatar(e) {
      this.tempAvatarUrl = e.detail.avatarUrl;
    },
    onNicknameBlur(e) {
      this.tempNickname = e.detail.value;
    },
    onNicknameInput(e) {
      this.tempNickname = e.detail.value;
    },
    handleFinalLogin() {
      if (!this.tempAvatarUrl) {
        return common_vendor.index.showToast({ title: "请选择头像", icon: "none" });
      }
      if (!this.tempNickname.trim()) {
        return common_vendor.index.showToast({ title: "请输入昵称", icon: "none" });
      }
      common_vendor.index.showLoading({ title: "登录中...", mask: true });
      if (this.tempAvatarUrl.startsWith("http://tmp/") || this.tempAvatarUrl.startsWith("wxfile://")) {
        common_vendor.index.uploadFile({
          url: utils_config.config.baseUrl + "/upload/image",
          filePath: this.tempAvatarUrl,
          name: "file",
          success: (uploadRes) => {
            const finalAvatar = uploadRes.data.replace(/\"/g, "");
            this.loginByBackend(this.tempNickname, finalAvatar);
          },
          fail: () => {
            common_vendor.index.hideLoading();
            common_vendor.index.showToast({ title: "头像上传失败", icon: "none" });
          }
        });
      } else {
        this.loginByBackend(this.tempNickname, this.tempAvatarUrl);
      }
    },
    loginByBackend(nickname, avatar) {
      const existingUser = common_vendor.index.getStorageSync("userInfo");
      common_vendor.index.login({
        provider: "weixin",
        success: (loginRes) => {
          common_vendor.index.request({
            url: utils_config.config.baseUrl + "/user/login",
            method: "POST",
            data: {
              code: loginRes.code,
              nickname,
              avatar,
              userId: existingUser && existingUser.id ? existingUser.id : ""
            },
            success: (res) => {
              if (res.statusCode === 200 && res.data && res.data.id) {
                common_vendor.index.setStorageSync("userInfo", res.data);
                this.isLogin = true;
                this.userInfo = res.data;
                this.showLoginModal = false;
                this.tempAvatarUrl = "";
                this.tempNickname = "";
                common_vendor.index.showToast({ title: "登录成功", icon: "success" });
              }
            },
            fail: () => {
              common_vendor.index.showToast({ title: "服务器连接失败", icon: "none" });
            },
            complete: () => {
              common_vendor.index.hideLoading();
            }
          });
        },
        fail: () => {
          common_vendor.index.hideLoading();
          common_vendor.index.showToast({ title: "微信登录失败", icon: "none" });
        }
      });
    },
    logout() {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定退出并清除缓存吗？",
        success: (res) => {
          if (res.confirm) {
            common_vendor.index.removeStorageSync("userInfo");
            this.isLogin = false;
            this.userInfo = { nickname: "", avatar: "" };
          }
        }
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $options.formatImg($data.isLogin ? $data.userInfo.avatar : "/static/logo.png"),
    b: common_vendor.t($data.isLogin ? $data.userInfo.nickname : "点击授权登录"),
    c: common_vendor.t($data.isLogin ? "已通过微信授权" : "体验更多功能请先登录"),
    d: common_vendor.o((...args) => $options.handleLogin && $options.handleLogin(...args)),
    e: $data.showLoginModal
  }, $data.showLoginModal ? {
    f: common_vendor.o(($event) => $data.showLoginModal = false),
    g: $data.tempAvatarUrl || "/static/logo.png",
    h: common_vendor.o((...args) => $options.onChooseAvatar && $options.onChooseAvatar(...args)),
    i: common_vendor.o((...args) => $options.onNicknameBlur && $options.onNicknameBlur(...args)),
    j: common_vendor.o([($event) => $data.tempNickname = $event.detail.value, (...args) => $options.onNicknameInput && $options.onNicknameInput(...args)]),
    k: $data.tempNickname,
    l: common_vendor.o(($event) => $data.showLoginModal = false),
    m: common_vendor.o((...args) => $options.handleFinalLogin && $options.handleFinalLogin(...args))
  } : {}, {
    n: common_vendor.o(($event) => $options.navigateTo("/pages/my-posts/my-posts")),
    o: common_vendor.o(($event) => $options.navigateTo("/pages/my-bookings/my-bookings")),
    p: common_vendor.o(($event) => $options.navigateTo("/pages/my-favorites/my-favorites")),
    q: common_vendor.o(($event) => $options.navigateTo("/pages/my-comment/my-comment")),
    r: $data.isLogin
  }, $data.isLogin ? {
    s: common_vendor.o((...args) => $options.logout && $options.logout(...args))
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/me/me.js.map
