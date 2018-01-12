/*
Navicat MySQL Data Transfer

Source Server         : 172.17.210.180
Source Server Version : 50619
Source Host           : 172.17.210.180:3306
Source Database       : sso

Target Server Type    : MYSQL
Target Server Version : 50619
File Encoding         : 65001

Date: 2017-11-22 15:03:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for sso_admin_role_org_unit
-- ----------------------------
DROP TABLE IF EXISTS `sso_admin_role_org_unit`;
CREATE TABLE `sso_admin_role_org_unit` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `org_unit_id` int(11) NOT NULL DEFAULT '-1' COMMENT '关联-组织单元信息表ID',
  `sso_admin_role_id` int(11) NOT NULL DEFAULT '-1' COMMENT '关联-系统管理员角色ID',
  `unit_code` varchar(28) NOT NULL DEFAULT '' COMMENT '组织编码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` varchar(10) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_user` varchar(10) NOT NULL DEFAULT '' COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_roleid_orgunitid` (`org_unit_id`,`sso_admin_role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=683 DEFAULT CHARSET=utf8 COMMENT='管理员角色-组织单元信息表 关联表';
