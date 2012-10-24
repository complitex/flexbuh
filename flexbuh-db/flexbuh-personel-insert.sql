insert into `schedule` (`id`, `version`, `entry_into_force_date`, `name`,
`period_number_date`, `item_day_off`, `reg_work_time_unit`, `period_schedule`) value
(1, 1, now(), '5 дневная 40 часовая рабочая неделя с 9 до 18', 7, '6,7', 'DAY',
'9-13,14-18;9-13,14-18;9-13,14-18;9-13,14-18;9-13,14-18');

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
