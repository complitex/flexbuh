package org.complitex.flexbuh.service.user;

import com.google.common.collect.Maps;
import org.apache.commons.lang.NotImplementedException;
import org.complitex.flexbuh.entity.user.PersonProfile;
import org.complitex.flexbuh.entity.user.Session;
import org.complitex.flexbuh.service.DomainObjectBean;

import javax.ejb.Stateless;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 01.09.11 15:49
 */
@Stateless
public class SessionBean extends DomainObjectBean<Session> {
	public static final String NS = SessionBean.class.getName();

	public Session getSessionByCookie(String cookie) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		params.put("cookie", cookie);
		return (Session)sqlSession().selectOne(getNameSpace() + ".findByCookie", params);
	}

	public Session getSessionByPersonalProfile(PersonProfile personProfile) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("personalProfileId", personProfile.getId());
		params.put("table", getTable());
		return  (Session)sqlSession().selectOne(getNameSpace() + ".findByPersonProfile", params);
	}

	@Override
	public void delete(Session o) {
		throw new NotImplementedException("Delete with profile");
	}

	@Override
	public void create(Session o) {
		create(o, null);
	}

	public void create(Session o, PersonProfile attachPersonProfile) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("cookie", o.getCookie());
		if (attachPersonProfile != null) {
			params.put("personProfileId", attachPersonProfile.getId());
		} else {
			params.put("personProfileId", null);
		}
		params.put("table", getTable());
		sqlSession().insert(getNameSpace() + ".create", params);
		o.setId((Long)params.get("id"));
	}

	@Override
	protected String getNameSpace() {
		return NS;
	}

	@Override
	protected String getTable() {
		return Session.TABLE;
	}
}
