-- 友情链接/广告位表
CREATE TABLE osh_friend_link (
    id          BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    sort_order  INT NOT NULL DEFAULT 1 COMMENT '排序序号（1-5）',
    name        VARCHAR(64) NOT NULL COMMENT '链接名称',
    description VARCHAR(500) DEFAULT null comment '广告详情',
    url         VARCHAR(512) NOT NULL COMMENT '链接地址',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    create_by   BIGINT DEFAULT NULL COMMENT '创建人',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    update_by   BIGINT DEFAULT NULL COMMENT '更新人',
    delete_flag TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0=未删除，1=已删除',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='友情链接表';
