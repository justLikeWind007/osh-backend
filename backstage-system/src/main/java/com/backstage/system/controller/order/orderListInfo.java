package com.backstage.system.controller.order;

import com.backstage.common.core.domain.R;
import com.backstage.common.annotation.Anonymous;
import com.backstage.common.core.domain.AjaxResult;
import com.backstage.system.domain.vo.order.GoodsVo;
import com.backstage.system.service.order.OrderListInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pc/goods")
public class orderListInfo {

    @Autowired
    private OrderListInfoService odrService;
    @Anonymous
    @GetMapping("/read")
    public R getList(@RequestParam String type,@RequestParam Integer id){

        R a = new R();


        // course课程，column专栏，book电子书，flashsale秒杀，group拼团，live直播
        // 返回 id titile cover price type(video 或者其他)
        GoodsVo S = odrService.getOrderInfo(type, id);
//        if(S==null){
//            return R.fail();
//        }

//        a.ok(S,  "ok");
        return a;
    }


}
