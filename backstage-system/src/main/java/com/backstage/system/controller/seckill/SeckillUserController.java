package com.backstage.system.controller.seckill;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.RateLimiter;
import com.backstage.common.core.controller.BaseController;
import com.backstage.common.core.domain.R;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.enums.LimitType;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.config.properties.SearchEsProperties;
import com.backstage.system.domain.vo.seckill.SeckillActivityUserVO;
import com.backstage.system.domain.vo.seckill.SeckillAnnouncementVO;
import com.backstage.system.domain.vo.seckill.SeckillRecentOrderVO;
import com.backstage.system.domain.vo.seckill.SeckillResultVO;
import com.backstage.system.service.announcement.ISeckillAnnouncementService;
import com.backstage.system.service.seckill.IOshSeckillActivityService;
import com.backstage.system.service.seckill.IOshSeckillOrderService;
import com.backstage.system.service.seckill.ISeckillItemEsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(SeckillUserController.class);

    @Autowired
    private IOshSeckillActivityService activityService;

    @Autowired
    private IOshSeckillOrderService orderService;

    @Autowired
    private ISeckillAnnouncementService seckillAnnouncementService;

    @Autowired
    private ISeckillItemEsService seckillItemEsService;

    @Autowired
    private SearchEsProperties searchEsProperties;

    /**
     * 接口8：查询进行中的秒杀活动列表（用户端）
     * 优先走 ES 搜索，ES 不可用时降级到 MySQL
     * 两条路径统一返回活动维度结构（活动 + 嵌套 items），前端无需区分
     */
    @Anonymous
    @GetMapping("/activity/list")
    public TableDataInfo activeList(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer goodsType,
            @RequestParam(required = false) List<String> tagNameList) {
        if (searchEsProperties.isEnabled()) {
            try {
                log.info("使用 ES 查询秒杀活动列表, keyword={}, goodsType={}, tagNameList={}", title, goodsType, tagNameList);
                com.backstage.common.core.page.PageDomain pageDomain =
                        com.backstage.common.core.page.TableSupport.buildPageRequest();
                int pageNum = pageDomain.getPageNum() != null ? pageDomain.getPageNum() : 1;
                int pageSize = pageDomain.getPageSize() != null ? pageDomain.getPageSize() : 10;
                com.backstage.common.response.PageResponse<com.backstage.system.domain.vo.seckill.SeckillActivityUserVO> page =
                        seckillItemEsService.searchActivities(title, goodsType, tagNameList, pageNum, pageSize);
                TableDataInfo rsp = new TableDataInfo();
                rsp.setCode(com.backstage.common.constant.HttpStatus.SUCCESS);
                rsp.setMsg("查询成功");
                rsp.setRows(page.getRows());
                rsp.setTotal(page.getTotal());
                return rsp;
            } catch (Exception ex) {
                log.warn("秒杀 ES 查询失败，降级到 MySQL, keyword={}, goodsType={}", title, goodsType, ex);
            }
        }
        // 降级：MySQL 路径，原有逻辑不变，返回结构与 ES 路径一致
        startPage();
        List<SeckillActivityUserVO> list = activityService.selectActiveActivityList(title, goodsType, tagNameList);
        return getDataTable(list);
    }

    /**
     * 全量同步进行中活动的秒杀商品明细到 ES（运维接口）
     */
    @Anonymous
    @PostMapping("/esSync/all")
    public R<Integer> syncAllItemsToEs() {
        return R.ok(seckillItemEsService.syncAllItemsToEs(), "ok");
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
     * 返回：seckillNo 立即可用；orderNo 在消费者 checkout 成功后才有值，前端需轮询 getSeckillResult() 获取
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
     * 外部按 seckillNo（秒杀尝试号）取消，内部通过秒杀单查到 orderNo 再调支付系统
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
     * 接口14：通过秒杀尝试号查询订单状态（支付完成后前端轮询用）
     * seckillNo 是秒杀尝试号，唯一标识一次有效秒杀尝试
     */
    @GetMapping("/order/status/{seckillNo}")
    public R<SeckillResultVO> getOrderStatus(@PathVariable String seckillNo) {
        Long userId = getCurrentUser().getId();
        SeckillResultVO result = orderService.getOrderStatusBySeckillNo(seckillNo, userId);
        return result != null ? R.ok(result) : R.fail("订单不存在");
    }
    /**
     * 接口15：查询秒杀动态栏（最近成交记录）
     * 数据来源：osh_announcement（biz_type='seckill_dynamic'）
     * 不需要登录，匿名可访问
     */
    @Anonymous
    @GetMapping("/recent/orders")
    public R<List<SeckillAnnouncementVO>> recentOrders(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        return R.ok(seckillAnnouncementService.getSeckillDynamics(limit));
    }

    /**
     * 接口16：查询秒杀公告栏
     * 数据来源：osh_announcement（biz_type='seckill_notice'）
     * 不需要登录，匿名可访问
     */
    @Anonymous
    @GetMapping("/announcement/notices")
    public R<List<SeckillAnnouncementVO>> seckillNotices(
            @RequestParam(required = false, defaultValue = "10") int limit) {
        return R.ok(seckillAnnouncementService.getSeckillNotices(limit));
    }
}
