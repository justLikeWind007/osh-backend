package com.backstage.system.domain.vo;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * OshUser: 九转苍翎
 * Date: 2026/3/12
 * Time: 15:02
 */
public class LiveUserVo {
    private String isbuy;
    private String isfava;

    public String getIsbuy() {
        return isbuy;
    }

    public void setIsbuy(String isbuy) {
        this.isbuy = isbuy;
    }

    public String getIsfava() {
        return isfava;
    }

    public void setIsfava(String isfava) {
        this.isfava = isfava;
    }

    @Override
    public String toString() {
        return "LiveUserVo{" +
                "isbuy='" + isbuy + '\'' +
                ", isfava='" + isfava + '\'' +
                '}';
    }
}
