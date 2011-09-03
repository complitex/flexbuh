package org.complitex.flexbuh.service.user;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.user.PersonProfile;
import org.complitex.flexbuh.entity.user.Session;
import org.complitex.flexbuh.entity.user.User;
import org.complitex.flexbuh.service.DomainObjectBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 01.09.11 14:56
 */
@Stateless
public class UserBean extends DomainObjectBean<User> {
	public static final String NS = UserBean.class.getName();
	private static final String SESSION_PERSON_PROFILE_TABLE = "session_person_profile";

	@EJB
	private PersonProfileBean personProfileBean;

	@Override
	public User read(long id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("table", getTable());
		params.put("session_table", Session.TABLE);
		params.put("person_profile_table", PersonProfile.TABLE);
		params.put("session_person_profile_table", SESSION_PERSON_PROFILE_TABLE);
		return (User) sqlSession().selectOne(getNameSpace() + ".findById", params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		params.put("session_table", Session.TABLE);
		params.put("person_profile_table", PersonProfile.TABLE);
		params.put("session_person_profile_table", SESSION_PERSON_PROFILE_TABLE);
		return sqlSession().selectList(getNameSpace() + ".readAll", params);
	}

	public void createCompanyProfile(Session session, PersonProfile newCompanyProfile) {

		personProfileBean.create(newCompanyProfile);

		Map<String, Object> params = Maps.newHashMap();
		params.put("sessionId", session.getId());
		params.put("personProfileId", newCompanyProfile.getId());
		params.put("session_person_profile_table", SESSION_PERSON_PROFILE_TABLE);
		sqlSession().insert(getNameSpace() + ".createCompanyProfile", params);
	}

	@Override
	protected String getNameSpace() {
		return NS;
	}

	@Override
	protected String getTable() {
		return User.TABLE;
	}
}