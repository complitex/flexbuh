package org.complitex.flexbuh.service.user;

import org.complitex.flexbuh.entity.user.PersonType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * @author Pavel Sknar
 *         Date: 07.09.11 19:21
 */
public class String2PersonType extends XmlAdapter<String,PersonType> {

	private final static Logger log = LoggerFactory.getLogger(String2PersonType.class);

	@EJB
	private PersonTypeBean personTypeBean;

    @Override
    public String marshal(PersonType personType) {
        return personType != null && personType.getCode() != null? Integer.toString(personType.getCode()): "";
    }

	@Override
    public PersonType unmarshal(String string ) {
		PersonType personType = personTypeBean.findByCode(string);
		log.debug("unmarshal persontype {} by code '{}'", personType, string);
		return personType;
	}
}
