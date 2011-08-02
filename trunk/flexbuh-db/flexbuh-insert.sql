-- --------------------------------
-- Users
-- --------------------------------
INSERT INTO USER (`id`, `login`, `password`) VALUE (1, 'admin', '21232f297a57a5a743894a0e4a801fc3');
INSERT INTO usergroup (`id`, `login`, `group_name`) VALUE (1, 'admin', 'ADMINISTRATORS');
INSERT INTO USER (`id`, `login`, `password`)  VALUE (2, 'ANONYMOUS', 'ANONYMOUS');