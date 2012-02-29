package org.complitex.flexbuh.common.entity.user;

import com.google.common.collect.Lists;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.Address;
import org.complitex.flexbuh.common.entity.DomainObject;
import org.complitex.flexbuh.common.entity.organization.OrganizationBase;

import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:26
 */
public class User extends DomainObject {
	private String login;
	private String password;
    private Long sessionId;
    private String firstName;
    private String middleName;
    private String lastName;
    private Date birthday;
    private String email;
    private String phone;

    private Address address;

    private List<String> roles = Lists.newArrayList();
    private List<OrganizationBase> organizations = Lists.newArrayList();

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

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public List<OrganizationBase> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(List<OrganizationBase> organizations) {
        this.organizations = organizations;
    }

    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
