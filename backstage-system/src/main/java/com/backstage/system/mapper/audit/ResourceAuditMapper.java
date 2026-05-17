package com.backstage.system.mapper.audit;

import com.backstage.system.domain.audit.ResourceAuditItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResourceAuditMapper {

    List<ResourceAuditItemVO> selectPendingList(@Param("tableName") String tableName,
                                                @Param("offset") Integer offset,
                                                @Param("pageSize") Integer pageSize,
                                                @Param("keyword") String keyword);

    Long countPending(@Param("tableName") String tableName,
                      @Param("keyword") String keyword);

    ResourceAuditItemVO selectAuditNotifyItem(@Param("tableName") String tableName,
                                              @Param("resourceId") Long resourceId);

    int updateAuditStatus(@Param("tableName") String tableName,
                          @Param("resourceId") Long resourceId,
                          @Param("status") Integer status,
                          @Param("operator") String operator,
                          @Param("operatorId") Long operatorId);
}
