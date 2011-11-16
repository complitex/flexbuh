package org.complitex.flexbuh.common.entity;

import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 05.08.11 11:47
 */
public abstract class LocalizedDomainObject extends DomainObject {
    private String nameUk;
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
