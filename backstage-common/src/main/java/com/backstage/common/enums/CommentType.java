package com.backstage.common.enums;

/**
 * 评论类型
 */
public enum CommentType
{
    /**
     * 课程评论
     */
    COURSE(1, "课程评论"),

    /**
     * 专栏评论
     */
    COLUMN_COURSE(2, "专栏评论"),

    /**
     * 直播评论
     */
    LIVE(3, "直播评论"),

    /**
     * 电子书评论
     */
    BOOK(4, "电子书评论");

    private final Integer code;

    private final String info;

    CommentType(Integer code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public Integer getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }

    public static CommentType getByCode(Integer code)
    {
        if (code == null)
        {
            return null;
        }
        for (CommentType value : values())
        {
            if (value.code.equals(code))
            {
                return value;
            }
        }
        return null;
    }
}
