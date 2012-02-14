package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.FIOBean;
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
import java.util.Locale;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 14:52
 */
@Stateless
public class CounterpartBean extends AbstractBean{
    private final static Logger log = LoggerFactory.getLogger(CounterpartBean.class);
    
    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private FIOBean fioBean;

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

    public void save(Counterpart counterpart, Locale locale) {
        fioBean.createFIO(counterpart.getFirstName(), counterpart.getMiddleName(), counterpart.getLastName(), locale);

        if (counterpart.getId() == null){
            sqlSession().insert("insertCounterpart", counterpart);
        }else{
            sqlSession().update("updateCounterpart", counterpart);
        }
    }

    public void delete(Long id){
        sqlSession().delete("deleteCounterpart", id);
    }

    public int save(Long sessionId, InputStream inputStream, Locale locale) {
        try {
            CounterpartRowSet counterpartRowSet = (CounterpartRowSet) JAXBContext
                    .newInstance(CounterpartRowSet.class)
                    .createUnmarshaller()
                    .unmarshal(inputStream);
            
            Long personalProfileId = personProfileBean.getSelectedPersonProfileId(sessionId);

            if (counterpartRowSet != null && counterpartRowSet.getCounterparts() != null) {
                for (Counterpart counterpart : counterpartRowSet.getCounterparts()){
                    counterpart.setSessionId(sessionId);
                    counterpart.setPersonProfileId(personalProfileId);

                    save(counterpart, locale);
                }

                return counterpartRowSet.getCounterparts().size();
            }
        } catch (JAXBException e) {
            log.error("Ошибка импорта контрагентов", e);
        }

        return 0;
    }
}
