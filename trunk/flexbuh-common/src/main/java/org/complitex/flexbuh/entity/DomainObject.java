package org.complitex.flexbuh.entity;

import java.io.Serializable;

/**
 * @author Pavel Sknar
 *         Date: 08.08.11 16:43
 */
public abstract class DomainObject implements Serializable {
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public abstract String getTable();
}
