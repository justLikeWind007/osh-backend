package com.backstage.system.service.member;

import com.backstage.system.domain.member.OshMemberPlan;
import com.backstage.system.domain.member.dto.MemberCheckoutDTO;
import com.backstage.system.domain.member.vo.MemberCenterVO;
import com.backstage.system.domain.member.vo.MemberOrderVO;
import com.backstage.system.domain.vo.pay.OrderCheckoutRespVO;

import java.util.List;

public interface MemberCenterService {
    MemberCenterVO getCenter(Long userId);
    List<OshMemberPlan> listPlans();
    OrderCheckoutRespVO checkout(Long userId, MemberCheckoutDTO dto);
    List<MemberOrderVO> listOrders(Long userId);
}
