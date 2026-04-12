package com.backstage.system.service.website;

import com.baomidou.mybatisplus.extension.service.IService;
import com.backstage.system.domain.website.OshWebsiteUserRating;

import java.util.Map;

/**
* @author 24333
* @description 针对表【osh_website_user_rating(网站用户评价记录表)】的数据库操作Service
* @createDate 2026-04-11 20:36:12
*/
public interface OshWebsiteUserRatingService extends IService<OshWebsiteUserRating> {

    int submitRating(Long userId, Long websiteId, Integer ratingType);
}
