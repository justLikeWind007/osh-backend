package com.backstage.system.domain.vo;

import com.backstage.system.domain.group.GroupActivity;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/5
 * Time: 19:12
 */
public class GroupColumnVo extends ColumnDetailVo {

    private GroupActivity group;

    public GroupActivity getGroup() {
        return group;
    }

    public void setGroup(GroupActivity group) {
        this.group = group;
    }

    @Override
    public String toString() {
        return "GroupColumnVo{" +
                "group=" + group +
                '}';
    }
}
