package com.backstage.system.mapper.column;

import com.backstage.system.domain.vo.FlashColumnVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysFlashColumnMapper {
    /**
     * 查询专栏
     *
     * @param id 专栏主键
     * @return 专栏
     */
    public FlashColumnVo selectOshColumnById(Long id);

    /**
     * 查询专栏列表
     *
     * @param oshColumn 专栏
     * @return 专栏集合
     */
    public List<FlashColumnVo> selectOshColumnList(FlashColumnVo oshColumn);

    /**
     * 新增专栏
     *
     * @param oshColumn 专栏
     * @return 结果
     */
    public int insertOshColumn(FlashColumnVo oshColumn);

    /**
     * 修改专栏
     *
     * @param oshColumn 专栏
     * @return 结果
     */
    public int updateOshColumn(FlashColumnVo oshColumn);

    /**
     * 删除专栏
     *
     * @param id 专栏主键
     * @return 结果
     */
    public int deleteOshColumnById(Long id);

    /**
     * 批量删除专栏
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteOshColumnByIds(Long[] ids);
}
