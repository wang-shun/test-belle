ALTER TABLE `biz_config`
ADD COLUMN `join_up_epp_flag`  tinyint(11) NOT NULL DEFAULT 0 COMMENT '是否接入epp,0:未接入，1:已接入' AFTER `update_user`;