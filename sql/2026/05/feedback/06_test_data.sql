-- ============================================
-- 反馈系统测试数据
-- 执行时间: 2026-05-05
-- 版本: V1.1 Test Data
-- 功能: 提供开发和测试环境的虚拟数据
-- 
-- ⚠️  注意事项：
-- 1. 此文件仅用于开发和测试环境
-- 2. 请勿在生产环境执行此脚本
-- 3. 执行前请确保已运行 00-05 的所有迁移脚本
-- 4. 用户ID (1-19) 需要在系统中真实存在
-- 5. 执行后会插入约 22 条反馈和 18 条评论
-- ============================================

USE backstage;

-- 设置字符集支持 emoji
SET NAMES utf8mb4;
SET CHARACTER_SET_CLIENT = utf8mb4;
SET CHARACTER_SET_CONNECTION = utf8mb4;
SET CHARACTER_SET_RESULTS = utf8mb4;

-- 临时禁用外键检查（避免反馈ID引用问题）
SET FOREIGN_KEY_CHECKS = 0;

-- 清空旧测试数据（如存在）
DELETE FROM `assistant_feedback_process_record` WHERE `feedback_id` <= 22;
DELETE FROM `assistant_feedback_comment` WHERE `feedback_id` <= 22;
DELETE FROM `assistant_feedback` WHERE `id` <= 22;

-- 注意：不重置自增ID，因为 id 字段不是 AUTO_INCREMENT，需要手动指定

-- ============================================
-- 1. 插入测试用户（如果不存在）
-- ============================================
-- 注意：这里假设用户表已存在，如果需要创建测试用户，请根据实际用户表结构调整

-- ============================================
-- 2. 插入公告（管理员发布）
-- ============================================
INSERT INTO `assistant_feedback`
(`id`, `category_id`, `user_id`, `ticket_no`, `title`, `content`, `page_path`, `status`, `is_pinned`, `pin_order`, `comment_count`, `view_count`, `create_time`, `update_time`)
VALUES
(1, (SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'announcement'), 1, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 7 DAY), '%Y%m%d'), '000001'), '🎉 反馈系统正式上线！', '亲爱的用户们，我们的反馈系统已经正式上线了！现在您可以：\n\n1. 提交各类反馈（建议、错误、提问等）\n2. 查看其他用户的反馈\n3. 参与评论和讨论\n4. 关注反馈处理进度\n\n感谢大家的支持！', '/', 'resolved', 1, 1, 5, 128, DATE_SUB(NOW(), INTERVAL 7 DAY), NOW()),

(2, (SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'announcement'), 1, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 3 DAY), '%Y%m%d'), '000002'), '📢 系统维护通知', '系统将于本周六（2026-05-10）凌晨 2:00-4:00 进行例行维护，期间可能无法访问。请大家提前做好准备，给您带来的不便敬请谅解。', '/', 'resolved', 1, 2, 3, 89, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),

(3, (SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'announcement'), 1, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 DAY), '%Y%m%d'), '000003'), '🆕 新功能预告', '下周我们将推出以下新功能：\n\n1. 课程笔记功能\n2. 学习进度统计\n3. 个性化推荐\n\n敬请期待！', '/', 'in_progress', 1, 3, 8, 156, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW());

-- ============================================
-- 3. 插入建议类反馈
-- ============================================
INSERT INTO `assistant_feedback`
(`category_id`, `user_id`, `ticket_no`, `title`, `content`, `page_path`, `status`, `is_pinned`, `pin_order`, `comment_count`, `view_count`, `create_time`, `update_time`)
VALUES
((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'suggestion'), 2, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 5 DAY), '%Y%m%d'), '000004'), '💡 建议增加暗黑模式', '希望能够增加暗黑模式功能，方便夜间使用。现在的白色背景在晚上看起来比较刺眼，如果能提供暗黑模式选项就太好了！\n\n建议可以：\n1. 在设置中添加主题切换选项\n2. 支持跟随系统主题\n3. 记住用户的选择', '/settings', 'submitted', 0, 0, 12, 234, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'suggestion'), 3, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 4 DAY), '%Y%m%d'), '000005'), '💡 课程视频支持倍速播放', '建议课程视频支持 0.5x、0.75x、1.0x、1.25x、1.5x、2.0x 等多种倍速播放选项。有些内容我已经比较熟悉，希望能快速过一遍。', '/course/1', 'in_progress', 0, 0, 8, 167, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'suggestion'), 4, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 3 DAY), '%Y%m%d'), '000006'), '💡 增加学习笔记功能', '希望能在课程页面增加笔记功能，可以记录学习心得和重点内容。最好能支持 Markdown 格式，方便整理知识点。', '/course/lesson/123', 'submitted', 0, 0, 15, 198, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'suggestion'), 5, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 2 DAY), '%Y%m%d'), '000007'), '💡 支持课程离线下载', '建议支持课程视频离线下载功能，这样在没有网络的情况下也能学习。可以设置下载质量（高清、标清等）来节省流量。', '/course/1', 'submitted', 0, 0, 6, 145, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW());

