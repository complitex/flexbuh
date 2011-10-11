/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- --------------------------
-- PersonType
-- --------------------------

DROP TABLE IF EXISTS `person_type`;

CREATE TABLE `person_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(40) NOT NULL,
  `name_uk` VARCHAR(255),
  `name_ru` VARCHAR(255),
  PRIMARY KEY (`id`),
  UNIQUE KEY `key_person_type__code` (`code`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- ------------------------------
-- PersonProfile
-- ------------------------------
DROP TABLE IF EXISTS `person_profile`;

CREATE TABLE  `person_profile` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор профайла',
  `session_id` BIGINT(20) NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `code_TIN` VARCHAR(45),
  `code_tax_inspection` INTEGER,
  `code_KVED` VARCHAR(45),
  `person_type_id` BIGINT(20) NOT NULL,
  `contract_date` DATE,
  `contract_number` VARCHAR(45),
  `zip_code` VARCHAR(45),
  `address` VARCHAR(1000),
  `phone` VARCHAR(255),
  `fax` VARCHAR(45),
  `email` VARCHAR(255),
  `director_FIO` VARCHAR(255),
  `accountant_FIO` VARCHAR(255),
  `director_INN` VARCHAR(45),
  `accountant_INN` VARCHAR(45),
  `ipn` VARCHAR(45),
  `num_svd_PDV` VARCHAR(45),
  PRIMARY KEY (`id`),
  KEY `key_session_id` (`session_id`),
  KEY `key_person_profile__person_type_id` (`person_type_id`),
  CONSTRAINT `fk_person_profile__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
  CONSTRAINT `fk_person_profile__person_type` FOREIGN KEY (`person_type_id`) REFERENCES `person_type` (`id`)
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
  `code` INTEGER NOT NULL,
  `region_code` BIGINT(20) NOT NULL,
  `code_area` INTEGER NOT NULL,
  `code_tax_inspection_type` INTEGER NOT NULL,
  `name_uk` VARCHAR(255) NOT NULL,
  `name_ru` VARCHAR(255),
  `area_name_uk` VARCHAR(255),
  `area_name_ru` VARCHAR(255),
  PRIMARY KEY (`id`),
  KEY `key_tax_inspection__code` (`code`)
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
  `tin` VARCHAR(10),
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
  `c_sti_orig` VARCHAR(10),
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
  CONSTRAINT `fk_declaration__declaration` FOREIGN KEY (`parent_id`) REFERENCES `declaration` (`id`),
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
  `name` VARCHAR(20),
  `value` VARCHAR(255),
  PRIMARY KEY (`id`),
  KEY `key_declaration_value__declaration_id` (`declaration_id`),
  CONSTRAINT `fk_declaration_value__declaration` FOREIGN KEY (`declaration_id`) REFERENCES `declaration` (`id`) ON DELETE CASCADE
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

