-- 答疑模块：问题表添加资源编号
ALTER TABLE `osh_question_answer_question` ADD COLUMN `no` char(8) DEFAULT NULL COMMENT '资源编号' AFTER `id`;

-- 答疑模块：回答表添加资源编号
ALTER TABLE `osh_question_answer_answer` ADD COLUMN `no` char(8) DEFAULT NULL COMMENT '资源编号' AFTER `id`;

-- 答疑模块：标签表添加资源编号
ALTER TABLE `osh_question_answer_tag` ADD COLUMN `no` char(8) DEFAULT NULL COMMENT '资源编号' AFTER `id`;

-- 开源项目模块：开源项目表添加资源编号
ALTER TABLE `osh_open_project` ADD COLUMN `no` char(8) DEFAULT NULL COMMENT '资源编号' AFTER `id`;

-- 开源项目模块：开源项目标签表添加资源编号
ALTER TABLE `osh_open_project_tag` ADD COLUMN `no` char(8) DEFAULT NULL COMMENT '资源编号' AFTER `id`;
