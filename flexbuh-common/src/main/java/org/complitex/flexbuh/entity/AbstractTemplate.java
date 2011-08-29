package org.complitex.flexbuh.entity;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:02
 */
public abstract class AbstractTemplate {
    private Long id;
    private String name;
    private String data;

    protected AbstractTemplate() {
    }

    protected AbstractTemplate(String name, String data) {
        this.name = name;
        this.data = data;
    }

    public abstract String getTable();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
