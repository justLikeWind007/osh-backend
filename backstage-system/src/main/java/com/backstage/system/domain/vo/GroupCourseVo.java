package com.backstage.system.domain.vo;

import com.backstage.system.domain.course.OshCoures;
import com.backstage.system.domain.group.GroupActivity;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/3
 * Time: 20:38
 */
public class GroupCourseVo extends OshCoures {

    /** 拼团活动信息 */
    private GroupActivity group;

    public GroupActivity getGroup() {
        return group;
    }

    public void setGroup(GroupActivity groupActivity) {
        this.group = groupActivity;
    }

    @Override
    public String toString() {
        return "GroupCourseVo{" +
                ", group=" + group +
                '}';
    }
}