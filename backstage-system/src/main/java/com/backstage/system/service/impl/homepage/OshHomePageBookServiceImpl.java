package com.backstage.system.service.impl.homepage;

import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.homepage.vo.HotBookVO;
import com.backstage.system.mapper.homepage.OshHomePageBookMapper;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.homepage.IOshHomePageBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页热门电子书 Service 实现
 *
 * @author jayTatum
 */
@Service
public class OshHomePageBookServiceImpl implements IOshHomePageBookService {

    @Autowired
    private OshHomePageBookMapper homePageBookMapper;

    @Autowired
    private OssService ossService;

    @Override
    public List<HotBookVO> getHotBooks(int limit) {
        // 1. 查询热门电子书（热度公式排序）
        List<HotBookVO> books = homePageBookMapper.selectHotBooks(limit);
        if (books.isEmpty()) {
            return books;
        }

        // 2. 批量查询标签
        List<Long> bookIds = books.stream()
                .map(HotBookVO::getId)
                .collect(Collectors.toList());
        List<HotBookVO> tagRels = homePageBookMapper.selectTagsByBookIds(bookIds);

        // 3. 按 bookId 分组
        Map<Long, List<String>> tagMap = tagRels.stream()
                .collect(Collectors.groupingBy(
                        HotBookVO::getBookId,
                        Collectors.mapping(HotBookVO::getTagName, Collectors.toList())
                ));

        // 4. 组装标签、跳转链接、封面URL
        for (HotBookVO vo : books) {
            // 每本书最多取前2个标签
            List<String> tags = tagMap.getOrDefault(vo.getId(), Collections.emptyList());
            vo.setTags(tags.subList(0, Math.min(2, tags.size())));
            // 详情页跳转路径
            vo.setDetailUrl("/book/detail/" + vo.getId());
            // 封面图片：相对路径转为临时签名URL（有效期1440分钟=24小时）
            if (StringUtils.isNotEmpty(vo.getCover())) {
                vo.setCover(ossService.getLimitedUrl(vo.getCover(), 1440));
            }
        }

        return books;
    }
}
