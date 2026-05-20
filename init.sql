-- ============================================
-- SSO Demo 数据库初始化脚本
-- 数据库: MySQL 8.+
-- Schema: oauth2server
-- 生成日期: 2026-05-21
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `oauth2server` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `oauth2server`;

-- ============================================
-- 用户表
-- ============================================
DROP TABLE IF EXISTS `ss_user`;
CREATE TABLE `ss_user` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `status` INT DEFAULT 1 COMMENT '状态: 1-正常, 0-禁用',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 角色表
-- ============================================
DROP TABLE IF EXISTS `ss_role`;
CREATE TABLE `ss_role` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(50) NOT NULL COMMENT '角色名',
    `desc` VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
    `create_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- ============================================
-- 权限表
-- ============================================
DROP TABLE IF EXISTS `ss_authority`;
CREATE TABLE `ss_authority` (
    `id` INT NOT NULL COMMENT '主键ID',
    `parent_id` INT DEFAULT NULL COMMENT '父权限ID',
    `name` VARCHAR(50) NOT NULL COMMENT '权限名称',
    `desc` VARCHAR(255) DEFAULT NULL COMMENT '权限描述',
    `resource` VARCHAR(255) DEFAULT NULL COMMENT '权限资源标识',
    `type` INT DEFAULT 0 COMMENT '权限类型: 0-菜单, 1-组件/按钮',
    `create_at` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- ============================================
-- 用户-角色关联表
-- ============================================
DROP TABLE IF EXISTS `ss_user_role_rel`;
CREATE TABLE `ss_user_role_rel` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `uid` INT NOT NULL COMMENT '用户ID',
    `rid` INT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_uid_rid` (`uid`, `rid`),
    KEY `idx_uid` (`uid`),
    KEY `idx_rid` (`rid`),
    CONSTRAINT `fk_user_role_user` FOREIGN KEY (`uid`) REFERENCES `ss_user` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_user_role_role` FOREIGN KEY (`rid`) REFERENCES `ss_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-角色关联表';

-- ============================================
-- 权限-角色关联表
-- ============================================
DROP TABLE IF EXISTS `ss_authority_role_rel`;
CREATE TABLE `ss_authority_role_rel` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `authority_id` INT NOT NULL COMMENT '权限ID',
    `role_id` INT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_authority_role` (`authority_id`, `role_id`),
    KEY `idx_authority_id` (`authority_id`),
    KEY `idx_role_id` (`role_id`),
    CONSTRAINT `fk_auth_role_auth` FOREIGN KEY (`authority_id`) REFERENCES `ss_authority` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_auth_role_role` FOREIGN KEY (`role_id`) REFERENCES `ss_role` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限-角色关联表';

-- ============================================
-- OAuth2 客户端配置表 (Spring Security OAuth2 标准)
-- ============================================
DROP TABLE IF EXISTS `oauth_client_details`;
CREATE TABLE `oauth_client_details` (
    `client_id` VARCHAR(256) NOT NULL COMMENT '客户端ID',
    `resource_ids` VARCHAR(256) DEFAULT NULL COMMENT '资源ID列表(逗号分隔)',
    `client_secret` VARCHAR(256) NOT NULL COMMENT '客户端密钥',
    `scope` VARCHAR(256) DEFAULT NULL COMMENT '授权范围(逗号分隔)',
    `authorized_grant_types` VARCHAR(256) DEFAULT NULL COMMENT '授权类型(逗号分隔): authorization_code,password,refresh_token,client_credentials,implicit',
    `web_server_redirect_uri` VARCHAR(256) DEFAULT NULL COMMENT '回调地址',
    `authorities` VARCHAR(256) DEFAULT NULL COMMENT '权限列表(逗号分隔)',
    `access_token_validity` INT DEFAULT 43200 COMMENT 'Access Token 有效期(秒), 默认12小时',
    `refresh_token_validity` INT DEFAULT 2592000 COMMENT 'Refresh Token 有效期(秒), 默认30天',
    `additional_information` VARCHAR(4096) DEFAULT NULL COMMENT '扩展信息(JSON格式)',
    `autoapprove` VARCHAR(256) DEFAULT NULL COMMENT '是否自动授权',
    PRIMARY KEY (`client_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='OAuth2客户端配置表';

-- ============================================
-- 初始数据
-- ============================================

-- 插入默认用户 (密码: 123456, 使用BCrypt加密)
-- BCrypt密码生成: new BCryptPasswordEncoder().encode("123456")
INSERT INTO `ss_user` (`id`, `username`, `password`, `status`) VALUES
(1, 'admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1),
(2, 'user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', 1);

-- 插入默认角色
INSERT INTO `ss_role` (`id`, `name`, `desc`, `create_at`) VALUES
(1, 'ROLE_ADMIN', '管理员角色', NOW()),
(2, 'ROLE_USER', '普通用户角色', NOW());

-- 插入默认权限
INSERT INTO `ss_authority` (`id`, `parent_id`, `name`, `desc`, `resource`, `type`, `create_at`) VALUES
(1, NULL, '系统管理', '系统管理菜单', NULL, 0, NOW()),
(2, 1, '用户管理', '用户管理菜单', NULL, 0, NOW()),
(3, NULL, 'API权限', 'API访问权限', NULL, 0, NOW()),
(4, 3, 'Hello接口', '访问/hello接口', 'api:hello', 1, NOW()),
(5, 3, '用户信息', '访问/user接口', 'api:user', 1, NOW());

-- 插入用户-角色关联
INSERT INTO `ss_user_role_rel` (`uid`, `rid`) VALUES
(1, 1),  -- admin 拥有管理员角色
(1, 2),  -- admin 也拥有普通用户角色
(2, 2);  -- user 拥有普通用户角色

-- 插入权限-角色关联
INSERT INTO `ss_authority_role_rel` (`authority_id`, `role_id`) VALUES
(4, 1),  -- 管理员角色拥有 api:hello 权限
(4, 2),  -- 普通用户角色拥有 api:hello 权限
(5, 1),  -- 管理员角色拥有 api:user 权限
(5, 2);  -- 普通用户角色拥有 api:user 权限

-- 插入OAuth2客户端配置
-- client_secret: secret (BCrypt加密后)
INSERT INTO `oauth_client_details` (
    `client_id`,
    `resource_ids`,
    `client_secret`,
    `scope`,
    `authorized_grant_types`,
    `web_server_redirect_uri`,
    `authorities`,
    `access_token_validity`,
    `refresh_token_validity`,
    `autoapprove`
) VALUES
(
    'web-client',
    'resource_server,resource_server_2',
    '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',
    'read,write,trust',
    'authorization_code,password,refresh_token,implicit,client_credentials',
    'http://localhost:8081/login',
    'ROLE_CLIENT',
    43200,
    2592000,
    'true'
),
(
    'resource-server',
    NULL,
    '{bcrypt}$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH',
    'read,write',
    'client_credentials',
    NULL,
    'ROLE_CLIENT',
    43200,
    NULL,
    'true'
);

-- ============================================
-- 索引优化
-- ============================================
-- 为提高查询性能，添加必要的索引
CREATE INDEX `idx_ss_user_status` ON `ss_user` (`status`);
CREATE INDEX `idx_ss_role_create_at` ON `ss_role` (`create_at`);
CREATE INDEX `idx_ss_authority_type` ON `ss_authority` (`type`);
CREATE INDEX `idx_ss_authority_create_at` ON `ss_authority` (`create_at`);
