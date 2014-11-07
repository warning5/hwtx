/*
Navicat MySQL Data Transfer

Source Server         : mysql
Source Server Version : 50539
Source Host           : localhost:3306
Source Database       : fnclelgcl

Target Server Type    : MYSQL
Target Server Version : 50539
File Encoding         : 65001

Date: 2014-09-08 23:55:55
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for def_authrule
-- ----------------------------
DROP TABLE IF EXISTS `def_authrule`;
CREATE TABLE `def_authrule` (
  `authRuleId` varchar(45) NOT NULL DEFAULT '0' COMMENT '权限规则ID',
  `regionAuthRule` varchar(255) DEFAULT NULL COMMENT '辖区权限',
  `orgTypeAuthRule` varchar(255) DEFAULT NULL COMMENT '机构类别权限',
  `orgAuthRule` varchar(255) DEFAULT NULL COMMENT '单个机构权限',
  PRIMARY KEY (`authRuleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='权限规则定义表';

-- ----------------------------
-- Records of def_authrule
-- ----------------------------

-- ----------------------------
-- Table structure for def_cat
-- ----------------------------
DROP TABLE IF EXISTS `def_cat`;
CREATE TABLE `def_cat` (
  `dcatId` varchar(45) NOT NULL COMMENT '环境测评类别定义ID',
  `dcatName` varchar(255) DEFAULT NULL COMMENT '环境测评类别名称',
  `dcatDefDate` date DEFAULT NULL COMMENT '环境测评类别定义时间',
  `dcatDefModDate` date DEFAULT NULL COMMENT '环境测评类别定义修改时间',
  `dcatExpireMark` tinyint(2) DEFAULT NULL COMMENT '环境测评类别定义过期标志位',
  `dcatDelMark` tinyint(2) DEFAULT NULL COMMENT '环境测评类别定义删除标志',
  `dcatScore` int(4) DEFAULT NULL COMMENT '环境测评类别定义分值',
  `dcatWeight` float(10,0) DEFAULT NULL COMMENT '环境测评类别定义权重',
  `dcatAuthRuleId` varchar(45) DEFAULT NULL COMMENT '类别定义权限规则ID (?)',
  `dcatCalRuleId` int(45) DEFAULT NULL COMMENT '计算规则ID',
  `dcatRemark` varchar(255) DEFAULT NULL COMMENT '环境测评类别说明',
  PRIMARY KEY (`dcatId`),
  KEY `fk_def_cat_def_authrule_1` (`dcatAuthRuleId`),
  CONSTRAINT `fk_def_cat_def_authrule_1` FOREIGN KEY (`dcatAuthRuleId`) REFERENCES `def_authrule` (`authRuleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='类别定义表';

-- ----------------------------
-- Records of def_cat
-- ----------------------------

-- ----------------------------
-- Table structure for def_class
-- ----------------------------
DROP TABLE IF EXISTS `def_class`;
CREATE TABLE `def_class` (
  `dclassId` varchar(45) NOT NULL DEFAULT '0' COMMENT '环境测评项目ID',
  `dclassName` varchar(255) DEFAULT NULL COMMENT '环境测评项目名称',
  `dclassAuthRuleId` int(11) DEFAULT NULL COMMENT '环境测评项目权限规则ID',
  `dcatId` varchar(45) DEFAULT NULL COMMENT '环境测评类别定义ID',
  `dclassDefDate` date DEFAULT NULL COMMENT '环境测评项目定义时间',
  `dclassModDate` double DEFAULT NULL COMMENT '环境测评项目修改时间',
  `dclassExpireMark` tinyint(2) DEFAULT NULL COMMENT '环境测评项目过期标志位',
  `dclassWeight` float(20,0) DEFAULT NULL COMMENT '环境测评项目权重',
  `dclassScore` int(20) DEFAULT NULL COMMENT '环境测评项目分值',
  `dclassDelMark` tinyint(2) DEFAULT NULL COMMENT '环境测评删除标志位',
  `dclassCalRuleId` varchar(45) DEFAULT NULL COMMENT '环境测评项目计算规则ID',
  `dclassRemark` varchar(255) DEFAULT NULL COMMENT '环境测评项目说明',
  PRIMARY KEY (`dclassId`),
  KEY `dclassCalRuleId` (`dclassCalRuleId`),
  CONSTRAINT `def_class_ibfk_1` FOREIGN KEY (`dclassCalRuleId`) REFERENCES `def_formula` (`calRuleId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='项目定义表';

-- ----------------------------
-- Records of def_class
-- ----------------------------

-- ----------------------------
-- Table structure for def_distr
-- ----------------------------
DROP TABLE IF EXISTS `def_distr`;
CREATE TABLE `def_distr` (
  `regionId` varchar(45) NOT NULL DEFAULT '0' COMMENT '行政辖区ID',
  `regionName` varchar(255) DEFAULT NULL COMMENT '行政辖区名称',
  `regionType` varchar(255) DEFAULT NULL COMMENT '行政辖区级别(省市县区）',
  `upRegionId` varchar(45) DEFAULT NULL COMMENT '上级行政辖区',
  PRIMARY KEY (`regionId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='行政辖区定义表';

-- ----------------------------
-- Records of def_distr
-- ----------------------------

-- ----------------------------
-- Table structure for def_formula
-- ----------------------------
DROP TABLE IF EXISTS `def_formula`;
CREATE TABLE `def_formula` (
  `calRuleId` varchar(45) NOT NULL DEFAULT '0' COMMENT '计算规则ID',
  `calObjectiveId` varchar(45) DEFAULT NULL COMMENT '计算目标ID',
  `calRule` varchar(255) DEFAULT NULL COMMENT '计算规则公式',
  `calRuleType` varchar(50) DEFAULT NULL COMMENT '计算规则目标类别（存储表名）是哪一项的计算规则',
  `calExpireMark` int(2) DEFAULT NULL,
  `calExpireDate` datetime DEFAULT NULL,
  `calCreateDate` datetime DEFAULT NULL,
  PRIMARY KEY (`calRuleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='计算规则定义表';

-- ----------------------------
-- Records of def_formula
-- ----------------------------

-- ----------------------------
-- Table structure for def_inidata
-- ----------------------------
DROP TABLE IF EXISTS `def_inidata`;
CREATE TABLE `def_inidata` (
  `dkpiId` varchar(45) DEFAULT NULL COMMENT '环境测评指标定义ID',
  `diniDataCalRuleId` varchar(45) DEFAULT NULL COMMENT '计算规则ID',
  `diniDataId` varchar(45) NOT NULL DEFAULT '0' COMMENT '环境测评源数据项定义ID',
  `diniDataAuthRuleId` varchar(45) DEFAULT NULL COMMENT '权限规则ID',
  `diniDataName` varchar(255) DEFAULT NULL COMMENT '源数据项名称',
  `submitOrgRole` varchar(45) DEFAULT NULL COMMENT '申报机构角色标识',
  `diniDataDefDate` date DEFAULT NULL COMMENT '源数据项定义时间',
  `diniDataModDate` date DEFAULT NULL COMMENT '源数据项修改时间',
  `diniDataExpireMark` tinyint(2) DEFAULT NULL COMMENT '源数据项过期标志位',
  `isStaticMark` tinyint(2) DEFAULT NULL COMMENT '源数据项是否为统计项',
  `diniDataDelMark` tinyint(2) DEFAULT NULL COMMENT '源数据项删除标志位',
  `diniDataRemark` varchar(255) DEFAULT NULL COMMENT '环境测评源数据项定义说明',
  PRIMARY KEY (`diniDataId`),
  KEY `fk_def_inidata_def_kpi_1` (`dkpiId`),
  KEY `fk_def_inidata_def_formula_1` (`diniDataCalRuleId`),
  KEY `fk_def_inidata_def_authrule_1` (`diniDataAuthRuleId`),
  CONSTRAINT `fk_def_inidata_def_authrule_1` FOREIGN KEY (`diniDataAuthRuleId`) REFERENCES `def_authrule` (`authRuleId`),
  CONSTRAINT `fk_def_inidata_def_formula_1` FOREIGN KEY (`diniDataCalRuleId`) REFERENCES `def_formula` (`calRuleId`),
  CONSTRAINT `fk_def_inidata_def_kpi_1` FOREIGN KEY (`dkpiId`) REFERENCES `def_kpi` (`dkpiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='指标计算源数据项定义表';

-- ----------------------------
-- Records of def_inidata
-- ----------------------------

-- ----------------------------
-- Table structure for def_kpi
-- ----------------------------
DROP TABLE IF EXISTS `def_kpi`;
CREATE TABLE `def_kpi` (
  `dkpiId` varchar(45) NOT NULL DEFAULT '0' COMMENT '环境测评指标定义ID',
  `dkpiName` varchar(255) DEFAULT NULL COMMENT '环境测评指标名称',
  `dclassId` varchar(45) DEFAULT NULL COMMENT '环境测评项目定义ID',
  `dkpiCalRuleId` varchar(45) DEFAULT NULL COMMENT '指标计算规则ID',
  `dkpiAuthRuleId` varchar(45) DEFAULT NULL COMMENT '指标定义权限规则ID',
  `dkpiDefDate` date DEFAULT NULL COMMENT '环境测评指标定义时间',
  `dkpiModDate` date DEFAULT NULL COMMENT '环境测评指标定义修改时间',
  `dkpiExpireMark` tinyint(2) DEFAULT NULL COMMENT '环境测评指标定义过期标志位',
  `dkpiWeight` float(20,0) DEFAULT NULL COMMENT '环境测评指标权重',
  `dkpiscore` int(4) DEFAULT NULL COMMENT '环境测评指标分值',
  `dkpiDelMark` tinyint(2) DEFAULT NULL COMMENT '环境测评指标指标删除标志位',
  `dkpiRemark` varchar(255) DEFAULT NULL COMMENT '环境测评指标说明',
  PRIMARY KEY (`dkpiId`),
  KEY `fk_def_kpi_def_formula_1` (`dkpiCalRuleId`),
  KEY `fk_def_kpi_def_authrule_1` (`dkpiAuthRuleId`),
  CONSTRAINT `fk_def_kpi_def_authrule_1` FOREIGN KEY (`dkpiAuthRuleId`) REFERENCES `def_authrule` (`authRuleId`),
  CONSTRAINT `fk_def_kpi_def_formula_1` FOREIGN KEY (`dkpiCalRuleId`) REFERENCES `def_formula` (`calRuleId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='指标定定义表';

-- ----------------------------
-- Records of def_kpi
-- ----------------------------

-- ----------------------------
-- Table structure for def_org
-- ----------------------------
DROP TABLE IF EXISTS `def_org`;
CREATE TABLE `def_org` (
  `orgId` varchar(45) NOT NULL DEFAULT '0' COMMENT '报送机构ID',
  `orgName` varchar(255) DEFAULT NULL COMMENT '报送机构名称',
  `orgType` varchar(255) DEFAULT NULL COMMENT '报送机构类别',
  `orgRegion` varchar(20) DEFAULT NULL COMMENT '报送机构所属行政辖区',
  PRIMARY KEY (`orgId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='机构定义表';

-- ----------------------------
-- Records of def_org
-- ----------------------------

-- ----------------------------
-- Table structure for value_cat
-- ----------------------------
DROP TABLE IF EXISTS `value_cat`;
CREATE TABLE `value_cat` (
  `vcatId` varchar(45) NOT NULL COMMENT '环境测评类别值ID',
  `vcatValue` float(20,0) DEFAULT NULL COMMENT '环境测评类别值',
  `vcatCalRuleId` varchar(45) DEFAULT NULL COMMENT '环境测评类别值计算规则ID',
  `vcatRegion` varchar(20) DEFAULT NULL COMMENT '所属行政辖区ID',
  `vcatCalDate` date DEFAULT NULL COMMENT '环境测评类别值计算时间',
  `dcatId` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`vcatId`),
  KEY `fk_value_cat_def_cat_1` (`dcatId`),
  KEY `fk_value_cat_def_distr_1` (`vcatRegion`),
  CONSTRAINT `fk_value_cat_def_distr_1` FOREIGN KEY (`vcatRegion`) REFERENCES `def_distr` (`regionId`),
  CONSTRAINT `fk_value_cat_def_cat_1` FOREIGN KEY (`dcatId`) REFERENCES `def_cat` (`dcatId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='类别值表';

-- ----------------------------
-- Records of value_cat
-- ----------------------------

-- ----------------------------
-- Table structure for value_class
-- ----------------------------
DROP TABLE IF EXISTS `value_class`;
CREATE TABLE `value_class` (
  `vclassId` varchar(45) NOT NULL DEFAULT '0' COMMENT '环境测评项目值ID',
  `dclassId` varchar(45) DEFAULT NULL COMMENT '环境测评项目定义ID',
  `vcatId` varchar(45) DEFAULT NULL COMMENT '环境测评类别值ID',
  `vclassCalDate` date DEFAULT NULL COMMENT '环境测评项目计算时间',
  `vclassCalRuleId` varchar(45) DEFAULT NULL COMMENT '环境测评项目计算规则ID',
  `vclassRegion` varchar(40) DEFAULT NULL COMMENT '环境测评项目所属辖区',
  `vclassAuthRuleId` varchar(45) DEFAULT NULL COMMENT '环境测评项目权限规则ID',
  `vclassValue` float(20,0) DEFAULT NULL COMMENT '环境权限项目值',
  PRIMARY KEY (`vclassId`),
  KEY `fk_value_class_value_cat_1` (`vcatId`),
  KEY `fk_value_class_def_distr_1` (`vclassRegion`),
  KEY `dclassId` (`dclassId`),
  CONSTRAINT `value_class_ibfk_1` FOREIGN KEY (`dclassId`) REFERENCES `def_class` (`dclassId`),
  CONSTRAINT `fk_value_class_def_distr_1` FOREIGN KEY (`vclassRegion`) REFERENCES `def_distr` (`regionId`),
  CONSTRAINT `fk_value_class_value_cat_1` FOREIGN KEY (`vcatId`) REFERENCES `value_cat` (`vcatId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='KPI值表';

-- ----------------------------
-- Records of value_class
-- ----------------------------

-- ----------------------------
-- Table structure for value_inidata
-- ----------------------------
DROP TABLE IF EXISTS `value_inidata`;
CREATE TABLE `value_inidata` (
  `viniDataId` varchar(45) NOT NULL DEFAULT '0' COMMENT '源数据项值ID',
  `diniDataId` varchar(45) DEFAULT NULL COMMENT '源数据项定义ID',
  `vkpiId` varchar(45) DEFAULT NULL COMMENT '环境测评指标值ID',
  `viniDataValue` float(20,2) DEFAULT NULL COMMENT '源数据项数据值',
  `viniDataCalRuleId` varchar(45) DEFAULT NULL COMMENT '统计源数据项计算规则Id',
  `viniDataSubDate` date DEFAULT NULL COMMENT '源数据项申报时间',
  `viniDataAuthRuleId` varchar(45) DEFAULT NULL COMMENT '源数据项权限规则ID',
  `viniDataRegion` varchar(20) DEFAULT NULL COMMENT '源数据项所属行政辖区',
  `viniDataSubmitOrgRole` varchar(45) DEFAULT NULL COMMENT '源数据项报送机构角色',
  `isStastic` tinyint(2) DEFAULT NULL COMMENT '是否为统计值',
  `viniDataCheckMark` tinyint(2) DEFAULT NULL COMMENT '源数据项审核标志：',
  `viniDate` date DEFAULT NULL,
  PRIMARY KEY (`viniDataId`),
  KEY `fk_value_inidata_def_inidata_1` (`diniDataId`),
  KEY `fk_value_inidata_value_kpi_1` (`vkpiId`),
  KEY `fk_value_inidata_def_formula_1` (`viniDataCalRuleId`),
  KEY `fk_value_inidata_def_authrule_1` (`viniDataAuthRuleId`),
  KEY `fk_value_inidata_def_distr_1` (`viniDataRegion`),
  KEY `viniDataSubmitOrg` (`viniDataSubmitOrgRole`),
  CONSTRAINT `fk_value_inidata_def_authrule_1` FOREIGN KEY (`viniDataAuthRuleId`) REFERENCES `def_authrule` (`authRuleId`),
  CONSTRAINT `fk_value_inidata_def_distr_1` FOREIGN KEY (`viniDataRegion`) REFERENCES `def_distr` (`regionId`),
  CONSTRAINT `fk_value_inidata_def_formula_1` FOREIGN KEY (`viniDataCalRuleId`) REFERENCES `def_formula` (`calRuleId`),
  CONSTRAINT `fk_value_inidata_def_inidata_1` FOREIGN KEY (`diniDataId`) REFERENCES `def_inidata` (`diniDataId`),
  CONSTRAINT `fk_value_inidata_value_kpi_1` FOREIGN KEY (`vkpiId`) REFERENCES `value_kpi` (`vkpiId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='源数据项定义表';

-- ----------------------------
-- Records of value_inidata
-- ----------------------------

-- ----------------------------
-- Table structure for value_inidatastatics
-- ----------------------------
DROP TABLE IF EXISTS `value_inidatastatics`;
CREATE TABLE `value_inidatastatics` (
  `viniDataId` varchar(45) DEFAULT NULL COMMENT '所需统计源数据值ID',
  `siniDataId` varchar(45) NOT NULL COMMENT '源数据项统计项ID',
  `submitOrg` varchar(255) DEFAULT NULL COMMENT '源数据项统计项报送机构',
  `siniDataValue` float(20,2) DEFAULT NULL COMMENT '源数据项统计项值',
  `siniDataRegion` varchar(20) DEFAULT NULL COMMENT '统计项报送机构所属辖区',
  `siniDataCheckMark` tinyint(2) DEFAULT NULL COMMENT '源数据项统计项审核标志',
  `siniDate` date DEFAULT NULL,
  PRIMARY KEY (`siniDataId`),
  KEY `fk_value_inidatastatics_value_inidata_1` (`viniDataId`),
  KEY `fk_value_inidatastatics_def_distr_1` (`siniDataRegion`),
  KEY `submitOrg` (`submitOrg`),
  CONSTRAINT `fk_value_inidatastatics_def_distr_1` FOREIGN KEY (`siniDataRegion`) REFERENCES `def_distr` (`regionId`),
  CONSTRAINT `fk_value_inidatastatics_value_inidata_1` FOREIGN KEY (`viniDataId`) REFERENCES `value_inidata` (`viniDataId`),
  CONSTRAINT `value_inidatastatics_ibfk_1` FOREIGN KEY (`submitOrg`) REFERENCES `def_org` (`orgId`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='源数据项统计项细节表';

-- ----------------------------
-- Records of value_inidatastatics
-- ----------------------------

-- ----------------------------
-- Table structure for value_kpi
-- ----------------------------
DROP TABLE IF EXISTS `value_kpi`;
CREATE TABLE `value_kpi` (
  `vkpiId` varchar(45) NOT NULL DEFAULT '0' COMMENT '环境测评指标值ID',
  `dkpiId` varchar(45) DEFAULT NULL COMMENT '环境测评项目指标定义ID',
  `vclassId` varchar(45) DEFAULT NULL COMMENT '环境测评项目值ID',
  `vkpiCalRuleId` varchar(45) DEFAULT NULL COMMENT '环境测评指标计算规则ID',
  `vkpiAuthRuleId` varchar(45) DEFAULT NULL COMMENT '环境测评指标权限ID',
  `vkpiCalDate` date DEFAULT NULL COMMENT '环境测评指标值计算时间',
  `vkpiRegion` varchar(20) DEFAULT NULL COMMENT '环境测评指标所属辖区',
  `vkpiValue` float(20,2) DEFAULT NULL COMMENT '环境测评指标值',
  `vkpiCheckMark` tinyint(2) DEFAULT NULL COMMENT '环境测评指标审核标志：0 未通过；1 通过； ',
  PRIMARY KEY (`vkpiId`),
  KEY `fk_value_kpi_def_kpi_1` (`dkpiId`),
  KEY `fk_value_kpi_value_class_1` (`vclassId`),
  KEY `fk_value_kpi_def_distr_1` (`vkpiRegion`),
  CONSTRAINT `fk_value_kpi_def_distr_1` FOREIGN KEY (`vkpiRegion`) REFERENCES `def_distr` (`regionId`),
  CONSTRAINT `fk_value_kpi_def_kpi_1` FOREIGN KEY (`dkpiId`) REFERENCES `def_kpi` (`dkpiId`),
  CONSTRAINT `fk_value_kpi_value_class_1` FOREIGN KEY (`vclassId`) REFERENCES `value_class` (`vclassId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='KPI值表';

-- ----------------------------
-- Records of value_kpi
-- ----------------------------
