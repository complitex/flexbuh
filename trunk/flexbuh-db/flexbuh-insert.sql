-- --------------------------------
-- Config
-- --------------------------------
insert into `config` (`name`, `value`) values ('IMPORT_FILE_STORAGE_DIR', '/tmp/flexbuh');

-- --------------------------------
-- Language
-- --------------------------------
INSERT INTO `language` (`id`, `lang_iso_code`, `default_lang`) VALUES (1, 'uk', 1);
INSERT INTO `language` (`id`, `lang_iso_code`, `default_lang`) VALUES (2, 'ru', 0);

--  -- --------------------------------
-- DictionaryType
-- --------------------------------
INSERT INTO dictionary_type (`id`, `code`) VALUES (1, 'currency');
INSERT INTO dictionary_type (`id`, `code`) VALUES (2, 'document');
INSERT INTO dictionary_type (`id`, `code`) VALUES (3, 'document_term');
INSERT INTO dictionary_type (`id`, `code`) VALUES (4, 'document_version');
INSERT INTO dictionary_type (`id`, `code`) VALUES (5, 'region');
INSERT INTO dictionary_type (`id`, `code`) VALUES (6, 'tax_inspection');

INSERT INTO dictionary_file_name (`id`, `dictionary_type_id`, `file_name`) VALUES (1, 1, 'SPR_CURRENCY.XML');
INSERT INTO dictionary_file_name (`id`, `dictionary_type_id`, `file_name`) VALUES (2, 2, 'SPR_DOC.XML');
INSERT INTO dictionary_file_name (`id`, `dictionary_type_id`, `file_name`) VALUES (3, 3, 'SPR_TERM.XML');
INSERT INTO dictionary_file_name (`id`, `dictionary_type_id`, `file_name`) VALUES (4, 4, 'SPR_VER.XML');
INSERT INTO dictionary_file_name (`id`, `dictionary_type_id`, `file_name`) VALUES (5, 5, 'SPR_REGION.XML');
INSERT INTO dictionary_file_name (`id`, `dictionary_type_id`, `file_name`) VALUES (6, 6, 'SPR_STI.XML');

INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (1, 1, 1, 'Довідник валют');
INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (2, 2, 1, 'Справочник валют');
INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (3, 1, 2, 'Довідник документів');
INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (4, 2, 2, 'Справочник валют');
INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (5, 1, 3, 'Довідник звітних періодів документів');
INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (6, 2, 3, 'Справочник отчетных периодов документов');
INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (7, 1, 4, 'Довідник версій звітних документів');
INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (8, 2, 4, 'Справочник версий отчетных документов');
INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (9, 1, 5, 'Довідник регіонів');
INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (10, 2, 5, 'Справочник регионов');
INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (11, 1, 6, 'Довідник податкових інспекцій');
INSERT INTO dictionary_type_name (`id`, `language_id`, `dictionary_type_id`, `value`) VALUES (12, 2, 6, 'Справочник налоговых инспекций');

INSERT INTO person_type (`id`, `code`) VALUES (1, '1'),(2, '2'),(3, '3'),(4, '4');
