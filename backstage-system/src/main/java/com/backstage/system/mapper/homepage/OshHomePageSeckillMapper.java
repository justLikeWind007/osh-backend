package com.backstage.system.mapper.homepage;

import com.backstage.system.domain.homepage.vo.HotSeckillVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 首页热门秒杀 Mapper
 *
 * @author jayTatum
 */
@Mapper
public interface OshHomePageSeckillMapper {

    /**
     * 查询当前进行中活动的热门秒杀商品
     */
    List<HotSeckillVO> selectHotSeckill(@Param("limit") int limit);
}
