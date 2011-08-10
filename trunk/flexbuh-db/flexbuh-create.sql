/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- ------------------------------
-- User
-- ------------------------------
DROP TABLE IF EXISTS `user`;

CREATE TABLE  `user` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор пользователя',
  `login` VARCHAR(45) NOT NULL COMMENT 'Имя пользователя',
  `password` VARCHAR(45) NOT NULL COMMENT 'MD5 хэш пароля',
  `user_info_object_id` BIGINT(20) COMMENT 'Идентификатор объекта информация о пользователе',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_key_login` (`login`),
  KEY `key_user_info_object_id` (`user_info_object_id`),
  CONSTRAINT `fk_user__user_info` FOREIGN KEY (`user_info_object_id`) REFERENCES `user_info` (`object_id`)
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
  PRIMARY KEY (`id`),
  UNIQUE INDEX `key_name`(`name`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Language
-- --------------------------

DROP TABLE IF EXISTS `language`;

CREATE TABLE `language` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `lang_iso_code` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `key_name`(`lang_iso_code`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- AreaName
-- --------------------------

DROP TABLE IF EXISTS `area_name`;

CREATE TABLE `area_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `language_id` INTEGER UNSIGNED NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_area_name__language_id` (`language_id`),
  CONSTRAINT `fk_area_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- CurrencyName
-- --------------------------

DROP TABLE IF EXISTS `currency_name`;

CREATE TABLE `currency_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `language_id` INTEGER UNSIGNED NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_currency_name__language_id` (`language_id`),
  CONSTRAINT `fk_currency_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DocumentName
-- --------------------------

DROP TABLE IF EXISTS `document_name`;

CREATE TABLE `document_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `language_id` INTEGER UNSIGNED NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_document_name__language_id` (`language_id`),
  CONSTRAINT `fk_document_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- NormativeDocumentName
-- --------------------------

DROP TABLE IF EXISTS `document_name`;

CREATE TABLE `normative_document_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `language_id` INTEGER UNSIGNED NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_normative_document_name__language_id` (`language_id`),
  CONSTRAINT `fk_normative_document_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- RegionName
-- --------------------------

DROP TABLE IF EXISTS `region_name`;

CREATE TABLE `region_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `language_id` INTEGER UNSIGNED NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_region_name__language_id` (`language_id`),
  CONSTRAINT `fk_region_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- TaxInspectionName
-- --------------------------

DROP TABLE IF EXISTS `tax_inspection_name`;

