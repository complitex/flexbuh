package org.complitex.flexbuh.common.entity;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 08.02.12 16:00
 */
public enum ApplicationConfig implements IConfig {

    SYSTEM_LOCALE("ru", Lists.newArrayList("uk", "ru"));

    private String defaultValue;
    private List<String> allowedValues;

    ApplicationConfig(String defaultValue, List<String> allowedValues) {
        this.defaultValue = defaultValue;
        this.allowedValues = allowedValues;
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public List<String> getAllowedValues() {
        return allowedValues;
    }

    @Override
    public String getGroupKey() {
        return "general";
    }
}
