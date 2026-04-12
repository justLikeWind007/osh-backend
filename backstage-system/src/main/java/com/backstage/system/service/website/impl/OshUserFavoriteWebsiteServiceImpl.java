package com.backstage.system.service.website.impl;

import com.backstage.common.constant.OshUserConstants;
import com.backstage.common.core.page.TableDataInfo;
import com.backstage.common.threadlocal.ThreadLocalUtil;
import com.backstage.system.domain.vo.website.UserFavoriteWebsiteVO;
import com.backstage.system.mapper.website.OshPracticalWebsiteMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.backstage.system.domain.website.OshUserFavoriteWebsite;
import com.backstage.system.mapper.website.OshUserFavoriteWebsiteMapper;
import com.backstage.system.service.website.OshUserFavoriteWebsiteService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.backstage.system.utils.UserContextUtil.getCurrentUser;

/**
* @author 24333
* @description 针对表【osh_user_favorite_website(用户收藏网站表)】的数据库操作Service实现
* @createDate 2026-04-01 15:01:09
*/
@Service
public class OshUserFavoriteWebsiteServiceImpl extends ServiceImpl<OshUserFavoriteWebsiteMapper, OshUserFavoriteWebsite>
    implements OshUserFavoriteWebsiteService{

    @Autowired
    private OshUserFavoriteWebsiteMapper userFavoriteWebsiteMapper;

    @Autowired
    private OshPracticalWebsiteMapper practicalWebsiteMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int favoriteWebsite(Long websiteId) {
        Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        //Long userId = 1L;
        // 1. 验证参数
        if (websiteId == null || userId == null)  throw new IllegalArgumentException("网站 ID 和用户 ID 不能为空");
        // 2. 检查是否已收藏
        int count = userFavoriteWebsiteMapper.getFavorited(websiteId, userId);
        if (count > 0) {
            throw new IllegalArgumentException("您已经收藏过该网站了");
        }
        practicalWebsiteMapper.addCollectionCount(websiteId);
        return userFavoriteWebsiteMapper.favoriteWebsite(websiteId, userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancelFavoriteWebsite(Long websiteId) {
       // Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
           Long userId = getCurrentUser().getId();
        //Long userId = 1L;
        // 1. 验证参数
        if (websiteId == null || userId == null) {
            throw new IllegalArgumentException("网站 ID 和用户 ID 不能为空");
        }
         int  count = userFavoriteWebsiteMapper.getFavorited(websiteId, userId);
        if (count == 0) {
            throw new IllegalArgumentException("您没有收藏该网站");
        }
            practicalWebsiteMapper.reduceCollectionCount(websiteId);
        return userFavoriteWebsiteMapper.cancelFavoriteWebsite(websiteId, userId);
    }

    @Override
    public TableDataInfo selectUserFavoriteList(Integer pageNum, Integer pageSize) {
        // 获取用户 ID
        //Long userId = ThreadLocalUtil.get(OshUserConstants.USER_ID,Long.class);
        Long userId = 5L;
        // 开启分页
        PageHelper.startPage(pageNum, pageSize);
        // 执行查询
        List<UserFavoriteWebsiteVO> list = userFavoriteWebsiteMapper.selectUserFavoriteList(userId);
         PageInfo<UserFavoriteWebsiteVO> pageInfo = new PageInfo<>(list);

        return new TableDataInfo(pageInfo.getList(), pageInfo.getTotal());
    }

}




