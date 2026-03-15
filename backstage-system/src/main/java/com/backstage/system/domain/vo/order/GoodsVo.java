package com.backstage.system.domain.vo.order;

public class GoodsVo {
    private int id;
    private String title;
    private String cover;
    private String price;
    private String type;

    public int getId() {
        return id;
    }
    public void setId(int id) {
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
    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public void getGoods(GoodsVo goods) {
        this.id = goods.getId();
        this.title = goods.getTitle();
        this.cover = goods.getCover();
        this.price = goods.getPrice();
        this.type = goods.getType();
    }

}
