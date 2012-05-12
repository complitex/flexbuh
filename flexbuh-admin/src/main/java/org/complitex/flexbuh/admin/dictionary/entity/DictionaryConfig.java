package org.complitex.flexbuh.admin.dictionary.entity;

import org.complitex.flexbuh.common.entity.IConfig;

import java.util.Collections;
import java.util.List;

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
    public List<String> getAllowedValues() {
        return Collections.emptyList();
    }

    @Override
    public String getGroupKey() {
        return "import";
    }
}
