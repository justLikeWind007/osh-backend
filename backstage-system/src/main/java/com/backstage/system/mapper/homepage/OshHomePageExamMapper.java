package com.backstage.system.mapper.homepage;

import com.backstage.system.domain.homepage.vo.HotExamVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页热门考试 Mapper
 *
 * @author jayTatum
 */
@Mapper
public interface OshHomePageExamMapper {

    /**
     * 查询热门考试（基于热度公式排序）
     */
    List<HotExamVO> selectHotExams(@Param("limit") int limit);

    /**
     * 根据考试ID批量查询标签
     */
    List<HotExamVO> selectTagsByExamIds(@Param("examIds") List<Long> examIds);
}
