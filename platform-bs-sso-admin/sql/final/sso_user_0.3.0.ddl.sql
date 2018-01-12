ALTER TABLE `sso_user`
ADD COLUMN `position_name`  varchar(10) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '岗位';
ALTER TABLE `sso_user`
DROP INDEX `index_id_card`,
ADD INDEX `idx_id_card` (`id_card`) USING BTREE ;