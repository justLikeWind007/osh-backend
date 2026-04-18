package com.backstage.system.request;

import java.util.List;

public class CourseDeleteRequest {
    private List<Long> ids;
    public List<Long> getIds() { return ids; }
    public void setIds(List<Long> ids) { this.ids = ids; }
}