-- ============================================
-- 4. 插入错误类反馈
-- ============================================
INSERT INTO `assistant_feedback`
(`category_id`, `user_id`, `ticket_no`, `title`, `content`, `page_path`, `status`, `is_pinned`, `pin_order`, `comment_count`, `view_count`, `create_time`, `update_time`)
VALUES
((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'bug'), 6, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 6 DAY), '%Y%m%d'), '000008'), '🐛 视频播放卡顿问题', '在观看课程视频时经常出现卡顿现象，特别是在高峰时段。已经尝试切换网络和清除缓存，问题依然存在。\n\n环境信息：\n- 浏览器：Chrome 120\n- 操作系统：macOS 14.0\n- 网络：100M 宽带', '/course/lesson/456', 'resolved', 0, 0, 9, 178, DATE_SUB(NOW(), INTERVAL 6 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'bug'), 7, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 5 DAY), '%Y%m%d'), '000009'), '🐛 登录后页面空白', '今天登录后首页显示空白，刷新多次都不行。清除缓存后恢复正常，但过一会儿又出现同样问题。', '/', 'resolved', 0, 0, 5, 123, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'bug'), 8, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 2 DAY), '%Y%m%d'), '000010'), '🐛 考试提交失败', '在做在线考试时，点击提交按钮后一直转圈，最后提示"提交失败，请重试"。重试多次都不行，导致考试时间浪费。', '/paper/1', 'in_progress', 0, 0, 11, 201, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'bug'), 9, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 DAY), '%Y%m%d'), '000011'), '🐛 移动端布局错乱', '在手机上访问网站时，部分页面布局错乱，文字和图片重叠在一起，无法正常浏览。\n\n设备信息：\n- 手机：iPhone 13\n- 系统：iOS 17.0\n- 浏览器：Safari', '/course/1', 'submitted', 0, 0, 7, 156, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW());

-- ============================================
-- 5. 插入提问类反馈
-- ============================================
INSERT INTO `assistant_feedback`
(`category_id`, `user_id`, `ticket_no`, `title`, `content`, `page_path`, `status`, `is_pinned`, `pin_order`, `comment_count`, `view_count`, `create_time`, `update_time`)
VALUES
((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'question'), 10, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 4 DAY), '%Y%m%d'), '000012'), '❓ 如何查看学习进度？', '请问在哪里可以查看自己的学习进度？我想知道每门课程的完成情况和总体学习时长。', '/user', 'resolved', 0, 0, 4, 89, DATE_SUB(NOW(), INTERVAL 4 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'question'), 11, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 3 DAY), '%Y%m%d'), '000013'), '❓ 课程证书如何获取？', '完成课程学习后如何获取证书？是自动发放还是需要申请？证书是电子版还是纸质版？', '/course/1', 'resolved', 0, 0, 6, 112, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'question'), 12, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 2 DAY), '%Y%m%d'), '000014'), '❓ 会员权益有哪些？', '请问购买会员后具体有哪些权益？和普通用户相比有什么区别？', '/createorder', 'submitted', 0, 0, 8, 145, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'question'), 13, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 DAY), '%Y%m%d'), '000015'), '❓ 如何修改个人信息？', '想修改个人资料中的手机号和邮箱，但是找不到修改入口，请问在哪里可以修改？', '/user/edit', 'resolved', 0, 0, 3, 67, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW());

