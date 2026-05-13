package com.backstage.system.mapper.info_gap;

import com.backstage.system.domain.vo.info_gap.InfoGapVO;
import com.backstage.system.domain.info_gap.OshInfoGap;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OshInfoGapMapper extends BaseMapper<OshInfoGap> {
    // 分页联查
    List<InfoGapVO> selectInfoGapPage(@Param("type") String type, @Param("currentUserId") Long currentUserId);

    // 手动插入
    int insertInfoGap(OshInfoGap entity);

    // 手动原子更新计数
    int updateCountAtomically(@Param("id") Long id, @Param("column") String column);

    void decrementCountAtomically(@Param("id")Long infoId,@Param("column")  String column);

    // 当前用户关注的信息差列表
    List<InfoGapVO> selectInfoGapPageForFollow(@Param("currentUserId") Long currentUserId);

    // 查询当前用户发布的信息差
    List<InfoGapVO> selectInfoGapPageForMySelf(@Param("currentUserId") Long currentUserId);
}