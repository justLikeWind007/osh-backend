package com.backstage.system.service.impl;

import com.backstage.system.domain.SysFlashSale;
import com.backstage.system.domain.vo.FlashColumnVo;
import com.backstage.system.domain.vo.FlashCourseVo;
import com.backstage.system.mapper.SysFlashsaleMapper;
import com.backstage.system.mapper.column.SysFlashColumnMapper;
import com.backstage.system.mapper.course.SysFlashCourseMapper;
import com.backstage.system.service.ISysFlashsaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysFlashsaleServiceImpl implements ISysFlashsaleService {
    @Autowired
    private SysFlashsaleMapper flashsaleMapper;

    @Autowired
    private SysFlashCourseMapper courseMapper;

//    @Autowired
//    private SysCourseMapper courseMapper;

    @Autowired
    private SysFlashColumnMapper columnMapper; // 专栏Mapper

    @Override
    public FlashColumnVo selectFlashsaleColumnDetail(Long flashsaleId) {
        // 1. 根据秒杀ID查询秒杀活动信息
        SysFlashSale flashsale = flashsaleMapper.selectOshFlashsaleById(flashsaleId);
        if (flashsale == null) {
            return null;
        }

        // 2. 根据秒杀信息里的 goodsId 查询专栏详情
        // 注意：在秒杀专栏里，goodsId 对应的就是专栏表的 ID
        FlashColumnVo column = columnMapper.selectOshColumnById(flashsale.getGoodsId());

        if (column != null) {
            // 3. 查询该专栏关联的课程列表（复用你刚才写好的 SQL）
            List<FlashCourseVo> courses = courseMapper.selectCoursesByColumnId(column.getId());
            column.setColumnCourses(courses);

            // 4. 将秒杀信息塞进专栏对象里
            column.setFlashsale(flashsale);

            // 5. 设置 isbuy 状态（默认为 false）
            column.setBuyFlag(0);
        }

        return column;
    }

    /**
     * 查询秒杀活动
     *
     * @param id 秒杀活动主键
     * @return 秒杀活动
     */
    @Override
    public SysFlashSale selectOshFlashsaleById(Long id)
    {
        return flashsaleMapper.selectOshFlashsaleById(id);
    }

    /**
     * 查询秒杀活动列表
     *
     * @param oshFlashsale 秒杀活动
     * @return 秒杀活动
     */
    @Override
    public List<SysFlashSale> selectOshFlashsaleList(SysFlashSale oshFlashsale)
    {
        return flashsaleMapper.selectOshFlashsaleList(oshFlashsale);
    }

    /**
     * 新增秒杀活动
     *
     * @param oshFlashsale 秒杀活动
     * @return 结果
     */
    @Override
    public int insertOshFlashsale(SysFlashSale oshFlashsale)
    {
        return flashsaleMapper.insertOshFlashsale(oshFlashsale);
    }

    /**
     * 修改秒杀活动
     *
     * @param oshFlashsale 秒杀活动
     * @return 结果
     */
    @Override
    public int updateOshFlashsale(SysFlashSale oshFlashsale)
    {
        return flashsaleMapper.updateOshFlashsale(oshFlashsale);
    }

    /**
     * 批量删除秒杀活动
     *
     * @param ids 需要删除的秒杀活动主键
     * @return 结果
     */
    @Override
    public int deleteOshFlashsaleByIds(Long[] ids)
    {
        return flashsaleMapper.deleteOshFlashsaleByIds(ids);
    }

    /**
     * 删除秒杀活动信息
     *
     * @param id 秒杀活动主键
     * @return 结果
     */
    @Override
    public int deleteOshFlashsaleById(Long id)
    {
        return flashsaleMapper.deleteOshFlashsaleById(id);
    }


    @Override
    public FlashCourseVo selectFlashsaleReadDetail(Long flashsaleId) {

        // 1. 直接用 flashsale_id 查秒杀表
        SysFlashSale flashsale = flashsaleMapper.selectOshFlashsaleById(flashsaleId);
        if (flashsale == null) return null;


        // 2. 查对应的课程信息
        FlashCourseVo course = courseMapper.selectOshCourseById(flashsale.getGoodsId());

        if (course != null) {
            course.setFlashsale(flashsale); // 嵌套
            return course;
        }
        return null;
    }
}
