package com.backstage.system.service.website;

import com.backstage.common.core.page.TableDataInfo;
import com.backstage.system.domain.dto.website.WebsiteAuditDTO;
import com.backstage.system.domain.dto.website.WebsiteQueryDTO;
import com.backstage.system.domain.dto.website.WebsiteSubmitDTO;
import com.backstage.system.domain.vo.website.OshPracticalWebsiteVO;

import java.util.List;

/**
* @author 24333
* @description 针对表【osh_practical_website(实用网站表)】的数据库操作Service
* @createDate 2026-03-26 19:22:13
*/
public interface OshPracticalWebsiteService {

    /**
     * 分页查询实用网站列表（带筛选条件）
     *
     * @param queryDTO 查询参数
     * @return 实用网站列表
     */
    List<OshPracticalWebsiteVO> selectWebsitePage(WebsiteQueryDTO queryDTO);

    /**
     * 增加网站点击次数
     *
     * @param websiteId 网站 ID
     * @return 结果
     */
    int incrementClickCount(Long websiteId);


    /**
     * 用户提交网站
     *
     * @param submitDto 提交的网站信息
     * @return 影响行数
     */
    int submitWebsite(WebsiteSubmitDTO submitDto);
    /**
     * 管理员审核网站
     *
     * @param auditDto 审核信息
     * @return 是否审核成功
     */
    Boolean auditWebsite(WebsiteAuditDTO auditDto);
    /**
     * 查询待审核的网站列表
     *
     * @param pageNum  页码
     * @param pageSize 页大小
     * @return 待审核的网站列表
     */
    TableDataInfo selectAuditList(Integer pageNum, Integer pageSize);
    /**
     * 批量删除网站
     *
     * @param websiteIds 要删除的网站 ID 列表
     * @return 删除的网站数量
     */
    int batchDeleteWebsite(List<Integer> websiteIds);

    OshPracticalWebsiteVO getAuditDetail(Long websiteId);
}
