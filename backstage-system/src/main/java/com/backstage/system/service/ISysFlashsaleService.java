package com.backstage.system.service;

import com.backstage.system.domain.SysFlashSale;
import com.backstage.system.domain.vo.FlashColumnVo;
import com.backstage.system.domain.vo.FlashCourseVo;

import java.util.List;

public interface ISysFlashsaleService {
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
     * 批量删除秒杀活动
     *
     * @param ids 需要删除的秒杀活动主键集合
     * @return 结果
     */
    public int deleteOshFlashsaleByIds(Long[] ids);

    /**
     * 删除秒杀活动信息
     *
     * @param id 秒杀活动主键
     * @return 结果
     */
    public int deleteOshFlashsaleById(Long id);

    /**
     * 查询课程详情（包含秒杀信息）
     */
    public FlashCourseVo selectFlashsaleReadDetail(Long id);

    /**
     * 查询秒杀专栏详情（包含专栏信息、课程列表、秒杀活动信息）
     * @param flashsaleId 秒杀活动ID
     * @return 专栏对象
     */
    public FlashColumnVo selectFlashsaleColumnDetail(Long flashsaleId);
}
