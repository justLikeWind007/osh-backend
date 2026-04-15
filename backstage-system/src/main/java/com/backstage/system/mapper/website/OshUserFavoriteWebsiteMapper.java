package com.backstage.system.mapper.website;

import com.backstage.system.domain.vo.website.UserFavoriteWebsiteVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.backstage.system.domain.website.OshUserFavoriteWebsite;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
* @author 24333
* @description 针对表【osh_user_favorite_website(用户收藏网站表)】的数据库操作Mapper
* @createDate 2026-04-01 15:01:09
* @Entity generator.domain.OshUserFavoriteWebsite
*/
public interface OshUserFavoriteWebsiteMapper extends BaseMapper<OshUserFavoriteWebsite> {
    /**
     * 用户收藏网站
     * @param websiteId 网站 ID
     * @param userId 用户 ID
     * @return 影响行数（1=成功，0=失败）
     */
@Insert("insert into osh_user_favorite_website(website_id, user_id) values (#{websiteId}, #{userId})")
    int favoriteWebsite(@Param("websiteId") Long websiteId, @Param("userId") Long userId);
/**
     * 取消收藏网站
     * @param websiteId 网站 ID
     * @param userId 用户 ID
     * @return 影响行数（1=成功，0=失败）
     */
    @Update("update osh_user_favorite_website set delete_flag = 1 where website_id = #{websiteId} and user_id = #{userId}")
    int cancelFavoriteWebsite(@Param("websiteId") Long websiteId, @Param("userId") Long userId);
    @Select("select count(*) from osh_user_favorite_website where website_id = #{websiteId} and user_id = #{userId} and delete_flag = 0")
    int getFavorited(@Param("websiteId") Long websiteId, @Param("userId") Long userId);

    List<UserFavoriteWebsiteVO> selectUserFavoriteList(Long userId);
}




