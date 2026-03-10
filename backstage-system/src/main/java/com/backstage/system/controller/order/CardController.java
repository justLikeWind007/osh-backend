package com.backstage.system.controller.order;

import com.backstage.common.annotation.Anonymous;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.backstage.system.domain.order.Card;
import com.backstage.system.service.order.ICardService;

/**
 * 卡券 Controller - 适配 /pc/card/list?page=1&limit=20
 * 
 * @author ruoyi
 * @date 2026-03-05
 */
@RestController
@RequestMapping("/pc/user_coupon")
public class CardController {

    @Autowired
    private ICardService cardService;

    // 核心：适配 /pc/user_coupon?goods_id=6&type=course&page=1
    @Anonymous
    @GetMapping("")
    public Map<String, Object> getUserCouponList(
            @RequestParam Long goods_id,    // 必填：goods_id 课程/专栏 ID
            @RequestParam String type,      // 必填：type（column 专栏/course 课程）
            @RequestParam Integer page    // 必填：page 页面码
    ) {
        // 构建查询条件
        Card card = new Card();
        card.setGoodsId(goods_id);
        card.setType(type);
        
        // 查询列表（未使用的优惠券）
        // card.setUsed(0);
        
        // 使用 PageHelper 分页（每页 20 条）
        PageHelper.startPage(page, 20);
        List<Card> list = cardService.selectCardList(card);
        
        // 转换为前端需要的格式，price 转为字符串，时间格式化为 yyyy-MM-dd HH:mm:ss
        List<Map<String, Object>> formattedList = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Card item : list) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", item.getId());
            map.put("title", item.getTitle());
            map.put("price", item.getPrice() != null ? item.getPrice().toString() : "0.00");
            map.put("start_time", item.getStartTime() != null ? sdf.format(item.getStartTime()) : "");
            map.put("end_time", item.getEndTime() != null ? sdf.format(item.getEndTime()) : "");
            map.put("type", item.getType());
            map.put("used", item.getUsed());
            map.put("goods_id", item.getGoodsId());
            formattedList.add(map);
        }
        
        // 使用 LinkedHashMap 确保 JSON 输出顺序：msg -> data -> code
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("msg", "ok");
        result.put("data", new LinkedHashMap<String, Object>() {{
            put("count", (long) list.size()); // 返回实际查询到的条数
            put("rows", formattedList);
        }});
        result.put("code", 20000);
        
        return result;
    }
}
