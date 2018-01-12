/*
Navicat MySQL Data Transfer

Source Server         : 172.17.210.180
Source Server Version : 50619
Source Host           : 172.17.210.180:3306
Source Database       : sso

Target Server Type    : MYSQL
Target Server Version : 50619
File Encoding         : 65001

Date: 2017-11-22 15:03:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for biz_config
-- ----------------------------
DROP TABLE IF EXISTS `biz_config`;
CREATE TABLE `biz_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `biz_code` varchar(20) NOT NULL DEFAULT '' COMMENT '业务系统代码',
  `biz_name` varchar(30) NOT NULL DEFAULT '' COMMENT '业务系统名称',
  `biz_secret` varchar(32) NOT NULL DEFAULT '' COMMENT '业务系统秘钥',
  `login_url` varchar(200) NOT NULL DEFAULT '' COMMENT '登录url',
  `register_url` varchar(200) NOT NULL DEFAULT '' COMMENT '注册的url',
  `verify_user_pwd_url` varchar(200) NOT NULL DEFAULT '' COMMENT '验证用户密码url',
  `del_user_url` varchar(200) NOT NULL DEFAULT '' COMMENT '删除用户url',
  `sync_user_info_url` varchar(200) NOT NULL DEFAULT '' COMMENT '同步数据URL',
  `icon` varchar(50) NOT NULL DEFAULT '' COMMENT '系统图标url',
  `phone` varchar(32) NOT NULL DEFAULT '' COMMENT '手机号，多个号码用逗号分隔',
  `email` varchar(32) NOT NULL DEFAULT '' COMMENT '邮件',
  `description` varchar(50) NOT NULL DEFAULT '' COMMENT '描述，备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` varchar(10) NOT NULL DEFAULT '' COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `update_user` varchar(10) NOT NULL DEFAULT '' COMMENT '修改人',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uniq_biz_code` (`biz_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8 COMMENT='业务系统配置';
