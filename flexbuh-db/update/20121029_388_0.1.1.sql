alter table `position` drop foreign key `fk_position__allowance`;
alter table `position` drop column `allowance_id`;
alter table `allowance` add column `position_id` BIGINT(20);
alter table `allowance` add CONSTRAINT `fk_allowance__position` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`);
alter table `allowance` drop key `unique_key_allowance`;
alter table `allowance` add UNIQUE KEY `unique_key_allowance` (`id`, `version`, `position_id`);

DROP TABLE IF EXISTS `position_allowance`;

CREATE TABLE `position_allowance` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,
  `position_id` BIGINT(20) NOT NULL,
  `allowance_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`, `version`),
  CONSTRAINT `fk_position_allowance__position` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`),
  CONSTRAINT `fk_position_allowance__allowance` FOREIGN KEY (`allowance_id`) REFERENCES `allowance` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `update` (`date`, `date_version`, `number_revision`) VALUE ('2012-10-29', '0.1.1', '388');