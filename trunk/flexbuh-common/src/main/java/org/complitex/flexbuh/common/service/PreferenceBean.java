package org.complitex.flexbuh.common.service;

import org.complitex.flexbuh.common.entity.Preference;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.04.12 14:47
 */
@Stateless
public class PreferenceBean extends AbstractBean{
    public final static String NS = PreferenceBean.class.getName();

    public Preference getPreference(Long sessionId, String key){
        return (Preference) sqlSession().selectOne(NS + ".selectPreference", new Preference(sessionId, key));
    }

    public List<Preference> getPreferences(Long sessionId){
        return sqlSession().selectList(NS + ".selectPreferences", sessionId);
    }

    public void save(Preference preference){
        if (preference.getId() != null){
            sqlSession().update(NS + ".updatePreference", preference);
        } else {
            sqlSession().insert(NS + ".insertPreference", preference);
        }
    }

    public void delete(Long id){
        sqlSession().delete(NS + ".deletePreference", id);
    }
}
