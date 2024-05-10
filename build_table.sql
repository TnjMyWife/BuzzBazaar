CREATE
DATABASE /*!32312 IF NOT EXISTS*/`bbsdb` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `bbsdb`;

DROP TABLE IF EXISTS `post`;

CREATE TABLE `post` (
                               `post_id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子主键id',
                               `publish_user_id` bigint NOT NULL COMMENT '发布者id',
                               `post_title` varchar(64) NOT NULL DEFAULT '' COMMENT '帖子标题',
                               `post_content` mediumtext NOT NULL COMMENT '帖子内容',
                               `post_category_id` int NOT NULL COMMENT '帖子分类id',
                               `post_category_name` varchar(50) NOT NULL COMMENT '帖子分类(冗余字段)',
                               `post_status` tinyint NOT NULL DEFAULT '1' COMMENT '0-未审核 1-审核通过 2-审核失败 (默认审核通过)',
                               `post_views` bigint NOT NULL DEFAULT '0' COMMENT '阅读量',
                               `post_comments` bigint NOT NULL DEFAULT '0' COMMENT '评论量',
                               `post_collects` bigint NOT NULL DEFAULT '0' COMMENT '收藏量',
                               `last_update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最新修改时间',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
                               PRIMARY KEY (`post_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
                               `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户主键id',
                               `login_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '登陆名称(默认为邮箱号码)',
                               `password_md5` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT 'MD5加密后的密码',
                               `nick_name` varchar(8) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '昵称',
                               `head_img_url` varchar(128) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '头像',
                               `gender` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '性别',
                               `location` varchar(4) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '居住地',
                               `introduce` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '个人简介',
                               `user_status` tinyint NOT NULL DEFAULT '0' COMMENT '用户状态 0=正常 1=禁言',
                               `last_login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '最新登录时间',
                               `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
                               PRIMARY KEY (`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS category;

CREATE TABLE `category` (
                                    `category_id` int NOT NULL AUTO_INCREMENT COMMENT '分类表主键',
                                    `category_name` varchar(16) NOT NULL COMMENT '分类的名称',
                                    `category_rank` int NOT NULL DEFAULT '1' COMMENT '分类的排序值 被使用的越多数值越大',
                                    `is_deleted` tinyint NOT NULL DEFAULT '0' COMMENT '是否删除 0=否 1=是',
                                    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                    PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;

DROP TABLE IF EXISTS collect;

CREATE TABLE `collect` (
                                          `record_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                          `post_id` bigint NOT NULL DEFAULT '0' COMMENT '收藏帖子主键',
                                          `user_id` bigint NOT NULL DEFAULT '0' COMMENT '收藏者id',
                                          `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
                                          PRIMARY KEY (`record_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;


DROP TABLE IF EXISTS comment;

CREATE TABLE `comment` (
                                   `comment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键id',
                                   `post_id` bigint NOT NULL DEFAULT '0' COMMENT '关联的帖子主键',
                                   `comment_user_id` bigint NOT NULL DEFAULT '0' COMMENT '评论者id',
                                   `comment_body` varchar(200) NOT NULL DEFAULT '' COMMENT '评论内容',
                                   `parent_comment_user_id` bigint NOT NULL DEFAULT '0' COMMENT '所回复的上一级评论的userId',
                                   `comment_create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
                                   `is_deleted` tinyint DEFAULT '0' COMMENT '是否删除 0-未删除 1-已删除',
                                   PRIMARY KEY (`comment_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;
