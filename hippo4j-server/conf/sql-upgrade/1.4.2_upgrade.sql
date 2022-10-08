ALTER TABLE config Modify COLUMN item_id varchar(128) COMMENT '项目ID';

ALTER TABLE inst_config Modify COLUMN item_id varchar(128) COMMENT '项目ID';

ALTER TABLE inst_config Modify COLUMN instance_id varchar(64) COMMENT '实例ID';

ALTER TABLE his_run_data Modify COLUMN item_id varchar(128) COMMENT '项目ID';

ALTER TABLE his_run_data Modify COLUMN instance_id varchar(64) COMMENT '实例ID';

ALTER TABLE config Modify COLUMN tp_id varchar(256) COMMENT '线程池ID';

ALTER TABLE inst_config Modify COLUMN tp_id varchar(256) COMMENT '线程池ID';

ALTER TABLE his_run_data Modify COLUMN tp_id varchar(256) COMMENT '线程池ID';

ALTER TABLE notify Modify COLUMN tp_id varchar(256) COMMENT '线程池ID';
