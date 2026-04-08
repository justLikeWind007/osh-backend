package com.backstage.system.mapper.coupon;

import java.util.List;
import com.backstage.system.domain.coupon.OshCard;
import com.backstage.system.domain.vo.OshCardVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;


/**
 * 卡券信息Mapper接口
 * 
 * @author ruoyi
 * @date 2026-03-15
 */
public interface OshCardMapper 
{
    /**
     * 查询卡券信息
     * 
     * @param id 卡券信息主键
     * @return 卡券详细信息
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
     * 删除卡券信息
     * 
     * @param id 卡券信息主键
     * @return 结果
     */
    public int deleteOshCardById(Long id);

    /**
     * 批量删除卡券信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOshCardByIds(Long[] ids);

    @Select("SELECT id, title, price, start_time, end_time, type, used, goods_id FROM osh_card WHERE used = 0")
    List<OshCardVO> getOshCardList();

    @Insert("INSERT INTO osh_card (id) VALUES (#{id})")
    Boolean receiveCoupon(OshCard oshCard);
}
