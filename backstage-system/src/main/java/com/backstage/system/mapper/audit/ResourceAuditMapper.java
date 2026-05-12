package com.backstage.system.mapper.audit;

import com.backstage.system.domain.audit.ResourceAuditItemVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ResourceAuditMapper {

    List<ResourceAuditItemVO> selectPendingList(@Param("tableName") String tableName,
                                                @Param("offset") Integer offset,
                                                @Param("pageSize") Integer pageSize);

    Long countPending(@Param("tableName") String tableName);

    int approvePending(@Param("tableName") String tableName,
                       @Param("resourceId") Long resourceId,
                       @Param("operator") String operator,
                       @Param("operatorId") Long operatorId);
}
