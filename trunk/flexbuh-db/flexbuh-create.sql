/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- ------------------------------
-- PersonProfile
-- ------------------------------
DROP TABLE IF EXISTS `session`;

CREATE TABLE  `person_profile` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор профайла',
  `name` VARCHAR(255) NOT NULL,
  `codeTIN` VARCHAR(45),
  `codeTaxInspection` INTEGER,
  `codeKVED` VARCHAR(45),
  `personType` INTEGER NOT NULL,
  `contractDate` TIMESTAMP,
  `contractNumber` VARCHAR(45),
  `zipCode` VARCHAR(45),
  `address` VARCHAR(1000),
  `phone` VARCHAR(255),
  `fax` VARCHAR(45),
  `email` VARCHAR(255),
  `directorFIO` VARCHAR(255),
  `accountantFIO` VARCHAR(255),
  `directorINN` VARCHAR(45),
  `accountantINN` VARCHAR(45),
  `ipn` VARCHAR(45),
  `numSvdPDV` VARCHAR(45),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Профайл';

-- ------------------------------
-- Session
-- ------------------------------
DROP TABLE IF EXISTS `session`;

CREATE TABLE  `session` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор сессии',
  `cookie` VARCHAR(255) NOT NULL COMMENT 'Куки сессии',
  `person_profile_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор профайла',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key_cookie` (`cookie`),
  KEY `key_session__person_profile_id` (`person_profile_id`),
  CONSTRAINT `fk_user__person_profile` FOREIGN KEY (`person_profile_id`) REFERENCES `person_profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Сессия';

-- ------------------------------
-- Session`s person profiles
-- ------------------------------
DROP TABLE IF EXISTS `session_person_profile`;

CREATE TABLE  `session_person_profile` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор соответствия',
  `session_id` VARCHAR(255) NOT NULL COMMENT 'Идентификотор сессии',
  `person_profile_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор профайла',
  PRIMARY KEY (`id`),
  UNIQUE (`session_id`, `person_profile_id`),
  KEY `key_session_person_profile__session_id` (`session_id`),
  CONSTRAINT `fk_session_person_profile__person_profile` FOREIGN KEY (`person_profile_id`) REFERENCES `person_profile` (`id`),
  CONSTRAINT `fk_session_person_profile__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Профайлы сессии';

-- ------------------------------
-- User
-- ------------------------------
DROP TABLE IF EXISTS `user`;

CREATE TABLE  `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор пользователя',
  `login` VARCHAR(45) NOT NULL COMMENT 'Имя пользователя',
  `password` VARCHAR(45) NOT NULL COMMENT 'MD5 хэш пароля',
  `session_id` BIGINT(20) COMMENT 'Идентификатор сессии',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key_login` (`login`),
  UNIQUE KEY `unique_session` (`session_id`),
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
-- Language
-- --------------------------

DROP TABLE IF EXISTS `language`;

CREATE TABLE `language` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `lang_iso_code` VARCHAR(45) NOT NULL,
  `default_lang` TINYINT(1),
  PRIMARY KEY (`id`),
  UNIQUE (`lang_iso_code`),
  UNIQUE INDEX `key_name`(`lang_iso_code`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DictionaryType
-- --------------------------

DROP TABLE IF EXISTS `dictionary_type`;

CREATE TABLE `dictionary_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_dictionary_type__code` (`code`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DictionaryFileName
-- --------------------------

DROP TABLE IF EXISTS `dictionary_file_name`;

CREATE TABLE `dictionary_file_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `dictionary_type_id` BIGINT(20) NOT NULL,
  `file_name` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_dictionary_file_name__dictionary_type_id` (`dictionary_type_id`),
  CONSTRAINT `fk_dictionary_file_name__dictionary_type` FOREIGN KEY (`dictionary_type_id`) REFERENCES `dictionary_type` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DictionaryTypeName
-- --------------------------

DROP TABLE IF EXISTS `dictionary_type_name`;

CREATE TABLE `dictionary_type_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `language_id` BIGINT(20) NOT NULL,
  `dictionary_type_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`language_id`, `dictionary_type_id`),
  KEY `key_dictionary_type_name__language_id` (`language_id`),
  KEY `key_dictionary_type_name__dictionary_type_id` (`dictionary_type_id`),
  KEY `key_dictionary_type_name` (`language_id`, `dictionary_type_id`),
  CONSTRAINT `fk_dictionary_type_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
  CONSTRAINT `fk_dictionary_type_name__dictionary_type` FOREIGN KEY (`dictionary_type_id`) REFERENCES `dictionary_type` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Currency
-- --------------------------

DROP TABLE IF EXISTS `currency`;

CREATE TABLE `currency` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `status` INTEGER NOT NULL,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` TIMESTAMP,
  `end_date` TIMESTAMP,
  `code_number` INTEGER NOT NULL,
  `code_string` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- CurrencyName
-- --------------------------

DROP TABLE IF EXISTS `currency_name`;

CREATE TABLE `currency_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `language_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  `currency_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_currency_name__language_id` (`language_id`),
  KEY `key_currency_id` (`currency_id`),
  CONSTRAINT `fk_currency_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
  CONSTRAINT `fk_currency_name__currency` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`) ON DELETE CASCADE
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Document
-- --------------------------

DROP TABLE IF EXISTS `document`;

