DROP PROCEDURE IF EXISTS userAddressCursor;/
CREATE PROCEDURE userAddressCursor()
BEGIN
  DECLARE flag TINYINT DEFAULT 0;
  DECLARE userId BIGINT(20);
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
  DECLARE userCursor CURSOR
        FOR SELECT `id`, `zip_code`, `country`, `region`, `area`,
        `city`, `city_type`, `street`, `street_type`,
        `building`, `apartment` from `user`;
  DECLARE CONTINUE HANDLER FOR NOT FOUND SET flag=1;
  SET flag=0;
  OPEN userCursor;
          WHILE (NOT flag) DO
            FETCH userCursor INTO userId, zipCodeVar, countryVar, regionVar, areaVar, cityVar,
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
                UPDATE `user` SET `address_id`=@address_id WHERE `id`=userId;
				SET flag=0;
              END IF;
          END WHILE;
  CLOSE userCursor;
END;/
-- ALTER TABLE `user` DROP FOREIGN KEY `fk_user__address`;/

-- ALTER TABLE `user` drop column `address_id`;/

ALTER TABLE `user`
add column `address_id` BIGINT (20) COMMENT 'Идентификатор адреса пользователя';/

ALTER TABLE `user`
add constraint `fk_user__address` FOREIGN KEY (`address_id`) REFERENCES `address` (`id`);/

CALL userAddressCursor();/

ALTER TABLE `user`
drop column `zip_code`,
drop column `country`,
drop column `region`,
drop column `area`,
drop column `city`,
drop column `city_type`,
drop column `street`,
drop column `street_type`,
drop column `building`,
drop column `apartment`;/

INSERT INTO `update` (`date`) VALUES ('2012-06-08');/
