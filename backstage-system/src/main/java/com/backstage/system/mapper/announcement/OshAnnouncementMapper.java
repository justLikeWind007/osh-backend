package com.backstage.system.mapper.announcement;

import com.backstage.system.domain.vo.announcement.ToolAnnouncementVO;
import com.backstage.system.domain.vo.seckill.SeckillAnnouncementVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface OshAnnouncementMapper {

    @Select("SELECT id, title, link, create_time AS createTime " +
            "FROM osh_announcement " +
            "WHERE delete_flag = 0 AND status = 0 AND resource_type = 'tool' " +
            "ORDER BY create_time DESC " +
            "LIMIT 5")
    List<ToolAnnouncementVO> selectLatestToolAnnouncements();

    // ==================== 秒杀公告/动态 ====================

    /**
     * 插入一条秒杀公告或动态记录
     * scene='seckill', trigger_type='auto', status=1(启用)
     */
    @Insert("INSERT INTO osh_announcement " +
            "(title, link, icon, icon_color, status, scene, resource_type, resource_id, " +
            " sort, is_top, delete_flag, trigger_type, biz_type, create_by, create_time, update_by, update_time) " +
            "VALUES " +
            "(#{title}, #{link}, #{icon}, #{iconColor}, 1, 'seckill', 'seckill', #{resourceId}, " +
            " #{sort}, 0, 0, 'auto', #{bizType}, 'system', NOW(), 'system', NOW())")
    int insertSeckillAnnouncement(@Param("title") String title,
                                  @Param("link") String link,
                                  @Param("icon") String icon,
                                  @Param("iconColor") String iconColor,
                                  @Param("resourceId") Long resourceId,
                                  @Param("sort") int sort,
                                  @Param("bizType") String bizType);

    /**
     * 查询秒杀公告栏（biz_type='seckill_notice'），按 sort 降序、create_time 降序
     */
    @Select("SELECT id, title, link, icon, icon_color AS iconColor, biz_type AS bizType, create_time AS createTime " +
            "FROM osh_announcement " +
            "WHERE delete_flag = 0 AND status = 1 AND scene = 'seckill' AND biz_type = 'seckill_notice' " +
            "ORDER BY sort DESC, create_time DESC " +
            "LIMIT #{limit}")
    List<SeckillAnnouncementVO> selectSeckillNotices(@Param("limit") int limit);

    /**
     * 查询秒杀动态栏（biz_type='seckill_dynamic'），按 create_time 降序
     */
    @Select("SELECT id, title, link, icon, icon_color AS iconColor, biz_type AS bizType, create_time AS createTime " +
            "FROM osh_announcement " +
            "WHERE delete_flag = 0 AND status = 1 AND scene = 'seckill' AND biz_type = 'seckill_dynamic' " +
            "ORDER BY create_time DESC " +
            "LIMIT #{limit}")
    List<SeckillAnnouncementVO> selectSeckillDynamics(@Param("limit") int limit);

    /**
     * 检查某个活动商品明细是否已存在公告记录（防止定时任务重复插入）
     * resource_id 存 activityId，biz_type='seckill_notice'，link 包含 itemId 用于区分同活动多商品
     */
    @Select("SELECT COUNT(1) FROM osh_announcement " +
            "WHERE delete_flag = 0 AND scene = 'seckill' AND biz_type = 'seckill_notice' " +
            "AND resource_id = #{activityId} AND link = #{link}")
    int countSeckillNoticeByActivityAndLink(@Param("activityId") Long activityId,
                                            @Param("link") String link);

    /**
     * 检查某条动态是否已存在（防止回填时重复插入，用 title 精确匹配）
     */
    @Select("SELECT COUNT(1) FROM osh_announcement " +
            "WHERE delete_flag = 0 AND scene = 'seckill' AND biz_type = 'seckill_dynamic' " +
            "AND title = #{title}")
    int countSeckillDynamicByTitle(@Param("title") String title);

    /**
     * 软删除某活动下的所有公告记录（活动下架时调用）
     */
    @Update("UPDATE osh_announcement SET delete_flag = 1, update_time = NOW() " +
            "WHERE scene = 'seckill' AND biz_type = 'seckill_notice' " +
            "AND resource_id = #{activityId} AND delete_flag = 0")
    int deleteSeckillNoticesByActivityId(@Param("activityId") Long activityId);
}
