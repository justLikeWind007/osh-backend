-- 课程标签管理功能 SQL 脚本
-- 用于支持课程标签的新增、查询等操作

-- 如果表不存在则创建（幂等性处理）
DROP TABLE IF EXISTS `osh_course_tag`;
CREATE TABLE `osh_course_tag` (
    `id`          bigint(20)      NOT NULL AUTO_INCREMENT         COMMENT '标签 ID',
    `name`        varchar(50)     NOT NULL                        COMMENT '标签名称',
    `sort`        int(11)         NOT NULL DEFAULT 0              COMMENT '排序权重（越大越靠前）',
    `use_count`   int(11)         NOT NULL DEFAULT 0              COMMENT '关联课程使用数量',
    `status`      tinyint(1)      NOT NULL DEFAULT 1              COMMENT '状态：0-禁用 1-启用',
    `remark`      varchar(500)    DEFAULT NULL                    COMMENT '备注',
    `delete_flag` tinyint(1)      NOT NULL DEFAULT 0              COMMENT '逻辑删除：0-正常 1-删除',
    `create_by`   varchar(64)     DEFAULT ''                      COMMENT '创建者',
    `create_time` datetime        DEFAULT NULL                    COMMENT '创建时间',
    `update_by`   varchar(64)     DEFAULT ''                      COMMENT '更新者',
    `update_time` datetime        DEFAULT NULL                    COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_name`      (`name`),
    KEY `idx_use_count`       (`use_count` DESC),
    KEY `idx_sort`            (`sort` DESC),
    KEY `idx_delete_flag`     (`delete_flag`)
) ENGINE=InnoDB AUTO_INCREMENT=1
  CHARACTER SET=utf8mb4 COLLATE=utf8mb4_unicode_ci
  COMMENT='课程标签表';

-- 初始化默认标签数据（如果不存在）
INSERT INTO `osh_course_tag` (`name`, `sort`, `use_count`, `status`, `create_by`, `create_time`)
SELECT '开源项目', 100, 0, 1, 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `osh_course_tag` WHERE `name` = '开源项目');

INSERT INTO `osh_course_tag` (`name`, `sort`, `use_count`, `status`, `create_by`, `create_time`)
SELECT 'AI 企业刚需', 90, 0, 1, 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `osh_course_tag` WHERE `name` = 'AI 企业刚需');

INSERT INTO `osh_course_tag` (`name`, `sort`, `use_count`, `status`, `create_by`, `create_time`)
SELECT '企业刚需', 80, 0, 1, 'admin', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `osh_course_tag` WHERE `name` = '企业刚需');

-- 菜单权限配置（如果需要）
-- 注意：实际使用时需要根据系统菜单 ID 规则调整
