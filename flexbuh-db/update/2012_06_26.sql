ALTER TABLE `organization_type`
add column `name` VARCHAR(255),
drop column `name_uk`,
drop column `name_ru`;

INSERT INTO `update` (`date`) VALUES ('2012-06-26');