package com.backstage.system.domain.vo;

import java.io.Serializable;
import java.util.List;

/**
 * 电子书章节菜单视图对象
 *
 * @author backstage
 */
public class BookMenuVO implements Serializable
{
    private static final long serialVersionUID = 1L;

    /** 电子书基本信息 */
    private BookSimpleVO detail;

    /** 章节列表 */
    private List<BookChapterVO> menus;

    public BookSimpleVO getDetail()
    {
        return detail;
    }

    public void setDetail(BookSimpleVO detail)
    {
        this.detail = detail;
    }

    public List<BookChapterVO> getMenus()
    {
        return menus;
    }

    public void setMenus(List<BookChapterVO> menus)
    {
        this.menus = menus;
    }
}
