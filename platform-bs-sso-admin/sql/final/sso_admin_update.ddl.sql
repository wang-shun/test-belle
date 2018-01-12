ALTER TABLE `sso_admin`
MODIFY COLUMN `create_user`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建人' AFTER `create_time`,
MODIFY COLUMN `update_user`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '修改人' AFTER `update_time`;