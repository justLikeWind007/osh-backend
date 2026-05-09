package com.backstage.system.mapper.servergroup;

import com.backstage.system.domain.servergroup.OshGroupActivity;
import com.backstage.system.domain.servergroup.OshGroupOrder;
import com.backstage.system.domain.servergroup.OshGroupWork;
import com.backstage.system.domain.vo.GroupActivityListVO;
import com.backstage.system.domain.vo.GroupWorkListVO;
import com.backstage.system.domain.vo.ServerGroupUserVo;
import com.backstage.system.domain.vo.MyGroupListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 服务器拼团Mapper接口
 * 
 * @author system
 * @date 2026-04-18
 */
@Mapper
public interface OshGroupServerMapper {
    
    /**
     * 查询拼团活动列表（C端展示用）
     * 
     * @param status 状态筛选（可选）
     * @return 拼团活动列表
     */
    List<GroupActivityListVO> selectGroupActivityList(@Param("status") Integer status);
    
    /**
     * 根据ID查询拼团活动详情
     * 
     * @param id 拼团活动ID
     * @return 拼团活动
     */
    OshGroupActivity selectGroupActivityById(@Param("id") Long id);
    
    /**
     * 查询我的拼团列表
     * 
     * @param userId 用户ID
     * @param groupStatus 组团状态筛选（可选）
     * @return 我的拼团列表
     */
    List<MyGroupListVO> selectMyGroupList(@Param("userId") Long userId, @Param("groupStatus") Integer groupStatus);
    
    /**
     * 查询拼团详情
     * 
     * @param id 拼团活动ID
     * @return 拼团详情
     */
    OshGroupActivity selectGroupDetailById(@Param("id") Long id);
    
    /**
     * 查询参团用户列表
     * 
     * @param groupActivityId 拼团活动ID
     * @return 参团用户列表
     */
    List<ServerGroupUserVo> selectGroupUsers(@Param("groupActivityId") Long groupActivityId);
    
    /**
     * 检查用户是否已参团
     * 
     * @param groupActivityId 拼团活动ID
     * @param userId 用户ID
     * @return 参团记录数
     */
    int checkUserJoined(@Param("groupActivityId") Long groupActivityId, @Param("userId") Long userId);
    
    /**
     * 插入参团记录
     * 
     * @param work 参团记录
     * @return 影响行数
     */
    int insertGroupWork(OshGroupWork work);

    /**
     * 查询全量组团记录列表（管理端）
     *
     * @param groupStatus 组团状态筛选（可选）：0-进行中 1-已成团 2-已取消/过期
     * @return 组团记录列表
     */
    List<GroupWorkListVO> selectGroupWorkList(@Param("groupStatus") Integer groupStatus);
    
    /**
     * 更新拼团活动人数（乐观锁）
     * 
     * @param id 拼团活动ID
     * @param currentNum 当前人数
     * @param status 状态
     * @param serverStartTime 服务器开始时间
     * @param serverEndTime 服务器结束时间
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int updateGroupActivityWithLock(
            @Param("id") Long id,
            @Param("currentNum") Integer currentNum,
            @Param("status") Integer status,
            @Param("serverStartTime") LocalDateTime serverStartTime,
            @Param("serverEndTime") LocalDateTime serverEndTime,
            @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 更新拼团活动人数+1
     * 
     * @param id 拼团活动ID
     * @param currentNum 当前人数
     * @param status 状态
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int incrementGroupActivityCurrentNum(
            @Param("id") Long id,
            @Param("currentNum") Integer currentNum,
            @Param("status") Integer status,
            @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 更新拼团活动状态为已结束（达到人数上限）
     * 
     * @param id 拼团活动ID
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int updateGroupActivityStatusEnded(@Param("id") Long id, @Param("updateTime") LocalDateTime updateTime);
    
    /**
     * 插入拼团订单
     * 
     * @param order 拼团订单
     * @return 影响行数
     */
    int insertGroupOrder(OshGroupOrder order);
    
    /**
     * 根据订单号查询订单
     * 
     * @param orderNo 订单号
     * @return 订单
     */
    OshGroupOrder selectGroupOrderByOrderNo(@Param("orderNo") String orderNo);
    
    /**
     * 计算动态价格
     * 
     * @param basePrice 基础价格
     * @param totalDuration 总时长（月）
     * @param remainingMonths 剩余月数
     * @return 实际价格
     */
    BigDecimal calculateDynamicPrice(
            @Param("basePrice") BigDecimal basePrice,
            @Param("totalDuration") Integer totalDuration,
            @Param("remainingMonths") BigDecimal remainingMonths);
    
    /**
     * 模糊查询用户名列表（用于手动添加参团用户）
     * 
     * @param keyword 搜索关键词（支持用户名、昵称模糊匹配）
     * @param limit 返回数量限制
     * @return 用户信息列表
     */
    List<Map<String, Object>> selectUsernamesByKeyword(
            @Param("keyword") String keyword,
            @Param("limit") Integer limit);
}
