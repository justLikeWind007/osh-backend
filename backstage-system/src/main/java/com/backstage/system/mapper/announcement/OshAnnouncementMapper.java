package com.backstage.system.mapper.announcement;

import com.backstage.system.domain.vo.seckill.SeckillAnnouncementVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface OshAnnouncementMapper {

    // ==================== 秒杀公告/动态 ====================

    /**
     * 插入一条秒杀公告或动态记录
     * channel=1 系统动态（公告栏），channel=2 业务动态（动态栏）
     * module='seckill'，source='system'，status=4（已发布）
     */
    @Insert("INSERT INTO osh_announcement " +
            "(title, link, icon, color, status, channel, module, resource_type, resource_id, " +
            " sort, is_top, delete_flag, source, source_module, create_by, create_time, update_by, update_time) " +
            "VALUES " +
            "(#{title}, #{link}, #{icon}, #{color}, 4, #{channel}, 'seckill', #{resourceType}, #{resourceId}, " +
            " #{sort}, 0, 0, 'system', 'seckill', 'system', NOW(), 'system', NOW())")
    int insertSeckillAnnouncement(@Param("title") String title,
                                  @Param("link") String link,
                                  @Param("icon") String icon,
                                  @Param("color") String color,
                                  @Param("resourceType") String resourceType,
                                  @Param("resourceId") Long resourceId,
                                  @Param("sort") int sort,
                                  @Param("channel") int channel);

    /**
     * 查询秒杀公告栏（channel=1），按 sort 降序、create_time 降序
     */
    @Select("SELECT id, title, link, icon, color AS iconColor, channel, create_time AS createTime " +
            "FROM osh_announcement " +
            "WHERE delete_flag = 0 AND status = 4 AND module = 'seckill' AND channel = 1 " +
            "ORDER BY sort DESC, create_time DESC " +
            "LIMIT #{limit}")
    List<SeckillAnnouncementVO> selectSeckillNotices(@Param("limit") int limit);

    /**
     * 查询秒杀动态栏（channel=2），按 create_time 降序
     */
    @Select("SELECT id, title, link, icon, color AS iconColor, channel, create_time AS createTime " +
            "FROM osh_announcement " +
            "WHERE delete_flag = 0 AND status = 4 AND module = 'seckill' AND channel = 2 " +
            "ORDER BY create_time DESC " +
            "LIMIT #{limit}")
    List<SeckillAnnouncementVO> selectSeckillDynamics(@Param("limit") int limit);

    /**
     * 检查某个活动商品明细是否已存在公告记录（防止定时任务重复插入）
     * module='seckill'，channel=1（公告栏），resource_id 存 activityId，link 区分同活动多商品
     */
    @Select("SELECT COUNT(1) FROM osh_announcement " +
            "WHERE delete_flag = 0 AND module = 'seckill' AND channel = 1 " +
            "AND resource_id = #{activityId} AND link = #{link}")
    int countSeckillNoticeByActivityAndLink(@Param("activityId") Long activityId,
                                            @Param("link") String link);

    /**
     * 检查某条动态是否已存在（防止回填时重复插入，用 title 精确匹配）
     */
    @Select("SELECT COUNT(1) FROM osh_announcement " +
            "WHERE delete_flag = 0 AND module = 'seckill' AND channel = 2 " +
            "AND title = #{title}")
    int countSeckillDynamicByTitle(@Param("title") String title);

    /**
     * 软删除某活动下的所有公告记录（活动下架时调用）
     */
    @Update("UPDATE osh_announcement SET delete_flag = 1, update_time = NOW() " +
            "WHERE module = 'seckill' AND channel = 1 " +
            "AND resource_id = #{activityId} AND delete_flag = 0")
    int deleteSeckillNoticesByActivityId(@Param("activityId") Long activityId);
}
