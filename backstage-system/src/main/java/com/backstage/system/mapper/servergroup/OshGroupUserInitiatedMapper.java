package com.backstage.system.mapper.servergroup;

import com.backstage.system.domain.servergroup.OshGroupUserInitiated;
import com.backstage.system.domain.vo.UserInitiatedActivityListVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户发起拼团Mapper接口
 * 
 * @author system
 * @date 2026-05-05
 */
@Mapper
public interface OshGroupUserInitiatedMapper {
    
    /**
     * 插入用户发起拼团记录
     * 
     * @param initiated 发起记录
     * @return 影响行数
     */
    int insertUserInitiated(OshGroupUserInitiated initiated);
    
    /**
     * 根据ID查询用户发起拼团记录
     * 
     * @param id 发起记录ID
     * @return 发起记录
     */
    OshGroupUserInitiated selectById(@Param("id") Long id);
    
    /**
     * 查询用户发起的拼团列表
     * 
     * @param userId 用户ID
     * @param groupStatus 组团状态筛选（可选）
     * @return 发起的拼团列表
     */
    List<OshGroupUserInitiated> selectByUserId(@Param("userId") Long userId, @Param("groupStatus") Integer groupStatus);
    
    /**
     * 查询用户发起拼团活动列表（C端展示用）
     * 
     * @param status 状态筛选（可选）
     * @return 用户发起拼团活动列表
     */
    List<UserInitiatedActivityListVO> selectUserInitiatedActivityList(@Param("status") Integer status);
    
    /**
     * 更新发起拼团记录
     * 
     * @param initiated 发起记录
     * @return 影响行数
     */
    int updateUserInitiated(OshGroupUserInitiated initiated);
    
    /**
     * 更新当前参团人数
     * 
     * @param id 发起记录ID
     * @param currentNum 当前人数
     * @return 影响行数
     */
    int updateCurrentNum(@Param("id") Long id, @Param("currentNum") Integer currentNum);
    
    /**
     * 更新组团状态
     * 
     * @param id 发起记录ID
     * @param groupStatus 组团状态
     * @return 影响行数
     */
    int updateGroupStatus(@Param("id") Long id, @Param("groupStatus") Integer groupStatus);
    
    /**
     * 检查用户是否已参与该拼团
     * 
     * @param activityId 拼团活动ID
     * @param userId 用户ID
     * @return 参与记录数
     */
    int checkUserAlreadyJoined(@Param("activityId") Long activityId, @Param("userId") Long userId);
    
    /**
     * 查询拼团活动详情（用于校验）
     * 
     * @param activityId 拼团活动ID
     * @return 拼团活动信息（包含当前人数、最大人数、状态等）
     */
    java.util.Map<String, Object> selectActivityDetail(@Param("activityId") Long activityId);
}

