package org.complitex.flexbuh.common.entity;

import org.complitex.flexbuh.common.annotation.Display;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 11:47
 */
@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public abstract class LocalizedDomainObject extends DomainObject {
    @NotNull
    @XmlElement(name = "NAME")
    private String nameUk;

    @Display(visible = false)
    private String nameRu;

    public String getName(Locale locale){
        switch (locale.getLanguage()){
            case "ru":
                return nameRu != null ? nameRu : nameUk;
            default:
                return nameUk;
        }
    }

    public String getNameUk() {
        return nameUk;
    }

    public void setNameUk(String nameUk) {
        this.nameUk = nameUk;
    }

    public String getNameRu() {
        return nameRu;
    }

    public void setNameRu(String nameRu) {
        this.nameRu = nameRu;
    }
}
