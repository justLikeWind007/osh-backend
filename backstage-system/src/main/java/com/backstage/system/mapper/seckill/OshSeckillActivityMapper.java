package com.backstage.system.mapper.seckill;

import com.backstage.system.domain.seckill.OshSeckillActivity;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 秒杀活动 Mapper 接口
 *
 * @author backstage
 * @date 2026-04-28
 */
public interface OshSeckillActivityMapper {

    /** 根据ID查询 */
    OshSeckillActivity selectActivityById(Long id);

    /** 列表查询（支持按 title 模糊、status 过滤） */
    List<OshSeckillActivity> selectActivityList(OshSeckillActivity activity);

    /** 新增 */
    int insertActivity(OshSeckillActivity activity);

    /** 修改 */
    int updateActivity(OshSeckillActivity activity);

    /** 批量逻辑删除 */
    int deleteActivityByIds(@Param("ids") List<Long> ids);

    /** 批量更新活动状态 */
    int updateActivityStatusByIds(@Param("ids") List<Long> ids, @Param("status") Integer status);
}
