package com.backstage.framework.handler;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * MyBatis Plus 自动填充处理器
 *
 * @author backstage
 */
public class MyBatisPlusMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "createBy", Long.class, getUserId());
        this.strictInsertFill(metaObject, "updateBy", Long.class, getUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", Long.class, getUserId());
    }

    private Long getUserId() {
        return ThreadLocalUtil.get(OshUserConstants.USER_ID, Long.class);
    }
}
