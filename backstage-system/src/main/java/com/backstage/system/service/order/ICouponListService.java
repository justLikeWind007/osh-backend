package com.backstage.system.service.order;

import java.util.List;
import com.backstage.system.domain.order.Coupon;

/**
 * 卡券 Service 接口
 * 
 * @author ruoyi
 * @date 2026-03-05
 */
public interface ICouponListService
{
    /**
     * 查询卡券
     * 
     * @param id 卡券主键
     * @return 卡券
     */
    public Coupon selectCardById(Long id);

    /**
     * 查询卡券列表
     * 
     * @param coupon 卡券
     * @return 卡券集合
     */
    public List<Coupon> selectCardList(Coupon coupon);

    /**
     * 新增卡券
     * 
     * @param coupon 卡券
     * @return 结果
     */
    public int insertCard(Coupon coupon);

    /**
     * 修改卡券
     * 
     * @param coupon 卡券
     * @return 结果
     */
    public int updateCard(Coupon coupon);

    /**
     * 批量删除卡券
     * 
     * @param ids 需要删除的卡券主键集合
     * @return 结果
     */
    public int deleteCardByIds(Long[] ids);

    /**
     * 删除卡券信息
     * 
     * @param id 卡券主键
     * @return 结果
     */
    public int deleteCardById(Long id);
}
