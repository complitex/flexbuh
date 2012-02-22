package org.complitex.flexbuh.common.entity;

import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;

/**
 * @author Pavel Sknar
 *         Date: 08.08.11 16:43
 */
public abstract class DomainObject implements Serializable {
	private Long id;

	@XmlTransient
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DomainObject that = (DomainObject) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
