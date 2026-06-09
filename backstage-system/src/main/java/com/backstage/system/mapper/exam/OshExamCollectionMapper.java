package com.backstage.system.mapper.exam;

import com.backstage.system.domain.exam.OshExamCollection;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OshExamCollectionMapper extends BaseMapper<OshExamCollection> {

    /** 查询用户对某考试的收藏记录（含已删除） */
    OshExamCollection selectByUserIdAndExamId(@Param("userId") Long userId, @Param("examId") Long examId);

    /** 查询用户收藏的所有考试ID */
    List<Long> selectCollectedExamIdsByUserId(@Param("userId") Long userId);

    /** 更新 delete_flag */
    int updateDeleteFlag(@Param("id") Long id, @Param("deleteFlag") Integer deleteFlag, @Param("operator") String operator);
}
