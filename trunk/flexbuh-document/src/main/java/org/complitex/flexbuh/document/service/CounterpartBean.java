package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 14:52
 */
@Stateless
public class CounterpartBean extends AbstractBean{
    private final static Logger log = LoggerFactory.getLogger(CounterpartBean.class);
    
    @EJB
    private PersonProfileBean personProfileBean;

    public Counterpart getCounterpart(Long id){
        return (Counterpart) sqlSession().selectOne("selectCounterpart", id);
    }

    public List<Counterpart>  getCounterparts(FilterWrapper<Counterpart> filter){
        return sqlSession().selectList("selectCounterparts", filter);
    }

    public Integer getCounterpartCount(FilterWrapper<Counterpart> filter){
        return (Integer) sqlSession().selectOne("selectCounterpartCount", filter);
    }

    /**
     * @param sessionId Идентификатор сессии
     * @return Список всех контрагентов в данной сессии
     */
    public List<Counterpart> getAllCounterparts(Long sessionId){
        return sqlSession().selectList("selectAllCounterparts", sessionId);
    }

    public void save(Counterpart counterpart) {
        if (counterpart.getId() == null){
            sqlSession().insert("insertCounterpart", counterpart);
        }else{
            sqlSession().update("updateCounterpart", counterpart);
        }
    }

    public void delete(Long id){
        sqlSession().delete("deleteCounterpart", id);
    }
}
