package org.complitex.flexbuh.common.entity;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.complitex.flexbuh.common.util.StringUtil;

import java.io.Serializable;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 27.02.12 11:51
 */
public class Address implements Serializable {
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

    public String toWebView() {

        return addField(", ", zipCode, country, region, area, addField(" ", cityType, city),
                addField(" ", streetType, street), building, apartment);
    }

    private String addField(String delimiter, String... args) {
        List<String> fields = Lists.newArrayList();

        for (String arg : args) {
            if (StringUtils.isNotEmpty(arg)) {
                fields.add(arg);
            }
        }

        return StringUtils.join(fields, delimiter);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
