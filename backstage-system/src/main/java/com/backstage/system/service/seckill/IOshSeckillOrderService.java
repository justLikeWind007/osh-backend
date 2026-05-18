package com.backstage.system.service.seckill;

import com.backstage.system.domain.seckill.OshSeckillOrder;
import com.backstage.system.domain.vo.seckill.SeckillOrderAdminVO;
import com.backstage.system.domain.vo.seckill.SeckillResultVO;

import java.util.List;

/**
 * 秒杀订单 Service 接口
 *
 * @author backstage
 * @date 2026-04-28
 */
public interface IOshSeckillOrderService {

    /**
     * 接口7：管理端查询秒杀订单列表
     */
    List<SeckillOrderAdminVO> selectOrderList(OshSeckillOrder order);

    /**
     * 根据秒杀单号查询订单（含归属校验用）
     */
    OshSeckillOrder getOrderBySeckillNo(String seckillNo, Long userId);

    /**
     * 通过秒杀单号查询订单状态（支付完成后前端轮询用）
     */
    SeckillResultVO getOrderStatusBySeckillNo(String seckillNo, Long userId);

    /**
     * 接口10：执行秒杀
     * 流程：校验活动 → 校验明细 → Lua 脚本扣减库存 → 写订单
     *
     * @param activityId 活动ID
     * @param itemId     活动商品明细ID（一个活动可含多个商品，需指定抢哪个）
     * @param userId     当前用户ID
     * @param quantity   本次购买数量（不得超过 limitPerUser 限制）
     * @return 秒杀结果VO（含订单号和支付截止时间）
     */
    SeckillResultVO doSeckill(Long activityId, Long itemId, Long userId, int quantity);

    /**
     * 接口11：查询秒杀结果（前端轮询）
     *
     * @param activityId 活动ID
     * @param itemId     活动商品明细ID
     * @param userId     当前用户ID
     * @return 秒杀结果VO
     */
    SeckillResultVO getSeckillResult(Long activityId, Long itemId, Long userId);

    /**
     * 接口12：取消秒杀订单
     *
     * @param seckillNo 秒杀订单编号
     * @param userId    当前用户ID（校验归属）
     */
    void cancelOrder(String seckillNo, Long userId);
}
