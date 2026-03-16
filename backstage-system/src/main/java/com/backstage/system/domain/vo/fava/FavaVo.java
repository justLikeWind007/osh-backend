package com.backstage.system.domain.vo.fava;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * 收藏列表外层对象
 */
public class FavaVo implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;          // 收藏记录的唯一ID
    private String type;      // 类型 (course 或 column)
    private GoodsVo goods;    // 嵌套的商品详情对象

    public FavaVo() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public GoodsVo getGoods() {
        return goods;
    }

    public void setGoods(GoodsVo goods) {
        this.goods = goods;
    }

    /**
     * 嵌套的商品详情类
     */
    public static class GoodsVo implements Serializable {
        private static final long serialVersionUID = 1L;

        private Long id;          // 课程或专栏的ID
        private String title;     // 标题
        private String cover;     // 封面图

        @JsonProperty("try")
        private String tryStr;    // 对应文档中的 "try" 字段
        private String type;      // 商品类型

        public GoodsVo() {}

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getCover() {
            return cover;
        }

        public void setCover(String cover) {
            this.cover = cover;
        }

        public String getTryStr() {
            return tryStr;
        }

        public void setTryStr(String tryStr) {
            this.tryStr = tryStr;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }
}