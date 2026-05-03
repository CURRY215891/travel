-- ============================================
-- 超级管理员功能数据库迁移脚本
-- 执行日期: 2026-04-15
-- ============================================

-- 1. 给 user 表添加 status 字段（用于封禁功能）
ALTER TABLE `user` ADD COLUMN `status` INT NULL DEFAULT 0 COMMENT '用户状态: 0-正常, 1-封禁' AFTER `avatar`;

-- 2. 给 post 表添加 audit_status 字段（用于内容审核）
ALTER TABLE `post` ADD COLUMN `audit_status` INT NULL DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-已通过, 2-已拒绝' AFTER `comment_count`;

-- 3. 给 comment 表添加 audit_status 字段（用于评论审核）
ALTER TABLE `comment` ADD COLUMN `audit_status` INT NULL DEFAULT 0 COMMENT '审核状态: 0-待审核, 1-已通过, 2-已拒绝' AFTER `parent_id`;

-- 4. 更新现有数据的默认值
UPDATE `user` SET `status` = 0 WHERE `status` IS NULL;
UPDATE `post` SET `audit_status` = 1 WHERE `audit_status` IS NULL; -- 现有帖子默认通过
UPDATE `comment` SET `audit_status` = 1 WHERE `audit_status` IS NULL; -- 现有评论默认通过

-- ============================================
-- 迁移完成！
-- ============================================
