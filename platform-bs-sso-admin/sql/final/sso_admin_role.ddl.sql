/*
Navicat MySQL Data Transfer

Source Server         : 172.17.210.180
Source Server Version : 50619
Source Host           : 172.17.210.180:3306
Source Database       : sso

Target Server Type    : MYSQL
Target Server Version : 50619
File Encoding         : 65001

Date: 2017-11-22 15:03:52
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sso_admin_role
-- ----------------------------
DROP TABLE IF EXISTS `sso_admin_role`;
CREATE TABLE `sso_admin_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `role_name` varchar(30) NOT NULL DEFAULT '' COMMENT '角色名称',
  `description` varchar(50) NOT NULL DEFAULT '' COMMENT '角色描述',
  `role_code` varchar(20) NOT NULL DEFAULT '' COMMENT '角色编码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` varchar(10) NOT NULL DEFAULT '-1' COMMENT '创建人',
  `create_user_id` int(11) NOT NULL DEFAULT '-1' COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_user` varchar(10) NOT NULL DEFAULT '-1' COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_role_code` (`role_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=78 DEFAULT CHARSET=utf8 COMMENT='系统管理员角色';
