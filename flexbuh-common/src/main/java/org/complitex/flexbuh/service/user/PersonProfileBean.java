package org.complitex.flexbuh.service.user;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.user.PersonProfile;
import org.complitex.flexbuh.entity.user.Session;
import org.complitex.flexbuh.entity.user.SessionPersonProfile;
import org.complitex.flexbuh.service.DomainObjectBean;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 18:43
 */
@Stateless
public class PersonProfileBean extends DomainObjectBean<PersonProfile> {
	public static final String NS = PersonProfileBean.class.getName();

	@SuppressWarnings("unchecked")
    @Deprecated
    /**
     * @deprecated change to find by sessionId
     */
	public List<PersonProfile> findCompanyProfilesBySessionCookie(String cookie) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		params.put("session_table", Session.TABLE);
		params.put("session_person_profile_table", SessionPersonProfile.TABLE);
		params.put("cookie", cookie);
		return sqlSession().selectList(getNameSpace() + ".findCompanyProfilesBySessionCookie", params);
	}

	@SuppressWarnings("unchecked")
    @Deprecated
	public List<PersonProfile> findCompanyProfilesBySessionCookie(String cookie, int first, int count) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		params.put("session_table", Session.TABLE);
		params.put("session_person_profile_table", SessionPersonProfile.TABLE);
		params.put("cookie", cookie);
		params.put("first", first);
		params.put("count", count);
		return sqlSession().selectList(getNameSpace() + ".findCompanyProfilesBySessionCookieLimit", params);
	}

	@SuppressWarnings("unchecked")
    @Deprecated
	public int countCompanyProfilesBySessionCookie(String cookie) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		params.put("session_table", Session.TABLE);
		params.put("session_person_profile_table", SessionPersonProfile.TABLE);
		params.put("cookie", cookie);
		return (Integer)sqlSession().selectOne(getNameSpace() + ".findCompanyProfilesBySessionCookieSize", params);
	}

	@Override
	protected String getNameSpace() {
		return NS;
	}

	@Override
	protected String getTable() {
		return PersonProfile.TABLE;
	}
}
