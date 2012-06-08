DROP PROCEDURE IF EXISTS organizationPhysicalCursor;/
DROP PROCEDURE IF EXISTS organizationJuridicalCursor;/
CREATE PROCEDURE organizationPhysicalCursor()
BEGIN
  DECLARE flag TINYINT DEFAULT 0;
  DECLARE addressIdVar BIGINT(20);
  DECLARE organizationId BIGINT(20);
  DECLARE organizationVersion BIGINT(20);
  DECLARE zipCodeVar VARCHAR(45);
  DECLARE countryVar VARCHAR(45);
  DECLARE regionVar VARCHAR(45);
  DECLARE areaVar VARCHAR(45);
  DECLARE cityVar VARCHAR(45);
  DECLARE cityTypeVar VARCHAR(45);
  DECLARE streetVar VARCHAR(45);
  DECLARE streetTypeVar VARCHAR(45);
  DECLARE buildingVar VARCHAR(45);
  DECLARE apartmentVar VARCHAR(45);
  DECLARE organizationCursor CURSOR
        FOR SELECT `id`, `version`, `physical_address_zip_code`, `physical_address_country`, `physical_address_region`, `physical_address_area`,
        `physical_address_city`, `physical_address_city_type`, `physical_address_street`, `physical_address_street_type`,
        `physical_address_building`, `physical_address_apartment` from `organization`;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET flag=1;
  SET flag=0;
  OPEN organizationCursor;
          WHILE (NOT flag) DO
            FETCH organizationCursor INTO organizationId, organizationVersion, zipCodeVar, countryVar, regionVar, areaVar, cityVar,
             cityTypeVar, streetVar, streetTypeVar, buildingVar, apartmentVar;
              IF NOT flag and ((zipCodeVar is not null) or (countryVar is not null) or (regionVar is not null) or (areaVar is not null)
               or (cityVar is not null) or (cityTypeVar is not null) or (streetVar is not null) or (streetTypeVar is not null)
               or (buildingVar is not null) or (apartmentVar is not null)) THEN
				
				SET @sql = 'SET @address_id = (SELECT id from `address` where';
				IF zipCodeVar is not null THEN
					SET @zip_code = '' + zipCodeVar;
					SET @sql = CONCAT(@sql, ' `zip_code` = @zip_code');
				ELSE
					SET @sql = CONCAT(@sql, ' `zip_code` is null');
				END IF;
				IF countryVar is not null THEN
					SET @country = '' + countryVar;
					SET @sql = CONCAT(@sql, ' ` and `country` = @country');
				ELSE
					SET @sql = CONCAT(@sql, ' and `country` is null');
				END IF;
				IF regionVar is not null THEN
					SET @region = '' + regionVar;
					SET @sql = CONCAT(@sql, ' ` and `region` = @region');
				ELSE
					SET @sql = CONCAT(@sql, ' and `region` is null');
				END IF;
				IF areaVar is not null THEN
					SET @area = '' + areaVar;
					SET @sql = CONCAT(@sql, ' ` and `area` = @area');
				ELSE
					SET @sql = CONCAT(@sql, ' and `area` is null');
				END IF;
				IF cityVar is not null THEN
					SET @city = '' + cityVar;
					SET @sql = CONCAT(@sql, ' ` and `city` = @city');
				ELSE
					SET @sql = CONCAT(@sql, ' and `city` is null');
				END IF;
				IF cityTypeVar is not null THEN
					SET @city_type = '' + cityTypeVar;
					SET @sql = CONCAT(@sql, ' ` and `city_type` = @city_type');
				ELSE
					SET @sql = CONCAT(@sql, ' and `city_type` is null');
				END IF;
				IF streetVar is not null THEN
					SET @street = '' + streetVar;
					SET @sql = CONCAT(@sql, ' ` and `street` = @street');
				ELSE
					SET @sql = CONCAT(@sql, ' and `street` is null');
				END IF;
				IF streetTypeVar is not null THEN
					SET @street_type = '' + streetTypeVar;
					SET @sql = CONCAT(@sql, ' ` and `street_type` = @street_type');
				ELSE
					SET @sql = CONCAT(@sql, ' and `street_type` is null');
				END IF;
				IF buildingVar is not null THEN
					SET @building = '' + buildingVar;
					SET @sql = CONCAT(@sql, ' ` and `building` = @building');
				ELSE
					SET @sql = CONCAT(@sql, ' and `building` is null');
				END IF;
				IF apartmentVar is not null THEN
					SET @apartment = '' + apartmentVar;
					SET @sql = CONCAT(@sql, ' ` and `apartment` = @apartment');
				ELSE
					SET @sql = CONCAT(@sql, ' and `apartment` is null');
				END IF;
				SET @sql = CONCAT(@sql, ')');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				-- SET @address_id = (SELECT id from `address` where `zip_code` = @zip_code and `country` = null);
				-- SELECT @address_id, @zip_code, @country;
				IF @address_id IS NULL or NOT @address_id > 0 THEN
                  INSERT into `address`
                    (`zip_code`, `country`, `region`, `area`, `city`, `city_type`, `street`, `street_type`, `building`, `apartment`)
                    VALUES (zipCodeVar, countryVar, regionVar, areaVar, cityVar, cityTypeVar, streetVar, streetTypeVar, buildingVar, apartmentVar);
                  SET @address_id = (SELECT LAST_INSERT_ID());
                END IF;
                UPDATE `organization` SET `physical_address_id`=@address_id
                  WHERE `id`=organizationId and `version`=organizationVersion;
				SET flag=0;
              END IF;
          END WHILE;
  CLOSE organizationCursor;
