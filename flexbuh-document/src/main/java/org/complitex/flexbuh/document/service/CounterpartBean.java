package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.entity.CounterpartFilter;
import org.complitex.flexbuh.document.entity.CounterpartRowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
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

    @SuppressWarnings("unchecked")
    public List<Counterpart>  getCounterparts(CounterpartFilter filter){
        return sqlSession().selectList("selectCounterparts", filter);
    }

    public Integer getCounterpartCount(CounterpartFilter filter){
        return (Integer) sqlSession().selectOne("selectCounterpartCount", filter);
    }

    public void save(Counterpart counterpart){
        if (counterpart.getId() == null){
            sqlSession().insert("insertCounterpart", counterpart);
        }else{
            sqlSession().update("updateCounterpart", counterpart);
        }
    }

    public void delete(Long id){
        sqlSession().delete("deleteCounterpart", id);
    }

    public void save(Long sessionId, InputStream inputStream) {
        try {
            CounterpartRowSet counterpartRowSet = (CounterpartRowSet) JAXBContext
                    .newInstance(CounterpartRowSet.class)
                    .createUnmarshaller()
                    .unmarshal(inputStream);
            
            Long personalProfileId = personProfileBean.getSelectedPersonProfileId(sessionId);

            for (Counterpart counterpart : counterpartRowSet.getCounterparts()){
                counterpart.setSessionId(sessionId);
                counterpart.setPersonProfileId(personalProfileId);

                save(counterpart);
            }
        } catch (JAXBException e) {
            log.error("Ошибка импорта контрагентов", e);
        }
    }
}
