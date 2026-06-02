package com.backstage.system.controller.user;

import com.backstage.common.annotation.OshUserEvent;
import com.backstage.common.annotation.OshUserLevel;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.user.OshUserViolation;
import com.backstage.system.mapper.user.OshUserMapper;
import com.backstage.system.mapper.user.OshUserViolationMapper;
import com.backstage.system.mapper.user.UserManageMapper;
import com.backstage.system.utils.UserContextUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 用户违规管理（仅创始人 level=6 可访问）
 */
@RestController
@RequestMapping("/pc/admin/user")
public class UserBlockController {

    /** 违规次数达到此值自动拉黑 */
    private static final int AUTO_BLOCK_THRESHOLD = 3;

    @Resource
    private OshUserMapper oshUserMapper;

    @Resource
    private OshUserViolationMapper oshUserViolationMapper;

    @Resource
    private UserManageMapper userManageMapper;

    /**
     * 标记用户违规（违规次数达到3次自动拉黑）
     */
    @PostMapping("/violation/record")
    @OshUserLevel(value = 6)
    @OshUserEvent(module = "用户模块", actionType = "违规记录", description = "用户违规记录")
    public R<String> recordViolation(@RequestBody Map<String, Object> params) {
        Long targetUserId = Long.valueOf(params.get("userId").toString());
        Integer violationType = Integer.valueOf(params.get("violationType").toString());
        String reason = params.get("reason") != null ? params.get("reason").toString() : "";

        Long operatorId = UserContextUtil.getCurrentUserId();

        // 记录违规
        OshUserViolation record = new OshUserViolation();
        record.setUserId(targetUserId);
        record.setViolationType(violationType);
        record.setReason(reason);
        record.setOperatorId(operatorId);
        oshUserViolationMapper.insert(record);

        // 更新违规次数
        OshUser user = oshUserMapper.selectById(targetUserId);
        int newCount = (user.getViolationCount() == null ? 0 : user.getViolationCount()) + 1;
        LambdaUpdateWrapper<OshUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OshUser::getId, targetUserId)
                .set(OshUser::getViolationCount, newCount);

        // 违规次数达到阈值自动拉黑
        if (newCount >= AUTO_BLOCK_THRESHOLD && user.getStatus() == 0) {
            updateWrapper.set(OshUser::getStatus, 1);
            oshUserMapper.update(null, updateWrapper);
            return R.ok("已标记违规，违规次数达到" + AUTO_BLOCK_THRESHOLD + "次，用户已被自动拉黑");
        }

        oshUserMapper.update(null, updateWrapper);
        return R.ok("已标记违规");
    }

    /**
     * 查询用户违规记录列表（包含已撤销的，通过 deleteFlag 区分）
     */
    @GetMapping("/violation/list")
    @OshUserLevel(value = 6)
    public R<List<Map<String, Object>>> getViolationList(@RequestParam Long userId) {
        List<Map<String, Object>> list = userManageMapper.selectViolationListAll(userId);
        return R.ok(list);
    }

    /**
     * 撤销违规记录
     */
    @PostMapping("/violation/revoke")
    @OshUserLevel(value = 6)
    public R<String> revokeViolation(@RequestBody Map<String, Object> params) {
        Long violationId = Long.valueOf(params.get("violationId").toString());

        OshUserViolation record = oshUserViolationMapper.selectById(violationId);
        if (record == null) return R.fail("违规记录不存在");
        if (record.getDeleteFlag() != null && record.getDeleteFlag() == 1) return R.fail("该记录已被撤销");

        // 标记为已撤销
        LambdaUpdateWrapper<OshUserViolation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(OshUserViolation::getId, violationId).set(OshUserViolation::getDeleteFlag, (byte) 1);
        oshUserViolationMapper.update(null, updateWrapper);

        // 减少用户违规次数
        OshUser user = oshUserMapper.selectById(record.getUserId());
        if (user != null && user.getViolationCount() != null && user.getViolationCount() > 0) {
            int newCount = user.getViolationCount() - 1;
            LambdaUpdateWrapper<OshUser> userUpdate = new LambdaUpdateWrapper<>();
            userUpdate.eq(OshUser::getId, record.getUserId())
                    .set(OshUser::getViolationCount, newCount);
            // 违规次数降到阈值以下，自动恢复正常状态
            if (newCount < AUTO_BLOCK_THRESHOLD && user.getStatus() == 1) {
                userUpdate.set(OshUser::getStatus, 0);
            }
            oshUserMapper.update(null, userUpdate);
        }

        return R.ok("违规记录已撤销");
    }
}
