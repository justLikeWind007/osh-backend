-- 2. 点赞记录表 (防止重复点赞，并用于判断用户是否点过赞)
CREATE TABLE `osh_bbs_support` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `post_id` bigint NOT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `idx_user_post` (`user_id`,`post_id`) -- 唯一索引防止并发重复点赞
);
