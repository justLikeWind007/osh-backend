package com.backstage.system.service.member.impl;

import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.member.OshMemberOrder;
import com.backstage.system.domain.member.OshMemberPlan;
import com.backstage.system.domain.member.dto.MemberCheckoutDTO;
import com.backstage.system.domain.member.vo.MemberCenterVO;
import com.backstage.system.domain.member.vo.MemberOrderVO;
import com.backstage.system.domain.member.vo.MemberStatusVO;
import com.backstage.system.domain.order.enums.ProductTypeEnum;
import com.backstage.system.domain.vo.pay.OrderCheckoutReqVO;
import com.backstage.system.domain.vo.pay.OrderCheckoutRespVO;
import com.backstage.system.mapper.member.OshMemberOrderMapper;
import com.backstage.system.mapper.member.OshMemberPlanMapper;
import com.backstage.system.service.member.MemberCenterService;
import com.backstage.system.service.order.OrderCheckoutService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberCenterServiceImpl implements MemberCenterService {
    private static final String MEMBER_TYPE_VIP = "vip";
    private static final String MEMBER_TYPE_SMALL_CLASS = "small_class";
    private static final int PLAN_STATUS_ENABLED = 1;
    private static final int PAY_STATUS_PENDING = 0;
    private static final int GRANT_STATUS_PENDING = 0;

    @Resource
    private OshMemberPlanMapper memberPlanMapper;

    @Resource
    private OshMemberOrderMapper memberOrderMapper;

    @Resource
    private OrderCheckoutService orderCheckoutService;

    @Override
    public MemberCenterVO getCenter(Long userId) {
        MemberStatusVO vip = defaultIfNull(
                memberOrderMapper.selectMemberStatus(userId, MEMBER_TYPE_VIP),
                MEMBER_TYPE_VIP,
                "VIP用户"
        );
        MemberStatusVO smallClass = defaultIfNull(
                memberOrderMapper.selectMemberStatus(userId, MEMBER_TYPE_SMALL_CLASS),
                MEMBER_TYPE_SMALL_CLASS,
                "小班用户"
        );

        MemberCenterVO vo = new MemberCenterVO();
        vo.setVip(vip);
        vo.setSmallClass(smallClass);
        vo.setCurrent(resolveCurrent(vip, smallClass));
        vo.setPlans(listPlans());
        return vo;
    }

    @Override
    public List<OshMemberPlan> listPlans() {
        LambdaQueryWrapper<OshMemberPlan> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OshMemberPlan::getDeleteFlag, (byte) 0)
                .eq(OshMemberPlan::getStatus, PLAN_STATUS_ENABLED)
                .orderByAsc(OshMemberPlan::getSort)
                .orderByAsc(OshMemberPlan::getId);
        return memberPlanMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderCheckoutRespVO checkout(Long userId, MemberCheckoutDTO dto) {
        if (userId == null) {
            throw new ServiceException("请先登录");
        }
        OshMemberPlan plan = memberPlanMapper.selectById(dto.getPlanId());
        validatePlan(plan);

        BigDecimal originalAmount = defaultMoney(plan.getOriginalPrice(), plan.getPrice());
        OrderCheckoutReqVO reqVO = new OrderCheckoutReqVO();
        reqVO.setUserId(userId);
        reqVO.setProductType(ProductTypeEnum.MEMBER.getCode());
        reqVO.setProductId(plan.getId());
        reqVO.setProductName(plan.getPlanName());
        reqVO.setOriginalAmount(originalAmount);
        reqVO.setDiscountAmount(originalAmount.subtract(plan.getPrice()));
        reqVO.setPayableAmount(plan.getPrice());
        reqVO.setChannel(dto.getChannel());
        reqVO.setUsePoints(false);

        OrderCheckoutRespVO checkout = orderCheckoutService.checkout(reqVO);
        memberOrderMapper.insert(buildMemberOrder(userId, plan, checkout.getOrderNo()));
        return checkout;
    }

    @Override
    public List<MemberOrderVO> listOrders(Long userId) {
        if (userId == null) {
            throw new ServiceException("请先登录");
        }
        return memberOrderMapper.selectMemberOrders(userId);
    }

    private OshMemberOrder buildMemberOrder(Long userId, OshMemberPlan plan, String orderNo) {
        OshMemberOrder order = new OshMemberOrder();
        LocalDateTime now = LocalDateTime.now();
        order.setUserId(userId);
        order.setPlanId(plan.getId());
        order.setOrderNo(orderNo);
        order.setMemberType(plan.getMemberType());
        order.setPlanNameSnapshot(plan.getPlanName());
        order.setDurationMonths(plan.getDurationMonths());
        order.setPayAmount(plan.getPrice());
        order.setPayStatus(PAY_STATUS_PENDING);
        order.setGrantStatus(GRANT_STATUS_PENDING);
        order.setCreateTime(now);
        order.setCreateBy(userId);
        order.setUpdateTime(now);
        order.setUpdateBy(userId);
        order.setDeleteFlag((byte) 0);
        return order;
    }

    private void validatePlan(OshMemberPlan plan) {
        if (plan == null || !Byte.valueOf((byte) 0).equals(plan.getDeleteFlag())) {
            throw new ServiceException("会员套餐不存在");
        }
        if (!Integer.valueOf(PLAN_STATUS_ENABLED).equals(plan.getStatus())) {
            throw new ServiceException("会员套餐已下架");
        }
        if (!MEMBER_TYPE_VIP.equals(plan.getMemberType()) && !MEMBER_TYPE_SMALL_CLASS.equals(plan.getMemberType())) {
            throw new ServiceException("会员套餐类型不正确");
        }
        if (plan.getDurationMonths() == null || plan.getDurationMonths() <= 0) {
            throw new ServiceException("会员套餐时长不正确");
        }
        if (MEMBER_TYPE_SMALL_CLASS.equals(plan.getMemberType()) && plan.getDurationMonths() < 12) {
            throw new ServiceException("小班用户仅支持年付套餐");
        }
        if (plan.getPrice() == null || plan.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("会员套餐价格不正确");
        }
    }

    private BigDecimal defaultMoney(BigDecimal value, BigDecimal fallback) {
        return value == null ? fallback : value;
    }

    private MemberStatusVO defaultIfNull(MemberStatusVO status, String type, String name) {
        if (status != null) {
            status.setActive(Boolean.TRUE.equals(status.getActive()));
            status.setRemainingDays(status.getRemainingDays() == null ? 0L : status.getRemainingDays());
            return status;
        }
        MemberStatusVO empty = new MemberStatusVO();
        empty.setMemberType(type);
        empty.setMemberName(name);
        empty.setActive(false);
        empty.setRemainingDays(0L);
        return empty;
    }

    private MemberStatusVO resolveCurrent(MemberStatusVO vip, MemberStatusVO smallClass) {
        if (Boolean.TRUE.equals(smallClass.getActive())) {
            return smallClass;
        }
        if (Boolean.TRUE.equals(vip.getActive())) {
            return vip;
        }
        MemberStatusVO normal = new MemberStatusVO();
        normal.setMemberType("user");
        normal.setMemberName("普通用户");
        normal.setActive(true);
        normal.setRemainingDays(0L);
        return normal;
    }
}