END;/
CREATE PROCEDURE organizationJuridicalCursor()
BEGIN
  DECLARE flag TINYINT DEFAULT 0;
  DECLARE addressIdVar BIGINT(20);
  DECLARE organizationId BIGINT(20);
  DECLARE organizationVersion BIGINT(20);
  DECLARE zipCodeVar VARCHAR(45);
  DECLARE countryVar VARCHAR(45);
  DECLARE regionVar VARCHAR(45);
  DECLARE areaVar VARCHAR(45);
  DECLARE cityVar VARCHAR(45);
  DECLARE cityTypeVar VARCHAR(45);
  DECLARE streetVar VARCHAR(45);
  DECLARE streetTypeVar VARCHAR(45);
  DECLARE buildingVar VARCHAR(45);
  DECLARE apartmentVar VARCHAR(45);
  DECLARE organizationCursor CURSOR
        FOR SELECT `id`, `version`, `juridical_address_zip_code`, `juridical_address_country`, `juridical_address_region`, `juridical_address_area`,
        `juridical_address_city`, `juridical_address_city_type`, `juridical_address_street`, `juridical_address_street_type`,
        `juridical_address_building`, `juridical_address_apartment` from `organization`;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET flag=1;
  SET flag=0;
  OPEN organizationCursor;
          WHILE (NOT flag) DO
            FETCH organizationCursor INTO organizationId, organizationVersion, zipCodeVar, countryVar, regionVar, areaVar, cityVar,
             cityTypeVar, streetVar, streetTypeVar, buildingVar, apartmentVar;
              IF NOT flag and ((zipCodeVar is not null) or (countryVar is not null) or (regionVar is not null) or (areaVar is not null)
               or (cityVar is not null) or (cityTypeVar is not null) or (streetVar is not null) or (streetTypeVar is not null)
               or (buildingVar is not null) or (apartmentVar is not null)) THEN
				
				SET @sql = 'SET @address_id = (SELECT id from `address` where';
				IF zipCodeVar is not null THEN
					SET @zip_code = '' + zipCodeVar;
					SET @sql = CONCAT(@sql, ' `zip_code` = @zip_code');
				ELSE
					SET @sql = CONCAT(@sql, ' `zip_code` is null');
				END IF;
				IF countryVar is not null THEN
					SET @country = '' + countryVar;
					SET @sql = CONCAT(@sql, ' ` and `country` = @country');
				ELSE
					SET @sql = CONCAT(@sql, ' and `country` is null');
				END IF;
				IF regionVar is not null THEN
					SET @region = '' + regionVar;
					SET @sql = CONCAT(@sql, ' ` and `region` = @region');
				ELSE
					SET @sql = CONCAT(@sql, ' and `region` is null');
				END IF;
				IF areaVar is not null THEN
					SET @area = '' + areaVar;
					SET @sql = CONCAT(@sql, ' ` and `area` = @area');
				ELSE
					SET @sql = CONCAT(@sql, ' and `area` is null');
				END IF;
				IF cityVar is not null THEN
					SET @city = '' + cityVar;
					SET @sql = CONCAT(@sql, ' ` and `city` = @city');
				ELSE
					SET @sql = CONCAT(@sql, ' and `city` is null');
				END IF;
				IF cityTypeVar is not null THEN
					SET @city_type = '' + cityTypeVar;
					SET @sql = CONCAT(@sql, ' ` and `city_type` = @city_type');
				ELSE
					SET @sql = CONCAT(@sql, ' and `city_type` is null');
				END IF;
				IF streetVar is not null THEN
					SET @street = '' + streetVar;
					SET @sql = CONCAT(@sql, ' ` and `street` = @street');
				ELSE
					SET @sql = CONCAT(@sql, ' and `street` is null');
				END IF;
				IF streetTypeVar is not null THEN
					SET @street_type = '' + streetTypeVar;
					SET @sql = CONCAT(@sql, ' ` and `street_type` = @street_type');
				ELSE
					SET @sql = CONCAT(@sql, ' and `street_type` is null');
				END IF;
				IF buildingVar is not null THEN
					SET @building = '' + buildingVar;
					SET @sql = CONCAT(@sql, ' ` and `building` = @building');
				ELSE
					SET @sql = CONCAT(@sql, ' and `building` is null');
				END IF;
				IF apartmentVar is not null THEN
					SET @apartment = '' + apartmentVar;
					SET @sql = CONCAT(@sql, ' ` and `apartment` = @apartment');
				ELSE
					SET @sql = CONCAT(@sql, ' and `apartment` is null');
				END IF;
				SET @sql = CONCAT(@sql, ')');
				PREPARE stmt FROM @sql;
				EXECUTE stmt;
				-- SET @address_id = (SELECT id from `address` where `zip_code` = @zip_code and `country` = null);
				-- SELECT @address_id, @zip_code, @country;
				IF @address_id IS NULL or NOT @address_id > 0 THEN
                  INSERT into `address`
                    (`zip_code`, `country`, `region`, `area`, `city`, `city_type`, `street`, `street_type`, `building`, `apartment`)
                    VALUES (zipCodeVar, countryVar, regionVar, areaVar, cityVar, cityTypeVar, streetVar, streetTypeVar, buildingVar, apartmentVar);
                  SET @address_id = (SELECT LAST_INSERT_ID());
                END IF;
                UPDATE `organization` SET `juridical_address_id`=@address_id
                  WHERE `id`=organizationId and `version`=organizationVersion;
				SET flag=0;
              END IF;
          END WHILE;
  CLOSE organizationCursor;
