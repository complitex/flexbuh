package org.complitex.flexbuh.document.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.complitex.flexbuh.document.entity.PersonProfile;
import org.complitex.flexbuh.document.entity.PersonType;
import org.complitex.flexbuh.service.ImportListener;
import org.complitex.flexbuh.service.ImportXMLService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

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

    @Override
    public void process(Long sessionId, ImportListener listener, File importFile, Date beginDate, Date endDate) {
        try {
            process(sessionId, listener, importFile.getName(), new FileInputStream(importFile), beginDate, endDate);
        } catch (FileNotFoundException e) {
            listener.begin();
            log.warn("Can not find file: " + importFile, e);
            listener.cancel();
        }
    }

    public void process(Long sessionId, ImportListener listener, String name, InputStream inputStream, Date beginDate, Date endDate) {
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

                    personProfileBean.save(personProfile);
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
                    personProfile.setTin(value);
                    break;
                case "C_STI":
                    personProfile.setCSti(Integer.parseInt(value));
                    break;
                case "C_STI_TIN":
                    personProfile.setCStiTin(value);
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
                    personProfile.setNumPvdSvd(value);
                    break;
            }

        }

        return personProfile;
    }
}
