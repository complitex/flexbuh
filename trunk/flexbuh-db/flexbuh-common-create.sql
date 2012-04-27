/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;

-- ------------------------------
-- Update
-- ------------------------------
DROP TABLE IF EXISTS `update`;

CREATE TABLE `update` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор обновления',
    `date_version` VARCHAR(64) NOT NULL DEFAULT 0 COMMENT 'Версия',
    `date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Дата обновления',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Обновление базы данных';

-- ------------------------------
-- PersonProfile
-- ------------------------------
DROP TABLE IF EXISTS `person_profile`;

CREATE TABLE  `person_profile` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор профайла',
  `session_id` BIGINT(20) NOT NULL,
  `person_type` INTEGER NOT NULL,
  `c_sti` INTEGER,
  `c_sti_tin` INTEGER,
  `tin` INTEGER,
  `name` VARCHAR(255),
  `last_name` VARCHAR(45),
  `first_name` VARCHAR(45),
  `middle_name` VARCHAR(45),
  `profile_name` VARCHAR(255) NOT NULL,
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
  `d_last_name` VARCHAR(45),
  `d_first_name` VARCHAR(45),
  `d_middle_name` VARCHAR(45),
  `b_inn` VARCHAR(45),
  `b_last_name` VARCHAR(45),
  `b_first_name` VARCHAR(45),
  `b_middle_name` VARCHAR(45),
  `tax_inspection_id` BIGINT(20),
  `selected` BOOLEAN,
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
  `cookie` VARCHAR(255) COMMENT 'Куки сессии',
  `create_date` DATETIME COMMENT 'Дата создания',
  `last_access_date` DATETIME COMMENT 'Дата последнего доступа',
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
  `first_name` VARCHAR(45) NOT NULL COMMENT 'Имя',
  `middle_name` VARCHAR(45) COMMENT 'Отчество',
  `last_name` VARCHAR(45) NOT NULL COMMENT 'Фамилия',
  `birthday` DATE COMMENT 'День рождения',
  `email` VARCHAR(45) NOT NULL COMMENT 'E-mail',
  `phone` VARCHAR(255) COMMENT 'Телефоны',
  `zip_code` VARCHAR(45) COMMENT 'Почтовый индекс',
  `country` VARCHAR(45) COMMENT 'Страна',
  `region` VARCHAR(45) COMMENT 'Регион',
  `area` VARCHAR(45) COMMENT 'Район',
  `city` VARCHAR(45) COMMENT 'Селение',
  `city_type` VARCHAR(45) COMMENT 'Тип селения (город/деревня/поселок)',
  `street` VARCHAR(45) COMMENT 'Улица',
  `street_type` VARCHAR(45) COMMENT 'Тип улицы',
  `building` VARCHAR(45) COMMENT 'Дом',
  `apartment` VARCHAR(45) COMMENT 'Квартира',

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
-- Organization
-- ------------------------------
DROP TABLE IF EXISTS `organization`;

CREATE TABLE  `organization` (
  `id` BIGINT(20) NOT NULL,
  `version` BIGINT(20) NOT NULL,
  `deleted` BOOLEAN,
  `entry_into_force_date` DATETIME NOT NULL,
  `completion_date` DATETIME,

  `name` VARCHAR(45) NOT NULL COMMENT 'Название организации',
  `type` VARCHAR(45) COMMENT 'Тип организации (ООО/ОАО/ИП/ЗАО)',
  `phone` VARCHAR(255) COMMENT 'Телефоны',
  `fax` VARCHAR(255) COMMENT 'Факсы',
  `email` VARCHAR(45) NOT NULL COMMENT 'E-mail',
  `http_address` VARCHAR(255) COMMENT 'Официальный веб адрес организации',

  `physical_address_zip_code` VARCHAR(45) COMMENT 'Физический адрес : почтовый индекс',
  `physical_address_country` VARCHAR(45) COMMENT 'Физический адрес : страна',
  `physical_address_region` VARCHAR(45) COMMENT 'Физический адрес : регион',
  `physical_address_area` VARCHAR(45) COMMENT 'Физический адрес : район',
  `physical_address_city` VARCHAR(45) COMMENT 'Физический адрес : селение',
  `physical_address_city_type` VARCHAR(45) COMMENT 'Физический адрес : тип селения (город/деревня/поселок)',
  `physical_address_street` VARCHAR(45) COMMENT 'Физический адрес : улица',
  `physical_address_street_type` VARCHAR(45) COMMENT 'Физический адрес : тип улицы',
  `physical_address_building` VARCHAR(45) COMMENT 'Физический адрес : дом',
  `physical_address_apartment` VARCHAR(45) COMMENT 'Физический адрес : квартира',

  `juridical_address_zip_code` VARCHAR(45) COMMENT 'Юридический адрес : почтовый индекс',
  `juridical_address_country` VARCHAR(45) COMMENT 'Юридический адрес : страна',
  `juridical_address_region` VARCHAR(45) COMMENT 'Юридический адрес : регион',
  `juridical_address_area` VARCHAR(45) COMMENT 'Юридический адрес : район',
  `juridical_address_city` VARCHAR(45) COMMENT 'Юридический адрес : селение',
  `juridical_address_city_type` VARCHAR(45) COMMENT 'Юридический адрес : тип селения (город/деревня/поселок)',
  `juridical_address_street` VARCHAR(45) COMMENT 'Юридический адрес : улица',
  `juridical_address_street_type` VARCHAR(45) COMMENT 'Юридический адрес : тип улицы',
  `juridical_address_building` VARCHAR(45) COMMENT 'Юридический адрес : дом',
  `juridical_address_apartment` VARCHAR(45) COMMENT 'Юридический адрес : квартира',

  PRIMARY KEY (`id`, `version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Организация';

