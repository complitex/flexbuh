DROP TABLE IF EXISTS `personnel`;

DROP TABLE IF EXISTS `position`;

CREATE TABLE `position` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,
  `name` VARCHAR(255) NOT NULL,
  `code` VARCHAR(255),
  `payment_id` BIGINT,
  `description` VARCHAR(1000),
  `schedule_id` BIGINT(20),
  `organization_id` BIGINT(20),
  `department_id` BIGINT(20),
  `master_position_id` BIGINT(20),
  PRIMARY KEY (`id`, `version`),
  CONSTRAINT `fk_position__schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`),
  CONSTRAINT `fk_position__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  CONSTRAINT `fk_position__department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `fk_position__master_position` FOREIGN KEY (`master_position_id`) REFERENCES `position` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `personnel` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT(20) NOT NULL,
  `object_id` BIGINT(20),
  `last_name` VARCHAR(64) NOT NULL,
  `first_name` VARCHAR(64) NOT NULL,
  `middle_name` VARCHAR(64) NOT NULL,
  `number` VARCHAR(64),
  `birthday` DATE,
  `gender` BOOLEAN,
  `inn` VARCHAR(64),
  `resident` BOOLEAN,
  `snils` VARCHAR(64),
  `address` VARCHAR(255),
  `passport` VARCHAR(255),
  `department_id` BIGINT,
  `position_id` BIGINT,
  `schedule_id` BIGINT,
  `payment_id` BIGINT,
  `current` BOOLEAN NOT NULL,
  `date_start` DATE NOT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT NOW(),
  `comment` VARCHAR(1000),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_personnel__department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `fk_personnel__position` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`),
  CONSTRAINT `fk_personnel__schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`),
  CONSTRAINT `fk_personnel__payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `update` (`date`, `date_version`) VALUES ('2012-06-28', '1');