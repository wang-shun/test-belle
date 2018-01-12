ALTER TABLE `sso_user`
MODIFY COLUMN `biz_user`  varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL DEFAULT '{}' COMMENT '绑定的业务系统,json字符串' AFTER `password`;