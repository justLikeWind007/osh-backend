package com.backstage.system.mapper.group;

import com.backstage.system.domain.group.GroupActivity;
import com.backstage.system.domain.group.GroupWork;
import com.backstage.system.domain.vo.GroupUserVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/3
 * Time: 20:55
 */
@Mapper
public interface GroupMapper {
    GroupActivity getGroupActivityById(Long id);

    List<GroupWork> getGroupWorkListByActivityId(Long groupId);

    List<Long> getGroupWorkUserListById(Long groupWorkId);

    List<GroupUserVo> getGroupUserListByIds(@Param("ids") List<Long> ids);
}
