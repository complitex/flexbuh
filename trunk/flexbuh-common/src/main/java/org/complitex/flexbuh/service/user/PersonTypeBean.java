package org.complitex.flexbuh.service.user;

import org.complitex.flexbuh.entity.user.PersonType;
import org.complitex.flexbuh.service.AbstractBean;

import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 *         Date: 01.09.11 13:45
 */
@Stateless
public class PersonTypeBean extends AbstractBean {
	public static final String NS = PersonTypeBean.class.getName();

	public PersonType findByCode(String code) {
		return (PersonType)sqlSession().selectOne(NS + ".findByCode", code);
	}
}
