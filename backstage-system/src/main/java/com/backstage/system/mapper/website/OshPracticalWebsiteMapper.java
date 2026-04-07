package com.backstage.system.mapper.website;

import com.backstage.system.domain.dto.website.WebsiteQueryDTO;
import com.backstage.system.domain.vo.website.OshPracticalWebsiteVO;
import com.backstage.system.domain.website.OshPracticalWebsite;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
* @author 24333
* @description 针对表【osh_practical_website(实用网站表)】的数据库操作Mapper
* @createDate 2026-03-26 19:22:13
* @Entity generator.domain.OshPracticalWebsite
*/
public interface OshPracticalWebsiteMapper  {
    /**
     * 分页查询实用网站列表（支持按名称和标签筛选）
     * @param queryDTO 查询参数
     * @return 网站 VO 列表
     */
    List<OshPracticalWebsiteVO> selectWebsitePage(@Param("queryDTO") WebsiteQueryDTO queryDTO);

    /**
     * 增加网站点击次数
     * @param websiteId 网站 ID
     * @return 影响行数
     */
    int incrementClickCount(@Param("websiteId") Long websiteId);

    /**
     * 用户提交网站
     * @param website 提交的网站信息
     * @return 影响行数
     */
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")// 返回主键
    @Insert("insert into osh_practical_website (name, url, description, logo_url, status) values (#{name}, #{url}, #{description}, #{logoUrl}, #{status})")
    int insertWebsite(OshPracticalWebsite website);

    /**
     * 根据 ID 查询网站信息
     * @param websiteId 网站 ID
     * @return 网站对象
     */
    @Select("SELECT * FROM osh_practical_website " +
            "WHERE id = #{websiteId} " +
            "AND del_flag = 0 " +
            "AND status = 0")
    OshPracticalWebsite selectById(Long websiteId);

    /**
     * 更新网站审核信息
     * @param website 要更新的网站信息
     * @return 影响行数
     */
    @Update("UPDATE osh_practical_website " +
            "SET status = #{website.status}, " +
            "    audit_by = #{website.auditBy}, " +
            "    audit_time = #{website.auditTime}, " +
            "    reject_reason = #{website.rejectReason} " +
            "WHERE id = #{website.id} " +
            "  AND del_flag = 0")
    Boolean updateStatusById(@Param("website") OshPracticalWebsite website);

    /**
     * 查询待审核的网站列表
     * @return 待审核的网站列表
     */
    @Select("SELECT * FROM osh_practical_website WHERE `del_flag` = 0 AND `status` = 0")
    List<OshPracticalWebsite> selectAuditList();
/**
     * 根据网站ID批量删除网站
     * @param websiteIds 要删除的网站 ID 列表
     * @return 删除的网站数量
     */
    int batchDeleteWebsite(List<Integer> websiteIds);

    @Select("SELECT * FROM osh_practical_website WHERE id = #{websiteId} AND del_flag = 0 AND status = #{status}")
    OshPracticalWebsiteVO selectByIdAndStatus(@Param("websiteId") Long websiteId, @Param("status") Integer status);

}




