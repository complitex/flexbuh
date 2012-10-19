alter table `allowance` add column `session_id` BIGINT(20);
alter table `allowance` add column `organization_id` BIGINT(20);
alter table `allowance` add CONSTRAINT `fk_allowance__organization` FOREIGN KEY (`organization_id`) REFERENCES `organization` (`id`);
alter table `allowance` add UNIQUE KEY `unique_key_allowance` (`id`, `version`);

alter table `position` add column `allowance_id` BIGINT(20);

ALTER TABLE `position` add CONSTRAINT `fk_position__allowance` FOREIGN KEY (`allowance_id`) REFERENCES `allowance` (`id`);

CREATE TABLE `allowance_type` (
  `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255),
  PRIMARY KEY (`id`)
)
ENGINE = InnoDB DEFAULT CHARSET=utf8;

insert into `allowance_type` (`id`, `name`) value (1, 'Надбавка за работу в районах Крайнего Севера и приравненных к ним местностях');
insert into `allowance_type` (`id`, `name`) value (2, 'Надбавка за вахтовый метод работы');
insert into `allowance_type` (`id`, `name`) value (3, 'Надбавка за классность');
insert into `allowance_type` (`id`, `name`) value (4, 'Надбавка за выслугу лет (стаж работы)');
insert into `allowance_type` (`id`, `name`) value (5, 'Надбавка за знание иностранного языка');
insert into `allowance_type` (`id`, `name`) value (6, 'Надбавка за профессиональное мастерство');
insert into `allowance_type` (`id`, `name`) value (7, 'Надбавка за высокие достижения в труде и высокийуровень квалификации');
insert into `allowance_type` (`id`, `name`) value (8, 'Надбавка за продолжительность непрерывной работы');
insert into `allowance_type` (`id`, `name`) value (9, 'Надбавка за ученую степень или звание');
insert into `allowance_type` (`id`, `name`) value (10, 'Доплата за работу в выходные и праздничные дни');
insert into `allowance_type` (`id`, `name`) value (11, 'Доплата за работу в ночное время');
insert into `allowance_type` (`id`, `name`) value (12, 'Доплата за сверхурочную работу, ненормированный рабочий день');
insert into `allowance_type` (`id`, `name`) value (13, 'Доплата при переводе на другую нижеоплачиваемую работу');
insert into `allowance_type` (`id`, `name`) value (14, 'Доплата за работу в тяжелых, вредных, опасных условиях труда');
insert into `allowance_type` (`id`, `name`) value (15, 'Доплата за работу в многосменном режиме');
insert into `allowance_type` (`id`, `name`) value (16, 'Доплата несовершеннолетним работникам в связи с сокращением их рабочего дня');
insert into `allowance_type` (`id`, `name`) value (17, 'Доплата при невыполнении норм выработкии изготовлении бракованной продукции не по вине работника');
insert into `allowance_type` (`id`, `name`) value (18, 'Доплата за совмещение профессий (должностей)');
insert into `allowance_type` (`id`, `name`) value (19, 'Доплата за расширение зон обслуживания или увеличение объема выполняемых работ');
insert into `allowance_type` (`id`, `name`) value (20, 'Доплата за выполнение обязанностей временно отсутствующего работника');

INSERT INTO `update` (`date`) VALUES ('2012-10-15');