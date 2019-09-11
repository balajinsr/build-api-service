/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;

DROP TABLE IF EXISTS `silo_names`;
CREATE TABLE `silo_names` (
  `silo_id` bigint(3) NOT NULL AUTO_INCREMENT,
  `silo_name` varchar(50) NOT NULL,
  UNIQUE KEY(silo_name),
  PRIMARY KEY (`silo_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `cycle_names`;
CREATE TABLE `cycle_names` (
  `cycle_id` bigint(3) NOT NULL AUTO_INCREMENT,
  `cycle_name` varchar(50) NOT NULL,
  UNIQUE KEY(cycle_name),
  PRIMARY KEY (`cycle_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `system_config`;
CREATE TABLE `system_config` (
  `system_id` bigint(3) NOT NULL AUTO_INCREMENT,
  `config_name` varchar(50) NOT NULL,
  `param_name` varchar(50) NOT NULL,
  `param_value` text NOT NULL,
  UNIQUE KEY(config_name,param_name),
  PRIMARY KEY (`system_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `system_templates`;
CREATE TABLE `system_templates` (
  `template_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `subject` varchar(250) NOT NULL,
  `body` text NOT NULL,
  `enable` char(1) DEFAULT 'Y',
  PRIMARY KEY (`template_id`),
  UNIQUE KEY `name_uniq` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `pull_request_data`;
CREATE TABLE `pull_request_data` (
  `pull_req_number` bigint(20) NOT NULL,
  `silo_id` bigint(3) NOT NULL,
  `task_id` varchar(10) NOT NULL,
  `create_date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `action` varchar(30) NOT NULL,
  `payload` text NOT NULL,
  PRIMARY KEY (`pull_req_number`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `build_audit`;
CREATE TABLE `build_audit` (
  `build_req_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `build_number` bigint(30) NOT NULL,
  `pull_req_number` bigint(30) NOT NULL,
  `create_date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `last_modified_date_time` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `silo_id` bigint(3) NOT NULL,
  `task_id` varchar(50) NOT NULL COMMENT 'dtnumber or case number',
  `status_code` bigint(40) NOT NULL,
  `parent_task_id` varchar(20) DEFAULT NULL,
  `build_additional_data` text NULL, 
   PRIMARY KEY (`build_req_id`),
   UNIQUE KEY `buildaudit` (`silo_id`,`build_number`,`task_id`)
  ) ENGINE=InnoDB DEFAULT CHARSET=latin1;
--comment:  build_additional_details - json format. columns - `committer_name` , `committer_email` , `reason`, build-duration , mergecommit_url
 
DROP TABLE IF EXISTS `module_names`;
CREATE TABLE `module_names` (
  `module_id` bigint(3) NOT NULL AUTO_INCREMENT,
  `module_name` varchar(50) NOT NULL,
  `is_deleted` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`module_id`),
  UNIQUE KEY `module_name` (`module_name`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `binary_audit`;
CREATE TABLE `binary_audit` (
  `binary_audit_id` bigint(30) NOT NULL AUTO_INCREMENT,
  `build_number` bigint(30) NOT NULL,
  `task_id` varchar(50) NOT NULL COMMENT 'dtnumber or case number',
  `silo_id` bigint(3) NOT NULL,
  `module_id` bigint(3) NOT NULL,
  `module_data` longtext NULL,
  `common_data` longtext NULL,
  PRIMARY KEY (`binary_audit_id`),
  FOREIGN KEY fk_siloId(silo_id) REFERENCES silo_names(silo_id),
  FOREIGN KEY fk_module_Id(module_id) REFERENCES module_names(module_id)
) ENGINE=InnoDB AUTO_INCREMENT=1  DEFAULT CHARSET=latin1;
-- module_data is json - {"addedDependencies": [{},{}], "deletedDependencies":"", "addedModuleJar":[], "deletedModuleJar":[]}
-- common_data is json- {"commonAddedDependencies":[{},{}], "commonDeletedDependencies":[{},{}], "commonAddedModuleJar":[], "commonDeletedModuleJars":[{},{}] }

DROP TABLE IF EXISTS `data_audit`;
CREATE TABLE `data_audit` (
  `data_audit_id` bigint(30) NOT NULL AUTO_INCREMENT,
  `build_number` bigint(30) NOT NULL,
  `silo_id` bigint(3) NOT NULL,
  `file_path` varchar(500) NOT NULL,
  `md5_value` varchar(50) DEFAULT NULL,
  `action` char(1) NOT NULL,
  PRIMARY KEY (`data_audit_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS `user_activities`;
CREATE TABLE `user_activities` (
  `user_act_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `user_name` varchar(50) NOT NULL,
  `silo_id` bigint(3) NOT NULL,
  `cycle_id` bigint(3) NOT NULL,
  `user_action_details` text NOT NULL,
  PRIMARY KEY (`user_act_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;
