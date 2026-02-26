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

 Date: 05/01/2026 10:37:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for attraction
-- ----------------------------
DROP TABLE IF EXISTS `attraction`;
CREATE TABLE `attraction`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `category_id` int NULL DEFAULT NULL COMMENT '所属分类ID',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '景点名称',
  `mainImage` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详情页轮播图，多张用逗号隔开',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `address` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '门票价格',
  `openTime` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `latitude` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '纬度(用于地图导航)',
  `longitude` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经度(用于地图导航)',
  `viewCount` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of attraction
-- ----------------------------
INSERT INTO `attraction` VALUES (1, NULL, '北京故宫', 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=600', NULL, '世界三大宫殿之一', NULL, 0.00, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '评论人ID',
  `attr_id` int NOT NULL COMMENT '景点/美食/酒店房间ID',
  `type` int NULL DEFAULT 1 COMMENT '评论类型: 1-景点, 2-美食, 3-酒店房间, 4-其他',
  `source` varchar(50) NULL DEFAULT 'app' COMMENT '评论来源',
  `parent_id` int NULL DEFAULT 0 COMMENT '父评论ID(追评)',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论文字',
  `star` int NULL DEFAULT 5 COMMENT '总体评分/酒店评分',
  `hygiene_score` int NULL DEFAULT 5 COMMENT '房间卫生评分',
  `environment_score` int NULL DEFAULT 5 COMMENT '周边环境评分',
  `service_score` int NULL DEFAULT 5 COMMENT '酒店服务评分',
  `facility_score` int NULL DEFAULT 5 COMMENT '设备设施评分',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '评论图片，多张用逗号隔开',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `attr_id` int NOT NULL COMMENT '景点ID',
  `status` int NULL DEFAULT 0 COMMENT '0: 待支付, 1: 已支付, 2: 已取消, 3: 已完成',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预订人姓名',
  `user_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预订人电话(必须11位数字)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `check_phone` CHECK (`user_phone` REGEXP '^[0-9]{11}$')
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------

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
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, 1, '今天去了长城，风景真的很壮观！这就是不到长城非好汉吧。', 'https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=800', 13, '2026-01-02 14:00:00', 0);
INSERT INTO `post` VALUES (2, 1, '推荐大家来这个小众打卡地，人少景美。', 'https://images.unsplash.com/photo-1540959733332-eab4deabeeaf?w=800', 85, '2026-01-02 15:30:00', 0);
INSERT INTO `post` VALUES (4, 1, '很棒都来玩', 'http://192.168.5.23:8080/uploads/57deb979-9915-4d17-b656-1e449f04e42b.png', 0, '2026-01-02 17:00:39', 2);
INSERT INTO `post` VALUES (5, 1, '垃圾', 'http://192.168.5.23:8080/uploads/9c49df4e-7106-492f-8f3d-15a9ec63a2f4.png', 12, '2026-01-02 17:56:14', 2);
INSERT INTO `post` VALUES (6, 1, '66666', 'http://192.168.5.23:8080/uploads/d5a96f06-adf0-49ef-b9f8-faac3e63e332.png,http://192.168.5.23:8080/uploads/12ccbb93-2d5f-4b7e-85f1-dcf13ef678bd.png,http://192.168.5.23:8080/uploads/88c7cfd3-e95d-4314-94f3-345b3a4660ee.png', 1, '2026-01-03 17:37:11', 1);

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
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '动态评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_comment
-- ----------------------------
INSERT INTO `post_comment` VALUES (1, 4, 1, '为啥', '2026-01-02 17:50:44');
INSERT INTO `post_comment` VALUES (2, 4, 1, '这么厉害吗', '2026-01-02 17:50:52');
INSERT INTO `post_comment` VALUES (3, 5, 1, '666', '2026-01-02 17:56:35');
INSERT INTO `post_comment` VALUES (4, 5, 1, '啥呀', '2026-01-02 17:58:29');
INSERT INTO `post_comment` VALUES (5, 6, 1, '666', '2026-01-03 17:46:53');

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
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_like
-- ----------------------------
INSERT INTO `post_like` VALUES (6, 5, 1, '2026-01-03 16:42:17');
INSERT INTO `post_like` VALUES (7, 1, 1, '2026-01-03 17:20:08');
INSERT INTO `post_like` VALUES (9, 6, 1, '2026-01-03 17:43:30');

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
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'mock_openid_微信用户', '维生素', 'https://thirdwx.qlogo.cn/mmopen/vi_32/POgEwh4mIHO4nibH0KlMECNjjGxQUq24ZEaGT4poC6icRiccVGKSyXwibcPq4BWmiaIGuG1icwxaQX6grC9VemZoJ8rg/132', '2026-01-02 16:29:56');

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
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorite
-- ----------------------------
INSERT INTO `user_favorite` VALUES (2, 1, 5, 1, '2026-01-03 17:04:46');
INSERT INTO `user_favorite` VALUES (3, 1, 1, 1, '2026-01-03 17:20:09');
INSERT INTO `user_favorite` VALUES (4, 1, 6, 1, '2026-01-03 17:43:28');

-- ----------------------------
-- Table structure for hotel_booking
-- ----------------------------
DROP TABLE IF EXISTS `hotel_booking`;
CREATE TABLE `hotel_booking`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL COMMENT '用户ID',
  `hotel_id` int NOT NULL COMMENT '酒店ID',
  `room_id` int NOT NULL COMMENT '房间ID',
  `check_in_date` date NOT NULL COMMENT '入住日期',
  `check_out_date` date NOT NULL COMMENT '退房日期',
  `total_price` decimal(10, 2) NOT NULL COMMENT '总价',
  `status` int NULL DEFAULT 0 COMMENT '0: 待支付, 1: 已支付, 2: 已取消, 3: 已完成',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预订人姓名',
  `user_phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '预订人电话(必须11位数字)',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  CONSTRAINT `check_hotel_phone` CHECK (`user_phone` REGEXP '^[0-9]{11}$')
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hotel_booking
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
