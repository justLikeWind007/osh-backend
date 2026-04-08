package com.backstage.system.service.impl;

import java.util.List;
import com.backstage.common.utils.DateUtils;
import com.backstage.system.domain.coupon.OshCard;
import com.backstage.system.domain.vo.OshCardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.backstage.system.mapper.coupon.OshCardMapper;
import com.backstage.system.service.IOshCardService;

/**
 * 卡券信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
@Service
public class OshCardServiceImpl implements IOshCardService 
{
    @Autowired
    private OshCardMapper oshCardMapper;

    /**
     * 查询卡券信息
     * 
     * @param id 卡券信息主键
     * @return 查询卡券信息
     */
    @Override
    public OshCard selectOshCardById(Long id)
    {
        return oshCardMapper.selectOshCardById(id);
    }

    /**
     * 查询卡券信息列表
     * 
     * @param oshCard 卡券信息
     * @return 卡券信息
     */
    @Override
    public List<OshCard> selectOshCardList(OshCard oshCard)
    {
        return oshCardMapper.selectOshCardList(oshCard);
    }

    /**
     * 新增卡券信息
     * 
     * @param oshCard 卡券信息
     * @return 结果
     */
    @Override
    public int insertOshCard(OshCard oshCard)
    {
        oshCard.setCreateTime(DateUtils.getNowDate());
        return oshCardMapper.insertOshCard(oshCard);
    }

    /**
     * 修改卡券信息
     * 
     * @param oshCard 卡券信息
     * @return 结果
     */
    @Override
    public int updateOshCard(OshCard oshCard)
    {
        oshCard.setUpdateTime(DateUtils.getNowDate());
        return oshCardMapper.updateOshCard(oshCard);
    }

    /**
     * 批量删除卡券信息
     * 
     * @param ids 需要删除的卡券信息主键
     * @return 结果
     */
    @Override
    public int deleteOshCardByIds(Long[] ids)
    {
        return oshCardMapper.deleteOshCardByIds(ids);
    }

    /**
     * 删除卡券信息信息
     * 
     * @param id 卡券信息主键
     * @return 结果
     */
    @Override
    public int deleteOshCardById(Long id)
    {
        return oshCardMapper.deleteOshCardById(id);
    }

    @Override
    public List<OshCardVO> getOshCardList() {
        List<OshCardVO>  cardlist = oshCardMapper.getOshCardList();
        return cardlist;
    }

    @Override
    public Boolean receiveCoupon(OshCard oshCard) {
        // 1. 校验优惠券 ID 是否为空
        if (oshCard.getId() == null) {
            return false;
        }

        // 2. 查询优惠券信息
        OshCard coupon = oshCardMapper.selectOshCardById(oshCard.getId());
        if (coupon != null) {
            return false;
        } else {

            return oshCardMapper.receiveCoupon(oshCard);
        }


    }
}
