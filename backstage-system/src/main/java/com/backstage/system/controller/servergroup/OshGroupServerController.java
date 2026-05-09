package com.backstage.system.controller.servergroup;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.common.utils.SecurityUtils;
import com.backstage.system.domain.dto.GroupCreateDTO;
import com.backstage.system.domain.dto.AddUserToGroupDTO;
import com.backstage.system.domain.vo.GroupActivityListVO;
import com.backstage.system.domain.vo.GroupCreateVO;
import com.backstage.system.domain.vo.GroupDetailVO;
import com.backstage.system.domain.vo.GroupWorkListVO;
import com.backstage.system.domain.vo.InitiableActivityVO;
import com.backstage.system.domain.vo.MyGroupListVO;
import com.backstage.system.domain.vo.UserInitiatedActivityListVO;
import com.backstage.system.service.servergroup.IOshGroupServerService;
import com.backstage.system.utils.UserContextUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 服务器拼团C端Controller
 * 
 * @author system
 * @date 2026-04-18
 */
@Api(tags = "服务器拼团C端接口")
@RestController
@RequestMapping("/pc/group")
public class OshGroupServerController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(OshGroupServerController.class);
    
    @Autowired
    private IOshGroupServerService groupServerService;
    
    /**
     * 查询拼团列表
     * 
     * GET /pc/group/activity/list
     * 
     * @param status 状态筛选（可选）
     * @return 拼团活动列表
     */
    @Anonymous
    @ApiOperation("查询拼团列表")
    @GetMapping("/activity/list")
    public TableDataInfo list(
            @ApiParam("状态筛选：1-进行中 2-拼团成功 3-已结束") 
            @RequestParam(value = "status", required = false) Integer status) {
        startPage();
        List<GroupActivityListVO> list = groupServerService.selectGroupActivityList(status);
        return getDataTable(list);
    }
    
    /**
     * 查询用户发起拼团列表
     * 
     * GET /pc/group/activity/initiated/list
     * 
     * @param status 状态筛选（可选）
     * @return 用户发起拼团活动列表
     */
    @Anonymous
    @ApiOperation("查询用户发起拼团列表")
    @GetMapping("/activity/initiated/list")
    public TableDataInfo initiatedList(
            @ApiParam("状态筛选：1-进行中 2-拼团成功 3-已结束") 
            @RequestParam(value = "status", required = false) Integer status) {
        startPage();
        List<UserInitiatedActivityListVO> list = groupServerService.selectUserInitiatedActivityList(status);
        return getDataTable(list);
    }
    
    /**
     * 接口2：查询我的拼团列表
     * 权限要求：登录用户都可查看自己的拼团记录
     * 
     * GET /pc/group/activity/mylist
     * 
     * @param groupStatus 组团状态筛选（可选）
     * @return 我的拼团列表
     */
    @ApiOperation("查询我的拼团列表")
    @GetMapping("/activity/mylist")
    public R<List<MyGroupListVO>> myList(
            @ApiParam("组团状态：0-进行中 1-已成团 2-已取消/过期") 
            @RequestParam(value = "groupStatus", required = false) Integer groupStatus) {
        // 从 ThreadLocal 获取网校用户ID（OshAuthenticationFilter 已写入）
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID, Long.class);
        if (userId == null) {
            return R.fail("请先登录");
        }
        
        // 查看我的拼团无需额外权限检查，登录用户均可查看自己的记录
        List<MyGroupListVO> list = groupServerService.selectMyGroupList(userId, groupStatus);
        return R.ok(list, "查询成功");
    }
    
    /**
     * 接口3：拼团详情
     * 
     * GET /pc/group/work/detail
     * 
     * @param activityId 拼团活动ID
     * @return 拼团详情
     */
    @Anonymous
    @ApiOperation("拼团详情")
    @GetMapping("/work/detail")
    public R<GroupDetailVO> detail(
            @ApiParam("拼团活动ID") @RequestParam(value = "activityId") Long activityId) {
        // 尝试获取当前用户ID（如果已登录）
        Long userId = null;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            // 未登录，userId为null
            logger.debug("用户未登录，将以匿名身份查看拼团详情");
        }
        
        GroupDetailVO detail = groupServerService.selectGroupDetail(activityId, userId);
        return R.ok(detail);
    }
    
    /**
     * 接口4：参与拼团
     * 权限要求：登录用户都可参与拼团
     * 
     * POST /pc/group/work/join
     * 
     * @param activityId 拼团活动ID
     * @param payMethod 支付方式：wechat-微信支付 alipay-支付宝
     * @return 订单号
     */
    @ApiOperation("参与拼团")
    @PostMapping("/work/join")
    public R<String> join(
            @ApiParam("拼团活动ID") @RequestParam(value = "activityId") Long activityId,
            @ApiParam("支付方式：wechat-微信支付 alipay-支付宝") 
            @RequestParam(value = "payMethod", defaultValue = "wechat") String payMethod) {
        // 从 ThreadLocal 获取网校用户ID（OshAuthenticationFilter 已写入）
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID, Long.class);
        if (userId == null) {
            return R.fail("请先登录");
        }
        
        // 参与拼团无需额外权限检查，登录用户均可参与
        String orderNo = groupServerService.joinGroup(activityId, userId, payMethod);
        return R.ok(orderNo, "参团成功");
    }
    
    /**
     * 接口5：获取可发起的拼团活动列表
     * 权限要求：只有管理员（level <= 2）可查看可发起的拼团活动
     * 
     * GET /pc/group/activity/initiable
     * 
     * @return 可发起的拼团活动列表
     */
    @ApiOperation("获取可发起的拼团活动列表")
    @GetMapping("/activity/initiable")
    public R<List<InitiableActivityVO>> initiableActivities() {
        // 权限验证：只有管理员（level <= 2）可查看可发起的拼团活动
        Integer userLevel = UserContextUtil.getCurrentLevel();
        if (userLevel == null || userLevel > 2) {
            return R.fail("权限不足，只有管理员可以查看可发起的拼团活动");
        }
        
        List<InitiableActivityVO> list = groupServerService.selectInitiableActivities();
        return R.ok(list, "查询成功");
    }
    
    /**
     * 接口6：发起拼团（创建组团）
     * 权限要求：只有管理员（level <= 2）可发起拼团
     * 
     * POST /pc/group/work/create
     * 
     * @param dto 发起拼团请求参数
     * @return 发起结果
     */
    @ApiOperation("发起拼团")
    @PostMapping("/work/create")
    public R<GroupCreateVO> create(
            @ApiParam("发起拼团请求参数") @Validated @RequestBody GroupCreateDTO dto) {
        // 从 ThreadLocal 获取网校用户ID（OshAuthenticationFilter 已写入）
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID, Long.class);
        if (userId == null) {
            return R.fail("请先登录");
        }
        
        // 权限验证：只有管理员（level <= 2）可以发起拼团
        Integer userLevel = UserContextUtil.getCurrentLevel();
        if (userLevel == null || userLevel > 2) {
            return R.fail("权限不足，只有管理员可以发起拼团");
        }
        
        try {
            GroupCreateVO result = groupServerService.createGroupWork(dto, userId);
            return R.ok(result, "发起成功");
        } catch (Exception e) {
            logger.error("发起拼团失败", e);
            return R.fail(e.getMessage());
        }
    }

    /**
     * 接口7：查询全量组团记录列表（管理端）
     *
     * GET /pc/group/work/list
     *
     * @param groupStatus 组团状态筛选（可选）：0-进行中 1-已成团 2-已取消/过期
     * @return 组团记录列表（分页）
     */
    @Anonymous
    @ApiOperation("查询全量组团记录列表")
    @GetMapping("/work/list")
    public TableDataInfo workList(
            @ApiParam("组团状态：0-进行中 1-已成团 2-已取消/过期")
            @RequestParam(value = "groupStatus", required = false) Integer groupStatus) {
        startPage();
        List<GroupWorkListVO> list = groupServerService.selectGroupWorkList(groupStatus);
        return getDataTable(list);
    }
    
    /**
     * 接口8：模糊查询用户名列表（用于手动添加参团用户）
     *
     * GET /pc/group/user/search
     *
     * @param keyword 搜索关键词（支持用户名、昵称模糊匹配）
     * @param limit 返回数量限制（可选，默认20）
     * @return 用户信息列表
     */
    @ApiOperation("模糊查询用户名列表")
    @GetMapping("/user/search")
    public R<List<Map<String, Object>>> searchUsers(
            @ApiParam("搜索关键词（支持用户名、昵称模糊匹配）")
            @RequestParam(value = "keyword", required = true) String keyword,
            @ApiParam("返回数量限制（可选，默认20）")
            @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit) {
        List<Map<String, Object>> users = groupServerService.searchUsernames(keyword, limit);
        return R.ok(users, "查询成功");
    }
    
    /**
     * 接口9：手动添加用户到拼团（管理员操作）
     * 权限要求：只有管理员（level <= 2）可手动添加用户到拼团
     *
     * POST /pc/group/user/add
     *
     * @param dto 添加用户请求参数
     * @return 添加结果
     */
    @ApiOperation("手动添加用户到拼团")
    @PostMapping("/user/add")
    public R<Map<String, Object>> addUserToGroup(
            @ApiParam("添加用户请求参数") @Validated @RequestBody AddUserToGroupDTO dto) {
        // 从 ThreadLocal 获取管理员ID
        Long operatorId = ThreadLocalUtil.get(OshUserConstants.USER_ID, Long.class);
        if (operatorId == null) {
            return R.fail("请先登录");
        }
        
        // 权限验证：只有管理员（level <= 2）可以手动添加用户到拼团
        Integer userLevel = UserContextUtil.getCurrentLevel();
        if (userLevel == null || userLevel > 2) {
            return R.fail("权限不足，只有管理员可以手动添加用户到拼团");
        }
        
        try {
            Map<String, Object> result = groupServerService.addUserToGroup(
                dto.getActivityId(), 
                dto.getUserId(), 
                dto.getRemark(), 
                operatorId
            );
            return R.ok(result, result.get("message").toString());
        } catch (Exception e) {
            logger.error("手动添加用户到拼团失败", e);
            return R.fail(e.getMessage());
        }
    }
}
