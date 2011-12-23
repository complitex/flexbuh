package org.complitex.flexbuh.document.entity;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.12.11 18:12
 */
public class PersonTypeAdapter extends XmlAdapter<Integer, PersonType>{
    @Override
    public PersonType unmarshal(Integer v) throws Exception {
        for (PersonType pt : PersonType.values()){
            if (pt.getCode() == v){
                return pt;
            }
        }

        return null;
    }

    @Override
    public Integer marshal(PersonType v) throws Exception {
        return v.getCode();
    }
}
