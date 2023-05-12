ALTER TABLE config Modify COLUMN item_id varchar(128) COMMENT '项目ID';

ALTER TABLE inst_config Modify COLUMN item_id varchar(128) COMMENT '项目ID';

ALTER TABLE inst_config Modify COLUMN instance_id varchar(64) COMMENT '实例ID';

ALTER TABLE his_run_data Modify COLUMN item_id varchar(128) COMMENT '项目ID';

ALTER TABLE his_run_data Modify COLUMN instance_id varchar(64) COMMENT '实例ID';

ALTER TABLE config Modify COLUMN tp_id varchar(256) COMMENT '线程池ID';

ALTER TABLE inst_config Modify COLUMN tp_id varchar(256) COMMENT '线程池ID';

ALTER TABLE his_run_data Modify COLUMN tp_id varchar(256) COMMENT '线程池ID';

ALTER TABLE notify Modify COLUMN tp_id varchar(256) COMMENT '线程池ID';

CREATE TABLE `his_config_verify`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `type` int NULL DEFAULT NULL COMMENT '变更类型',
  `mark` varchar(128)  DEFAULT NULL COMMENT '框架线程池类型',
  `tenant_id` varchar(128)  DEFAULT NULL COMMENT '租户ID',
  `item_id` varchar(128)  DEFAULT NULL COMMENT '项目ID',
  `tp_id` varchar(256)  DEFAULT NULL COMMENT '线程池ID',
  `identify` varchar(64)  DEFAULT NULL COMMENT '线程池唯一标识',
  `content` longtext DEFAULT NULL COMMENT '参数变更内容',
  `modify_all` tinyint(1) NULL DEFAULT NULL COMMENT '是否全部修改',
  `gmt_create` datetime NULL DEFAULT NULL COMMENT '参数变更时间',
  `modify_user` varchar(128)  DEFAULT NULL COMMENT '修改人',
  `verify_status` tinyint(1) NULL DEFAULT NULL COMMENT '审核状态 0：待审核 1：审核通过 2：审核拒绝',
  `gmt_verify` datetime NULL  DEFAULT NULL COMMENT '审核时间',
  `verify_user` varchar(128)  DEFAULT NULL COMMENT '审核人',
PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 DEFAULT CHARSET=utf8mb4 COMMENT = '参数变更审核记录表';
