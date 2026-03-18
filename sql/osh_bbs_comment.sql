CREATE TABLE `osh_bbs_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `post_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `user_id` bigint(20) NOT NULL COMMENT '评论者ID',
  `content` text NOT NULL COMMENT '评论内容',
  `reply_id` bigint(20) DEFAULT '0' COMMENT '回复目标评论ID，0为主评论',
  `reply_user_id` bigint(20) DEFAULT NULL COMMENT '被回复者ID',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '是否置顶',
  `is_delete` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_id` (`post_id`),
  KEY `idx_reply_id` (`reply_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='帖子评论表';