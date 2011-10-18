-- --------------------------------
-- Config
-- --------------------------------
insert into `config` (`name`, `value`) values ('IMPORT_FILE_STORAGE_DIR', '/tmp/flexbuh');

--  -- --------------------------------
-- DictionaryType
-- --------------------------------
INSERT INTO dictionary_type (`id`, `code`, `file_name`, `name_uk`, `name_ru`)
  VALUES (1, 'currency', 'SPR_CURRENCY.XML', 'Довідник валют', 'Справочник валют');
INSERT INTO dictionary_type (`id`, `code`, `file_name`, `name_uk`, `name_ru`)
  VALUES (2, 'document', 'SPR_DOC.XML', 'Довідник документів', 'Справочник отчетных документов');
INSERT INTO dictionary_type (`id`, `code`, `file_name`, `name_uk`, `name_ru`)
  VALUES (3, 'document_term', 'SPR_TERM.XML', 'Довідник звітних періодів документів', 'Справочник отчетных периодов документов');
INSERT INTO dictionary_type (`id`, `code`, `file_name`, `name_uk`, `name_ru`)
  VALUES (4, 'document_version', 'SPR_VER.XML', 'Довідник версій звітних документів', 'Справочник версий отчетных документов');
INSERT INTO dictionary_type (`id`, `code`, `file_name`, `name_uk`, `name_ru`)
  VALUES (5, 'region', 'SPR_REGION.XML', 'Довідник регіонів', 'Справочник регионов');
INSERT INTO dictionary_type (`id`, `code`, `file_name`, `name_uk`, `name_ru`)
  VALUES (6, 'tax_inspection', 'SPR_STI.XML', 'Довідник податкових інспекцій', 'Справочник налоговых инспекций');