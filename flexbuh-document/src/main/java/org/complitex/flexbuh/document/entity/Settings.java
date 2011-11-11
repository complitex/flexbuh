package org.complitex.flexbuh.document.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 11.11.11 14:47
 */
@XmlRootElement(name = "ROWSET")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Settings {
    @XmlElement(name = "ROW")
    private List<PersonProfile> personProfiles;

    public Settings() {
    }

    public Settings(List<PersonProfile> personProfiles) {
        this.personProfiles = personProfiles;
    }

    public List<PersonProfile> getPersonProfiles() {
        return personProfiles;
    }

    public void setPersonProfiles(List<PersonProfile> personProfiles) {
        this.personProfiles = personProfiles;
    }
}
