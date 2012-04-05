-- DROP TABLE IF EXISTS `preference`;

CREATE TABLE `preference` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор предпочтения',
  `session_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор сессии',
  `key` VARCHAR(255) NOT NULL COMMENT 'Ключ',
  `value` VARCHAR(255) NOT NULL COMMENT 'Значение',
  PRIMARY KEY (`id`),
  KEY `key_session_id` (`session_id`),
  UNIQUE KEY `unique_key` (`session_id`, `key`),
  CONSTRAINT `fk_preference__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Предпочтения пользователя';

INSERT INTO `update` (`date`, `date_version`) VALUES ('2012-04-05', 0);