const config = {
  // 后端 API 基础地址
  // baseUrl: 'http://10.148.32.54:8080',
  baseUrl: 'http://192.168.137.1:8080',
  // 统一图片路径处理
  getImgUrl(url) {
    if (!url) return '';
    if (url.startsWith('http')) return url;
    if (url.startsWith('data:image')) return url;
    
    // 1. 处理系统预置图片 (assets)
    if (url.includes('assets/')) {
      const path = url.startsWith('/') ? url : '/' + url;
      return config.baseUrl + path;
    }

    // 2. 处理带 static 前缀的上传路径 (如 static/uploads/xxx.jpg)
    if (url.includes('uploads/') && (url.startsWith('/static') || url.startsWith('static/'))) {
      const path = url.replace(/^\/?static/, '');
      const finalPath = path.startsWith('/') ? path : '/' + path;
      return config.baseUrl + finalPath;
    }
    
    // 2. 处理标准上传路径 (如 /uploads/xxx.jpg)
    if (url.includes('uploads/')) {
      const path = url.startsWith('/') ? url : '/' + url;
      return config.baseUrl + path;
    }
    
    // 3. 处理纯静态资源 (不含 uploads)
    if (url.startsWith('/static') || url.startsWith('static/')) {
      return url.startsWith('/') ? url : '/' + url;
    }
    
    // 4. 处理纯文件名 (如 abc.jpg)
    if (url && !url.includes('/') && !url.includes('\\')) {
      if (url.includes('-') || url.length > 20) {
        return config.baseUrl + '/uploads/' + url;
      }
      return '/static/static-iamge/' + url;
    }
    
    // 5. 默认作为内置静态图片处理
    const path = url.startsWith('/') ? url : '/' + url;
    return '/static/static-iamge' + path;
  }
};

export default config;
