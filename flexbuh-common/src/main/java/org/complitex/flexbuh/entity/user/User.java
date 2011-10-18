package org.complitex.flexbuh.entity.user;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.entity.SessionObject;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:26
 */
@XmlRootElement(name = "ROWSET")
public class User extends SessionObject {
	private String login;
	private String password;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
