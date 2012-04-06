drop database if exists flexbuh;
create database flexbuh DEFAULT CHARACTER SET 'utf8' COLLATE 'utf8_unicode_ci';
use flexbuh;
grant all privileges on flexbuh.* to flexbuh identified by 'flexbuh';
flush privileges;

-- ------------------------------
-- Update
-- ------------------------------

DROP TABLE IF EXISTS `update`;

CREATE TABLE `update` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор обновления',
    `date_version` VARCHAR(64) NOT NULL COMMENT 'Версия',
    `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата обновления',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Обновление базы данных';

INSERT INTO `update` (`date`, `date_version`) VALUES ('2012-04-05', 0);