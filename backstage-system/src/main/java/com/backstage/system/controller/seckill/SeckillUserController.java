package com.backstage.system.controller.seckill;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.RateLimiter;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.LimitType;
import com.backstage.common.exception.ServiceException;
import com.backstage.common.utils.ip.IpUtils;
import com.backstage.system.domain.order.enums.PayChannelEnum;
import com.backstage.system.domain.seckill.OshSeckillOrder;
import com.backstage.system.domain.user.OshUser;
import com.backstage.system.domain.vo.order.PayResponse;
import com.backstage.system.domain.vo.seckill.SeckillActivityUserVO;
import com.backstage.system.domain.vo.seckill.SeckillRecentOrderVO;
import com.backstage.system.domain.vo.seckill.SeckillResultVO;
import com.backstage.system.service.order.PayService;
import com.backstage.system.service.seckill.IOshSeckillActivityService;
import com.backstage.system.service.seckill.IOshSeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.List;

import static com.backstage.system.utils.UserContextUtil.getCurrentUser;

/**
 * 秒杀用户端 Controller
 * 接口8：用户端活动列表（仅进行中）
 * 接口9：用户端活动详情（仅进行中）
 * 接口10：执行秒杀
 * 接口11：查询秒杀结果
 * 接口12：取消秒杀订单
 *
 * @author backstage
 * @date 2026-04-28
 */
@RestController
@RequestMapping("/pc/seckill/user")
public class SeckillUserController extends BaseController {

    @Autowired
    private IOshSeckillActivityService activityService;

    @Autowired
    private IOshSeckillOrderService orderService;

    @Autowired
    private PayService payService;

    /**
     * 接口8：查询进行中的秒杀活动列表（用户端）
     * 支持按商品名称模糊搜索、按商品类型筛选
     */
    @Anonymous
    @GetMapping("/activity/list")
    public TableDataInfo activeList(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer goodsType) {
        startPage();
        List<SeckillActivityUserVO> list = activityService.selectActiveActivityList(title, goodsType);
        return getDataTable(list);
    }

    /**
     * 接口9：查询秒杀活动详情（用户端）
     */
    @Anonymous
    @GetMapping("/activity/detail/{id}")
    public R<SeckillActivityUserVO> activeDetail(@PathVariable Long id) {
        SeckillActivityUserVO vo = activityService.selectActiveActivityById(id);
        return vo != null ? R.ok(vo) : R.fail("活动不存在或已结束");
    }

    /**
     * 接口10：执行秒杀（核心接口）
     * userId 从登录 Token 中获取，无需前端传参
     * quantity 为本次购买数量，可选，默认为 1
     */
    @RateLimiter(key = "seckill:doSeckill:", time = 5, count = 3, limitType = LimitType.IP)
    @PostMapping("/do/{activityId}/{itemId}")
    public R<SeckillResultVO> doSeckill(@PathVariable Long activityId,
                                         @PathVariable Long itemId,
                                         @RequestParam(defaultValue = "1") int quantity) {
        try {
            Long userId = getCurrentUser().getId();
            SeckillResultVO result = orderService.doSeckill(activityId, itemId, userId, quantity);
            return R.ok(result);
        } catch (ServiceException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 接口11：查询秒杀结果（前端轮询）
     * userId 从登录 Token 中获取，无需前端传参
     */
    @GetMapping("/order/result/{activityId}/{itemId}")
    public R<SeckillResultVO> getSeckillResult(@PathVariable Long activityId,
                                                @PathVariable Long itemId) {
        Long userId = getCurrentUser().getId();
        SeckillResultVO result = orderService.getSeckillResult(activityId, itemId, userId);
        return result != null ? R.ok(result) : R.fail("暂未查到秒杀结果，请稍后重试");
    }

    /**
     * 接口12：取消秒杀订单
     * userId 从登录 Token 中获取，无需前端传参
     */
    @PostMapping("/order/cancel/{seckillNo}")
    public R cancelOrder(@PathVariable String seckillNo) {
        try {
            Long userId = getCurrentUser().getId();
            orderService.cancelOrder(seckillNo, userId);
            return R.ok("订单已取消");
        } catch (ServiceException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 接口14：通过秒杀单号查询订单状态（支付完成后前端轮询用）
     */
    @GetMapping("/order/status/{seckillNo}")
    public R<SeckillResultVO> getOrderStatus(@PathVariable String seckillNo) {
        Long userId = getCurrentUser().getId();
        SeckillResultVO result = orderService.getOrderStatusBySeckillNo(seckillNo, userId);
        return result != null ? R.ok(result) : R.fail("订单不存在");
    }
    /*
     * 前端拿到 seckillNo 后调此接口，返回支付链接（payurl）或二维码（qrcode）
     * channel 可选，支持 wxpay / alipay，默认微信支付
     */    @PostMapping("/order/pay/{seckillNo}")
    public R<PayResponse> pay(@PathVariable String seckillNo,
                              @RequestParam(required = false, defaultValue = "wxpay") String channel) {
        Long userId = getCurrentUser().getId();

        // 查询秒杀订单
        OshSeckillOrder order = orderService.getOrderBySeckillNo(seckillNo, userId);
        if (order == null) {
            return R.fail("订单不存在");
        }
        if (!order.getUserId().equals(userId)) {
            return R.fail("无权操作此订单");
        }
        if (order.getStatus() != 0) {
            return R.fail("订单状态不正确，无法发起支付");
        }

        // 校验支付渠道，不合法时降级为微信支付
        PayChannelEnum channelEnum = PayChannelEnum.fromValue(channel);
        String channelValue = (channelEnum != null && channelEnum != PayChannelEnum.FREE)
                ? channelEnum.getValue()
                : PayChannelEnum.WXPAY.getValue();

        // 发起支付，以 seckillNo 作为外部订单号
        String clientIp = IpUtils.getIpAddr();
        String money = order.getTotalAmount().toString();  // 实付总金额（seckillPrice × quantity）
        String name = order.getGoodsTitle();
        PayResponse resp = payService.createPay(seckillNo, name, money, clientIp, channelValue);

        if (resp.getCode() != 1) {
            return R.fail("发起支付失败：" + resp.getMsg());
        }
        return R.ok(resp);
    }

    /**
     * 接口15：查询最近成交记录（用于首页滚动条展示）
     * 不需要登录，匿名可访问
     * limit 默认10条，最大50条
     */
    @Anonymous
    @GetMapping("/recent/orders")
    public R<List<SeckillRecentOrderVO>> recentOrders(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        return R.ok(orderService.getRecentPaidOrders(limit));
    }
}
