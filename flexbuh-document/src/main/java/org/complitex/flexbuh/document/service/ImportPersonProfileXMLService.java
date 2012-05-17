package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.PersonType;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.service.ImportListener;
import org.complitex.flexbuh.common.service.ImportXMLService;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import java.io.InputStream;

/**
 * @author Pavel Sknar
 *         Date: 10.09.11 11:51
 */
@Stateless
public class ImportPersonProfileXMLService extends ImportXMLService<PersonProfile> {
    private final static Logger log = LoggerFactory.getLogger(ImportPersonProfileXMLService.class);

    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private ConfigBean configBean;

    public void process(Long sessionId, ImportListener<PersonProfile> listener, String name, InputStream inputStream) {
        listener.begin();

        try {
            PersonProfile.RS rs = (PersonProfile.RS) JAXBContext.newInstance(PersonProfile.RS.class)
                    .createUnmarshaller()
                    .unmarshal(inputStream);

            for (PersonProfile personProfile : rs.getRows()) {
                personProfile.setSessionId(sessionId);

                if (personProfile.getProfileName() == null) {
                    personProfile.setProfileName(personProfile.getName());
                }

                if (PersonType.PHYSICAL_PERSON.equals(personProfile.getPersonType())){
                    personProfile.parsePhysicalNames();
                }

                personProfileBean.save(personProfile);

                listener.processed(personProfile);

                log.info("Профиль загружен", new Event(EventCategory.IMPORT, personProfile));
            }

            listener.completed();
        } catch (Exception e) {
            log.error("Ошибка загрузки профиля", e);

            listener.error(e.getMessage());
        }
    }
}
