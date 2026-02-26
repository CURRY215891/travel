"use strict";
const config = {
  // 后端 API 基础地址
  baseUrl: "http://192.168.5.10:8080",
  // 统一图片路径处理
  getImgUrl(url) {
    if (!url)
      return "";
    if (url.startsWith("http"))
      return url;
    if (url.startsWith("data:image"))
      return url;
    if (url.includes("assets/")) {
      const path2 = url.startsWith("/") ? url : "/" + url;
      return config.baseUrl + path2;
    }
    if (url.includes("uploads/") && (url.startsWith("/static") || url.startsWith("static/"))) {
      const path2 = url.replace(/^\/?static/, "");
      const finalPath = path2.startsWith("/") ? path2 : "/" + path2;
      return config.baseUrl + finalPath;
    }
    if (url.includes("uploads/")) {
      const path2 = url.startsWith("/") ? url : "/" + url;
      return config.baseUrl + path2;
    }
    if (url.startsWith("/static") || url.startsWith("static/")) {
      return url.startsWith("/") ? url : "/" + url;
    }
    if (url && !url.includes("/") && !url.includes("\\")) {
      if (url.includes("-") || url.length > 20) {
        return config.baseUrl + "/uploads/" + url;
      }
      return "/static/static-iamge/" + url;
    }
    const path = url.startsWith("/") ? url : "/" + url;
    return "/static/static-iamge" + path;
  }
};
exports.config = config;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/config.js.map
