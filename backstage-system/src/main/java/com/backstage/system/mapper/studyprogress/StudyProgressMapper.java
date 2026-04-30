package com.backstage.system.mapper.studyprogress;

import com.backstage.system.domain.vo.studyprogress.UserStudyProgressVo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 学习进度 Mapper 接口
 */
public interface StudyProgressMapper {
    /**
     * 查询用户学习进度列表
     */
    public List<UserStudyProgressVo> selectStudyProgressList(@Param("userId") Long userId);
    int saveOrUpdateProgress(@Param("vo") UserStudyProgressVo vo, @Param("userId") Long userId);}