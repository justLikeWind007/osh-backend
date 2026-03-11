package com.backstage.system.service.impl.order;

import com.backstage.common.utils.DateUtils;
import com.backstage.common.utils.uuid.UUID;
import com.backstage.system.domain.order.OshLearn;
import com.backstage.system.domain.vo.order.GoodsVo;
import com.backstage.system.service.order.OrderListInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

@Service
public class OrderListInfoServiceImpl implements OrderListInfoService {

//    @Autowired
//    private OrderListInfoServiceImpl orderListInfoService;


    public GoodsVo CreateOrderInfo(Object s){

        if(s instanceof Object){

        }else if(s instanceof String){

        }else if(s instanceof Integer){

        }else if(s instanceof Long){

        }else if(s instanceof Double){

        }else if(s instanceof Float){

        }else if(s instanceof Boolean){

        }else if(s instanceof Byte){

        }else if(s instanceof Short){

        }else if(s instanceof Character){

        }
        GoodsVo a = new GoodsVo();
//        a.setId(s.id);
        a.setTitle("title");
        a.setCover("cover");
        a.setPrice("0.00");
        a.setType("video");


        return a;
    }

    @Override
    public GoodsVo getOrderInfo(String type, Integer id) {
        // TODO 下单前获取信息接口
        // 查询对应模块的 id titile cover price type(video 或者其它)
        if (type=="course"){

        }else if (type=="column"){

        }else if (type=="book"){

        }else if (type=="flashsale"){

        }else if (type=="group"){

        }else if (type=="live"){

        }

        return null;
    }
}
