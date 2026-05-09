package com.backstage.system.service.servergroup;

import com.backstage.system.domain.dto.GroupCreateDTO;
import com.backstage.system.domain.vo.GroupActivityListVO;
import com.backstage.system.domain.vo.GroupCreateVO;
import com.backstage.system.domain.vo.GroupDetailVO;
import com.backstage.system.domain.vo.GroupWorkListVO;
import com.backstage.system.domain.vo.InitiableActivityVO;
import com.backstage.system.domain.vo.MyGroupListVO;
import com.backstage.system.domain.vo.UserInitiatedActivityListVO;

import java.util.List;
import java.util.Map;

/**
 * 服务器拼团Service接口
 * 
 * @author system
 * @date 2026-04-18
 */
public interface IOshGroupServerService {
    
    /**
     * 查询拼团活动列表（C端）
     * 
     * @param status 状态筛选（可选）
     * @return 拼团活动列表
     */
    List<GroupActivityListVO> selectGroupActivityList(Integer status);
    
    /**
     * 查询用户发起拼团活动列表（C端）
     * 
     * @param status 状态筛选（可选）
     * @return 用户发起拼团活动列表
     */
    List<UserInitiatedActivityListVO> selectUserInitiatedActivityList(Integer status);
    
    /**
     * 查询可发起的拼团活动列表
     * 
     * @return 可发起的拼团活动列表
     */
    List<InitiableActivityVO> selectInitiableActivities();
    
    /**
     * 查询我的拼团列表
     * 
     * @param userId 用户ID
     * @param groupStatus 组团状态筛选（可选）
     * @return 我的拼团列表
     */
    List<MyGroupListVO> selectMyGroupList(Long userId, Integer groupStatus);
    
    /**
     * 查询拼团详情
     * 
     * @param activityId 拼团活动ID
     * @param userId 当前用户ID（可为null）
     * @return 拼团详情
     */
    GroupDetailVO selectGroupDetail(Long activityId, Long userId);
    
    /**
     * 参与拼团
     * 
     * @param activityId 拼团活动ID
     * @param userId 用户ID
     * @param payMethod 支付方式
     * @return 订单号
     */
    String joinGroup(Long activityId, Long userId, String payMethod);
    
    /**
     * 发起拼团（创建组团）
     * 
     * @param dto 发起拼团请求参数
     * @param userId 用户ID
     * @return 发起结果
     */
    GroupCreateVO createGroupWork(GroupCreateDTO dto, Long userId);

    /**
     * 查询全量组团记录列表（管理端）
     *
     * @param groupStatus 组团状态筛选（可选）：0-进行中 1-已成团 2-已取消/过期
     * @return 组团记录列表
     */
    List<GroupWorkListVO> selectGroupWorkList(Integer groupStatus);
    
    /**
     * 模糊查询用户名列表（用于手动添加参团用户）
     *
     * @param keyword 搜索关键词（支持用户名、昵称模糊匹配）
     * @param limit 返回数量限制（默认20）
     * @return 用户信息列表
     */
    List<Map<String, Object>> searchUsernames(String keyword, Integer limit);
    
    /**
     * 手动添加用户到拼团（管理员操作）
     *
     * @param activityId 拼团活动ID
     * @param userId 用户ID
     * @param remark 备注说明
     * @param operatorId 操作人ID（管理员）
     * @return 添加结果
     */
    Map<String, Object> addUserToGroup(Long activityId, Long userId, String remark, Long operatorId);
}
