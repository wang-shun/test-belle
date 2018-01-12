/*
Navicat MySQL Data Transfer

Source Server         : 172.17.210.180
Source Server Version : 50619
Source Host           : 172.17.210.180:3306
Source Database       : sso

Target Server Type    : MYSQL
Target Server Version : 50619
File Encoding         : 65001

Date: 2017-11-22 15:03:40
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for org_unit
-- ----------------------------
DROP TABLE IF EXISTS `org_unit`;
CREATE TABLE `org_unit` (
  `unit_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '组织ID',
  `unit_code` varchar(28) COLLATE utf8_bin DEFAULT NULL COMMENT '组织编码',
  `unit_level_name` varchar(8) COLLATE utf8_bin DEFAULT NULL COMMENT '组织级别名称',
  `unit_level_id` int(11) NOT NULL COMMENT '组织级别ID',
  `parent_id` int(11) NOT NULL COMMENT '组织父Id',
  `name` varchar(30) COLLATE utf8_bin DEFAULT NULL COMMENT '组织简称',
  `en_name` varchar(20) COLLATE utf8_bin DEFAULT NULL COMMENT '英文名称',
  `full_name` varchar(30) COLLATE utf8_bin NOT NULL COMMENT '组织全称',
  `org_status` tinyint(2) NOT NULL COMMENT '组织状态（0 无效  1 有效）',
  `complement` int(11) DEFAULT '0' COMMENT '编制人数',
  `total_employee_count` int(11) DEFAULT '0' COMMENT '总编制人数（含子组织）',
  `actury_comp_count` int(11) DEFAULT '0' COMMENT '实际编制人数',
  `actury_totalemp_count` int(11) DEFAULT '0' COMMENT '总实际人数',
  `owner_employee_name` varchar(10) COLLATE utf8_bin DEFAULT '' COMMENT ' 组织负责人名称',
  `owner_employee_id` int(11) DEFAULT '0' COMMENT ' 组织负责人ID',
  `sort` smallint(6) NOT NULL COMMENT '排序',
  `effective_time` date NOT NULL COMMENT '生效期间',
  `end_time` date DEFAULT NULL COMMENT '结束时间',
  `oa_id` int(11) DEFAULT '0' COMMENT 'OA对应ID',
  `delflag` tinyint(2) NOT NULL DEFAULT '0' COMMENT '删除状态(1:已删除 0:正常)',
  `create_user` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(20) COLLATE utf8_bin NOT NULL COMMENT '修改人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  `remark` varchar(100) COLLATE utf8_bin DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`unit_id`),
  KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB AUTO_INCREMENT=32404 DEFAULT CHARSET=utf8 COLLATE=utf8_bin COMMENT='组织单元信息表';