-- ============================================
-- 6. 插入求助类反馈
-- ============================================
INSERT INTO `assistant_feedback`
(`category_id`, `user_id`, `ticket_no`, `title`, `content`, `page_path`, `status`, `is_pinned`, `pin_order`, `comment_count`, `view_count`, `create_time`, `update_time`)
VALUES
((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'help'), 14, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 3 DAY), '%Y%m%d'), '000016'), '🆘 忘记密码无法登录', '我忘记了登录密码，点击"忘记密码"后没有收到验证码邮件。已经检查了垃圾邮件箱，还是没有。请帮忙处理一下，谢谢！', '/login', 'in_progress', 0, 0, 5, 98, DATE_SUB(NOW(), INTERVAL 3 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'help'), 15, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 2 DAY), '%Y%m%d'), '000017'), '🆘 支付后未到账', '昨天购买了年度会员，支付成功但是账户还是显示普通用户。订单号：202605050001，请尽快处理！', '/createorder', 'in_progress', 0, 0, 7, 134, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'help'), 16, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 DAY), '%Y%m%d'), '000018'), '🆘 课程无法播放', '购买的课程一直显示"加载中"，无法播放。其他课程都正常，就这一门有问题。已经尝试换浏览器和设备，问题依旧。', '/course/lesson/789', 'submitted', 0, 0, 4, 87, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW());

-- ============================================
-- 7. 插入其它类反馈
-- ============================================
INSERT INTO `assistant_feedback`
(`category_id`, `user_id`, `ticket_no`, `title`, `content`, `page_path`, `status`, `is_pinned`, `pin_order`, `comment_count`, `view_count`, `create_time`, `update_time`)
VALUES
((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'other'), 17, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 5 DAY), '%Y%m%d'), '000019'), '📝 感谢团队的辛勤付出', '使用这个平台学习已经半年了，课程质量很高，界面也很友好。特别感谢客服团队的耐心解答。希望平台越办越好！', '/', 'resolved', 0, 0, 18, 267, DATE_SUB(NOW(), INTERVAL 5 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'other'), 18, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 2 DAY), '%Y%m%d'), '000020'), '📝 课程内容建议', '希望能增加更多实战项目课程，理论课程已经很丰富了，但是实践类的课程相对较少。建议可以增加一些企业级项目案例。', '/course/1', 'submitted', 0, 0, 10, 189, DATE_SUB(NOW(), INTERVAL 2 DAY), NOW()),

((SELECT `id` FROM `assistant_feedback_category` WHERE `code` = 'other'), 19, CONCAT('TK', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 DAY), '%Y%m%d'), '000021'), '📝 社区氛围很好', '这里的学习氛围很好，大家都很乐于分享和讨论。希望能继续保持这种良好的社区文化。', '/question_answer/1', 'resolved', 0, 0, 12, 156, DATE_SUB(NOW(), INTERVAL 1 DAY), NOW());

-- ============================================
-- 8. 插入评论数据
-- ============================================

