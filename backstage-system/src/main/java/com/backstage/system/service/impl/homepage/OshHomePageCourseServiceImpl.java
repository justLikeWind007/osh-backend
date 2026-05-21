package com.backstage.system.service.impl.homepage;


import com.backstage.common.utils.StringUtils;
import com.backstage.system.domain.homepage.vo.HotCourseVO;
import com.backstage.system.mapper.homepage.OshHomePageCourseMapper;
import com.backstage.system.service.common.OssService;
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

    @Override
    public List<HotCourseVO> getHotCourses(int limit) {
        // 1. 查询热门课程（热度公式排序）
        List<HotCourseVO> courses = homePageCourseMapper.selectHotCourses(limit);
        if (courses.isEmpty()) {
            return courses;
        }

        // 2. 批量查询标签
        List<Long> courseIds = courses.stream()
                .map(HotCourseVO::getId)
                .collect(Collectors.toList());
        List<HotCourseVO> tagRels = homePageCourseMapper.selectTagsByCourseIds(courseIds);

        // 3. 按 courseId 分组
        Map<Long, List<String>> tagMap = tagRels.stream()
                .collect(Collectors.groupingBy(
                        HotCourseVO::getCourseId,
                        Collectors.mapping(HotCourseVO::getTagName, Collectors.toList())
                ));

        // 4. 组装标签、跳转链接、封面URL
        for (HotCourseVO vo : courses) {
            // 每个课程最多取前2个标签
            List<String> tags = tagMap.getOrDefault(vo.getId(), Collections.emptyList());
            vo.setTags(tags.subList(0, Math.min(2, tags.size())));
            // 详情页跳转路径
            vo.setDetailUrl("/course_detail/" + vo.getId());
            // 封面图片：相对路径转为临时签名URL（有效期1440分钟=24小时）
            if (StringUtils.isNotEmpty(vo.getCover())) {
                vo.setCover(ossService.getLimitedUrl(vo.getCover(), 1440));
            }
        }

        return courses;
    }
}
