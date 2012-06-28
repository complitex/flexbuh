insert into `usergroup` (`login`, `group_name`) select distinct `login`, 'AUTHORIZED' from `user` where `login` != 'ANONYMOUS';

INSERT INTO `update` (`date`) VALUES ('2012-06-28');
