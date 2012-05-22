package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.IProcessListener;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.util.XmlUtil;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.entity.CounterpartRowSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 22.05.12 15:13
 */
@Stateless
public class CounterpartService {
    private final static Logger log = LoggerFactory.getLogger(CounterpartService.class);

    @EJB
    private CounterpartBean counterpartBean;

    public void save(Long sessionId, Long personProfileId, InputStream inputStream, IProcessListener<Counterpart> listener) {
        Counterpart counterpart = null;

        try {
            CounterpartRowSet counterpartRowSet = (CounterpartRowSet) JAXBContext
                    .newInstance(CounterpartRowSet.class)
                    .createUnmarshaller()
                    .unmarshal(inputStream);

            if (counterpartRowSet != null && counterpartRowSet.getCounterparts() != null) {
                for (Counterpart cp : counterpartRowSet.getCounterparts()){
                    counterpart = cp;

                    cp.setSessionId(sessionId);
                    cp.setPersonProfileId(personProfileId);

                    counterpartBean.save(cp);

                    log.info("Контрагент загружен", new Event(EventCategory.IMPORT, cp));

                    if (listener != null){
                        listener.onSuccess(cp);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка загрузки контрагентов", e);

            if (listener != null){
                listener.onError(counterpart, e);
            }else {
                throw new RuntimeException(e);
            }
        }
    }

    public InputStream getInputStream(Long sessionId){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            CounterpartRowSet counterpartRowSet = new CounterpartRowSet(counterpartBean.getAllCounterparts(sessionId), true);
            CounterpartRowSet logCounterpartRowSet = new CounterpartRowSet(counterpartBean.getAllCounterparts(sessionId));

            XmlUtil.writeXml(CounterpartRowSet.class, counterpartRowSet, outputStream, "windows-1251");

            log.info("Выгрузка контрагентов", new Event(EventCategory.EXPORT, logCounterpartRowSet));

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Ошибка выгрузки контрагентов");
        }

        return null;
    }
}
