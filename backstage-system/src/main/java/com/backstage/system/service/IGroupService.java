package com.backstage.system.service;

import com.backstage.system.domain.vo.GroupColumnVo;
import com.backstage.system.domain.vo.GroupCourseVo;
import com.backstage.system.domain.vo.GroupWorkVo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/3
 * Time: 20:55
 */
public interface IGroupService {
    GroupCourseVo course(Long id, Long groupId);

    GroupColumnVo column(Long id, Long groupId);

    List<GroupWorkVo> selectGroupList(Long groupId);
}
