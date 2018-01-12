ALTER TABLE `sso_admin_role`
MODIFY COLUMN `create_user`  varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '-1' COMMENT '创建人' AFTER `create_time`,
modify COLUMN `update_user` varchar(32) NOT NULL DEFAULT '-1' COMMENT '修改人' after update_time;