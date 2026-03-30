package com.backstage.system.mapper.info_gap;

import com.backstage.system.domain.vo.info_gap.InfoGapVO;
import com.backstage.system.domain.info_gap.OshInfoGap;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OshInfoGapMapper {
    // 分页联查
    Page<InfoGapVO> selectInfoGapPage(Page<InfoGapVO> page, @Param("type") String type, @Param("currentUserId") Long currentUserId);

    // 手动插入
    int insertInfoGap(OshInfoGap entity);

    // 手动原子更新计数
    int updateCountAtomically(@Param("id") Long id, @Param("column") String column);

    void decrementCountAtomically(@Param("id")Long infoId,@Param("column")  String column);
}