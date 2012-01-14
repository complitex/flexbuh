package org.complitex.flexbuh.common.entity.user;

import com.google.common.collect.Lists;
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
    private Long sessionId;
    private String firstName;
    private String middleName;
    private String lastName;
    private Date birthday;
    private String email;
    private String phone;
    private String zipCode;
    private String country;
    private String region;
    private String area;
    private String city;
    private String cityType;
    private String street;
    private String streetType;
    private String building;
    private String apartment;
    private String organizationName;
    private String organizationPhone;
    private String organizationDepartment;
    private String organizationPost;
    private String organizationZipCode;
    private String organizationCountry;
    private String organizationRegion;
    private String organizationArea;
    private String organizationCity;
    private String organizationCityType;
    private String organizationStreet;
    private String organizationStreetType;
    private String organizationBuilding;
    private String organizationApartment;

    private List<String> roles = Lists.newArrayList();

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

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCityType() {
        return cityType;
    }

    public void setCityType(String cityType) {
        this.cityType = cityType;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetType() {
        return streetType;
    }

    public void setStreetType(String streetType) {
        this.streetType = streetType;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
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

    public String getOrganizationDepartment() {
        return organizationDepartment;
    }

    public void setOrganizationDepartment(String organizationDepartment) {
        this.organizationDepartment = organizationDepartment;
    }

    public String getOrganizationPost() {
        return organizationPost;
    }

    public void setOrganizationPost(String organizationPost) {
        this.organizationPost = organizationPost;
    }

    public String getOrganizationZipCode() {
        return organizationZipCode;
    }

    public void setOrganizationZipCode(String organizationZipCode) {
        this.organizationZipCode = organizationZipCode;
    }

    public String getOrganizationCountry() {
        return organizationCountry;
    }

    public void setOrganizationCountry(String organizationCountry) {
        this.organizationCountry = organizationCountry;
    }

    public String getOrganizationRegion() {
        return organizationRegion;
    }

    public void setOrganizationRegion(String organizationRegion) {
        this.organizationRegion = organizationRegion;
    }

    public String getOrganizationArea() {
        return organizationArea;
    }

    public void setOrganizationArea(String organizationArea) {
        this.organizationArea = organizationArea;
    }

    public String getOrganizationCity() {
        return organizationCity;
    }

    public void setOrganizationCity(String organizationCity) {
        this.organizationCity = organizationCity;
    }

    public String getOrganizationCityType() {
        return organizationCityType;
    }

    public void setOrganizationCityType(String organizationCityType) {
        this.organizationCityType = organizationCityType;
    }

    public String getOrganizationStreet() {
        return organizationStreet;
    }

    public void setOrganizationStreet(String organizationStreet) {
        this.organizationStreet = organizationStreet;
    }

    public String getOrganizationStreetType() {
        return organizationStreetType;
    }

    public void setOrganizationStreetType(String organizationStreetType) {
        this.organizationStreetType = organizationStreetType;
    }

    public String getOrganizationBuilding() {
        return organizationBuilding;
    }

    public void setOrganizationBuilding(String organizationBuilding) {
        this.organizationBuilding = organizationBuilding;
    }

    public String getOrganizationApartment() {
        return organizationApartment;
    }

    public void setOrganizationApartment(String organizationApartment) {
        this.organizationApartment = organizationApartment;
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
