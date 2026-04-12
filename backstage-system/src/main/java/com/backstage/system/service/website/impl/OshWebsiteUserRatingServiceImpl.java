package com.backstage.system.service.website.impl;

import com.backstage.system.domain.website.OshPracticalWebsite;
import com.backstage.system.mapper.website.OshPracticalWebsiteMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backstage.system.domain.website.OshWebsiteUserRating;
import com.backstage.system.mapper.website.OshWebsiteUserRatingMapper;
import com.backstage.system.service.website.OshWebsiteUserRatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;

/**
* @author 24333
* @description 针对表【osh_website_user_rating(网站用户评价记录表)】的数据库操作Service实现
* @createDate 2026-04-11 20:36:12
*/
@Service
public class OshWebsiteUserRatingServiceImpl extends ServiceImpl<OshWebsiteUserRatingMapper, OshWebsiteUserRating>
    implements OshWebsiteUserRatingService{
    private static final Logger log = LoggerFactory.getLogger(OshWebsiteUserRatingServiceImpl.class);


    @Autowired
    private OshWebsiteUserRatingMapper ratingMapper;

    @Autowired
    private OshPracticalWebsiteMapper websiteMapper;

    @Autowired
    private OshPracticalWebsiteServiceImpl websiteService;

    @Override
    public int submitRating(Long userId, Long websiteId, Integer ratingType) {
        log.info("用户{}开始评价网站{}, 评价类型: {}", userId, websiteId, ratingType);

        //参数校验
        if (userId == null || websiteId == null || ratingType == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        if (ratingType < 1 || ratingType > 3) {
            throw new IllegalArgumentException("评价类型必须是1(好评)、2(中评)或3(差评)");
        }
        //检查网站是否存在且已通过审核
        OshPracticalWebsite website = websiteMapper.selectByIdForUpdate(websiteId);
        if (website == null) {
            throw new IllegalArgumentException("网站不存在或未通过审核");
        }
        //查询用户是否已经评价过这个网站
        OshWebsiteUserRating existingRating = ratingMapper.selectByUserAndWebsite(userId, websiteId);

        if (existingRating == null) {
            // 情况1: 用户第一次评价,执行新增逻辑
            handleNewRating(userId, websiteId, ratingType);
            websiteMapper.addCount(websiteId,ratingType);
            if (ratingType == 1 || ratingType == 3){
                websiteService.updateWebsiteRatingScore(websiteId);
                log.info("根据网站id更新网站评分成功");
            }

            log.info("用户{}新增评价成功", userId);
        } else {
            // 情况2: 用户已评价过,判断是否需要修改
            Integer oldRatingType = existingRating.getRatingType();
            if (oldRatingType.equals(ratingType)) {
                // 评价类型相同,不需要修改
                log.info("用户{}已评价过该网站,评价类型相同,无需修改", userId);
                throw new IllegalArgumentException("您已经评价过了");
            }
                // 评价类型不同,执行修改逻辑
                 handleModifyRating(existingRating.getId(), userId, websiteId, oldRatingType, ratingType);
                // 修改网站评价数
                websiteMapper.updateCount(websiteId,oldRatingType,ratingType);
                log.info("用户{}修改评价成功,从{}改为{}", userId, oldRatingType, ratingType);
                if (ratingType == 1 || ratingType == 3){
                    websiteService.updateWebsiteRatingScore(websiteId);
                    log.info("根据网站id更新网站评分成功");
                }

        }
        // ④ 重新计算网站评分
        //updateWebsiteRatingScore(websiteId);
        // ⑤ 返回最新的评价统计信息
        //Map<String, Object> stats = websiteMapper.selectRatingStats(websiteId);
        return 1;
    }

    private int handleModifyRating(Long id, Long userId, Long websiteId, Integer oldRatingType, Integer ratingType) {
        return ratingMapper.handleModifyRating(id, userId, websiteId, oldRatingType, ratingType);
    }

    private int handleNewRating(Long userId, Long websiteId, Integer ratingType) {
        return ratingMapper.handleNewRating(userId, websiteId, ratingType);

    }
}




