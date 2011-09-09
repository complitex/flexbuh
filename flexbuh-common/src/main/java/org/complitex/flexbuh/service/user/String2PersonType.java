package org.complitex.flexbuh.service.user;

import org.complitex.flexbuh.entity.user.PersonType;

import javax.ejb.EJB;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Pavel Sknar
 *         Date: 07.09.11 19:21
 */
public class String2PersonType extends XmlAdapter<String,PersonType> {

	@EJB
	private PersonTypeBean personTypeBean;

    @Override
    public String marshal(PersonType personType) {
        return personType != null && personType.getCode() != null? Integer.toString(personType.getCode()): "";
    }

	@Override
    public PersonType unmarshal(String string ) {
		return personTypeBean.findByCode(string);
    }
}
