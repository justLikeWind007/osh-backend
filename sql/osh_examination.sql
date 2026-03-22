表不是我建的
数据插入如下
INSERT INTO `osh_examination`
(`title`, `total_score`, `pass_score`, `expire`, `question_count`, `status`, `is_delete`)
VALUES
('2026年上半年入职安全生产考试', 100, 60, 60, 50, 1, 0),
('Java后端核心技术能力评估（高级）', 120, 72, 90, 40, 1, 0),
('企业文化与员工手册合规测试', 100, 80, 45, 20, 1, 0);
INSERT INTO `osh_examination`
(`id`, `title`, `total_score`, `pass_score`, `expire`, `question_count`, `start_time`, `end_time`, `status`, `create_by`, `create_time`, `is_delet`)
VALUES
(246, 'hu-20', 100, 60, 60, 10, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 1, 'admin', NOW(), 0),
(245, 'hu-19', 100, 60, 60, 10, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 1, 'admin', NOW(), 0),
(244, 'hu-18', 100, 60, 60, 10, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 1, 'admin', NOW(), 0),
(243, 'hu-17', 100, 60, 60, 10, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 1, 'admin', NOW(), 0),
(242, 'hu-16', 100, 60, 60, 10, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 1, 'admin', NOW(), 0),
(241, 'hu-15', 100, 60, 60, 10, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 1, 'admin', NOW(), 0),
(240, 'hu-14', 100, 60, 60, 10, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 1, 'admin', NOW(), 0),
(239, 'hu-13', 100, 60, 60, 10, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 1, 'admin', NOW(), 0),
(238, 'hu-12', 100, 60, 60, 10, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 1, 'admin', NOW(), 0),
(237, 'hu-11', 100, 60, 60, 10, NOW(), DATE_ADD(NOW(), INTERVAL 1 YEAR), 1, 'admin', NOW(), 0);