package com.backstage.system.domain.user;

import com.backstage.common.core.domain.entity.OSHBaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/4/9
 * Time: 19:32
 */
@TableName("osh_user_violation_record")
public class OshUserViolationRecord extends OSHBaseEntity {
    /**
     * 主键id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 违规用户id
     */
    private Long userId;

    /**
     * 违规类型：1=乱答，2=广告，3=恶意灌水，4=其他
     */
    private Integer violationType;

    /**
     * 违规原因（管理员填写或系统自动判定）
     */
    private String reason;

    /**
     * 操作人id（管理员id，系统自动判定则为null）
     */
    private Long operatorId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getViolationType() {
        return violationType;
    }

    public void setViolationType(Integer violationType) {
        this.violationType = violationType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    /**
     * 违规类型枚举
     */
    public enum ViolationTypeEnum {
        WRONG_ANSWER(1, "乱答"),
        ADVERTISEMENT(2, "广告"),
        MALICIOUS_SPAM(3, "恶意灌水"),
        OTHER(4, "其他");

        private final Integer code;
        private final String description;

        ViolationTypeEnum(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        public static ViolationTypeEnum fromCode(Integer code) {
            for (ViolationTypeEnum type : values()) {
                if (type.code.equals(code)) {
                    return type;
                }
            }
            return null;
        }
    }
}