END;/
ALTER TABLE `organization` DROP FOREIGN KEY `fk_organization__physical_address`;/
ALTER TABLE `organization` DROP FOREIGN KEY `fk_organization__juridical_address`;/

DROP TABLE IF EXISTS `address`;/

CREATE TABLE  `address` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT 'Идентификатор адреса',
  `zip_code` VARCHAR(45) COMMENT 'Почтовый индекс',
  `country` VARCHAR(45) COMMENT 'Страна',
  `region` VARCHAR(45) COMMENT 'Регион',
  `area` VARCHAR(45) COMMENT 'Район',
  `city` VARCHAR(45) COMMENT 'Селение',
  `city_type` VARCHAR(45) COMMENT 'Тип селения (город/деревня/поселок)',
  `street` VARCHAR(45) COMMENT 'Улица',
  `street_type` VARCHAR(45) COMMENT 'Тип улицы (проспект/бульвар)',
  `building` VARCHAR(45) COMMENT 'Дом',
  `apartment` VARCHAR(45) COMMENT 'Квартира',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT 'Адрес';/

ALTER TABLE `organization` drop column `physical_address_id`, drop column `juridical_address_id`;/

ALTER TABLE `organization`
add column `physical_address_id` BIGINT (20) COMMENT 'Идентификатор физического адреса организации',
add column `juridical_address_id` BIGINT (20) COMMENT 'Идентификатор юридического адреса организации';/

ALTER TABLE `organization`
add constraint `fk_organization__physical_address` FOREIGN KEY (`physical_address_id`) REFERENCES `address` (`id`);/

ALTER TABLE `organization`
add constraint `fk_organization__juridical_address` FOREIGN KEY (`juridical_address_id`) REFERENCES `address` (`id`);/

CALL organizationPhysicalCursor();/

CALL organizationJuridicalCursor();/

ALTER TABLE `organization`
drop column `physical_address_zip_code`,
drop column `physical_address_country`,
drop column `physical_address_region`,
drop column `physical_address_area`,
drop column `physical_address_city`,
drop column `physical_address_city_type`,
drop column `physical_address_street`,
drop column `physical_address_street_type`,
drop column `physical_address_building`,
drop column `physical_address_apartment`,
drop column `juridical_address_zip_code`,
drop column `juridical_address_country`,
drop column `juridical_address_region`,
drop column `juridical_address_area`,
drop column `juridical_address_city`,
drop column `juridical_address_city_type`,
drop column `juridical_address_street`,
drop column `juridical_address_street_type`,
drop column `juridical_address_building`,
drop column `juridical_address_apartment`;/

INSERT INTO `update` (`date`) VALUES ('2012-06-04');/

