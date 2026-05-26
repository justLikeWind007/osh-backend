package com.backstage.system.controller.ad;

import com.backstage.common.annotation.Anonymous;
import com.backstage.common.annotation.OshUserLevel;
import com.backstage.common.core.domain.R;
import com.backstage.system.domain.ad.OshFriendLink;
import com.backstage.system.mapper.ad.OshFriendLinkMapper;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 友情链接管理
 */
@RestController
@RequestMapping("/pc/site/friend-link")
public class FriendLinkController {

    @Resource
    private OshFriendLinkMapper friendLinkMapper;

    /**
     * 查询友情链接列表（匿名可访问）
     */
    @Anonymous
    @GetMapping("/list")
    public R list() {
        List<OshFriendLink> links = friendLinkMapper.selectAll();
        return R.ok(links);
    }

    /**
     * 保存友情链接（管理员，level >= 4）
     * 全量替换：前端传入最多5条链接
     */
    @PostMapping("/save")
    @OshUserLevel(value = 4)
    public R save(@RequestBody List<OshFriendLink> links) {
        if (links == null || links.size() > 5) {
            return R.fail("友情链接数量不能超过5个");
        }
        // 设置排序序号
        for (int i = 0; i < links.size(); i++) {
            links.get(i).setSortOrder(i + 1);
        }
        friendLinkMapper.deleteAll();
        if (!links.isEmpty()) {
            friendLinkMapper.batchInsert(links);
        }
        return R.ok();
    }
}
