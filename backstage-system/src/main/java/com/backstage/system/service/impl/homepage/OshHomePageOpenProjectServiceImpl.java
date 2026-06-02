package com.backstage.system.service.impl.homepage;

import com.backstage.system.domain.homepage.vo.HotOpenProjectVO;
import com.backstage.system.mapper.homepage.OshHomePageOpenProjectMapper;
import com.backstage.system.service.homepage.IOshHomePageModulePathService;
import com.backstage.system.service.homepage.IOshHomePageOpenProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 首页热门开源项目 Service 实现
 *
 * @author jayTatum
 */
@Service
public class OshHomePageOpenProjectServiceImpl implements IOshHomePageOpenProjectService {

    @Autowired
    private OshHomePageOpenProjectMapper homePageOpenProjectMapper;

    @Autowired
    private IOshHomePageModulePathService modulePathService;

    @Override
    public List<HotOpenProjectVO> getHotOpenProjects(int limit) {
        List<HotOpenProjectVO> projects = homePageOpenProjectMapper.selectHotOpenProjects(limit);
        if (projects.isEmpty()) {
            return projects;
        }

        List<Long> projectIds = projects.stream().map(HotOpenProjectVO::getId).collect(Collectors.toList());
        List<HotOpenProjectVO> tagRels = homePageOpenProjectMapper.selectTagsByProjectIds(projectIds);

        Map<Long, List<String>> tagMap = tagRels.stream()
                .collect(Collectors.groupingBy(
                        HotOpenProjectVO::getProjectId,
                        Collectors.mapping(HotOpenProjectVO::getTagName, Collectors.toList())
                ));

        for (HotOpenProjectVO vo : projects) {
            List<String> tags = tagMap.getOrDefault(vo.getId(), Collections.emptyList());
            vo.setTagList(tags.subList(0, Math.min(3, tags.size())));
            vo.setDetailUrl(modulePathService.getListPath("openproject"));
        }

        return projects;
    }
}
