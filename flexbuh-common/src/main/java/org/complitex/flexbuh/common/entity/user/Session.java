package org.complitex.flexbuh.common.entity.user;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.DomainObject;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:27
 */
public class Session extends DomainObject {
	private String cookie;

    private Date createDate;
    private Date lastAccessDate;

    public Session() {
        createDate = new Date();
        lastAccessDate = new Date();
    }

    public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastAccessDate() {
        return lastAccessDate;
    }

    public void setLastAccessDate(Date lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
