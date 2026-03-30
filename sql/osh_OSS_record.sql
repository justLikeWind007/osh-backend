CREATE TABLE `sys_oss_operation_log` (
     `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
     `file_key` varchar(512) NOT NULL COMMENT 'OSS文件唯一标识（key）,文件在bucket的位置',
     `original_name` varchar(255) DEFAULT NULL COMMENT '文件原始名称',
     `file_suffix` varchar(20) DEFAULT NULL COMMENT '文件后缀（mp4,jpg,png,pdf）',
     `file_size` bigint DEFAULT '0' COMMENT '文件大小（字节）',
     `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型：video/image/audio/doc/other',

     `operation_type` varchar(50) NOT NULL COMMENT '操作类型：UPLOAD-上传 DELETE-删除 DOWNLOAD-下载 PLAY-视频播放 RENAME-重命名',
     `operation_times` int DEFAULT '1' COMMENT '操作次数（同一文件同一操作累计）',
     `flow_size` bigint DEFAULT '0' COMMENT '消耗流量（字节，播放/下载有效）',

     `bucket_name` varchar(255) DEFAULT NULL COMMENT 'OSS桶名',
     `created_by` varchar(100) DEFAULT NULL COMMENT '操作人账号',
     `created_by_name` varchar(100) DEFAULT NULL COMMENT '操作人名称',
     `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
     `update_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',

     `client_ip` varchar(50) DEFAULT NULL COMMENT '客户端IP',
     `user_agent` varchar(1024) DEFAULT NULL COMMENT '客户端浏览器/设备信息',
     `remark` varchar(512) DEFAULT NULL COMMENT '备注',

     PRIMARY KEY (`id`),
     KEY `idx_file_key` (`file_key`),
     KEY `idx_operation_type` (`operation_type`),
     KEY `idx_created_at` (`created_at`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='OSS公共操作日志表（统计次数/流量/文件）';