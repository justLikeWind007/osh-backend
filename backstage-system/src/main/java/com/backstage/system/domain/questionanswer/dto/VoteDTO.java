package com.backstage.system.domain.questionanswer.dto;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: 九转苍翎
 * Date: 2026/3/29
 * Time: 18:51
 */
public class VoteDTO {
    private Long answerId;

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
    }

    @Override
    public String toString() {
        return "VoteDTO{" +
                "answerId=" + answerId +
                '}';
    }
}
