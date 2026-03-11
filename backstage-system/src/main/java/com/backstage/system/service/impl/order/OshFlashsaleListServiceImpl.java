package com.backstage.system.service.impl.order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.backstage.common.utils.DateUtils;
import com.backstage.common.utils.uuid.UUID;
import com.backstage.system.domain.order.OshLearn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.backstage.system.mapper.order.OshFlashsaleListMapper;
import com.backstage.system.domain.order.OshFlashsaleList;
import com.backstage.system.service.order.IOshFlashsaleListService;

/**
 * 创建秒杀订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-10
 */
@Service
public class OshFlashsaleListServiceImpl implements IOshFlashsaleListService 
{
    @Autowired
    private OshFlashsaleListMapper oshFlashsaleListMapper;

    /**
     * 查询创建秒杀订单
     * 
     * @param id 创建秒杀订单主键
     * @return 创建秒杀订单
     */
    @Override
    public OshFlashsaleList selectOshFlashsaleListById(Long id)
    {
        return oshFlashsaleListMapper.selectOshFlashsaleListById(id);
    }

    /**
     * 查询创建秒杀订单列表
     * 
     * @param oshFlashsaleList 创建秒杀订单
     * @return 创建秒杀订单
     */
    @Override
    public List<OshFlashsaleList> selectOshFlashsaleListList(OshFlashsaleList oshFlashsaleList)
    {
        return oshFlashsaleListMapper.selectOshFlashsaleListList(oshFlashsaleList);
    }



    public OshFlashsaleList CreateList(Long flashId,Long price, Long totalPrice){

        OshFlashsaleList a = new OshFlashsaleList();
        a.setSchoolId(1L);
        a.setUserId(1L);

        // ruoyi自带 拼接年月日 和 uuid
        String orderNo = DateUtils.datePath() + "_" + UUID.randomUUID().toString().substring(0, 9);
        a.setNo(orderNo);

        a.setStatus("pending");
        a.setPrice(String.format("%.1f", price.doubleValue()));
        a.setTotalPrice(String.format("%.1f", totalPrice.doubleValue()));
        a.setType("flashsale");
        a.setFlashsaleId(flashId);
        Date date = new Date();
        // 创建格式化对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));  // 设置为 UTC 时区

        // 格式化
        String isoString = sdf.format(date);
        System.out.println(isoString);  // 输出：2021-06-20T09:41:21.245Z
        a.setUpdatedTime(isoString);
        a.setCreatedTime(isoString);

        oshFlashsaleListMapper.insertOshFlashsaleList(a);
        return a;
    }

    /**
     * 新增创建秒杀订单
     * 
     * @param flashsale_id 创建秒杀订单
     * @return 结果
     */
    @Override
    public OshFlashsaleList insertOshFlashsaleList(Long flashsale_id)
    {


        //TODO 创建秒杀订单
        boolean is_exist = false;
        long price=0,totalPrice=0,goodId=0;
        if (flashsale_id>=1){
            // 秒杀列表查询id是否存在 并且获取价格price 和 goods_id
            is_exist=true;
        }


        if (!is_exist){
            return null;
        }
        //  goods_id 查找course column book 三张表的id是否匹配
        // 获取价格price
        // 查找课程表的内容获取价格 price(total_price)


        OshFlashsaleList fl = CreateList(flashsale_id,price,totalPrice);



        return fl;
    }

    /**
     * 修改创建秒杀订单
     * 
     * @param oshFlashsaleList 创建秒杀订单
     * @return 结果
     */
    @Override
    public int updateOshFlashsaleList(OshFlashsaleList oshFlashsaleList)
    {
        return oshFlashsaleListMapper.updateOshFlashsaleList(oshFlashsaleList);
    }

    /**
     * 批量删除创建秒杀订单
     * 
     * @param ids 需要删除的创建秒杀订单主键
     * @return 结果
     */
    @Override
    public int deleteOshFlashsaleListByIds(Long[] ids)
    {
        return oshFlashsaleListMapper.deleteOshFlashsaleListByIds(ids);
    }

    /**
     * 删除创建秒杀订单信息
     * 
     * @param id 创建秒杀订单主键
     * @return 结果
     */
    @Override
    public int deleteOshFlashsaleListById(Long id)
    {
        return oshFlashsaleListMapper.deleteOshFlashsaleListById(id);
    }
}
