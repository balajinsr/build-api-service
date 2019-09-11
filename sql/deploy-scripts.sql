DROP TABLE IF EXISTS `release_task_audit`;
DROP TABLE IF EXISTS `deployment_audit`;
DROP TABLE IF EXISTS `release_audit`;


-- artifacts release tables - start
CREATE TABLE `release_audit` (
  `release_id` varchar(30) NOT NULL,
  `silo_id` bigint(3) NOT NULL,
  `cycle_id` bigint(3) NOT NULL,
  `create_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deployment_type` int(11) NOT NULL DEFAULT '1' COMMENT '1-Regular Deployment, 2-Rollback Deployment',
  `scr_number` varchar(20) DEFAULT NULL,
  `status_code` int(11) NOT NULL,
  `reason` text,
  `is_dockerized` int(11) NOT NULL DEFAULT '0' COMMENT '0 - Not Dockerized, 1- Dockerized',
  PRIMARY KEY (`release_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;


CREATE TABLE `release_task_audit` ( 
  `release_id` varchar(30) NOT NULL,
  `task_id` varchar(50) NOT NULL,
  `build_number` bigint(30) NOT NULL,
  `status_code` int(11) DEFAULT '1' COMMENT '0 - Deployed, 1 - Not Deployed and 2 - Rollback Completed',
  FOREIGN KEY fk_release_id(release_id) REFERENCES release_audit(release_id),
  UNIQUE KEY `uniq_contraint` (`release_id`,`task_id`,`build_number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
-- artifacts release tables - end

-- deployment tables - start.
CREATE TABLE `deployment_audit` (
  `trigg_id` bigint(30) NOT NULL AUTO_INCREMENT,
  `release_id` varchar(30) NOT NULL,
  `trigger_date_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `deployed_date_time` timestamp NULL,
  `trigg_ref_number` varchar(50) DEFAULT NULL COMMENT 'reference number of the deployment ',
  `status_code` int(11) NOT NULL DEFAULT '1',  --  COMMENT '0 - Deployment Success, 1- Not Deployed, 2-RollBack Success
  `trigger_url` varchar(120) NOT NULL,
  `deployment_payload` longtext NOT NULL,
   PRIMARY KEY (`trigg_id`),
   FOREIGN KEY fk_rel_id(release_id) REFERENCES release_audit(release_id), 
   UNIQUE KEY `unique_ref_num` (`trigg_ref_number`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
-- deployment tables - end.


-- configuration tables
DROP TABLE IF EXISTS `environment_mapping`;
CREATE TABLE `environment_mapping` (
  `mapping_id` bigint(30) NOT NULL AUTO_INCREMENT,
  `silo_id` bigint(3) NOT NULL,
  `cycle_id` bigint(3) NOT NULL,
  `environment_name` varchar(100) NOT NULL,
  `sf_platform_name` varchar(100) NOT NULL,
  `product_details` text,
  PRIMARY KEY (`mapping_id`),
  UNIQUE KEY `env_mapping` (`silo_id`,`cycle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
