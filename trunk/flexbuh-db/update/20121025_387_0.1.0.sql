alter table `update` add column `number_revision` VARCHAR(64) NOT NULL COMMENT 'Номер ревизии';

INSERT INTO `update` (`date`, `date_version`, `number_revision`) VALUE ('2012-10-19', '0.1.0', '387');