-- 公告 1 的评论
INSERT INTO `assistant_feedback_comment`
(`feedback_id`, `user_id`, `content`, `parent_id`, `root_id`, `reply_to_user_id`, `reply_to_user_name`, `comment_level`, `is_admin_reply`, `create_time`)
VALUES
(1, 2, '太好了！期待已久的功能终于上线了！👍', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(1, 1, '感谢支持！我们会继续努力完善功能。', 1, 1, 2, '用户2', 2, 1, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(1, 3, '界面很漂亮，使用体验不错！', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1, 4, '建议增加反馈分类筛选功能', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(1, 1, '已经有了哦，在列表页顶部可以看到分类按钮。', 4, 4, 4, '用户4', 2, 1, DATE_SUB(NOW(), INTERVAL 6 DAY));

-- 建议 1 的评论（暗黑模式）
INSERT INTO `assistant_feedback_comment`
(`feedback_id`, `user_id`, `content`, `parent_id`, `root_id`, `reply_to_user_id`, `reply_to_user_name`, `comment_level`, `is_admin_reply`, `create_time`)
VALUES
(4, 5, '强烈支持！晚上看白色背景确实很刺眼。', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 6, '+1，希望能尽快实现这个功能。', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 7, '可以参考一下 GitHub 的暗黑模式实现', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(4, 1, '感谢建议！我们已经将暗黑模式列入开发计划，预计下个月上线。', 0, 0, NULL, NULL, 1, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(4, 2, '太好了！期待上线！', 4, 4, 1, '管理员', 2, 0, DATE_SUB(NOW(), INTERVAL 4 DAY));

-- 错误 1 的评论（视频卡顿）
INSERT INTO `assistant_feedback_comment`
(`feedback_id`, `user_id`, `content`, `parent_id`, `root_id`, `reply_to_user_id`, `reply_to_user_name`, `comment_level`, `is_admin_reply`, `create_time`)
VALUES
(8, 8, '我也遇到同样的问题，特别是晚上 8-10 点。', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(8, 1, '我们已经定位到问题，是 CDN 节点负载过高导致的。已经增加了服务器资源，现在应该恢复正常了。', 0, 0, NULL, NULL, 1, 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(8, 6, '确认已经恢复正常，现在播放很流畅！', 2, 2, 1, '管理员', 2, 0, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(8, 9, '感谢团队的快速响应！👍', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 5 DAY));

-- 提问 1 的评论（学习进度）
INSERT INTO `assistant_feedback_comment`
(`feedback_id`, `user_id`, `content`, `parent_id`, `root_id`, `reply_to_user_id`, `reply_to_user_name`, `comment_level`, `is_admin_reply`, `create_time`)
VALUES
(12, 1, '您可以在"用户中心 > 学习记录"中查看详细的学习进度。', 0, 0, NULL, NULL, 1, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(12, 10, '找到了，谢谢！', 1, 1, 1, '管理员', 2, 0, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(12, 11, '建议在首页也显示学习进度，这样更方便查看。', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(12, 1, '好建议！我们会考虑在首页增加学习进度卡片。', 3, 3, 11, '用户11', 2, 1, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 感谢反馈的评论
INSERT INTO `assistant_feedback_comment`
(`feedback_id`, `user_id`, `content`, `parent_id`, `root_id`, `reply_to_user_id`, `reply_to_user_name`, `comment_level`, `is_admin_reply`, `create_time`)
VALUES
(20, 12, '同感！这个平台真的很用心。', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(20, 13, '课程质量确实不错，学到了很多东西。', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(20, 1, '感谢大家的支持和认可！我们会继续努力，为大家提供更好的学习体验。', 0, 0, NULL, NULL, 1, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(20, 14, '希望能推出更多高质量课程！', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(20, 15, '客服态度真的很好，点赞！', 0, 0, NULL, NULL, 1, 0, DATE_SUB(NOW(), INTERVAL 4 DAY));

-- ============================================
-- 9. 更新反馈的评论数量
-- ============================================
UPDATE `assistant_feedback` SET `comment_count` = 5 WHERE `id` = 1;
UPDATE `assistant_feedback` SET `comment_count` = 5 WHERE `id` = 4;
UPDATE `assistant_feedback` SET `comment_count` = 4 WHERE `id` = 8;
UPDATE `assistant_feedback` SET `comment_count` = 4 WHERE `id` = 12;
UPDATE `assistant_feedback` SET `comment_count` = 5 WHERE `id` = 20;

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================
-- 10. 验证数据
-- ============================================

-- 查看反馈统计
SELECT
    c.name AS category_name,
    COUNT(*) AS feedback_count,
    SUM(CASE WHEN f.status = 'submitted' THEN 1 ELSE 0 END) AS submitted_count,
    SUM(CASE WHEN f.status = 'in_progress' THEN 1 ELSE 0 END) AS in_progress_count,
    SUM(CASE WHEN f.status = 'resolved' THEN 1 ELSE 0 END) AS resolved_count
FROM assistant_feedback f
LEFT JOIN assistant_feedback_category c ON f.category_id = c.id
WHERE f.delete_flag = 0
GROUP BY c.id, c.name
ORDER BY c.sort_order;

-- 查看评论统计
SELECT
    COUNT(*) AS total_comments,
    SUM(CASE WHEN comment_level = 1 THEN 1 ELSE 0 END) AS level1_comments,
    SUM(CASE WHEN comment_level = 2 THEN 1 ELSE 0 END) AS level2_comments,
    SUM(CASE WHEN is_admin_reply = 1 THEN 1 ELSE 0 END) AS admin_replies
FROM assistant_feedback_comment
WHERE delete_flag = 0;

-- ============================================
-- 11. 插入处理记录（状态流转历史）
-- ============================================

-- 为所有反馈插入初始提交记录
INSERT INTO `assistant_feedback_process_record`
(`id`, `feedback_id`, `from_status`, `to_status`, `operator_id`, `operator_name`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `delete_flag`)
SELECT
    (`id` * 100) + 1,
    `id`,
    NULL,
    'PENDING',
    `user_id`,
    CONCAT('用户', `user_id`),
    '用户提交反馈',
    `create_time`,
    `create_time`,
    `user_id`,
    `user_id`,
    0
FROM `assistant_feedback`
WHERE `id` <= 22 AND `delete_flag` = 0;

-- 为已处理的反馈插入状态流转记录
INSERT INTO `assistant_feedback_process_record`
(`id`, `feedback_id`, `from_status`, `to_status`, `operator_id`, `operator_name`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `delete_flag`)
SELECT
    (`id` * 100) + 2,
    `id`,
    'PENDING',
    CASE
        WHEN `status` = 'in_progress' THEN 'PROCESSING'
        WHEN `status` = 'resolved' THEN 'RESOLVED'
        WHEN `status` = 'closed' THEN 'CLOSED'
        ELSE `status`
    END,
    1,
    '管理员',
    CASE
        WHEN `status` = 'in_progress' THEN '管理员已开始处理'
        WHEN `status` = 'resolved' THEN '问题已解决'
        WHEN `status` = 'closed' THEN '反馈已关闭'
        ELSE CONCAT('状态更新为', `status`)
    END,
    DATE_ADD(`create_time`, INTERVAL 2 HOUR),
    DATE_ADD(`create_time`, INTERVAL 2 HOUR),
    1,
    1,
    0
FROM `assistant_feedback`
WHERE `id` <= 22
  AND `delete_flag` = 0
  AND `status` IN ('in_progress', 'resolved', 'closed');

-- 为部分已解决的反馈增加中间处理记录（模拟完整流转）
INSERT INTO `assistant_feedback_process_record`
(`id`, `feedback_id`, `from_status`, `to_status`, `operator_id`, `operator_name`, `remark`, `create_time`, `update_time`, `create_by`, `update_by`, `delete_flag`)
VALUES
-- 公告 1：待处理 -> 处理中 -> 已解决
(103, 1, 'PENDING', 'PROCESSING', 1, '管理员', '开始处理反馈', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), 1, 1, 0),
(104, 1, 'PROCESSING', 'RESOLVED', 1, '管理员', '反馈已处理完成', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 1, 1, 0),

-- 视频卡顿问题：待处理 -> 处理中 -> 已解决
(803, 8, 'PENDING', 'PROCESSING', 1, '管理员', '已定位问题，正在优化 CDN 配置', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), 1, 1, 0),
(804, 8, 'PROCESSING', 'RESOLVED', 1, '管理员', 'CDN 节点已扩容，问题已解决', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 1, 1, 0),

-- 登录空白问题：待处理 -> 处理中 -> 已解决
(903, 9, 'PENDING', 'PROCESSING', 1, '管理员', '正在排查前端缓存问题', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), 1, 1, 0),
(904, 9, 'PROCESSING', 'RESOLVED', 1, '管理员', '已修复缓存策略，问题解决', DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), 1, 1, 0);

-- ============================================
-- 执行完成
-- ============================================

SELECT '✅ 测试数据插入完成！' AS message;
SELECT CONCAT('共插入 ', COUNT(*), ' 条反馈记录') AS feedback_summary FROM assistant_feedback WHERE delete_flag = 0;
SELECT CONCAT('共插入 ', COUNT(*), ' 条评论记录') AS comment_summary FROM assistant_feedback_comment WHERE delete_flag = 0;
SELECT CONCAT('共插入 ', COUNT(*), ' 条处理记录') AS process_record_summary FROM assistant_feedback_process_record WHERE delete_flag = 0;
