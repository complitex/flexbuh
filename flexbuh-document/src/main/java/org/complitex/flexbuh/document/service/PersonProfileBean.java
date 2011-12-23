package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.AbstractFilter;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.document.entity.PersonProfile;

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
        return (PersonProfile) sqlSession().selectOne(NS + ".selectPersonProfile", id);
    }

    @SuppressWarnings("unchecked")
    public List<PersonProfile> getAllPersonProfiles(Long sessionId){
        return sqlSession().selectList(NS + ".selectAllPersonProfiles", sessionId);
    }

    @SuppressWarnings({"unchecked"})
    public List<PersonProfile> getPersonProfiles(Long sessionId, int first, int count){
        return sqlSession().selectList(NS + ".selectPersonProfiles", new AbstractFilter(sessionId, first, count));
    }

    public int getPersonalProfileCount(Long sessionId){
        return (Integer) sqlSession().selectOne(NS + ".selectPersonProfilesCount", sessionId);
    }

    public void save(PersonProfile personProfile){
        if (personProfile.getId() == null) {
            sqlSession().insert(NS + ".insertPersonProfile", personProfile);
        }else {
            sqlSession().insert(NS + ".updatePersonProfile", personProfile);
        }
    }
    
    public void delete(Long personProfileId){
        sqlSession().delete("deletePersonProfile", personProfileId);
    }
}
