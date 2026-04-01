package com.backstage.system.domain.questionanswer.vo;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/29
 * Time: 18:14
 */
public class QueryQuestionListVO {
    private String resourceNo;
    private String resourceType;
    private String username;
    private String title;
    private String content;
    private String isPaidOnly;
    private Byte status;
    private Long solvedAnswerId;
    private Integer viewCount;
    private Integer followCount;
    private List<String> tags;
}
