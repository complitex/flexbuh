DROP TABLE IF EXISTS `personnel`;

DROP TABLE IF EXISTS `position`;

DROP TABLE IF EXISTS `schedule`;

DROP TABLE IF EXISTS `payment`;

CREATE TABLE `schedule` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,
  `name` VARCHAR(255),
  `type` VARCHAR(255),
  `day_of_year` VARCHAR(1000),
  `day_of_week` VARCHAR(255),
  `work_hours` VARCHAR(255),
  `lunch_hours` VARCHAR(255),
  `comment` VARCHAR(1000),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `payment` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,
  `salary` FLOAT,
  `currency_unit` VARCHAR (45),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `position` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,
  `name` VARCHAR(255),
  `code` VARCHAR(255),
  `payment_id` BIGINT(20),
  `description` VARCHAR(1000),
  `schedule_id` BIGINT(20),
  `organization_id` BIGINT(20) NOT NULL,
  `department_id` BIGINT(20),
  PRIMARY KEY (`id`, `version`),
  UNIQUE KEY `key_unique` (`id`, `version`, `department_id`),
  CONSTRAINT `fk_position__payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`),
  CONSTRAINT `fk_position__schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`),
  CONSTRAINT `fk_position__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`),
  CONSTRAINT `fk_position__department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

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
  `registration_address_id` BIGINT(20),
  `actual_address_id` BIGINT(20),
  `position_id` BIGINT(20) NOT NULL,
  `schedule_id` BIGINT(20),
  `payment_id` BIGINT(20),
  `comment` VARCHAR(1000),
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_personnel__registration_address` FOREIGN KEY (`registration_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `fk_personnel__actual_address` FOREIGN KEY (`actual_address_id`) REFERENCES `address` (`id`),
  CONSTRAINT `fk_personnel__position` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`),
  CONSTRAINT `fk_personnel__schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`),
  CONSTRAINT `fk_personnel__payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
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

INSERT INTO `update` (`date`) VALUES ('2012-07-27');