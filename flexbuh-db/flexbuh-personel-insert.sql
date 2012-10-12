DROP TABLE IF EXISTS `schedule`;

CREATE TABLE `schedule` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,
  `name` VARCHAR(255),
  `period_number_date` FLOAT,
  `item_day_off` VARCHAR(255),
  `reg_work_time_unit` VARCHAR (8),
  `period_schedule` VARCHAR (1000),
  `year_schedule` TEXT,
  `pattern` BOOLEAN,
  `total_work_time` BOOLEAN,
  `organization_id` BIGINT(20),
  `comment` VARCHAR(1000),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_schedule__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

insert into `schedule` (`id`, `version`, `entry_into_force_date`, `name`,
`period_number_date`, `item_day_off`, `reg_work_time_unit`, `period_schedule`) value
(1, 1, now(), '5 дневная 40 часовая рабочая неделя с 9 до 18', 7, '6,7', 'DAY',
'9-13,14-18;9-13,14-18;9-13,14-18;9-13,14-18;9-13,14-18');