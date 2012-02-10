package org.complitex.flexbuh.common.entity;

import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.02.11 13:10
 */
public interface IConfig {
    public String name();

    public String getDefaultValue();

    public List<String> getAllowedValues();

    public String getGroupKey();
}
