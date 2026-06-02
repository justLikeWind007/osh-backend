package com.backstage.system.service.website;

import com.backstage.system.domain.announcement.vo.AnnouncementMarqueeVO;

import java.util.List;

/**
 * 实用网站公告/动态 Service 接口
 *
 * 公告栏（channel=1）：审核通过时写入，内容为新上线的网站名称
 * 动态栏（channel=2）：用户提交好评时写入，内容为脱敏用户名 + 网站名称
 */
public interface IWebsiteAnnouncementService {

    /**
     * 审核通过时写入一条公告记录（channel=1）
     *
     * @param websiteId   网站 ID
     * @param websiteName 网站名称
     */
    void insertWebsiteNotice(Long websiteId, String websiteName);

    /**
     * 用户好评时写入一条动态记录（channel=2）
     *
     * @param userId      用户 ID（用于查询脱敏用户名）
     * @param websiteId   网站 ID
     * @param websiteName 网站名称
     */
    void insertWebsiteDynamic(Long userId, Long websiteId, String websiteName);

    /**
     * 查询实用网站公告栏
     *
     * @param limit 返回条数
     */
    List<AnnouncementMarqueeVO> getWebsiteNotices(int limit);

    /**
     * 查询实用网站动态栏
     *
     * @param limit 返回条数
     */
    List<AnnouncementMarqueeVO> getWebsiteDynamics(int limit);
}
