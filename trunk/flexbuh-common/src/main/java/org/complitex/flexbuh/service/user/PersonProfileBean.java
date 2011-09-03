package org.complitex.flexbuh.service.user;

import org.complitex.flexbuh.entity.user.PersonProfile;
import org.complitex.flexbuh.service.DomainObjectBean;

import javax.ejb.Stateless;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 18:43
 */
@Stateless
public class PersonProfileBean extends DomainObjectBean<PersonProfile> {
	public static final String NS = PersonProfileBean.class.getName();

	@Override
	protected String getNameSpace() {
		return NS;
	}

	@Override
	protected String getTable() {
		return PersonProfile.TABLE;
	}
}