CREATE TABLE `document` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `status` INTEGER NOT NULL,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` TIMESTAMP,
  `end_date` TIMESTAMP,
  `document_type` VARCHAR(40) NOT NULL,
  `document_sub_type` VARCHAR(40) NOT NULL,
  `parent_document_type` VARCHAR(40),
  `parent_document_sub_type` VARCHAR(40),
  `cnt_set` TINYINT(1) NOT NULL,
  `selected` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (document_type, document_sub_type),
  KEY `key_document` (document_type, document_sub_type),
  KEY `key_document__parent_document` (parent_document_type, parent_document_sub_type)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DocumentName
-- --------------------------

DROP TABLE IF EXISTS `document_name`;

CREATE TABLE `document_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `language_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(4000) NOT NULL,
  `document_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_document_name__language_id` (`language_id`),
  KEY `key_document_name__document_id` (`document_id`),
  CONSTRAINT `fk_document_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
  CONSTRAINT `fk_document_name__document` FOREIGN KEY (`document_id`) REFERENCES `document` (`id`) ON DELETE CASCADE
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DocumentTerm
-- --------------------------

DROP TABLE IF EXISTS `document_term`;

CREATE TABLE `document_term` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `status` INTEGER NOT NULL,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` TIMESTAMP,
  `end_date` TIMESTAMP,
  `document_type` VARCHAR(40) NOT NULL,
  `document_sub_type` VARCHAR(40) NOT NULL,
  `document_version` INTEGER NOT NULL,
  `date_term` TIMESTAMP NOT NULL,
  `period_month` INTEGER NOT NULL,
  `period_type` INTEGER NOT NULL,
  `period_year` INTEGER NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_document_term__document` (document_type, document_sub_type)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DocumentVersion
-- --------------------------

DROP TABLE IF EXISTS `document_version`;

CREATE TABLE `document_version` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `status` INTEGER NOT NULL,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` TIMESTAMP,
  `end_date` TIMESTAMP,
  `document_type` VARCHAR(40) NOT NULL,
  `document_sub_type` VARCHAR(40) NOT NULL,
  `version` INTEGER NOT NULL,
  UNIQUE (document_type, document_sub_type, version),
  PRIMARY KEY (`id`),
  KEY `key_document_version__document` (document_type, document_sub_type),
  KEY `key_document_version__document_version` (document_type, document_sub_type, version)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- NormativeDocumentName
-- --------------------------

DROP TABLE IF EXISTS `normative_document_name`;

CREATE TABLE `normative_document_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `language_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(4000) NOT NULL,
  `document_version_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_normative_document_name__language_id` (`language_id`),
  KEY `key_normative_document_name__document_version_id` (`document_version_id`),
  CONSTRAINT `fk_normative_document_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
  CONSTRAINT `fk_normative_document_name__document_version` FOREIGN KEY (`document_version_id`) REFERENCES `document_version` (`id`) ON DELETE CASCADE
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Region
-- --------------------------

DROP TABLE IF EXISTS `region`;

CREATE TABLE `region` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `status` INTEGER NOT NULL,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` TIMESTAMP,
  `end_date` TIMESTAMP,
  `code` INTEGER NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_region__code` (`code`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;


-- --------------------------
-- RegionName
-- --------------------------

DROP TABLE IF EXISTS `region_name`;

CREATE TABLE `region_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `language_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  `region_id` BIGINT(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_region_name__language_id` (`language_id`),
  KEY `key_region_name__region_id` (`region_id`),
  CONSTRAINT `fk_region_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
  CONSTRAINT `fk_region_name__region` FOREIGN KEY (`region_id`) REFERENCES `region` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- TaxInspection
-- --------------------------

DROP TABLE IF EXISTS `tax_inspection`;

CREATE TABLE `tax_inspection` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `status` INTEGER NOT NULL,
  `upload_date` TIMESTAMP NOT NULL,
  `begin_date` TIMESTAMP,
  `end_date` TIMESTAMP,
  `code` INTEGER NOT NULL,
  `region_code` BIGINT(20) NOT NULL,
  `code_area` INTEGER NOT NULL,
  `code_tax_inspection_type` INTEGER NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_tax_inspection__code` (`code`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;


-- --------------------------
-- AreaName
-- --------------------------

DROP TABLE IF EXISTS `area_name`;

CREATE TABLE `area_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `language_id` BIGINT(20) NOT NULL,
  `tax_inspection_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_area_name__language_id` (`language_id`),
  KEY `key_area_name__tax_inspection_id` (`tax_inspection_id`),
  CONSTRAINT `fk_area_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
  CONSTRAINT `fk_area_name__tax_inspection` FOREIGN KEY (`tax_inspection_id`) REFERENCES `tax_inspection` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- TaxInspectionName
-- --------------------------

DROP TABLE IF EXISTS `tax_inspection_name`;

CREATE TABLE `tax_inspection_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `language_id` BIGINT(20) NOT NULL,
  `tax_inspection_id` BIGINT(20) NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_tax_inspection_name__language_id` (`language_id`),
  KEY `key_tax_inspection_name__tax_inspection_id` (`tax_inspection_id`),
  CONSTRAINT `fk_tax_inspection_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`),
  CONSTRAINT `fk_tax_inspection_name__tax_inspection` FOREIGN KEY (`tax_inspection_id`) REFERENCES `tax_inspection` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

