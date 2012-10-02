ALTER TABLE `schedule`
  drop column `type`,
  drop `day_of_year`,
  drop `day_of_week`,
  drop `work_hours`,
  drop `lunch_hours`;

ALTER TABLE `schedule`
  add column `period_number_date` FLOAT,
  add column `item_day_off` VARCHAR(255),
  add column `reg_work_time_unit` VARCHAR (8),
  add column `period_schedule` VARCHAR (1000),
  add column `year_schedule` TEXT,
  add column `pattern` BOOLEAN,
  add column `total_work_time` BOOLEAN,
  add column `organization_id` BIGINT(20);

ALTER TABLE `schedule`
  add constraint `fk_schedule__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`);

INSERT INTO `schedule` (`id`, `version`, `entry_into_force_date`, `name`,
`period_number_date`, `item_day_off`, `reg_work_time_unit`, `period_schedule`) value
(1, 1, now(), '5 дневная 40 часовая рабочая неделя с 9 до 18',
 7, '6,7', 'DAY', '9-13,14-18;9-13,14-18;9-13,14-18;9-13,14-18;9-13,14-18');

INSERT INTO `update` (`date`) VALUES ('2012-10-02');