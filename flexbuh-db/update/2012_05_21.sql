ALTER TABLE `logging_event`
  ADD KEY `key_timestmp` (`timestmp`),
  ADD  KEY `key_logger_name` (`logger_name`),
  ADD KEY `key_level_string` (`level_string`);

INSERT INTO `update` (`date`) VALUES ('2012-05-21');