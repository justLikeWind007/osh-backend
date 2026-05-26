-- 1. osh_user 表新增邀请码字段
ALTER TABLE osh_user ADD COLUMN invite_code VARCHAR(16) DEFAULT NULL COMMENT '用户邀请码' AFTER introduction;
ALTER TABLE osh_user ADD UNIQUE KEY uk_invite_code (invite_code);

-- 2. 新增邀请关系表
CREATE TABLE osh_user_invitation (
    id          BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    inviter_id  BIGINT NOT NULL COMMENT '邀请人用户ID',
    invitee_id  BIGINT NOT NULL COMMENT '被邀请人用户ID',
    invite_code VARCHAR(16) NOT NULL COMMENT '使用的邀请码',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_invitee (invitee_id),
    KEY idx_inviter (inviter_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户邀请关系表';