package com.backstage.system.mapper.website;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backstage.system.domain.website.OshWebsiteUserRating;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
* @author 24333
* @description 针对表【osh_website_user_rating(网站用户评价记录表)】的数据库操作Mapper
* @createDate 2026-04-11 20:36:12
* @Entity generator.domain.OshWebsiteUserRating
*/
public interface OshWebsiteUserRatingMapper extends BaseMapper<OshWebsiteUserRating> {
    @Select("select id, user_id, website_id, rating_type from osh_website_user_rating where user_id = #{userId} and website_id = #{websiteId} and delete_flag = 0")
    OshWebsiteUserRating selectByUserAndWebsite(@Param("userId") Long userId, @Param("websiteId") Long websiteId);

    @Update("update osh_website_user_rating set rating_type = #{ratingType} where user_id = #{userId} and website_id = #{websiteId} and delete_flag = 0")
    int  handleNewRating(@Param("userId") Long userId, @Param("websiteId") Long websiteId, @Param("ratingType") Integer ratingType);

    @Update("update osh_website_user_rating set rating_type = #{ratingType} where id = #{id} and user_id = #{userId} and website_id = #{websiteId} and rating_type = #{oldRatingType} and delete_flag = 0")
    int handleModifyRating(@Param("id") Long id, @Param("userId") Long userId, @Param("websiteId") Long websiteId, @Param("oldRatingType") Integer oldRatingType, @Param("ratingType") Integer ratingType);
}




