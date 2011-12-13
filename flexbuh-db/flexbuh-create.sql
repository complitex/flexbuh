/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- ------------------------------
-- PersonProfile
-- ------------------------------
DROP TABLE IF EXISTS `person_profile`;

CREATE TABLE  `person_profile` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор профайла',
  `session_id` BIGINT(20) NOT NULL,
  `person_type` INTEGER NOT NULL,
  `c_sti` INTEGER,
  `c_sti_tin` VARCHAR(45),
  `tin` VARCHAR(45),
  `name` VARCHAR(255) NOT NULL,
  `num_pdv_svd` VARCHAR(45),
  `ipn` VARCHAR(45),
  `kved` VARCHAR(45),
  `koatuu` VARCHAR(45),
  `contract_date` DATE,
  `contract_number` VARCHAR(45),
  `zip_code` VARCHAR(45),
  `address` VARCHAR(1000),
  `phone` VARCHAR(255),
  `fax` VARCHAR(45),
  `email` VARCHAR(255),
  `d_inn` VARCHAR(45),
  `d_fio` VARCHAR(255),
  `b_inn` VARCHAR(45),
  `b_fio` VARCHAR(255),
  `tax_inspection_id` BIGINT(20),
  PRIMARY KEY (`id`),
  KEY `key_session_id` (`session_id`),
  KEY `key_tax_inspection_id` (`tax_inspection_id`),
  CONSTRAINT `fk_person_profile__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
  CONSTRAINT `fk_person_profile__tax_inspection` FOREIGN KEY (`tax_inspection_id`) REFERENCES `tax_inspection` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Профайл';

-- ------------------------------
-- Session
-- ------------------------------
DROP TABLE IF EXISTS `session`;

CREATE TABLE  `session` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор сессии',
  `cookie` VARCHAR(255) NOT NULL COMMENT 'Куки сессии',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key_cookie` (`cookie`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Сессия';

-- ------------------------------
-- User
-- ------------------------------
DROP TABLE IF EXISTS `user`;

CREATE TABLE  `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор пользователя',
  `session_id` BIGINT(20) COMMENT 'Идентификатор сессии',
  `login` VARCHAR(45) NOT NULL COMMENT 'Имя пользователя',
  `password` VARCHAR(45) NOT NULL COMMENT 'MD5 хэш пароля',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_session` (`session_id`),
  UNIQUE KEY `unique_key_login` (`login`),
  CONSTRAINT `fk_user__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Пользователь';

-- ------------------------------
-- Usergroup
-- ------------------------------
DROP TABLE IF EXISTS `usergroup`;

CREATE TABLE  `usergroup` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор группы пользователей',
  `login` VARCHAR(45) NOT NULL COMMENT 'Имя пользователя',
  `group_name` VARCHAR(45) NOT NULL COMMENT 'Название группы',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_login__group_name` (`login`, `group_name`),
  CONSTRAINT `fk_usergroup__user` FOREIGN KEY (`login`) REFERENCES `user` (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Группа пользователей';

-- ------------------------------
-- Config
-- ------------------------------

DROP TABLE IF EXISTS `config`;

CREATE TABLE `config` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор настройки',
    `name` VARCHAR(64) NOT NULL COMMENT 'Имя',
    `value` VARCHAR(255) NOT NULL COMMENT 'Значение',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Настройки';

-- ------------------------------
-- Update
-- ------------------------------

DROP TABLE IF EXISTS `update`;

CREATE TABLE `update` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор обновления',
    `version` VARCHAR(64) NOT NULL COMMENT 'Версия',
    `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата обновления',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Обновление базы данных';


-- --------------------------
-- XSL
-- --------------------------

DROP TABLE IF EXISTS `template_xsl`;

CREATE TABLE `template_xsl` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `data` MEDIUMTEXT NOT NULL,
  `upload_date` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `key_name`(`name`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- FO
-- --------------------------

DROP TABLE IF EXISTS `template_fo`;

CREATE TABLE `template_fo` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `data` MEDIUMTEXT NOT NULL,
  `upload_date` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `key_name`(`name`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- XSD
-- --------------------------

DROP TABLE IF EXISTS `template_xsd`;

CREATE TABLE `template_xsd` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `data` MEDIUMTEXT NOT NULL,
  `upload_date` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `key_name`(`name`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Controls
-- --------------------------

DROP TABLE IF EXISTS `template_control`;

CREATE TABLE `template_control` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  `data` MEDIUMTEXT NOT NULL,
  `upload_date` TIMESTAMP NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `key_name`(`name`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DictionaryType
-- --------------------------

DROP TABLE IF EXISTS `dictionary_type`;

CREATE TABLE `dictionary_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(40) NOT NULL,
  `file_name` VARCHAR(255) NOT NULL,
  `name_uk` VARCHAR(255) NOT NULL,
  `name_ru` VARCHAR(255),
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_dictionary_type__code` (`code`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Currency
-- --------------------------

DROP TABLE IF EXISTS `currency`;

CREATE TABLE `currency` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` DATETIME,
  `end_date` DATETIME,
  `code_number` INTEGER NOT NULL,
  `code_string` VARCHAR(40) NOT NULL,
  `name_uk` VARCHAR(255),
  `name_ru` VARCHAR(255),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Document
-- --------------------------

DROP TABLE IF EXISTS `document`;

CREATE TABLE `document` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` DATETIME,
  `end_date` DATETIME,
  `c_doc` VARCHAR(40) NOT NULL,
  `c_doc_sub` VARCHAR(40) NOT NULL,
  `parent_c_doc` VARCHAR(40),
  `parent_c_doc_sub` VARCHAR(40),
  `cnt_set` TINYINT(1) NOT NULL,
  `selected` TINYINT(1),
  `name_uk` VARCHAR(4000) NOT NULL,
  `name_ru` VARCHAR(4000),
  PRIMARY KEY (`id`),
  UNIQUE (c_doc, c_doc_sub),
  KEY `key_document` (c_doc, c_doc_sub),
  KEY `key_document__parent_document` (parent_c_doc, parent_c_doc_sub)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DocumentTerm
-- --------------------------

DROP TABLE IF EXISTS `document_term`;

CREATE TABLE `document_term` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` DATETIME,
  `end_date` DATETIME,
  `c_doc` VARCHAR(40) NOT NULL,
  `c_doc_sub` VARCHAR(40) NOT NULL,
  `c_doc_ver` INTEGER NOT NULL,
  `date_term` TIMESTAMP NOT NULL,
  `period_month` INTEGER NOT NULL,
  `period_type` INTEGER NOT NULL,
  `period_year` INTEGER NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_document_term__document` (`c_doc`, `c_doc_sub`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DocumentVersion
-- --------------------------

DROP TABLE IF EXISTS `document_version`;

CREATE TABLE `document_version` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` DATETIME,
  `end_date` DATETIME,
  `c_doc` VARCHAR(40) NOT NULL,
  `c_doc_sub` VARCHAR(40) NOT NULL,
  `c_doc_ver` INTEGER NOT NULL,
  `name_uk` VARCHAR(4000) NOT NULL,
  `name_ru` VARCHAR(4000),
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_unique` (c_doc, c_doc_sub, c_doc_ver)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Region
-- --------------------------

DROP TABLE IF EXISTS `region`;

CREATE TABLE `region` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` DATETIME,
  `end_date` DATETIME,
  `code` INTEGER NOT NULL,
  `name_uk` VARCHAR(255) NOT NULL,
  `name_ru` VARCHAR(255),
  PRIMARY KEY (`id`),
  KEY `key_region__code` (`code`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- TaxInspection
-- --------------------------

DROP TABLE IF EXISTS `tax_inspection`;

CREATE TABLE `tax_inspection` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` DATETIME,
  `end_date` DATETIME,
  `c_sti` INTEGER NOT NULL,
  `c_reg` BIGINT(20) NOT NULL,
  `c_raj` INTEGER NOT NULL,
  `t_sti` INTEGER NOT NULL,
  `name_uk` VARCHAR(255) NOT NULL,
  `name_ru` VARCHAR(255),
  `name_raj_uk` VARCHAR(255),
  `name_raj_ru` VARCHAR(255),
  PRIMARY KEY (`id`),
  KEY `key_tax_inspection__c_sti` (`c_sti`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Declaration
-- --------------------------

DROP TABLE IF EXISTS `declaration`;

CREATE TABLE `declaration` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `parent_id` BIGINT(20),
  `session_id` BIGINT(20) NOT NULL,
  `tin` INTEGER,
  `c_doc` VARCHAR(3) NOT NULL,
  `c_doc_sub` VARCHAR(3) NOT NULL,
  `c_doc_ver` INTEGER NOT NULL,
  `c_doc_type` INTEGER,
  `c_doc_cnt` INTEGER,
  `c_reg` INTEGER,
  `c_raj` INTEGER,
  `period_month` INTEGER NOT NULL,
  `period_type` INTEGER NOT NULL,
  `period_year` INTEGER NOT NULL,
  `c_sti_orig` INTEGER,
  `c_doc_stan` INTEGER,
  `d_fill` VARCHAR(8),
  `software` VARCHAR(32),
  `num`  VARCHAR(10),
  `type`  VARCHAR(32),
  `filename`  VARCHAR(32),
  `date` DATETIME,
  PRIMARY KEY (`id`),
  KEY `key_declaration__parent_id` (`parent_id`),
  KEY `key_declaration__session_id` (`session_id`),
  CONSTRAINT `fk_declaration__declaration` FOREIGN KEY (`parent_id`) REFERENCES `declaration` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_declaration__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
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
-- Feedback
-- --------------------------

DROP TABLE IF EXISTS `feedback`;

CREATE TABLE `feedback` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT(20),
  `name` VARCHAR(255) NOT NULL,
  `email` VARCHAR(255),
  `message` TEXT NOT NULL,
  `date` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Counterpart
-- --------------------------

DROP TABLE IF EXISTS `counterpart`;

CREATE TABLE `counterpart` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT(20) NOT NULL,
  `hk` VARCHAR(64),
  `hname` VARCHAR(255) NOT NULL,
  `hloc` VARCHAR(255),
  `htel` VARCHAR(32),
  `hnspdv` VARCHAR(64),
  PRIMARY KEY (`id`),
  KEY `key_counterpart__session_id` (`session_id`),
  CONSTRAINT `fk_counterpart__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Counterpart
-- --------------------------

DROP TABLE IF EXISTS `employee`;

CREATE TABLE `employee` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `session_id` BIGINT(20) NOT NULL,
  `htin` INTEGER,
  `hname` VARCHAR(255) NOT NULL,
  `hbirthday` DATE,
  `hdate_in` DATE,
  `hdate_out` DATE,
  PRIMARY KEY (`id`),
  KEY `key_employee__session_id` (`session_id`),
  CONSTRAINT `fk_employee__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Field
-- --------------------------

DROP TABLE IF EXISTS `field`;

CREATE TABLE `field` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(32) NOT NULL,
  `spr_name` VARCHAR(32) NOT NULL,
  `prefix` VARCHAR(32),
  `alias` VARCHAR(32) NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Field
-- --------------------------

DROP TABLE IF EXISTS `field`;

CREATE TABLE `field` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `field_code_id` BIGINT(20) NOT NULL,
  `name` VARCHAR(32) NOT NULL,
  `spr_name` VARCHAR(64) NOT NULL,
  `prefix` VARCHAR(32),
  `alias` VARCHAR(32),
  PRIMARY KEY (`id`),
  KEY `key_field__field_code_id` (`field_code_id`),
  CONSTRAINT `fk_field__field_code` FOREIGN KEY (`field_code_id`) REFERENCES `field_code` (`id`) ON DELETE CASCADE
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Code
-- --------------------------

DROP TABLE IF EXISTS `code`;

CREATE TABLE `code` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `field_code_id` BIGINT(20) NOT NULL,
  `code` VARCHAR(32) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_code__field_code_id` (`field_code_id`),
  CONSTRAINT `fk_code__field_code` FOREIGN KEY (`field_code_id`) REFERENCES `field_code` (`id`) ON DELETE CASCADE
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- FieldCode
-- --------------------------

DROP TABLE IF EXISTS `field_code`;

CREATE TABLE `field_code` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Department
-- --------------------------

DROP TABLE IF EXISTS `department`;

CREATE TABLE `department` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20),
  `parent_id` BIGINT,
  `name` VARCHAR(64),
  `current` BOOLEAN NOT NULL,
  `date_start` DATE NOT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  KEY `key_department__parent_id` (`parent_id`),
  CONSTRAINT `fk_department__department` FOREIGN KEY (`parent_id`) REFERENCES `department` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- RateType
-- --------------------------

DROP TABLE IF EXISTS `rate_type`;

CREATE TABLE `rate_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20),
  `name` VARCHAR(255),
  `current` BOOLEAN NOT NULL,
  `date_start` DATE NOT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Schedule
-- --------------------------

DROP TABLE IF EXISTS `schedule`;

CREATE TABLE `schedule` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20),
  `name` VARCHAR(255),
  `current` BOOLEAN NOT NULL,
  `date_start` DATE NOT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Position
-- --------------------------

DROP TABLE IF EXISTS `position`;

CREATE TABLE `position` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20),
  `name` VARCHAR(255),
  `rate_count` INTEGER,
  `rate_type_id` BIGINT(20),
  `min_salary` DECIMAL(15,2),
  `max_salary` DECIMAL(15,2),
  `schedule_id` BIGINT(20),
  `description` VARCHAR(255),
  `current` BOOLEAN NOT NULL,
  `date_start` DATE NOT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`),
  KEY `key_position__schedule_id` (`schedule_id`),
  CONSTRAINT `fk_position__schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Payment
-- --------------------------

DROP TABLE IF EXISTS `payment`;

CREATE TABLE `payment` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20),
  `name` VARCHAR(255),
  `current` BOOLEAN NOT NULL,
  `date_start` DATE NOT NULL,
  `updated` TIMESTAMP NOT NULL DEFAULT NOW(),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- PersonnelRecord
-- --------------------------

DROP TABLE IF EXISTS `personnel`;

CREATE TABLE `personnel` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `object_id` BIGINT(20),
  `last_name` VARCHAR(64) NOT NULL,
  `first_name` VARCHAR(64) NOT NULL,
  `middle_name` VARCHAR(64) NOT NULL,
  `personnel_number` VARCHAR(64),
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
  PRIMARY KEY (`id`),
  KEY `key_personnel__department_id` (`department_id`),
  KEY `key_personnel__position_id` (`position_id`),
  KEY `key_personnel__schedule_id` (`schedule_id`),
  KEY `key_personnel__payment_id` (`payment_id`),
  CONSTRAINT `fk_personnel__department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`),
  CONSTRAINT `fk_personnel__position` FOREIGN KEY (`position_id`) REFERENCES `position` (`id`),
  CONSTRAINT `fk_personnel__schedule` FOREIGN KEY (`schedule_id`) REFERENCES `schedule` (`id`),
  CONSTRAINT `fk_personnel__payment` FOREIGN KEY (`payment_id`) REFERENCES `payment` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

