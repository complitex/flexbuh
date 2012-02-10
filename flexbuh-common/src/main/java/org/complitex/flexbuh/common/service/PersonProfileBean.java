package org.complitex.flexbuh.common.service;

import org.complitex.flexbuh.common.entity.AbstractFilter;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.service.user.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 18:43
 */
@Stateless
public class PersonProfileBean extends AbstractBean {
    private final static Logger log = LoggerFactory.getLogger(PersonProfileBean.class);

    public static final String NS = PersonProfileBean.class.getName();

    @EJB
    private FIOBean fioBean;

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

    public void save(PersonProfile personProfile, Locale locale){

        fioBean.createFIO(personProfile.getFirstName(), personProfile.getMiddleName(), personProfile.getLastName(), locale);
        fioBean.createFIO(personProfile.getBFirstName(), personProfile.getBMiddleName(), personProfile.getBLastName(), locale);
        fioBean.createFIO(personProfile.getDFirstName(), personProfile.getDMiddleName(), personProfile.getDLastName(), locale);

        if (personProfile.getId() == null) {
            sqlSession().insert(NS + ".insertPersonProfile", personProfile);
        }else {
            sqlSession().insert(NS + ".updatePersonProfile", personProfile);
        }
    }
    
    public void delete(Long personProfileId){
        sqlSession().delete("deletePersonProfile", personProfileId);
    }
    
    public PersonProfile getSelectedPersonProfile(Long sessionId){
        return (PersonProfile) sqlSession().selectOne("selectSelectedPersonProfile", sessionId);
    }

    public Long getSelectedPersonProfileId(Long sessionId){
        return (Long) sqlSession().selectOne("selectSelectedPersonProfileId", sessionId);
    }
    
    public void deselectAllPersonProfile(Long sessionId){
        sqlSession().update("deselectAllPersonProfile", sessionId);
    } 

    public void setSelectedPersonProfile(PersonProfile personProfile){
        deselectAllPersonProfile(personProfile.getSessionId());

        personProfile.setSelected(true);

        sqlSession().insert(NS + ".updatePersonProfile", personProfile);
    }
}
