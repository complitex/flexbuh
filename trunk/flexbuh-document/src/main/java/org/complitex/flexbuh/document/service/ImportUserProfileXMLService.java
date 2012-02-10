package org.complitex.flexbuh.document.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.complitex.flexbuh.common.entity.ApplicationConfig;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.*;
import java.io.InputStream;
import java.text.ParseException;
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
public class ImportUserProfileXMLService extends ImportXMLService {
    private final static Logger log = LoggerFactory.getLogger(ImportUserProfileXMLService.class);

    @Resource
    protected UserTransaction userTransaction;

    @EJB
    private PersonProfileBean personProfileBean;

	@EJB
	private EventObjectFactory eventObjectFactory;

    @EJB
    private ConfigBean configBean;

    public void process(Long sessionId, ImportListener listener, String name, InputStream inputStream, Locale locale, Date beginDate, Date endDate) {
        listener.begin();

        Date importDate = new Date();
        List<PersonProfile> docDictionaries = Lists.newArrayList();
        try {
            org.w3c.dom.Document document = getDocument(inputStream);
            processDocument(sessionId, "ROW", beginDate, endDate, importDate, docDictionaries, document);
            processDocument(sessionId, "row", beginDate, endDate, importDate, docDictionaries, document);

            userTransaction.begin();
            try{
                for (PersonProfile personProfile : docDictionaries) {
                    personProfile.setSessionId(sessionId);
                    personProfile.setProfileName(personProfile.getName());

                    if (PersonType.PHYSICAL_PERSON.equals(personProfile.getPersonType())){
                        personProfile.parsePhysicalNames();
                    }

                    personProfileBean.save(personProfile, locale != null? locale: getSystemLocale());

                    listener.getChildImportListener(personProfile).completed();

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
            listener.cancel();
        }
    }

    private void processDocument(Long sessionId, String tagName, Date beginDate, Date endDate, Date importDate,
                                 List<PersonProfile> docDictionaries, Document document)
            throws ParseException, SystemException, NotSupportedException, RollbackException, HeuristicRollbackException,
            HeuristicMixedException {
        NodeList nodeRows = document.getElementsByTagName(tagName);
        for (int i = 0; i < nodeRows.getLength(); i++) {
            docDictionaries.add(processDictionaryNode(nodeRows.item(i), importDate, beginDate, endDate));
        }
    }

    private PersonProfile processDictionaryNode(Node contentNode, Date importDate, Date beginDate, Date endDate) throws ParseException {
        PersonProfile personProfile = new PersonProfile();

        NamedNodeMap attributes = contentNode.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node currentAttribute = attributes.item(i);
            if (StringUtils.equalsIgnoreCase(currentAttribute.getNodeName(), "selected")) {
                personProfile.setSelected(Boolean.parseBoolean(currentAttribute.getTextContent()));
            }
        }

        NodeList contentNodeRow = contentNode.getChildNodes();
        for (int j = 0; j < contentNodeRow.getLength(); j++) {
            Node currentNode = contentNodeRow.item(j);

            String value = currentNode.getTextContent();

            switch (currentNode.getNodeName().toUpperCase()){
                case "NAME":
                    personProfile.setName(value);
                    break;
                case "TIN":
                    personProfile.setTin(Integer.valueOf(value));
                    break;
                case "C_STI":
                    personProfile.setCSti(Integer.parseInt(value));
                    break;
                case "C_STI_TIN":
                    personProfile.setCStiTin(Integer.valueOf(value));
                    break;
                case "KVED":
                    personProfile.setKved(value);
                    break;
                case "KOATUU":
                    personProfile.setKoatuu(value);
                    break;
                case "PERSON_TYPE":
                    personProfile.setPersonType(PersonType.get(Integer.parseInt(value)));
                    break;
                case "CONTRACT_DATE":
                    //todo
                    break;
                case "CONTRACT_NUMBER":
                    //todo
                    break;
                case "ZIPCODE":
                    personProfile.setZipCode(value);
                    break;
                case "ADRESS":
                    personProfile.setAddress(value);
                    break;
                case "PHONE":
                    personProfile.setPhone(value);
                    break;
                case "FAX":
                    personProfile.setFax(value);
                    break;
                case "EMAIL":
                    personProfile.setEmail(value);
                    break;
                case "DFIO":
                    personProfile.setDFio(value);
                    break;
                case "BFIO":
                    personProfile.setBFio(value);
                    break;
                case "DINN":
                    personProfile.setDInn(value);
                    break;
                case "BINN":
                    personProfile.setBInn(value);
                    break;
                case "IPN":
                    personProfile.setIpn(value);
                    break;
                case "NUMPDVSVD":
                    personProfile.setNumPdvSvd(value);
                    break;
            }

        }

        return personProfile;
    }

    private Locale getSystemLocale() {
        return new Locale(configBean.getString(ApplicationConfig.SYSTEM_LOCALE, true));
    }
}
