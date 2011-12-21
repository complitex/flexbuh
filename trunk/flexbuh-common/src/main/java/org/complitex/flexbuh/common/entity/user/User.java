package org.complitex.flexbuh.common.entity.user;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.entity.DomainObject;
import org.complitex.flexbuh.common.entity.SessionObject;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 14:26
 */
public class User extends DomainObject {
	private String login;
	private String password;
    private Session session;
    private String firstName;
    private String middleName;
    private String lastName;
    private Date birthday;
    private String email;
    private String phone;
    private String zipCode;
    private String address;
    private String organizationName;
    private String organizationPhone;
    private String organizationZipCode;
    private String organizationAddress;

    private List<String> roles;

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

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationPhone() {
        return organizationPhone;
    }

    public void setOrganizationPhone(String organizationPhone) {
        this.organizationPhone = organizationPhone;
    }

    public String getOrganizationZipCode() {
        return organizationZipCode;
    }

    public void setOrganizationZipCode(String organizationZipCode) {
        this.organizationZipCode = organizationZipCode;
    }

    public String getOrganizationAddress() {
        return organizationAddress;
    }

    public void setOrganizationAddress(String organizationAddress) {
        this.organizationAddress = organizationAddress;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
