package com.backstage.system.service.impl.homepage;

import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.homepage.vo.HotCourseVO;
import com.backstage.system.mapper.homepage.OshHomePageCourseMapper;
import com.backstage.system.service.common.OssService;
import com.backstage.system.service.homepage.IOshHomePageModulePathService;
import com.backstage.system.service.homepage.IOshHomePageCourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页热门课程 Service 实现
 *
 * @author jayTatum
 */
@Service
public class OshHomePageCourseServiceImpl implements IOshHomePageCourseService {

    @Autowired
    private OshHomePageCourseMapper homePageCourseMapper;

    @Autowired
    private OssService ossService;

    @Autowired
    private IOshHomePageModulePathService modulePathService;

    @Override
    public List<HotCourseVO> getHotCourses(int limit) {
        List<HotCourseVO> courses = homePageCourseMapper.selectHotCourses(limit);
        if (courses.isEmpty()) {
            return courses;
        }

        List<Long> courseIds = courses.stream().map(HotCourseVO::getId).collect(Collectors.toList());
        List<HotCourseVO> tagRels = homePageCourseMapper.selectTagsByCourseIds(courseIds);

        Map<Long, List<String>> tagMap = tagRels.stream()
                .collect(Collectors.groupingBy(
                        HotCourseVO::getCourseId,
                        Collectors.mapping(HotCourseVO::getTagName, Collectors.toList())
                ));

        for (HotCourseVO vo : courses) {
            List<String> tags = tagMap.getOrDefault(vo.getId(), Collections.emptyList());
            vo.setTags(tags.subList(0, Math.min(2, tags.size())));
            vo.setDetailUrl(modulePathService.getDetailPath("course", vo.getId()));
            vo.setListUrl(modulePathService.getListPath("course"));
            if (StringUtils.isNotEmpty(vo.getCover())) {
                vo.setCover(ossService.getLimitedUrl(vo.getCover(), 1440));
            }
        }

        return courses;
    }
}
