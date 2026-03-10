package com.backstage.system.mapper.order;

import java.util.List;
import com.backstage.system.domain.order.Card;

/**
 * 卡券 Mapper 接口
 * 
 * @author ruoyi
 * @date 2026-03-05
 */
public interface CardMapper 
{
    /**
     * 查询卡券
     * 
     * @param id 卡券主键
     * @return 卡券
     */
    public Card selectCardById(Long id);

    /**
     * 查询卡券列表
     * 
     * @param card 卡券
     * @return 卡券集合
     */
    public List<Card> selectCardList(Card card);

    /**
     * 新增卡券
     * 
     * @param card 卡券
     * @return 结果
     */
    public int insertCard(Card card);

    /**
     * 修改卡券
     * 
     * @param card 卡券
     * @return 结果
     */
    public int updateCard(Card card);

    /**
     * 删除卡券
     * 
     * @param id 卡券主键
     * @return 结果
     */
    public int deleteCardById(Long id);

    /**
     * 批量删除卡券
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteCardByIds(Long[] ids);
}
