package org.complitex.flexbuh.service.user;

import org.complitex.flexbuh.entity.AbstractFilter;
import org.complitex.flexbuh.entity.user.PersonProfile;
import org.complitex.flexbuh.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 18:43
 */
@Stateless
public class PersonProfileBean extends AbstractBean {
	public static final String NS = PersonProfileBean.class.getName();

    public PersonProfile getPersonProfile(Long id){
        return (PersonProfile) sqlSession().selectOne(NS + "selectPersonProfile", id);
    }

    @SuppressWarnings({"unchecked"})
    public List<PersonProfile> getPersonProfiles(Long sessionId, int first, int count){
        return sqlSession().selectList(NS + ".selectPersonProfiles", new AbstractFilter(sessionId, first, count));
    }

    public Integer getPersonalProfileCount(Long sessionId){
        return (Integer) sqlSession().selectOne(NS + ".selectPersonProfilesCount");
    }

    public void save(PersonProfile personProfile){
        sqlSession().insert(NS + ".insertPersonProfile");
    }
}
