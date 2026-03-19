package com.backstage.system.mapper.live;

import com.backstage.system.domain.vo.LiveDetailVo;
import com.backstage.system.domain.vo.LiveQueryVo;
import com.backstage.system.domain.vo.LiveUserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/8
 * Time: 14:25
 */
@Mapper
public interface LiveMapper {
    LiveDetailVo getLiveInfoById(Long id);

    LiveUserVo getLiveUserById(@Param("liveId") Long liveId, @Param("userId") Long userId);

    List<LiveQueryVo> getLiveList();
}
