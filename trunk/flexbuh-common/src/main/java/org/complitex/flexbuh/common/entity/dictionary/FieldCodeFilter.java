package org.complitex.flexbuh.common.entity.dictionary;

import org.complitex.flexbuh.common.entity.AbstractFilter;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.11.11 17:13
 */
public class FieldCodeFilter extends AbstractFilter{
    private String code;

    private String name;

    private String sprName;

    public FieldCodeFilter() {
    }

    public FieldCodeFilter(int first, int count) {
        super(first, count);
    }

    public FieldCodeFilter(String code, String name, String sprName) {
        this.code = code;
        this.name = name;
        this.sprName = sprName;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSprName() {
        return sprName;
    }

    public void setSprName(String sprName) {
        this.sprName = sprName;
    }
}