CREATE TABLE `tax_inspection_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `language_id` INTEGER UNSIGNED NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_tax_inspection_name__language_id` (`language_id`),
  CONSTRAINT `fk_tax_inspection_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DictionaryTypeName
-- --------------------------

DROP TABLE IF EXISTS `dictionary_type_name`;

CREATE TABLE `dictionary_type_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `language_id` INTEGER UNSIGNED NOT NULL,
  `value` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_dictionary_type_name__language_id` (`language_id`),
  CONSTRAINT `fk_dictionary_type_name__language` FOREIGN KEY (`language_id`) REFERENCES `language` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DictionaryType
-- --------------------------

DROP TABLE IF EXISTS `dictionary_type`;

CREATE TABLE `dictionary_type` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_dictionary_type__code` (`code`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- CollectionDictionaryTypeName
-- --------------------------

DROP TABLE IF EXISTS `collection_dictionary_type_name`;

CREATE TABLE `collection_dictionary_type_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `dictionary_type_id` INTEGER UNSIGNED NOT NULL,
  `dictionary_type_name_id` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (dictionary_type_id, dictionary_type_name_id),
  KEY `key_collection_dictionary_type_name__dictionary_type` (`dictionary_type_id`),
  KEY `key_collection_dictionary_type_name__dictionary_type_name` (`dictionary_type_name_id`),
  KEY `key_collection_dictionary_type_name` (dictionary_type_id, dictionary_type_name_id),
  CONSTRAINT `fk_collection_dictionary_type_name__dictionary_type` FOREIGN KEY (`dictionary_type_id`) REFERENCES `dictionary_type` (`id`),
  CONSTRAINT `fk_collection_dictionary_type_name__dictionary_type_name` FOREIGN KEY (`dictionary_type_name_id`) REFERENCES `dictionary_type_name` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Currency
-- --------------------------

DROP TABLE IF EXISTS `currency`;

CREATE TABLE `currency` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `begin_date` TIMESTAMP,
  `end_date` TIMESTAMP,
  `dictionary_type_id` INTEGER UNSIGNED NOT NULL,
  `code_number` INTEGER UNSIGNED NOT NULL,
  `code_string` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_currency__dictionary_type` (`dictionary_type_id`),
  CONSTRAINT `fk_currency__dictionary_type` FOREIGN KEY (`dictionary_type_id`) REFERENCES `dictionary_type` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- CollectionCurrencyName
-- --------------------------

DROP TABLE IF EXISTS `collection_currency_name`;

CREATE TABLE `collection_currency_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `currency_id` INTEGER UNSIGNED NOT NULL,
  `currency_name_id` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (currency_id, currency_name_id),
  KEY `key_collection_currency_name__currency` (`currency_id`),
  KEY `key_collection_currency_name__currency_name` (`currency_name_id`),
  KEY `key_collection_currency_name` (`currency_id`, `currency_name_id`),
  CONSTRAINT `fk_collection_currency_name__currency` FOREIGN KEY (`currency_id`) REFERENCES `currency` (`id`),
  CONSTRAINT `fk_collection_currency_name__currency_name` FOREIGN KEY (`currency_name_id`) REFERENCES `currency_name` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DocumentSubType
-- --------------------------

DROP TABLE IF EXISTS `document_sub_type`;

CREATE TABLE `document_sub_type` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_document_sub_type__code` (`code`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DocumentType
-- --------------------------

DROP TABLE IF EXISTS `document_type`;

CREATE TABLE `document_type` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(40) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_document_type__code` (`code`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Document
-- --------------------------

DROP TABLE IF EXISTS `document`;

CREATE TABLE `document` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `dictionary_type_id` INTEGER UNSIGNED NOT NULL,
  `document_type_id` INTEGER UNSIGNED NOT NULL,
  `document_sub_type_id` INTEGER UNSIGNED NOT NULL,
  `parent_document_type_id` INTEGER UNSIGNED NOT NULL,
  `parent_document_sub_type_id` INTEGER UNSIGNED NOT NULL,
  `cnt_set` TINYINT(1) NOT NULL,
  `selected` TINYINT(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_document__dictionary_type_id` (`dictionary_type_id`),
  KEY `key_document` (document_type_id, document_sub_type_id),
  KEY `key_document__parent_document` (parent_document_type_id, parent_document_sub_type_id),
  CONSTRAINT `fk_document__dictionary_type` FOREIGN KEY (`dictionary_type_id`) REFERENCES `dictionary_type` (`id`),
  CONSTRAINT `fk_document__document_type` FOREIGN KEY (`document_type_id`) REFERENCES `document_type` (`id`),
  CONSTRAINT `fk_document__sub_document_type` FOREIGN KEY (`document_sub_type_id`) REFERENCES `document_sub_type` (`id`),
  CONSTRAINT `fk_document__parent_document_type` FOREIGN KEY (`parent_document_type_id`) REFERENCES `document_type` (`id`),
  CONSTRAINT `fk_document__parent_sub_document_type` FOREIGN KEY (`parent_document_sub_type_id`) REFERENCES `document_sub_type` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- CollectionDocumentName
-- --------------------------

DROP TABLE IF EXISTS `collection_document_name`;

CREATE TABLE `collection_document_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `document_id` INTEGER UNSIGNED NOT NULL,
  `document_name_id` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (document_id, document_name_id),
  KEY `key_collection_document_name__document` (`document_id`),
  KEY `key_collection_document_name__document_name` (`document_name_id`),
  KEY `key_collection_document_name` (`document_id`, `document_name_id`),
  CONSTRAINT `fk_collection_document_name__document` FOREIGN KEY (`document_id`) REFERENCES `document` (`id`),
  CONSTRAINT `fk_collection_document_name__document_name` FOREIGN KEY (`document_name_id`) REFERENCES `document_name` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DocumentTerm
-- --------------------------

DROP TABLE IF EXISTS `document_term`;

CREATE TABLE `document_term` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `dictionary_type_id` INTEGER UNSIGNED NOT NULL,
  `document_type_id` INTEGER UNSIGNED NOT NULL,
  `document_sub_type_id` INTEGER UNSIGNED NOT NULL,
  `document_version` INTEGER UNSIGNED NOT NULL,
  `data_term` TIMESTAMP NOT NULL,
  `selected` TINYINT(1) NOT NULL,
  `period_month` INTEGER NOT NULL,
  `period_type` INTEGER NOT NULL,
  `period_year` INTEGER NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_document_term__dictionary_type_id` (`dictionary_type_id`),
  KEY `key_document_term__document` (document_type_id, document_sub_type_id),
  CONSTRAINT `fk_document_term__dictionary_type` FOREIGN KEY (`dictionary_type_id`) REFERENCES `dictionary_type` (`id`),
  CONSTRAINT `fk_document_term__document_type` FOREIGN KEY (`document_type_id`) REFERENCES `document_type` (`id`),
  CONSTRAINT `fk_document_term__sub_document_type` FOREIGN KEY (`document_sub_type_id`) REFERENCES `document_sub_type` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- DocumentVersion
-- --------------------------

DROP TABLE IF EXISTS `document_version`;

CREATE TABLE `document_version` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `dictionary_type_id` INTEGER UNSIGNED NOT NULL,
  `begin_date` TIMESTAMP,
  `end_date` TIMESTAMP,
  `document_type_id` INTEGER UNSIGNED NOT NULL,
  `document_sub_type_id` INTEGER UNSIGNED NOT NULL,
  `document_version` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_document_version__dictionary_type_id` (`dictionary_type_id`),
  KEY `key_document_version__document` (document_type_id, document_sub_type_id),
  KEY `key_document_version__document_version` (document_type_id, document_sub_type_id, document_version),
  CONSTRAINT `fk_document_version__dictionary_type` FOREIGN KEY (`dictionary_type_id`) REFERENCES `dictionary_type` (`id`),
  CONSTRAINT `fk_document_version__document_type` FOREIGN KEY (`document_type_id`) REFERENCES `document_type` (`id`),
  CONSTRAINT `fk_document_version__sub_document_type` FOREIGN KEY (`document_sub_type_id`) REFERENCES `document_sub_type` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- CollectionNormativeDocumentName
-- --------------------------

DROP TABLE IF EXISTS `collection_normative_document_name`;

CREATE TABLE `collection_normative_document_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `document_version_id` INTEGER UNSIGNED NOT NULL,
  `normative_document_name_id` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (document_version_id, normative_document_name_id),
  KEY `key_collection_normative_document_name__document_version` (`document_version_id`),
  KEY `key_collection_normative_document_name__normative_document_name` (`normative_document_name_id`),
  KEY `key_collection_normative_document_name` (`document_version_id`, `normative_document_name_id`),
  CONSTRAINT `fk_collection_normative_document_name__document_version` FOREIGN KEY (`document_version_id`) REFERENCES `document_version` (`id`),
  CONSTRAINT `fk_collection_normative_document_name__normative_document_name` FOREIGN KEY (`normative_document_name_id`) REFERENCES `normative_document_name` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Region
-- --------------------------

DROP TABLE IF EXISTS `region`;

CREATE TABLE `region` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `dictionary_type_id` INTEGER UNSIGNED NOT NULL,
  `begin_date` TIMESTAMP,
  `end_date` TIMESTAMP,
  `code` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_region__dictionary_type` (`dictionary_type_id`),
  KEY `key_region__code` (`code`),
  CONSTRAINT `fk_region__dictionary_type` FOREIGN KEY (`dictionary_type_id`) REFERENCES `dictionary_type` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- CollectionRegionName
-- --------------------------

DROP TABLE IF EXISTS `collection_region_name`;

CREATE TABLE `collection_region_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `region_id` INTEGER UNSIGNED NOT NULL,
  `region_name_id` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (region_id, region_name_id),
  KEY `key_collection_region_name__region` (`region_id`),
  KEY `key_collection_region_name__region_name` (`region_name_id`),
  KEY `key_collection_region_name` (`region_id`, `region_name_id`),
  CONSTRAINT `fk_collection_region_name__region` FOREIGN KEY (`region_id`) REFERENCES `region` (`id`),
  CONSTRAINT `fk_collection_region_name__region_name` FOREIGN KEY (`region_name_id`) REFERENCES `region_name` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- TaxInspection
-- --------------------------

DROP TABLE IF EXISTS `tax_inspection`;

CREATE TABLE `tax_inspection` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `dictionary_type_id` INTEGER UNSIGNED NOT NULL,
  `code` INTEGER UNSIGNED NOT NULL,
  `region_code` INTEGER UNSIGNED NOT NULL,
  `code_area` INTEGER UNSIGNED NOT NULL,
  `code_tax_inspection_type` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  KEY `key_tax_inspection__dictionary_type` (`dictionary_type_id`),
  KEY `key_tax_inspection__code` (`code`),
  CONSTRAINT `fk_tax_inspection__dictionary_type` FOREIGN KEY (`dictionary_type_id`) REFERENCES `dictionary_type` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- CollectionRegionName
-- --------------------------

DROP TABLE IF EXISTS `collection_tax_inspection_name`;

CREATE TABLE `collection_tax_inspection_name` (
  `id` INTEGER UNSIGNED NOT NULL AUTO_INCREMENT,
  `tax_inspection_id` INTEGER UNSIGNED NOT NULL,
  `tax_inspection_name_id` INTEGER UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (tax_inspection_id, tax_inspection_name_id),
  KEY `key_collection_tax_inspection_name__tax_inspection` (`tax_inspection_id`),
  KEY `key_collection_tax_inspection_name__tax_inspection_name` (`tax_inspection_name_id`),
  KEY `key_collection_tax_inspection_name` (`tax_inspection_id`, `tax_inspection_name_id`),
  CONSTRAINT `fk_collection_tax_inspection_name__tax_inspection` FOREIGN KEY (`tax_inspection_id`) REFERENCES `tax_inspection` (`id`),
  CONSTRAINT `fk_collection_tax_inspection_name__tax_inspection_name` FOREIGN KEY (`tax_inspection_name_id`) REFERENCES `tax_inspection_name` (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

