/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- --------------------------
-- Declaration
-- --------------------------

DROP TABLE IF EXISTS `declaration`;

CREATE TABLE `declaration` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT(20),
  `session_id` BIGINT(20) NOT NULL,
  `person_profile_id` BIGINT(20),
  `tin` INTEGER,
  `c_doc` VARCHAR(3) NOT NULL,
  `c_doc_sub` VARCHAR(3) NOT NULL,
  `c_doc_ver` INTEGER NOT NULL,
  `c_doc_type` INTEGER,
  `c_doc_cnt` INTEGER,
  `c_reg` INTEGER,
  `c_raj` INTEGER,
  `period_type` INTEGER NOT NULL,
  `period_month` INTEGER NOT NULL,
  `period_year` INTEGER NOT NULL,
  `c_sti_orig` INTEGER,
  `c_doc_stan` INTEGER,
  `d_fill` VARCHAR(8),
  `software` VARCHAR(32),
  `num`  VARCHAR(10),
  `type`  VARCHAR(32),
  `filename`  VARCHAR(32),
  `date` DATETIME,
  `validated` BOOLEAN,
  PRIMARY KEY (`id`),
  -- UNIQUE KEY `key_unique` (`person_profile_id`, `c_doc`, `c_doc_sub`, `period_type`, `period_month`, `period_year`),
  KEY `key_declaration__parent_id` (`parent_id`),
  KEY `key_declaration__session_id` (`session_id`),
  KEY `key_declaration__person_profile_id` (`person_profile_id`),
  CONSTRAINT `fk_declaration__declaration` FOREIGN KEY (`parent_id`) REFERENCES `declaration` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_declaration__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
  CONSTRAINT `fk_declaration__person_profile` FOREIGN KEY (`person_profile_id`) REFERENCES `person_profile` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Declaration Value
-- --------------------------

DROP TABLE IF EXISTS `declaration_value`;

CREATE TABLE `declaration_value` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `declaration_id` BIGINT(20) NOT NULL,
  `row_num` INTEGER,
  `name` VARCHAR(20) NOT NULL,
  `value` VARCHAR(255),
  PRIMARY KEY (`id`),
  KEY `key_declaration_value__declaration_id` (`declaration_id`),
  CONSTRAINT `fk_declaration_value__declaration` FOREIGN KEY (`declaration_id`) REFERENCES `declaration` (`id`) ON DELETE CASCADE
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Counterpart
-- --------------------------

DROP TABLE IF EXISTS `counterpart`;

CREATE TABLE `counterpart` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT(20) NOT NULL,
  `person_profile_id` BIGINT(20) NOT NULL,
  `hk` VARCHAR(64),
  `hname` VARCHAR(255) NOT NULL,
  `hloc` VARCHAR(255),
  `htel` VARCHAR(32),
  `hnspdv` VARCHAR(64),
  PRIMARY KEY (`id`),
  KEY `key_counterpart__session_id` (`session_id`),
  KEY `key_counterpart__person_profile_id` (`person_profile_id`),
  CONSTRAINT `fk_counterpart__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
  CONSTRAINT `fk_counterpart__person_profile` FOREIGN KEY (`person_profile_id`) REFERENCES `person_profile` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Employee
-- --------------------------

DROP TABLE IF EXISTS `employee`;

CREATE TABLE `employee` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT(20) NOT NULL,
  `person_profile_id` BIGINT(20) NOT NULL,
  `htin` INTEGER,
  `last_name` VARCHAR(45),
  `first_name` VARCHAR(45),
  `middle_name` VARCHAR(45),
  `hbirthday` DATE,
  `hdate_in` DATE,
  `hdate_out` DATE,
  PRIMARY KEY (`id`),
  KEY `key_employee__session_id` (`session_id`),
  KEY `key_employee__person_profile_id` (`person_profile_id`),
  CONSTRAINT `fk_employee__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
  CONSTRAINT `fk_employee__person_profile` FOREIGN KEY (`person_profile_id`) REFERENCES `person_profile` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

