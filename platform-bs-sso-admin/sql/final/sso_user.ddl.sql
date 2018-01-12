/*
Navicat MySQL Data Transfer

Source Server         : 172.17.210.180
Source Server Version : 50619
Source Host           : 172.17.210.180:3306
Source Database       : sso

Target Server Type    : MYSQL
Target Server Version : 50619
File Encoding         : 65001

Date: 2017-11-22 20:58:45
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sso_user
-- ----------------------------
DROP TABLE IF EXISTS `sso_user`;
CREATE TABLE `sso_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `login_name` varchar(32) NOT NULL DEFAULT '' COMMENT '用户名',
  `sure_name` varchar(32) NOT NULL DEFAULT '' COMMENT '真实姓名',
  `mobile` varchar(32) NOT NULL DEFAULT '' COMMENT '手机号',
  `telephone_number` varchar(32) NOT NULL DEFAULT '' COMMENT '座机号',
  `employee_number` varchar(32) NOT NULL DEFAULT '' COMMENT '工号',
  `employee_type` int(11) NOT NULL DEFAULT '0' COMMENT '员工类型',
  `email` varchar(32) NOT NULL DEFAULT '' COMMENT '邮件',
  `password` varchar(255) NOT NULL COMMENT '密码',
  `biz_user` varchar(255) NOT NULL DEFAULT '{}' COMMENT '绑定的业务系统,json字符串',
  `organizational_unit_name` varchar(30) NOT NULL DEFAULT '' COMMENT '所属机构名称',
  `organization_code` varchar(28) NOT NULL DEFAULT '' COMMENT '所属机构代码',
  `unit_id` int(11) NOT NULL DEFAULT '-1' COMMENT '所属机构主键',
  `create_user` varchar(32) NOT NULL DEFAULT '' COMMENT '创建者',
  `create_user_id` int(11) NOT NULL DEFAULT '-1' COMMENT '创建者主键',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_user` varchar(32) NOT NULL DEFAULT '' COMMENT '修改人',
  `sex` tinyint(2) NOT NULL DEFAULT '1' COMMENT '性别1：男0：女',
  `state` tinyint(2) NOT NULL DEFAULT '1' COMMENT '用户状态： 0：初始状态，1：正常，2已锁定',
  `del_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '1已删除 0未删除 ',
  `description` varchar(100) NOT NULL DEFAULT '' COMMENT '备注',
  `id_card` varchar(32) NOT NULL DEFAULT '' COMMENT '身份证号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_login_name` (`login_name`) USING BTREE,
  KEY `idx_mobile` (`mobile`) USING BTREE,
  KEY `idx_biz_user` (`biz_user`) USING BTREE,
  KEY `idx_organization_code` (`organization_code`) USING BTREE,
  KEY `idx_employee_number` (`employee_number`) USING BTREE COMMENT '工号索引'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
