package com.backstage.system.service.course;

/**
 * 课程索引删除消息。
 * 删除链路只依赖课程 ID，保持消息体最小化，减少业务侧和 Flink 侧的耦合。
 */
public class CourseIndexDeleteMessage
{
    private Long id;

    public CourseIndexDeleteMessage()
    {
    }

    public CourseIndexDeleteMessage(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }
}
