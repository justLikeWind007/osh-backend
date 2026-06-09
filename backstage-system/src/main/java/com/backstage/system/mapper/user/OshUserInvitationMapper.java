package com.backstage.system.mapper.user;

import com.backstage.system.domain.user.OshUserInvitation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 用户邀请关系 Mapper（不继承 BaseMapper，避免 MP 逻辑删除拦截器追加 delete_flag 条件）
 */
@Mapper
public interface OshUserInvitationMapper {

    /**
     * 插入邀请关系
     */
    void insertInvitation(OshUserInvitation invitation);

    /**
     * 查询我邀请的人列表
     */
    List<Map<String, Object>> selectInviteeListByInviterId(@Param("inviterId") Long inviterId);
}
