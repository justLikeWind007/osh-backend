package com.backstage.system.service.impl.order;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.backstage.common.utils.DateUtils;
import com.backstage.common.utils.uuid.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.backstage.system.mapper.order.OshGroupOrderMapper;
import com.backstage.system.domain.order.OshGroupOrder;
import com.backstage.system.service.order.IOshGroupOrderService;

/**
 * 订单Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-11
 */
@Service
public class OshGroupOrderServiceImpl implements IOshGroupOrderService 
{
    @Autowired
    private OshGroupOrderMapper oshGroupOrderMapper;

    /**
     * 查询订单
     * 
     * @param id 订单主键
     * @return 订单
     */
    @Override
    public OshGroupOrder selectOshGroupOrderById(Long id)
    {
        return oshGroupOrderMapper.selectOshGroupOrderById(id);
    }

    /**
     * 查询订单列表
     * 
     * @param oshGroupOrder 订单
     * @return 订单
     */
    @Override
    public List<OshGroupOrder> selectOshGroupOrderList(OshGroupOrder oshGroupOrder)
    {
        return oshGroupOrderMapper.selectOshGroupOrderList(oshGroupOrder);
    }

    /**
     * 新增订单
     * 
     * @param oshGroupOrder 订单
     * @return 结果
     */
    @Override
    public int insertOshGroupOrder(OshGroupOrder oshGroupOrder)
    {

        return oshGroupOrderMapper.insertOshGroupOrder(oshGroupOrder);
    }

    /**
     * 修改订单
     * 
     * @param oshGroupOrder 订单
     * @return 结果
     */
    @Override
    public int updateOshGroupOrder(OshGroupOrder oshGroupOrder)
    {
        return oshGroupOrderMapper.updateOshGroupOrder(oshGroupOrder);
    }

    /**
     * 批量删除订单
     * 
     * @param ids 需要删除的订单主键
     * @return 结果
     */
    @Override
    public int deleteOshGroupOrderByIds(Long[] ids)
    {
        return oshGroupOrderMapper.deleteOshGroupOrderByIds(ids);
    }

    /**
     * 删除订单信息
     * 
     * @param id 订单主键
     * @return 结果
     */
    @Override
    public int deleteOshGroupOrderById(Long id)
    {
        return oshGroupOrderMapper.deleteOshGroupOrderById(id);
    }


    public OshGroupOrder CreateGroupList(OshGroupOrder oshGroupOrder){

        OshGroupOrder a = new OshGroupOrder();
        a.setId(1L);
        a.setSchoolId(1L);
        a.setUserId(1L);
        String orderNo = DateUtils.datePath() + "_" + UUID.randomUUID().toString().substring(0, 9);
        a.setNo(orderNo);
        a.setStatus("pendding");
        a.setPrice(new BigDecimal(0));
        a.setTotalPrice(new BigDecimal(0));
        a.setType("group");
        Date date = new Date();

        // 创建格式化对象
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));  // 设置为 UTC 时区

        // 格式化
        String isoString = sdf.format(date);
        System.out.println(isoString);  // 输出：2021-06-20T09:41:21.245Z
        a.setUpdatedTime(new Date(isoString));
        a.setCreatedTime(new Date(isoString));

        oshGroupOrderMapper.insertOshGroupOrder(a);
        return a;
    }

    @Override
    public OshGroupOrder findGroupId(Integer group_id, Integer group_work_id) {

        //TODO 立即拼团service实现
        // 调用service获取拼团相关数值 group_id对应数据
        // 返回json格式 id school_id user_id no status price total_price type updated_time created_time


        OshGroupOrder oshGroupOrder = new OshGroupOrder();

//        传入查询到的值
//        CreateGroupList();



    	return oshGroupOrder;
    }
}
