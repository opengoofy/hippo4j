ALTER TABLE config Modify COLUMN keep_alive_time int(11) COMMENT '线程存活时间（秒）';

ALTER TABLE config Add execute_time_out int(11) COMMENT '执行超时时间（毫秒）' AFTER keep_alive_time;
