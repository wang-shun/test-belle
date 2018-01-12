# test 测试系统结构脚本
# version：test_iris-0.0.1
#
#
# MODIFIED  (MM/DD/YY)
# xu.t       07/06/17     -- 创建
#
#
# ******************************************** test 数据库表列表****************************************************
# 测试主表	                                                    创建表	test_record
# -------------------------------------------------------------------------------------------------------------------


tee 1.table.ddl.log #输出日志文件

use sso

alter table biz_config add `join_up_epp_flag` tinyint(11) NOT NULL DEFAULT '0' COMMENT '是否接入epp,0:未接入，1:已接入' AFTER `update_user`;

alter table sso_user ADD COLUMN `position_name`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '岗位';

ALTER TABLE `sso_user` ADD INDEX `idx_id_card` (`id_card`) ;

notee