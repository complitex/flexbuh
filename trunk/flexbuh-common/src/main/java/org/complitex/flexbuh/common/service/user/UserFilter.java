package org.complitex.flexbuh.common.service.user;

import com.google.common.collect.Lists;
import org.complitex.flexbuh.common.entity.AbstractFilter;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 19.12.11 12:21
 */
public class UserFilter extends AbstractFilter {

    private String login;

    private String firstName;
    private String middleName;
    private String lastName;

    private String address;

    private List<String> roles = Lists.newArrayList();

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRole() {
        return roles.size() > 0? roles.get(0): null;
    }

    public void setRole(String role) {
        roles.clear();
        roles.add(role);
    }

    public void appendRole(String role) {
        roles.add(role);
    }

    public List<String> getRoles() {
        return roles;
    }
}
