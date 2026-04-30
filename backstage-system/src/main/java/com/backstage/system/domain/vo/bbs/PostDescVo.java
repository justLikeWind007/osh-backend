package com.backstage.system.domain.vo.bbs;

import java.util.List;

public class PostDescVo {
    private List<String> images;
    private String text;

    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}