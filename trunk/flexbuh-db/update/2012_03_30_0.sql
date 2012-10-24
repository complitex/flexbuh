DROP TABLE IF EXISTS `update`;

CREATE TABLE `update` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор обновления',
    `date_version` VARCHAR(64) NOT NULL COMMENT 'Версия',
    `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата обновления',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Обновление базы данных';

INSERT INTO `update` (`date`, `date_version`) VALUES ('2012-03-30', 0);