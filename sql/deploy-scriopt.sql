DROP TABLE IF EXISTS `release_audit`;

CREATE TABLE `release_audit` (
  `release_id` varchar(30) NOT NULL,
  `silo_id` bigint(3) NOT NULL,
  `cycle_id` bigint(3) NOT NULL,
  `create_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `manifest` longtext NOT NULL,
  `artifact_package_xml` longtext NOT NULL,
  `deployment_type` int(11) NOT NULL DEFAULT '1' COMMENT '1-Regular Deployment, 2-Rollback Deployment',
  `scr_number` varchar(20) DEFAULT NULL,
  `status_code` int(11) NOT NULL,
  `reason` text,
  `is_dockerized` int(11) NOT NULL DEFAULT '0' COMMENT '0 - Not Dockerized, 1- Dockerized',
  UNIQUE KEY `deploymentaudit` (`release_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


DROP TABLE IF EXISTS `release_task_audit`;
CREATE TABLE `release_task_audit` (
  `release_id` varchar(30) NOT NULL,
  `task_id` varchar(50) NOT NULL,
  `build_number` bigint(30) NOT NULL,
  `status` int(11) DEFAULT '1' COMMENT '0 - Deployed, 1 - Not Deployed and 2 - Rollback Completed',
  UNIQUE KEY `release_id` (`release_id`,`task_id`,`build_number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `deployment_audit` (
  `release_id` varchar(30) NOT NULL,
  `trigg_id` bigint(3) NOT NULL,
  `deployed_date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` int(11) NOT NULL DEFAULT '1',  --  COMMENT '0 - Deployment Success, 1- Not Deployed, 2-RollBack Success'
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `deployment_trigger_audit` (
  `trigg_id` bigint(30) NOT NULL AUTO_INCREMENT,
  `trigger_date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `release_id` varchar(30) NOT NULL,
  `ra_release_id` varchar(50) DEFAULT NULL,
  `uuid` varchar(50) DEFAULT NULL
  `deployment_payload` longtext NOT NULL,
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `environment_mapping` (
  `silo_id` bigint(3) NOT NULL,
  `cycle_id` bigint(3) NOT NULL,
  `environment_name` varchar(100) NOT NULL,
  `tomcat_base_path` varchar(500) NOT NULL,
  `apache_base_path` varchar(400) NOT NULL,
  `sf_platform_name` varchar(100) NOT NULL,
  `product_details` text,
  UNIQUE KEY `env_mapping` (`silo_id`,`cycle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

