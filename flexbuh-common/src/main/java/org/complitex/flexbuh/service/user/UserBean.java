package org.complitex.flexbuh.service.user;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.user.PersonProfile;
import org.complitex.flexbuh.entity.user.Session;
import org.complitex.flexbuh.entity.user.SessionPersonProfile;
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

	@EJB
	private PersonProfileBean personProfileBean;

	@EJB
	private SessionBean sessionBean;

	@Override
	public User read(long id) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("id", id);
		params.put("table", getTable());
		params.put("session_table", Session.TABLE);
		params.put("person_profile_table", PersonProfile.TABLE);
		params.put("session_person_profile_table", SessionPersonProfile.TABLE);
		return (User) sqlSession().selectOne(getNameSpace() + ".findById", params);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<User> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		params.put("session_table", Session.TABLE);
		params.put("person_profile_table", PersonProfile.TABLE);
		params.put("session_person_profile_table", SessionPersonProfile.TABLE);
		return sqlSession().selectList(getNameSpace() + ".readAll", params);
	}

	public void createCompanyProfile(Long sessionId, PersonProfile newCompanyProfile) {

		personProfileBean.create(newCompanyProfile);

		Map<String, Object> params = Maps.newHashMap();
		params.put("sessionId", sessionId);
		params.put("personProfileId", newCompanyProfile.getId());
		params.put("session_person_profile_table", SessionPersonProfile.TABLE);
		sqlSession().insert(getNameSpace() + ".createCompanyProfile", params);
	}

	public User getUserByCookie(String cookie) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("cookie", cookie);
		params.put("table", getTable());
		params.put("session_table", Session.TABLE);
		params.put("person_profile_table", PersonProfile.TABLE);
		params.put("session_person_profile_table", SessionPersonProfile.TABLE);

		User user = (User)sqlSession().selectOne(getNameSpace() + ".findBySessionCookie", params);

		System.out.println("find user: " + user);
		if (user == null && sessionBean.getSessionByCookie(cookie) != null) {
			System.out.println("user not found. Create anonymous");
			user = new User();
			user.setCompanyProfiles(personProfileBean.findCompanyProfilesBySessionCookie(cookie));
		}

		return user;
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