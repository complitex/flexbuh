/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- --------------------------
-- Department
-- --------------------------

DROP TABLE IF EXISTS `department`;

CREATE TABLE `department` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,
  `name` VARCHAR(255) NOT NULL,
  `code` VARCHAR(255),
  `organization_id` BIGINT(20) NOT NULL,
  `master_id` BIGINT(20),
  PRIMARY KEY (`id`, `version`),
  KEY `key_department__master_id` (`master_id`),
  CONSTRAINT `fk_department__master_department` FOREIGN KEY (`master_id`) REFERENCES `department` (`id`),
  CONSTRAINT `fk_department__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- RateType
-- --------------------------

DROP TABLE IF EXISTS `rate_type`;

CREATE TABLE `rate_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT(20) NOT NULL,
  `object_id` BIGINT(20),
  `name` VARCHAR(255),
  `current` BOOLEAN NOT NULL,
  `date_start` DATE NOT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT NOW(),
  `comment` VARCHAR(1000),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Schedule
-- --------------------------

DROP TABLE IF EXISTS `schedule`;

CREATE TABLE `schedule` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,
  `name` VARCHAR(255),
  `period_number_date` INT(4),
  `item_day_off` VARCHAR(255),
  `reg_work_time_unit` VARCHAR (8),
  `period_schedule` VARCHAR (1000),
  `year_schedule` TEXT,
  `pattern` BOOLEAN,
  `total_work_time` BOOLEAN,
  `organization_id` BIGINT(20),
  `comment` VARCHAR(1000),
  `session_id` BIGINT(20),
  PRIMARY KEY (`id`, `version`),
  CONSTRAINT `fk_schedule__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Position
-- --------------------------

DROP TABLE IF EXISTS `position`;

CREATE TABLE `position` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,
  `name` VARCHAR(255),
  `code` VARCHAR(255),
  `payment_salary` FLOAT,
  `payment_number` INT(7),
  `payment_currency_unit` VARCHAR (45),
  `payment_type` VARCHAR (45),
  `description` VARCHAR(1000),
  `schedule_id` BIGINT(20),
  `organization_id` BIGINT(20) NOT NULL,
  `department_id` BIGINT(20),
  -- `master_position_id` BIGINT(20),
  -- PRIMARY KEY (`id`, `version`),
  UNIQUE KEY `key_unique` (`id`, `version`, `department_id`),
  CONSTRAINT `fk_position__schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`),
  CONSTRAINT `fk_position__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  CONSTRAINT `fk_position__department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Personnel
-- --------------------------

DROP TABLE IF EXISTS `personnel`;

CREATE TABLE `personnel` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,
  `last_name` VARCHAR(64) NOT NULL,
  `first_name` VARCHAR(64) NOT NULL,
  `middle_name` VARCHAR(64) NOT NULL,
  `birthday` DATE,
  `gender` BOOLEAN,
  `marital_status` VARCHAR(64),
  `email` VARCHAR(255),
  `phone` VARCHAR(255),
  `medical_policy` VARCHAR(255),
  `inn` VARCHAR(64),
  `snils` VARCHAR(64),
  `passport` VARCHAR(255),
  `resident` BOOLEAN default TRUE,
  `employment_date` DATE NOT NULL,
  `termination_date` DATE,
  `payment_salary` FLOAT,
  `payment_currency_unit` VARCHAR (45),
  `registration_address_id` BIGINT(20),
  `actual_address_id` BIGINT(20),
  `position_id` BIGINT(20) NOT NULL,
  `schedule_id` BIGINT(20),
  `comment` VARCHAR(1000),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_personnel__registration_address` FOREIGN KEY (`registration_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `fk_personnel__actual_address` FOREIGN KEY (`actual_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `fk_personnel__position` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`),
  CONSTRAINT `fk_personnel__schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Allowance
-- --------------------------

DROP TABLE IF EXISTS `allowance`;

CREATE TABLE `allowance` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,
  `value` FLOAT,
  `calculation_unit` VARCHAR (45),
  `type` VARCHAR (45),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
