package com.backstage.system.controller.order;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.R;
import com.backstage.system.service.impl.order.IsWxPayNoServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pc/order/iswxpay")
public class IsWxPayController {

    @Autowired
    IsWxPayNoServiceImp mp;

    @Anonymous
    @PostMapping
    public R isWxPay(@RequestBody Map<String, String> params){

        // 查询订单是否支付 osh_wxpay表
        Boolean isWxPay = mp.isWxPayNo(params.get("no"));
        R r = new R();
        r.setMsg("fail");
        r.setData("……");
        if (isWxPay) {
            r.setCode(20000);
            Map map = new HashMap();
            map.put("trade_state", "NOTPAY");
            r.setData(map);
            r.setMsg("ok");
        }


        return r;
    }
}
