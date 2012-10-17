ALTER TABLE `position`
  add column `payment_number` INT(7),
  add column `payment_type` VARCHAR (45);

INSERT INTO `update` (`date`) VALUES ('2012-10-15');