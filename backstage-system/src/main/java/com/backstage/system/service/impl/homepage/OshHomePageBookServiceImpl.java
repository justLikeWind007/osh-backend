package com.backstage.system.service.impl.homepage;

import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.homepage.vo.HotBookVO;
import com.backstage.system.mapper.homepage.OshHomePageBookMapper;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.homepage.IOshHomePageModulePathService;
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

    @Autowired
    private IOshHomePageModulePathService modulePathService;

    @Override
    public List<HotBookVO> getHotBooks(int limit) {
        List<HotBookVO> books = homePageBookMapper.selectHotBooks(limit);
        if (books.isEmpty()) {
            return books;
        }

        List<Long> bookIds = books.stream().map(HotBookVO::getId).collect(Collectors.toList());
        List<HotBookVO> tagRels = homePageBookMapper.selectTagsByBookIds(bookIds);

        Map<Long, List<String>> tagMap = tagRels.stream()
                .collect(Collectors.groupingBy(
                        HotBookVO::getBookId,
                        Collectors.mapping(HotBookVO::getTagName, Collectors.toList())
                ));

        for (HotBookVO vo : books) {
            List<String> tags = tagMap.getOrDefault(vo.getId(), Collections.emptyList());
            vo.setTags(tags.subList(0, Math.min(2, tags.size())));
            vo.setDetailUrl(modulePathService.getDetailPath("book", vo.getId()));
            if (StringUtils.isNotEmpty(vo.getCover())) {
                vo.setCover(ossService.getLimitedUrl(vo.getCover(), 1440));
            }
        }

        return books;
    }
}