-- ------------------------------
-- User organizations
-- ------------------------------
DROP TABLE IF EXISTS `user_organization`;

CREATE TABLE  `user_organization` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор организации пользователя',
  `login` VARCHAR(45) NOT NULL COMMENT 'Имя пользователя',
  `organization_id` BIGINT (20) NOT NULL COMMENT 'Идентификатор организации',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_login__organization_id` (`login`, `organization_id`),
  CONSTRAINT `fk_user_organization__user` FOREIGN KEY (`login`) REFERENCES `user` (`login`),
  CONSTRAINT `fk_user_organization__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Организации пользователей';

-- --------------------------
-- Organization type
-- --------------------------

DROP TABLE IF EXISTS `organization_type`;

CREATE TABLE `organization_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name_uk` VARCHAR(255),
  `name_ru` VARCHAR(255),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

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
-- Street type
-- --------------------------

DROP TABLE IF EXISTS `street_type`;

CREATE TABLE `street_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name_uk` VARCHAR(255),
  `name_ru` VARCHAR(255),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- City type
-- --------------------------

DROP TABLE IF EXISTS `city_type`;

CREATE TABLE `city_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name_uk` VARCHAR(255),
  `name_ru` VARCHAR(255),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- First name
-- --------------------------

DROP TABLE IF EXISTS `first_name`;

CREATE TABLE `first_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name_uk` VARCHAR(255),
  `name_ru` VARCHAR(255),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Middle name
-- --------------------------

DROP TABLE IF EXISTS `middle_name`;

CREATE TABLE `middle_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name_uk` VARCHAR(255),
  `name_ru` VARCHAR(255),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Last name
-- --------------------------

DROP TABLE IF EXISTS `last_name`;

CREATE TABLE `last_name` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name_uk` VARCHAR(255),
  `name_ru` VARCHAR(255),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

-- --------------------------
-- Share
-- --------------------------

DROP TABLE IF EXISTS `share`;

CREATE TABLE `share` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор',
  `session_id` BIGINT(20) COMMENT 'Идентификатор сессии',
  `shared_to_session_id` BIGINT(20) COMMENT 'Общий идентификатор сессии',
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_share` (`session_id`, `shared_to_session_id`),
  KEY `key_session_id` (`session_id`),
  KEY `key_shared_to_session_id` (`shared_to_session_id`),
  CONSTRAINT `fk_share__session` FOREIGN KEY (`session_id`) REFERENCES `session` (`id`),
  CONSTRAINT `fk_share_shared__session` FOREIGN KEY (`shared_to_session_id`) REFERENCES `session` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Общие данные';

-- --------------------------
-- Preference
-- --------------------------

DROP TABLE IF EXISTS `preference`;

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

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;

