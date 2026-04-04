package com.backstage.system.service.comment;

import com.backstage.system.component.CommentForbiddenWordFilter;
import org.junit.Assert;
import org.junit.Test;

public class CommentForbiddenWordFilterTest {

    @Test
    public void shouldDetectForbiddenWordsAndPrintThem() {
        CommentForbiddenWordFilter filter = new CommentForbiddenWordFilter();
        filter.init();

        String[] contents = {
                "这条评论里包含强奸内容",
                "这条评论里包含色情内容",
                "这条评论里存在赌博信息",
                "杀人"
        };

        for (String content : contents) {
            String matchedWord = filter.matchForbiddenWord(content);
            if (matchedWord != null) {
                System.out.println("命中违禁词: " + matchedWord + "，原内容: " + content);
            }
            Assert.assertNotNull(matchedWord);
            Assert.assertTrue(filter.containsForbiddenWord(content));
        }
    }

    @Test
    public void shouldIgnoreNormalContent() {
        CommentForbiddenWordFilter filter = new CommentForbiddenWordFilter();
        filter.init();

        String matchedWord = filter.matchForbiddenWord("这是一条正常评论");

        Assert.assertNull(matchedWord);
        Assert.assertFalse(filter.containsForbiddenWord("这是一条正常评论"));
    }
}
