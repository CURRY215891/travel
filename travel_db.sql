/*
 Navicat Premium Dump SQL

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80042 (8.0.42)
 Source Host           : localhost:3306
 Source Schema         : travel_db

 Target Server Type    : MySQL
 Target Server Version : 80042 (8.0.42)
 File Encoding         : 65001

 Date: 02/04/2026 22:44:17
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for admin
-- ----------------------------
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录账号',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '登录密码',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '显示昵称',
  `role` int NOT NULL DEFAULT 2 COMMENT '角色: 1-超级管理员, 2-酒店管理员',
  `hotel_id` int NULL DEFAULT NULL COMMENT '所属酒店ID (仅role=2时有效)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '后台管理用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of admin
-- ----------------------------
INSERT INTO `admin` VALUES (1, 'admin', '123456', '系统总管', 1, NULL, '2026-04-01 20:14:18');
INSERT INTO `admin` VALUES (2, 'hotel1', '123456', '王府井希尔顿酒店管理员1', 2, 1, '2026-04-01 20:49:14');

-- ----------------------------
-- Table structure for attraction
-- ----------------------------
DROP TABLE IF EXISTS `attraction`;
CREATE TABLE `attraction`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_id` int NULL DEFAULT NULL COMMENT '所属分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '景点名称',
  `mainImage` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详情页轮播图，多张用逗号隔开',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '门票价格',
  `openTime` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `latitude` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '纬度(用于地图导航)',
  `longitude` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经度(用于地图导航)',
  `viewCount` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attraction
-- ----------------------------
INSERT INTO `attraction` VALUES (1, 1, '邯郸丛台公园', '/assets/ct1.jpg', NULL, '武灵丛台是邯郸的象征，始建于赵武灵王时期，是古赵文化的重要遗址，园内亭台楼阁，环境优美。', '河北省邯郸市丛台区中华大街北段', 0.00, '08:00-18:00', '36.6167', '114.4897', 1250);
INSERT INTO `attraction` VALUES (2, 1, '广府古城', 'https://th.bing.com/th/id/R.a7ca6494974dc1df9964a4eb7b89430b?rik=EUUzgU%2ff1SEaTw&riu=http%3a%2f%2fimg.pconline.com.cn%2fimages%2fupload%2fupc%2ftx%2fphotoblog%2f1012%2f12%2fc8%2f6196155_6196155_1292152467218.jpg&ehk=GO9Mnx42hKbJmVV%2f%2fo7UPUMsf3rFWj2EceesWkFXewA%3d&risl=&pid=ImgRaw&r=0', 'https://th.bing.com/th/id/R.a7ca6494974dc1df9964a4eb7b89430b?rik=EUUzgU%2ff1SEaTw&riu=http%3a%2f%2fimg.pconline.com.cn%2fimages%2fupload%2fupc%2ftx%2fphotoblog%2f1012%2f12%2fc8%2f6196155_6196155_1292152467218.jpg&ehk=GO9Mnx42hKbJmVV%2f%2fo7UPUMsf3rFWj2EceesWkFXewA%3d&risl=&pid=ImgRaw&r=0,https://youimg1.c-ctrip.com/target/10040c000000632vi9EC3.jpg', '中国历史文化名镇，杨氏、武氏太极拳的发源地，是一座“水城、古城、湿地”三位一体的古城。', '河北省邯郸市永年区广府镇', 70.00, '08:30-17:30', '36.7022', '114.7231', 980);
INSERT INTO `attraction` VALUES (3, 1, '响堂山石窟', 'https://th.bing.com/th/id/R.cd51f2a068006ec5618b4c7059c78ed7?rik=x%2bODScrIBY3KNA&riu=http%3a%2f%2fimg.pconline.com.cn%2fimages%2fphotoblog%2f7%2f5%2f9%2f6%2f7596358%2f20099%2f26%2f1253974510452_mthumb.jpg&ehk=eVrnVTT0YACSJ7cBw%2fgYxw5yp0JgY2xhIf%2bemp1QSkw%3d&risl=&pid=ImgRaw&r=0', NULL, '国家重点文物保护单位，始凿于北齐年间，石窟雕刻精美，是研究佛教美术和建筑的重要实物。', '河北省邯郸市峰峰矿区和村镇', 80.00, '09:00-17:00', '36.4528', '114.1614', 650);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, '历史名胜');
INSERT INTO `category` VALUES (2, '酒店住宿');
INSERT INTO `category` VALUES (3, '自然风光');
INSERT INTO `category` VALUES (4, '美食品尝');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '评论人ID',
  `attr_id` int NOT NULL COMMENT '景点ID',
  `type` int NULL DEFAULT 1 COMMENT '1: 景点, 2: 美食, 3: 酒店房间',
  `source` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'app' COMMENT '评论来源',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论文字',
  `star` int NULL DEFAULT 5 COMMENT '评分1-5星',
  `hygiene_score` int NULL DEFAULT 5 COMMENT '房间卫生评分',
  `environment_score` int NULL DEFAULT 5 COMMENT '周边环境评分',
  `service_score` int NULL DEFAULT 5 COMMENT '酒店服务评分',
  `facility_score` int NULL DEFAULT 5 COMMENT '设备设施评分',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评论图片，多张用逗号隔开',
  `parent_id` int NULL DEFAULT 0 COMMENT '父评论ID，0表示主评，非0表示追评',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 22 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (3, 1, 2, 1, 'app', '真棒都来玩吧', 5, 5, 5, 5, 5, NULL, 0, NULL);
INSERT INTO `comment` VALUES (5, 1, 2, 1, 'app', '不好玩别来了', 0, 5, 5, 5, 5, NULL, 3, NULL);
INSERT INTO `comment` VALUES (6, 1, 2, 1, 'app', '真的特别垃圾', 0, 5, 5, 5, 5, NULL, 3, NULL);
INSERT INTO `comment` VALUES (7, 1, 2, 1, 'app', '你觉得呢', 0, 5, 5, 5, 5, NULL, 3, NULL);
INSERT INTO `comment` VALUES (8, 1, 1, 1, 'app', '太棒了', 5, 5, 5, 5, 5, NULL, 0, NULL);
INSERT INTO `comment` VALUES (9, 1, 1, 1, 'app', '棒个蛋', 0, 5, 5, 5, 5, NULL, 8, NULL);
INSERT INTO `comment` VALUES (10, 1, 1, 1, 'app', '都别去', 0, 5, 5, 5, 5, NULL, 8, NULL);
INSERT INTO `comment` VALUES (11, 1, 1, 1, 'app', '别来', 0, 5, 5, 5, 5, NULL, 8, NULL);
INSERT INTO `comment` VALUES (12, 1, 3, 1, 'app', '666', 5, 5, 5, 5, 5, NULL, 0, NULL);
INSERT INTO `comment` VALUES (13, 2, 2, 1, 'app', '不好', 5, 5, 5, 5, 5, NULL, 0, NULL);
INSERT INTO `comment` VALUES (14, 2, 1, 2, 'app', '很棒都来吃', 5, 5, 5, 5, 5, NULL, 0, NULL);
INSERT INTO `comment` VALUES (15, 2, 2, 2, 'app', '很棒哦', 5, 5, 5, 5, 5, NULL, 0, NULL);
INSERT INTO `comment` VALUES (19, 2, 1, 3, 'app', '很棒', 5, 5, 5, 5, 5, '/uploads/70f96e13-428a-4a30-aeaf-53ff564e4e19.bmp', 0, '2026-01-28 21:47:42');
INSERT INTO `comment` VALUES (20, 2, 2, 3, 'app', '很棒', 5, 5, 5, 5, 5, '/uploads/0fe6ae96-8ea7-4337-8349-064ae9dc8b32.bmp', 0, '2026-01-28 21:54:26');
INSERT INTO `comment` VALUES (21, 2, 2, 1, 'app', '很不好', 0, 5, 5, 5, 5, NULL, 13, '2026-01-28 22:05:42');

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `attr_id` int NOT NULL COMMENT '景点ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------

-- ----------------------------
-- Table structure for food
-- ----------------------------
DROP TABLE IF EXISTS `food`;
CREATE TABLE `food`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '餐厅名称',
  `main_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `avg_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '人均消费',
  `business_hours` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '营业时间',
  `latitude` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '纬度',
  `longitude` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经度',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of food
-- ----------------------------
INSERT INTO `food` VALUES (1, '全聚德烤鸭', 'https://images.unsplash.com/photo-1525755662778-989d0524087e?w=800', NULL, '百年老字号，北京特色烤鸭。', '北京市东城区前门大街30号', 150.00, '11:00-21:00', '39.8973', '116.3971');
INSERT INTO `food` VALUES (2, '老上海生煎包', 'https://images.unsplash.com/photo-1541696432-82c6da8ce7bf?w=800', NULL, '底酥肉鲜，皮薄汁多，地道沪上风味。', '上海市黄浦区南京东路288号', 30.00, '07:00-20:00', '32.034567', '118.792345');

-- ----------------------------
-- Table structure for hotel
-- ----------------------------
DROP TABLE IF EXISTS `hotel`;
CREATE TABLE `hotel`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '酒店名称',
  `main_image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '轮播图',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `min_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '起步价',
  `star_level` int NULL DEFAULT 5 COMMENT '星级',
  `facilities` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '酒店选中的设施，逗号分隔',
  `latitude` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `longitude` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `tags` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '酒店选中的标签，逗号分隔',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel
-- ----------------------------
INSERT INTO `hotel` VALUES (1, '北京王府井希尔顿酒店', '/uploads/3e4a273d-355a-401f-8754-1cfed98e8851.jpg', NULL, '位于王府井核心地段，奢华住宿体验。', '北京市东城区王府井东街8号', 1200.00, 5, 'WIFI,游泳池,健身房,停车场,室外泳池,西餐厅', '32.041544', '118.791599', '亲子推荐,园林景观,精品酒店,豪华装修,近地铁');
INSERT INTO `hotel` VALUES (2, '上海静安香格里拉大酒店', 'https://images.unsplash.com/photo-1551882547-ff43c63efe5c?w=800', NULL, '俯瞰静安寺，交通便利，服务一流。', '上海市静安区延安中路1218号', 1500.00, 5, 'WIFI,游泳池,Spa,下午茶', '32.039215', '118.787455', NULL);

-- ----------------------------
-- Table structure for hotel_booking
-- ----------------------------
DROP TABLE IF EXISTS `hotel_booking`;
CREATE TABLE `hotel_booking`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `hotel_id` int NOT NULL COMMENT '酒店ID',
  `room_id` int NOT NULL COMMENT '房间ID',
  `check_in_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '入住日期',
  `check_out_date` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '退房日期',
  `total_price` decimal(10, 2) NOT NULL COMMENT '总价',
  `status` int NULL DEFAULT 0 COMMENT '状态: 0-待支付, 1-已支付, 2-已入住, 3-已取消, 4-退款中, 5-已退款',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预订人姓名',
  `user_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预订人电话',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `remark` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单备注',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `complete_time` datetime NULL DEFAULT NULL COMMENT '核销/完成时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel_booking
-- ----------------------------
INSERT INTO `hotel_booking` VALUES (3, 2, 1, 1, '2026-01-28', '2026-01-30', 2400.00, 3, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `hotel_booking` VALUES (4, 2, 1, 1, '2026-02-26', '2026-02-28', 2400.00, 3, '黎明', '133', NULL, NULL, NULL, NULL);
INSERT INTO `hotel_booking` VALUES (5, 2, 1, 2, '2026-01-29', '2026-01-30', 1400.00, 3, '王五', '13031982763', NULL, NULL, NULL, NULL);
INSERT INTO `hotel_booking` VALUES (6, 2, 1, 1, '2026-04-02', '2026-04-03', 1200.00, 2, '洛克', '13031982763', '2026-04-02 21:45:37', NULL, NULL, '2026-04-02 21:46:08');

-- ----------------------------
-- Table structure for hotel_room
-- ----------------------------
DROP TABLE IF EXISTS `hotel_room`;
CREATE TABLE `hotel_room`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `hotel_id` int NOT NULL COMMENT '所属酒店ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '房型名称',
  `image` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `price` decimal(10, 2) NOT NULL COMMENT '单价',
  `stock` int NULL DEFAULT 10 COMMENT '库存',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '房型描述',
  `area` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '面积',
  `bed_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '床型',
  `window` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '窗户',
  `facilities` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '选中的设施，逗号分隔',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel_room
-- ----------------------------
INSERT INTO `hotel_room` VALUES (1, 1, '豪华大床房', 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800,/uploads/a61517fa-6033-4aa4-9c3b-c9dc5a08bae7.jpg', 1200.00, 5, '45平米，独立卫浴，超大落地窗', NULL, NULL, NULL, '24小时热水,办公桌,吹风机,无线网络(WiFi),独立卫浴');
INSERT INTO `hotel_room` VALUES (2, 1, '行政双床房', 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=800', 1400.00, 3, '50平米，包含双人早餐，行政酒廊权益', NULL, NULL, NULL, NULL);
INSERT INTO `hotel_room` VALUES (3, 2, '外滩景观房', 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=800', 1800.00, 2, '一线江景，可直接看到东方明珠', NULL, NULL, NULL, NULL);
INSERT INTO `hotel_room` VALUES (4, 2, '尊贵套房', 'https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800', 2800.00, 1, '80平米大套房，独立客厅，私人管家服务', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for hotel_tag
-- ----------------------------
DROP TABLE IF EXISTS `hotel_tag`;
CREATE TABLE `hotel_tag`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '酒店标签基础库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel_tag
-- ----------------------------
INSERT INTO `hotel_tag` VALUES (2, '亲子推荐');
INSERT INTO `hotel_tag` VALUES (7, '园林景观');
INSERT INTO `hotel_tag` VALUES (1, '精品酒店');
INSERT INTO `hotel_tag` VALUES (6, '豪华装修');
INSERT INTO `hotel_tag` VALUES (4, '近地铁');
INSERT INTO `hotel_tag` VALUES (3, '靠海');
INSERT INTO `hotel_tag` VALUES (5, '高性价比');

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '发布者ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '发布内容',
  `image` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `likes` int NULL DEFAULT 0 COMMENT '点赞数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论总数',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, 1, '今天去了长城，风景真的很壮观！这就是不到长城非好汉吧。', 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800', 13, '2026-01-02 14:00:00', 0);
INSERT INTO `post` VALUES (2, 1, '推荐大家来这个小众打卡地，人少景美。', 'https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=800', 85, '2026-01-02 15:30:00', 0);
INSERT INTO `post` VALUES (4, 1, '很棒都来玩', '/uploads/57deb979-9915-4d17-b656-1e449f04e42b.png', 1, '2026-01-02 17:00:39', 2);
INSERT INTO `post` VALUES (5, 1, '垃圾', '/uploads/9c49df4e-7106-492f-8f3d-15a9ec63a2f4.png', 12, '2026-01-02 17:56:14', 2);
INSERT INTO `post` VALUES (6, 1, '66666', '/uploads/d5a96f06-adf0-49ef-b9f8-faac3e63e332.png,/uploads/12ccbb93-2d5f-4b7e-85f1-dcf13ef678bd.png,/uploads/88c7cfd3-e95d-4314-94f3-345b3a4660ee.png', 1, '2026-01-03 17:37:11', 2);
INSERT INTO `post` VALUES (8, 1, '不错欧', '/uploads/0987822c-ee1f-4107-8017-f2d7fc035d30.jpg', 0, '2026-01-06 15:46:03', 0);
INSERT INTO `post` VALUES (9, 1, '哈哈哈', '/uploads/b89341fc-a3c9-4492-9d4a-120a9a555203.jpg', 0, '2026-01-06 16:09:49', 0);
INSERT INTO `post` VALUES (10, 2, '我爱吃肯德基', '/uploads/ab26bc2c-7b0f-4156-ac44-ab484df015ff.bmp,/uploads/89679ecc-240e-466a-810d-ef5dbe425bb4.bmp', 1, '2026-01-10 17:34:37', 1);

-- ----------------------------
-- Table structure for post_comment
-- ----------------------------
DROP TABLE IF EXISTS `post_comment`;
CREATE TABLE `post_comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL COMMENT '关联的动态ID',
  `user_id` int NOT NULL COMMENT '评论人的用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '动态评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_comment
-- ----------------------------
INSERT INTO `post_comment` VALUES (1, 4, 1, '为啥', '2026-01-02 17:50:44');
INSERT INTO `post_comment` VALUES (2, 4, 1, '这么厉害吗', '2026-01-02 17:50:52');
INSERT INTO `post_comment` VALUES (3, 5, 1, '666', '2026-01-02 17:56:35');
INSERT INTO `post_comment` VALUES (4, 5, 1, '啥呀', '2026-01-02 17:58:29');
INSERT INTO `post_comment` VALUES (6, 6, 1, '滚滚滚', '2026-01-05 17:45:06');
INSERT INTO `post_comment` VALUES (7, 6, 1, '难崩', '2026-01-05 17:45:15');
INSERT INTO `post_comment` VALUES (8, 10, 2, '你好', '2026-03-02 17:57:34');

-- ----------------------------
-- Table structure for post_like
-- ----------------------------
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_post_user`(`post_id` ASC, `user_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_like
-- ----------------------------
INSERT INTO `post_like` VALUES (6, 5, 1, '2026-01-03 16:42:17');
INSERT INTO `post_like` VALUES (7, 1, 1, '2026-01-03 17:20:08');
INSERT INTO `post_like` VALUES (12, 6, 1, '2026-01-05 12:20:52');
INSERT INTO `post_like` VALUES (13, 4, 1, '2026-01-05 12:20:56');
INSERT INTO `post_like` VALUES (14, 10, 2, '2026-01-10 17:34:47');

-- ----------------------------
-- Table structure for search_log
-- ----------------------------
DROP TABLE IF EXISTS `search_log`;
CREATE TABLE `search_log`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NULL DEFAULT NULL COMMENT '用户ID',
  `keyword` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '搜索关键词',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '搜索日志表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of search_log
-- ----------------------------

-- ----------------------------
-- Table structure for system_facility
-- ----------------------------
DROP TABLE IF EXISTS `system_facility`;
CREATE TABLE `system_facility`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '设施名称',
  `type` int NULL DEFAULT 1 COMMENT '设施类型: 1-酒店设施, 2-房间设施',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_name`(`name` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 21 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '房间设施基础库' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of system_facility
-- ----------------------------
INSERT INTO `system_facility` VALUES (1, '免费停车场', 1);
INSERT INTO `system_facility` VALUES (2, '中餐厅', 1);
INSERT INTO `system_facility` VALUES (3, '西餐厅', 1);
INSERT INTO `system_facility` VALUES (4, '室外泳池', 1);
INSERT INTO `system_facility` VALUES (5, '健身房', 1);
INSERT INTO `system_facility` VALUES (6, '商务中心', 1);
INSERT INTO `system_facility` VALUES (7, '会议厅', 1);
INSERT INTO `system_facility` VALUES (8, '叫醒服务', 1);
INSERT INTO `system_facility` VALUES (9, '行李寄存', 1);
INSERT INTO `system_facility` VALUES (10, '前台贵重物品保险柜', 1);
INSERT INTO `system_facility` VALUES (11, '无线网络(WiFi)', 2);
INSERT INTO `system_facility` VALUES (12, '独立卫浴', 2);
INSERT INTO `system_facility` VALUES (13, '空调', 2);
INSERT INTO `system_facility` VALUES (14, '24小时热水', 2);
INSERT INTO `system_facility` VALUES (15, '电热水壶', 2);
INSERT INTO `system_facility` VALUES (16, '吹风机', 2);
INSERT INTO `system_facility` VALUES (17, '液晶电视机', 2);
INSERT INTO `system_facility` VALUES (18, '遮光窗帘', 2);
INSERT INTO `system_facility` VALUES (19, '书桌', 2);
INSERT INTO `system_facility` VALUES (20, '免费洗漱用品', 2);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '微信唯一标识',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户昵称',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户头像地址',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_openid`(`openid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'mock_openid_微信用户', '维生素', 'https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132', '2026-01-02 16:29:56');
INSERT INTO `user` VALUES (2, 'mock_openid_爱旅游的小人', '爱旅游的小人', '/uploads/2ad1f253-0442-4904-b301-4432deb830f2.jpeg', '2026-01-10 17:33:18');

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `target_id` bigint NOT NULL COMMENT '目标ID(动态ID或景点ID)',
  `type` int NOT NULL DEFAULT 1 COMMENT '类型: 1-动态, 2-景点',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_target_type`(`user_id` ASC, `target_id` ASC, `type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorite
-- ----------------------------
INSERT INTO `user_favorite` VALUES (2, 1, 5, 1, '2026-01-03 17:04:46');
INSERT INTO `user_favorite` VALUES (3, 1, 1, 1, '2026-01-03 17:20:09');
INSERT INTO `user_favorite` VALUES (5, 1, 6, 1, '2026-01-05 11:05:37');
INSERT INTO `user_favorite` VALUES (7, 1, 4, 1, '2026-01-05 12:20:58');
INSERT INTO `user_favorite` VALUES (8, 1, 2, 2, '2026-01-05 15:57:36');
INSERT INTO `user_favorite` VALUES (10, 1, 3, 2, '2026-01-06 15:41:40');
INSERT INTO `user_favorite` VALUES (12, 1, 4, 2, '2026-01-10 17:13:00');
INSERT INTO `user_favorite` VALUES (13, 1, 5, 2, '2026-01-10 17:13:04');
INSERT INTO `user_favorite` VALUES (14, 2, 4, 2, '2026-01-10 17:33:49');
INSERT INTO `user_favorite` VALUES (15, 2, 1, 2, '2026-01-10 17:33:54');
INSERT INTO `user_favorite` VALUES (16, 2, 6, 2, '2026-01-10 17:33:59');
INSERT INTO `user_favorite` VALUES (17, 2, 10, 1, '2026-01-10 17:34:47');
INSERT INTO `user_favorite` VALUES (18, 2, 1, 4, '2026-01-28 18:35:45');

SET FOREIGN_KEY_CHECKS = 1;
