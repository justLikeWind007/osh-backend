package com.backstage.system.service.order.impl;

import java.util.List;

import com.backstage.system.domain.order.Coupon;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.backstage.system.mapper.order.CouponListMapper;
import com.backstage.system.service.order.ICouponListService;

/**
 * 卡券 Service 业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-05
 */
@Service
public class CouponListServiceImpl implements ICouponListService
{
    @Autowired
    private CouponListMapper cardMapper;

    /**
     * 查询卡券
     * 
     * @param id 卡券主键
     * @return 卡券
     */
    @Override
    public Coupon selectCardById(Long id)
    {
        return cardMapper.selectCardById(id);
    }

    /**
     * 查询卡券列表
     * 
     * @param coupon 卡券
     * @return 卡券
     */
    @Override
    public List<Coupon> selectCardList(Coupon coupon)
    {
        return cardMapper.selectCardList(coupon);
    }

    /**
     * 新增卡券
     * 
     * @param coupon 卡券
     * @return 结果
     */
    @Override
    public int insertCard(Coupon coupon)
    {
        return cardMapper.insertCard(coupon);
    }

    /**
     * 修改卡券
     * 
     * @param coupon 卡券
     * @return 结果
     */
    @Override
    public int updateCard(Coupon coupon)
    {
        return cardMapper.updateCard(coupon);
    }

    /**
     * 批量删除卡券
     * 
     * @param ids 需要删除的卡券主键
     * @return 结果
     */
    @Override
    public int deleteCardByIds(Long[] ids)
    {
        return cardMapper.deleteCardByIds(ids);
    }

    /**
     * 删除卡券信息
     * 
     * @param id 卡券主键
     * @return 结果
     */
    @Override
    public int deleteCardById(Long id)
    {
        return cardMapper.deleteCardById(id);
    }
}
