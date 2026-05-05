package com.backstage.system.controller.seckill;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.vo.seckill.SeckillActivityUserVO;
import com.backstage.system.domain.vo.seckill.SeckillResultVO;
import com.backstage.system.service.seckill.IOshSeckillActivityService;
import com.backstage.system.service.seckill.IOshSeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    /**
     * 接口8：查询进行中的秒杀活动列表（用户端）
     */
    @Anonymous
    @GetMapping("/activity/list")
    public TableDataInfo activeList() {
        startPage();
        List<SeckillActivityUserVO> list = activityService.selectActiveActivityList();
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
     * activityId：活动ID
     * itemId：活动内的商品明细ID（一个活动可含多个商品，需指定抢哪个）
     * 测试阶段用 @Anonymous，上线前改为需要登录并从 Token 获取 userId
     */
    @Anonymous
    @PostMapping("/do/{activityId}/{itemId}")
    public R<SeckillResultVO> doSeckill(@PathVariable Long activityId,
                                         @PathVariable Long itemId,
                                         @RequestParam Long userId) {
        try {
            SeckillResultVO result = orderService.doSeckill(activityId, itemId, userId);
            return R.ok(result);
        } catch (ServiceException e) {
            return R.fail(e.getMessage());
        }
    }

    /**
     * 接口11：查询秒杀结果（前端轮询）
     */
    @Anonymous
    @GetMapping("/order/result/{activityId}/{itemId}")
    public R<SeckillResultVO> getSeckillResult(@PathVariable Long activityId,
                                                @PathVariable Long itemId,
                                                @RequestParam Long userId) {
        SeckillResultVO result = orderService.getSeckillResult(activityId, itemId, userId);
        return result != null ? R.ok(result) : R.fail("暂未查到秒杀结果，请稍后重试");
    }

    /**
     * 接口12：取消秒杀订单
     */
    @Anonymous
    @PostMapping("/order/cancel/{seckillNo}")
    public R cancelOrder(@PathVariable String seckillNo,
                          @RequestParam Long userId) {
        try {
            orderService.cancelOrder(seckillNo, userId);
            return R.ok("订单已取消");
        } catch (ServiceException e) {
            return R.fail(e.getMessage());
        }
    }
}
