package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.IProcessListener;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.PersonType;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.util.ReflectionUtil;
import org.complitex.flexbuh.common.util.XmlUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 10.09.11 11:51
 */
@Stateless
public class PersonProfileService {
    private final static Logger log = LoggerFactory.getLogger(PersonProfileService.class);

    @EJB
    private PersonProfileBean personProfileBean;

    public List<PersonProfile> getPersonProfiles(Long sessionId, InputStream inputStream) throws JAXBException {
        PersonProfile.RS rs = (PersonProfile.RS) JAXBContext.newInstance(PersonProfile.RS.class)
                .createUnmarshaller()
                .unmarshal(inputStream);

        for (PersonProfile personProfile : rs.getRows()){
            personProfile.setSessionId(sessionId);

            if (personProfile.getProfileName() == null) {
                personProfile.setProfileName(personProfile.getName());
            }

            if (PersonType.PHYSICAL_PERSON.equals(personProfile.getPersonType())){
                personProfile.parsePhysicalNames();
            }
        }

        return rs.getRows();
    }

    public void save(Long sessionId, InputStream inputStream, IProcessListener<PersonProfile> listener){
        PersonProfile personProfile = null;

        try {
            List<PersonProfile> personProfiles = getPersonProfiles(sessionId, inputStream);

            for (PersonProfile pp : personProfiles){
                personProfile = pp;

                personProfileBean.save(pp);

                log.info("Профиль загружен", new Event(EventCategory.IMPORT, pp));

                if (listener != null){
                    listener.onSuccess(pp);
                }
            }
        } catch (Exception e) {
            log.error("Ошибка загрузки профиля", e);

            if (listener != null){
                listener.onError(personProfile, e);
            }else {
                throw new RuntimeException(e);
            }
        }
    }

    public InputStream getInputStream(Long sessionId, Long selectedPersonProfileId){
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            //row set
            PersonProfile.RS rowSet = new PersonProfile.RS(personProfileBean.getAllPersonProfiles(sessionId));
            PersonProfile.RS logRowSet = new PersonProfile.RS(personProfileBean.getAllPersonProfiles(sessionId));

            boolean selectedSet = false;

            for (int i = 0, personProfilesSize = rowSet.getRows().size(); i < personProfilesSize; i++) {
                PersonProfile personProfile = rowSet.getRows().get(i);
                personProfile.mergePhysicalNames();

                //num
                personProfile.setNum(i + 1);

                //selected
                if (personProfile.getId().equals(selectedPersonProfileId)) {
                    personProfile.setSelected(true);
                    selectedSet = true;
                } else {
                    personProfile.setSelected(false);
                }

                //name
                if (personProfile.getName() == null || personProfile.getName().isEmpty()){
                    personProfile.setName(personProfile.getProfileName());
                }

                //empty for null
                ReflectionUtil.emptyOnNull(personProfile);

                //prepare opz format
                personProfile.setId(null);
                personProfile.setProfileName(null);
            }

            if (!selectedSet && !rowSet.getRows().isEmpty()){
                rowSet.getRows().get(0).setSelected(true);
            }

            //write xml
            XmlUtil.writeXml(PersonProfile.RS.class, rowSet, outputStream, "windows-1251");

            log.info("Выгрузка профилей", new Event(EventCategory.EXPORT, logRowSet));

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (Exception e) {
            log.error("Ошибка выгрузки профилей", e);
        }

        return null;
    }
}
