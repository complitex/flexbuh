DROP TABLE IF EXISTS `share`;

CREATE TABLE `share` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `session_id` BIGINT(20) COMMENT 'Идентификатор сессии',
  `shared_session_id` BIGINT(20) COMMENT 'Общий идентификатор сессии',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_share` (`session_id`, `shared_session_id`),
  KEY `key_session_id` (`session_id`),
  KEY `key_shared_session_id` (`shared_session_id`),
  CONSTRAINT `fk_share__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
  CONSTRAINT `fk_share_shared__session` FOREIGN KEY (`shared_session_id`) REFERENCES `session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Общие данные';

INSERT INTO `update` (`date`, `date_version`) VALUES ('2012-04-04', 0);