package com.backstage.system.controller.member;

import com.backstage.common.core.domain.R;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.member.OshMemberPlan;
import com.backstage.system.domain.member.dto.MemberCheckoutDTO;
import com.backstage.system.domain.member.vo.MemberCenterVO;
import com.backstage.system.domain.member.vo.MemberOrderVO;
import com.backstage.system.domain.vo.pay.OrderCheckoutRespVO;
import com.backstage.system.service.member.MemberCenterService;
import com.backstage.system.utils.UserContextUtil;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/pc/user/member")
public class MemberCenterController {
    @Resource
    private MemberCenterService memberCenterService;

    @GetMapping("/center")
    public R<MemberCenterVO> center() {
        return R.ok(memberCenterService.getCenter(currentUserId()));
    }

    @GetMapping("/plans")
    public R<List<OshMemberPlan>> plans() {
        return R.ok(memberCenterService.listPlans());
    }

    @PostMapping("/checkout")
    public R<OrderCheckoutRespVO> checkout(@Validated @RequestBody MemberCheckoutDTO dto) {
        return R.ok(memberCenterService.checkout(currentUserId(), dto));
    }

    @GetMapping("/orders")
    public R<List<MemberOrderVO>> orders() {
        return R.ok(memberCenterService.listOrders(currentUserId()));
    }

    private Long currentUserId() {
        Long userId = UserContextUtil.getCurrentUserIdSafely();
        if (userId == null) {
            throw new ServiceException("请先登录");
        }
        return userId;
    }
}
