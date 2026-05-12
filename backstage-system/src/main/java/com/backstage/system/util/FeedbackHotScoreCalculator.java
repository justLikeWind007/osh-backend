package com.backstage.system.util;

import java.util.function.IntUnaryOperator;

/**
 * 反馈热度分计算器
 * <p>
 * 算法设计：
 * 1. 互动分 = 点赞 × 4 + 收藏 × 3 + 评论 × 2
 * 2. 有效浏览 =  min(浏览, 互动×20) + max(0, 浏览-互动×20) / 10
 * 3. 热度分 = 互动分 × 4 + 有效浏览 × 1
 * <p>
 * 设计思路：用互动分作为浏览可信度的锚点，超过20倍的部分视为可疑流量打1折
 *
 * @author backstage
 */
public final class FeedbackHotScoreCalculator {

    /** 点赞权重 */
    private static final int LIKE_WEIGHT = 4;

    /** 收藏权重 */
    private static final int FAVORITE_WEIGHT = 3;

    /** 评论权重 */
    private static final int COMMENT_WEIGHT = 2;

    /** 互动分权重（在最终热度中的权重） */
    private static final int INTERACTION_SCORE_WEIGHT = 4;

    /** 浏览基础权重 */
    private static final int VIEW_WEIGHT = 1;

    /** 浏览可信度阈值：超过互动分的多少倍后开始打折 */
    private static final int VIEW_TRUST_THRESHOLD = 20;

    /** 超额浏览折扣率（10% = 1/10） */
    private static final int EXCESS_VIEW_DISCOUNT = 10;

    /** 私有构造，禁止实例化 */
    private FeedbackHotScoreCalculator() {
        throw new AssertionError("工具类禁止实例化");
    }

    /**
     * 计算热度分
     *
     * @param likeCount     点赞数
     * @param favoriteCount 收藏数
     * @param commentCount  评论数
     * @param viewCount     浏览数
     * @return 热度分
     */
    public static int calculate(int likeCount, int favoriteCount, int commentCount, int viewCount) {
        // 步骤1：计算互动分
        int interactionScore = calculateInteractionScore(likeCount, favoriteCount, commentCount);

        // 步骤2：计算有效浏览
        int effectiveViewCount = calculateEffectiveViewCount(viewCount, interactionScore);

        // 步骤3：计算最终热度
        return interactionScore * INTERACTION_SCORE_WEIGHT + effectiveViewCount * VIEW_WEIGHT;
    }

    /**
     * 构建热度分更新 SQL，保证数据库原子更新与 Java 侧热度算法一致。
     *
     * @param likeDelta     点赞增量
     * @param favoriteDelta 收藏增量
     * @param commentDelta  评论增量
     * @return 热度分 SQL 表达式
     */
    public static String buildHotScoreSql(int likeDelta, int favoriteDelta, int commentDelta) {
        String interactionScoreSql = buildInteractionScoreSql(likeDelta, favoriteDelta, commentDelta);
        String effectiveViewCountSql = buildEffectiveViewCountSql(interactionScoreSql);
        return "(" + interactionScoreSql + " * " + INTERACTION_SCORE_WEIGHT
                + " + " + effectiveViewCountSql + " * " + VIEW_WEIGHT + ")";
    }

    /**
     * 构建计数字段的增量 SQL，递减时自动兜底为 0。
     *
     * @param columnName 列名
     * @param delta      增量
     * @return 计数字段 SQL 表达式
     */
    public static String buildCountSql(String columnName, int delta) {
        String baseSql = "COALESCE(" + columnName + ", 0)";
        return delta >= 0 ? baseSql + " + " + delta : "GREATEST(" + baseSql + " - " + Math.abs(delta) + ", 0)";
    }

    /**
     * 计算互动分
     * <p>
     * 互动分 = 点赞 × 4 + 收藏 × 3 + 评论 × 2
     */
    public static int calculateInteractionScore(int likeCount, int favoriteCount, int commentCount) {
        return likeCount * LIKE_WEIGHT
                + favoriteCount * FAVORITE_WEIGHT
                + commentCount * COMMENT_WEIGHT;
    }

    /**
     * 计算有效浏览
     * <p>
     * 有效浏览 =  min(浏览, 互动×20) + max(0, 浏览-互动×20) / 10
     * <p>
     * 即：前20倍互动量的浏览全额计算，超出部分打1折
     *
     * @param viewCount        原始浏览数
     * @param interactionScore 互动分
     * @return 有效浏览数
     */
    public static int calculateEffectiveViewCount(int viewCount, int interactionScore) {
        int trustedViewLimit = interactionScore * VIEW_TRUST_THRESHOLD;

        // 使用函数式风格：构建计算管道
        IntUnaryOperator truncateTrusted = v -> Math.min(v, trustedViewLimit);
        IntUnaryOperator calculateExcess = v -> Math.max(0, viewCount - trustedViewLimit) / EXCESS_VIEW_DISCOUNT;
        IntUnaryOperator sumEffective = v -> truncateTrusted.applyAsInt(v) + calculateExcess.applyAsInt(v);

        return sumEffective.applyAsInt(viewCount);
    }

    private static String buildInteractionScoreSql(int likeDelta, int favoriteDelta, int commentDelta) {
        return "("
                + buildCountSql("like_count", likeDelta) + " * " + LIKE_WEIGHT
                + " + " + buildCountSql("favorite_count", favoriteDelta) + " * " + FAVORITE_WEIGHT
                + " + " + buildCountSql("comment_count", commentDelta) + " * " + COMMENT_WEIGHT
                + ")";
    }

    private static String buildEffectiveViewCountSql(String interactionScoreSql) {
        String trustedViewLimitSql = "(" + interactionScoreSql + " * " + VIEW_TRUST_THRESHOLD + ")";
        return "(LEAST(COALESCE(view_count, 0), " + trustedViewLimitSql + ") + "
                + "GREATEST(COALESCE(view_count, 0) - " + trustedViewLimitSql + ", 0) DIV " + EXCESS_VIEW_DISCOUNT + ")";
    }

    /**
     * 计算热度分（包装方法，支持链式调用场景）
     *
     * @param feedback 包含互动数据的反馈对象
     * @return 热度分
     */
    public static int calculate(FeedbackInteraction feedback) {
        return calculate(
                feedback.getLikeCount(),
                feedback.getFavoriteCount(),
                feedback.getCommentCount(),
                feedback.getViewCount()
        );
    }

    /**
     * 反馈互动数据接口（用于解耦）
     */
    public interface FeedbackInteraction {
        int getLikeCount();

        int getFavoriteCount();

        int getCommentCount();

        int getViewCount();
    }
}
