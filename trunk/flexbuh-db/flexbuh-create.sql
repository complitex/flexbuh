-- ------------------------------
-- User Organization //todo backward compatibility
-- ------------------------------
DROP TABLE IF EXISTS `user_organization`;

CREATE TABLE `user_organization` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор организации пользователей',
    `user_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор пользователя',
    `organization_object_id` BIGINT(20) NOT NULL COMMENT 'Идентификатор организации',
    `main` TINYINT(1) NOT NULL DEFAULT 0 COMMENT 'Является ли организация основной',
    PRIMARY KEY (`id`),
    UNIQUE KEY `key_unique` (`user_id`, `organization_object_id`),
    CONSTRAINT `fk_user_organization__user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Организация пользователей';

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
-- FO
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