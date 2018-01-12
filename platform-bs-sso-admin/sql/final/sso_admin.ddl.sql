/*
Navicat MySQL Data Transfer

Source Server         : 172.17.210.180
Source Server Version : 50619
Source Host           : 172.17.210.180:3306
Source Database       : sso

Target Server Type    : MYSQL
Target Server Version : 50619
File Encoding         : 65001

Date: 2017-11-22 15:03:46
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sso_admin
-- ----------------------------
DROP TABLE IF EXISTS `sso_admin`;
CREATE TABLE `sso_admin` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `login_name` varchar(32) NOT NULL DEFAULT '' COMMENT '登录名',
  `sure_name` varchar(32) NOT NULL DEFAULT '' COMMENT '真实姓名',
  `password` varchar(30) NOT NULL DEFAULT '' COMMENT '密码',
  `phone` varchar(32) NOT NULL DEFAULT '' COMMENT '手机号，多个号码用逗号分隔',
  `email` varchar(32) NOT NULL DEFAULT '' COMMENT '邮箱',
  `description` varchar(50) NOT NULL DEFAULT '' COMMENT '管理员描述',
  `state` tinyint(1) NOT NULL DEFAULT '1' COMMENT '管理员状态   1正常 2锁定',
  `role_id` int(11) NOT NULL DEFAULT '-1' COMMENT '管理员角色',
  `admin_type` int(11) NOT NULL DEFAULT '0' COMMENT '1:系统管理员 2：超级管理员 0：普通管理员（默认）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` varchar(10) NOT NULL DEFAULT '' COMMENT '创建人',
  `create_user_id` int(11) NOT NULL DEFAULT '-1' COMMENT '创建人id',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_user` varchar(10) NOT NULL DEFAULT '' COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_login_name` (`login_name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8 COMMENT='系统管理员';
