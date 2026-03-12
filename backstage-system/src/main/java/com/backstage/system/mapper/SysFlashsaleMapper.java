package com.backstage.system.mapper;

import com.backstage.system.domain.SysFlashSale;

import java.util.List;

public interface SysFlashsaleMapper {
    /**
     * 查询秒杀活动
     *
     * @param id 秒杀活动主键
     * @return 秒杀活动
     */
    public SysFlashSale selectOshFlashsaleById(Long id);

    /**
     * 查询秒杀活动列表
     *
     * @param oshFlashsale 秒杀活动
     * @return 秒杀活动集合
     */
    public List<SysFlashSale> selectOshFlashsaleList(SysFlashSale oshFlashsale);

    /**
     * 新增秒杀活动
     *
     * @param oshFlashsale 秒杀活动
     * @return 结果
     */
    public int insertOshFlashsale(SysFlashSale oshFlashsale);

    /**
     * 修改秒杀活动
     *
     * @param oshFlashsale 秒杀活动
     * @return 结果
     */
    public int updateOshFlashsale(SysFlashSale oshFlashsale);

    /**
     * 删除秒杀活动
     *
     * @param id 秒杀活动主键
     * @return 结果
     */
    public int deleteOshFlashsaleById(Long id);

    /**
     * 批量删除秒杀活动
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOshFlashsaleByIds(Long[] ids);
}
