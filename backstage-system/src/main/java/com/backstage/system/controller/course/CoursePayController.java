package com.backstage.system.controller.course;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.common.exception.ServiceException;
import com.backstage.system.domain.vo.order.PayResponse;
import com.backstage.system.service.course.ICoursePayService;
import com.backstage.system.utils.UserContextUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Course payment HTTP entry.
 *
 * Endpoints are intentionally namespaced under /pc/course/pay so they live next to other course
 * endpoints (e.g. OshCourseController @ /pc/course/*) without colliding with any existing
 * method-level path. The shared payment controllers (PayController / WxPayController /
 * IsWxPayController) are left untouched and continue to serve any other module that relies on them.
 */
@RestController
@RequestMapping("/pc/course/pay")
public class CoursePayController {

    @Autowired
    private ICoursePayService coursePayService;

    /**
     * Create a course payment order and return a QR code string (wxpay or alipay).
     *
     * Request body example:
     *   { "course_id": 1000, "pay_type": "wxpay" }
     *
     * Response data example:
     *   { "qrcode": "weixin://wxpay/bizpayurl?pr=xxx",
     *     "payurl": "",
     *     "out_trade_no": "abc123",
     *     "pay_type": "wxpay" }
     */
    @Anonymous
    @PostMapping("/create")
    public R<Map<String, Object>> create(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = UserContextUtil.getCurrentUserId();
        if (userId == null) {
            return R.fail("请先登录后再支付");
        }
        Long courseId = parseCourseId(body.get("course_id"));
        String payType = body.get("pay_type") == null ? "wxpay" : body.get("pay_type").toString();
        String clientIp = resolveClientIp(request);

        try {
            PayResponse gatewayResp = coursePayService.createCoursePay(courseId, payType, clientIp, userId);

            // Gateway uses code=1 for success; surface the failure to the client verbatim.
            if (gatewayResp.getCode() != 1) {
                String msg = gatewayResp.getMsg() == null ? "支付网关返回失败" : gatewayResp.getMsg();
                return R.fail(msg);
            }

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("qrcode", gatewayResp.getQrcode());
            data.put("payurl", gatewayResp.getPayurl());
            data.put("out_trade_no", gatewayResp.getOut_trade_no());
            data.put("pay_type", payType);
            return R.ok(data);
        } catch (ServiceException e) {
            return R.fail(e.getMessage());
        } catch (Exception e) {
            return R.fail("创建支付订单失败: " + e.getMessage());
        }
    }

    /**
     * Poll the gateway for the latest status of a previously created order.
     *
     * Query example: /pc/course/pay/status?out_trade_no=abc123
     * Response data: { "paid": true }
     */
    @Anonymous
    @GetMapping("/status")
    public R<Map<String, Object>> status(@RequestParam("out_trade_no") String outTradeNo) {
        Long userId = UserContextUtil.getCurrentUserId();
        if (userId == null) {
            return R.fail("请先登录后再查询支付状态");
        }
        boolean paid = coursePayService.isCoursePaid(outTradeNo, userId);
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("paid", paid);
        return R.ok(data);
    }

    private Long parseCourseId(Object raw) {
        if (raw == null) {
            throw new ServiceException("course_id 不能为空");
        }
        try {
            return Long.parseLong(raw.toString());
        } catch (NumberFormatException e) {
            throw new ServiceException("course_id 必须是整数");
        }
    }

    private String resolveClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
