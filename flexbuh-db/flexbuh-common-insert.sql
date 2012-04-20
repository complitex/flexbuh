-- ------------------------------
-- Update
-- ------------------------------

INSERT INTO `update` (`date`, `date_version`) VALUES ('2012-04-20', 0);

-- --------------------------------
-- User
-- --------------------------------

INSERT INTO `user` (`id`, `login`, `password`, `first_name`, `middle_name`, `last_name`, `email`) VALUE (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', '', '', '', '');
INSERT INTO `user` (`id`, `login`, `password`, `first_name`, `middle_name`, `last_name`, `email`)  VALUE (2, 'ANONYMOUS', 'ANONYMOUS', '', '', '', '');
INSERT INTO `usergroup` (`id`, `login`, `group_name`) VALUE (1, 'admin', 'ADMINISTRATORS');

-- --------------------------------
-- Config
-- --------------------------------
insert into `config` (`name`, `value`) values ('IMPORT_FILE_STORAGE_DIR', '/tmp/flexbuh');
insert into `config` (`name`, `value`) values ('SYSTEM_LOCALE', 'ru');

-- --------------------------------
-- Street type
-- --------------------------------
insert into `street_type` (`name_uk`, `name_ru`) values ('вулиця', 'улица');

-- --------------------------------
-- City type
-- --------------------------------
insert into `city_type` (`name_uk`, `name_ru`) values ('місто', 'город');
