package com.backstage.system.mapper.info_gap;

import com.backstage.system.domain.dto.info_gap.InfoGapAnnoRespDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OshInfoGapAnnouncementMapper {

    List<InfoGapAnnoRespDTO> selectLatestInfoGapAnnouncementsByChannel(@Param("channel") Integer channel);

    int insertInfoGapAnnouncement(@Param("title") String title,
                                  @Param("link") String link,
                                  @Param("channel") Integer channel,
                                  @Param("operator") String operator);
}
