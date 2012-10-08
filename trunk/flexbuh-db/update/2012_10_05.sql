ALTER TABLE `schedule` add column `session_id` BIGINT(20);

ALTER TABLE `personnel` drop foreign key `fk_personnel__schedule`;
ALTER TABLE `position` drop foreign key `fk_position__schedule`;

ALTER TABLE `schedule` drop primary key;

ALTER TABLE `schedule` add primary key (`id`, `version`);

ALTER TABLE `personnel` add CONSTRAINT `fk_personnel__schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`);
ALTER TABLE `position` add CONSTRAINT `fk_position__schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`);

INSERT INTO `update` (`date`) VALUES ('2012-10-05');