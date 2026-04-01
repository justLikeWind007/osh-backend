package com.backstage.system.service.comment;

import com.backstage.common.utils.StringUtils;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 基于 sensitive-word 的评论违禁词过滤。
 *
 * TODO 后续可以考虑介入 ES
 */
@Component
public class CommentForbiddenWordFilter {

    private SensitiveWordBs sensitiveWordBs;

    @PostConstruct
    public void init() {
        this.sensitiveWordBs = SensitiveWordBs.newInstance()
                .ignoreCase(true)
                .ignoreWidth(true)
                .ignoreChineseStyle(true)
                .ignoreEnglishStyle(true)
                .ignoreNumStyle(true)
                .ignoreRepeat(true)
                .init();
    }

    public boolean containsForbiddenWord(String content) {
        return StringUtils.isNotEmpty(content)
                && sensitiveWordBs != null
                && sensitiveWordBs.contains(content);
    }

    public String matchForbiddenWord(String content) {
        if (StringUtils.isEmpty(content) || sensitiveWordBs == null) {
            return null;
        }
        return sensitiveWordBs.findFirst(content);
    }
}
