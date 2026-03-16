package com.backstage.system.service.impl.order;

import com.backstage.system.domain.order.WxPay;
import com.backstage.system.mapper.order.WxPayMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IsWxPayNoServiceImp {

    @Autowired
    private WxPayMapper mp;

    public boolean isWxPayNo(String no){
        WxPay isWxPay = mp.selectWxPayByNo(no);
        if(isWxPay != null){
            return true;
        }
        return false;
    }
}
