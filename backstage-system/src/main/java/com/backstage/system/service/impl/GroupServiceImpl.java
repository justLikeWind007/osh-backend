package com.backstage.system.service.impl;

import com.backstage.system.domain.course.OshCourse;
import com.backstage.system.domain.group.GroupActivity;
import com.backstage.system.domain.group.GroupWork;
import com.backstage.system.domain.vo.*;
import com.backstage.system.mapper.column.ColumnMapper;
import com.backstage.system.mapper.course.OshCourseMapper;
import com.backstage.system.mapper.group.GroupMapper;
import com.backstage.system.service.IGroupService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/3
 * Time: 20:55
 */
@Service
public class GroupServiceImpl implements IGroupService {

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private OshCourseMapper courseMapper;
    @Autowired
    private ColumnMapper columnMapper;

    @Override
    public GroupCourseVo course(Long id, Long groupId) {
        GroupCourseVo groupCourseVO = new GroupCourseVo();
        GroupActivity groupActivity = groupMapper.getGroupActivityById(groupId);
        OshCourse sysCourse = courseMapper.selectCourseById(id);
        BeanUtils.copyProperties(sysCourse, groupCourseVO);
        groupCourseVO.setGroup(groupActivity);
        return groupCourseVO;
    }

    @Override
    public GroupColumnVo column(Long id, Long groupId) {
        GroupColumnVo groupColumnVO = new GroupColumnVo();
        GroupActivity groupActivity = groupMapper.getGroupActivityById(groupId);
        ColumnDetailVo columnDetail = columnMapper.getColumnDetail(id);
        groupColumnVO.setGroup(groupActivity);
        BeanUtils.copyProperties(columnDetail, groupColumnVO);
        return groupColumnVO;
    }

    @Override
    public List<GroupWorkVo> selectGroupList(Long groupId) {
        List<GroupWorkVo> groupWorkVoList = new ArrayList<>();
         List<GroupWork> groupWorkList = groupMapper.getGroupWorkListByActivityId(groupId);
         for (GroupWork groupWork : groupWorkList) {
             GroupWorkVo groupWorkVO = new GroupWorkVo();
             BeanUtils.copyProperties(groupWork, groupWorkVO);
             Long groupWorkId = groupWorkVO.getId();
             List<Long> userIds = groupMapper.getGroupWorkUserListById(groupWorkId);
             List<GroupUserVo> groupUserVoList = groupMapper.getGroupUserListByIds(userIds);
             groupWorkVO.setUsers(groupUserVoList);
             groupWorkVoList.add(groupWorkVO);
         }
        return groupWorkVoList;
    }
}