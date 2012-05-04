alter table `update` alter column `date_version` set default 0;

alter table `counterpart` add column `hname` VARCHAR(255) NOT NULL;

update `counterpart` set `hname` = concat_ws(' ', `last_name`, `first_name`, `middle_name`);

alter table `counterpart` drop column `first_name`;
alter table `counterpart` drop column `last_name`;
alter table `counterpart` drop column `middle_name`;

INSERT INTO `update` (`date`) VALUES ('2012-04-27');
