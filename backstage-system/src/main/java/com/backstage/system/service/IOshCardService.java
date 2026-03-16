package com.backstage.system.service;

import java.util.List;
import com.backstage.system.domain.coupon.OshCard;
import com.backstage.system.domain.vo.OshCardVo;

/**
 * 卡券信息Service接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface IOshCardService 
{
    /**
     * 查询卡券信息
     * 
     * @param id 卡券信息主键
     * @return 卡券信息
     */
    public OshCard selectOshCardById(Long id);

    /**
     * 查询卡券信息列表
     * 
     * @param oshCard 卡券信息
     * @return 卡券信息集合
     */
    public List<OshCard> selectOshCardList(OshCard oshCard);

    /**
     * 新增卡券信息
     * 
     * @param oshCard 卡券信息
     * @return 结果
     */
    public int insertOshCard(OshCard oshCard);

    /**
     * 修改卡券信息
     * 
     * @param oshCard 卡券信息
     * @return 结果
     */
    public int updateOshCard(OshCard oshCard);

    /**
     * 批量删除卡券信息
     * 
     * @param ids 需要删除的卡券信息主键集合
     * @return 结果
     */
    public int deleteOshCardByIds(Long[] ids);

    /**
     * 删除卡券信息信息
     * 
     * @param id 卡券信息主键
     * @return 结果
     */
    public int deleteOshCardById(Long id);

    List<OshCardVo> getOshCardList();

    Boolean receiveCoupon(OshCard oshCard);
}
