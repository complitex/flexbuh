package org.complitex.flexbuh.admin.importexport.entity;

import org.complitex.flexbuh.common.entity.IConfig;

/**
 * @author Pavel Sknar
 *         Date: 12.08.11 16:49
 */
public enum DictionaryConfig implements IConfig {
	IMPORT_FILE_STORAGE_DIR("/tmp/flexbuh");

    private String defaultValue;

    DictionaryConfig(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public String getGroupKey() {
        return "import";
    }
}
