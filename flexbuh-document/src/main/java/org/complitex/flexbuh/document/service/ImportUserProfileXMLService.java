package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.PersonType;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.logging.EventModel;
import org.complitex.flexbuh.common.logging.EventObjectFactory;
import org.complitex.flexbuh.common.logging.EventObjectId;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.service.ImportListener;
import org.complitex.flexbuh.common.service.ImportXMLService;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.document.entity.Settings;
import org.complitex.flexbuh.document.exception.ImportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.UserTransaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 10.09.11 11:51
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportUserProfileXMLService extends ImportXMLService<PersonProfile> {
    private final static Logger log = LoggerFactory.getLogger(ImportUserProfileXMLService.class);

    @Resource
    protected UserTransaction userTransaction;

    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private EventObjectFactory eventObjectFactory;

    @EJB
    private ConfigBean configBean;

    public void process(Long sessionId, ImportListener<PersonProfile> listener, String name, InputStream inputStream,
                        Locale locale, Date beginDate, Date endDate) {
        listener.begin();

        try {
            List<PersonProfile> docDictionaries = getPersonProfiles(inputStream);

            userTransaction.begin();

            try{
                for (PersonProfile personProfile : docDictionaries) {
                    personProfile.setSessionId(sessionId);

                    if (personProfile.getProfileName() == null) {
                        personProfile.setProfileName(personProfile.getName());
                    }

                    if (PersonType.PHYSICAL_PERSON.equals(personProfile.getPersonType())){
                        personProfile.parsePhysicalNames();
                    }

                    personProfileBean.save(personProfile);

                    listener.processed(personProfile);

                    log.info("Import person profile {}", new Object[]{personProfile, EventCategory.IMPORT,
                            new EventObjectId(personProfile.getId()), new EventModel(PersonProfile.class.getName()),
                            eventObjectFactory.getEventNewObject(personProfile)});
                }
            } catch (Throwable th) {
                log.error("Rollback user transaction");
                userTransaction.rollback();
                throw th;
            }
            userTransaction.commit();

            listener.completed();
        } catch (Throwable th) {
            log.warn("Cancel import user profile: " + name, th);
            listener.error();
        }
    }

    public List<PersonProfile> getPersonProfiles(InputStream inputStream) throws ImportException {
        try {
            Settings settings = (Settings) JAXBContext.newInstance(Settings.class)
                    .createUnmarshaller()
                    .unmarshal(inputStream);

            return settings.getPersonProfiles();
        } catch (JAXBException e) {
            log.error("Ошибка обработки файла профилей", e);
        }

        return null;
    }
}
