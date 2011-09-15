package org.complitex.flexbuh.entity.template;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:02
 */
public abstract class AbstractTemplate implements Serializable {
    private Long id;
    private String name;
    private String data;
	private Date uploadDate;

    protected AbstractTemplate() {
    }

    protected AbstractTemplate(String name, String data, Date uploadDate) {
        this.name = name;
        this.data = data;
		this.uploadDate = uploadDate;
    }

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

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}
}
