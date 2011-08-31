package org.complitex.flexbuh.entity.user;

import org.apache.commons.lang.StringUtils;
import org.complitex.flexbuh.entity.DomainObject;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:26
 */
public class User extends DomainObject {
	private static final String TABLE = "user";

	private String login;
	private String password;
	private PersonProfile userProfile;
	private List<PersonProfile> companyProfiles;
	private Session session;

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

	public PersonProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(PersonProfile userProfile) {
		this.userProfile = userProfile;
	}

	public List<PersonProfile> getCompanyProfiles() {
		return companyProfiles;
	}

	public void setCompanyProfiles(List<PersonProfile> companyProfiles) {
		this.companyProfiles = companyProfiles;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	private boolean isAnonymous() {
		return session != null && userProfile != null && StringUtils.isEmpty(login);
	}

	@Override
	public String getTable() {
		return TABLE;
	}
}
