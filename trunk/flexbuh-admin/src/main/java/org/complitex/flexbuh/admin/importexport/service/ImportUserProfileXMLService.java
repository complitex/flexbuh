package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.complitex.flexbuh.entity.user.PersonProfile;
import org.complitex.flexbuh.service.user.PersonTypeBean;
import org.complitex.flexbuh.service.user.UserBean;
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

	private final static int OBJECTS_TRANSACTION_SIZE = 1000;

	@Resource
    protected UserTransaction userTransaction;

	@EJB
	private PersonTypeBean personTypeBean;

	@EJB
	private UserBean userBean;

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
			commit(sessionId, docDictionaries, true);

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
			commit(sessionId, docDictionaries, false);
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
			if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME")) {
				personProfile.setName(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "TIN")) {
				personProfile.setCodeTIN(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_STI")) {
				personProfile.setCodeTaxInspection(Integer.parseInt(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "KVED")) {
				personProfile.setCodeKVED(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "PERSON_TYPE")) {

				personProfile.setPersonType(personTypeBean.findByCode(currentNode.getTextContent()));

			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "CONTRACT_DATE")) {
				// TODO Release
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "CONTRACT_NUMBER")) {
				// TODO Release
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "ZIPCODE")) {
				personProfile.setZipCode(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "ADRESS")) {
				personProfile.setAddress(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "PHONE")) {
				personProfile.setPhone(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "FAX")) {
				personProfile.setFax(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "EMAIL")) {
				personProfile.setEmail(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "DFIO")) {
				personProfile.setDirectorFIO(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "BFIO")) {
				personProfile.setAccountantFIO(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "DINN")) {
				personProfile.setDirectorINN(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "BINN")) {
				personProfile.setAccountantINN(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "IPN")) {
				personProfile.setIpn(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NUMPDVSVD")) {
				personProfile.setNumSvdPDV(currentNode.getTextContent());
			}
		}

		return personProfile;
	}

	private void commit(Long sessionId, List<PersonProfile> personProfiles, boolean finalTransaction)
			throws SystemException, NotSupportedException, RollbackException,
			HeuristicRollbackException, HeuristicMixedException {

		if (personProfiles.size() < OBJECTS_TRANSACTION_SIZE && !finalTransaction) {
			return;
		}
		userTransaction.begin();
		try{
			for (PersonProfile personProfile : personProfiles) {
				userBean.createCompanyProfile(sessionId, personProfile);
			}
		} catch (Throwable th) {
			log.error("Rollback user transaction");
			userTransaction.rollback();
			throw th;
		}
		userTransaction.commit();
		log.debug("Commit documents");
		personProfiles.clear();
	}
}
