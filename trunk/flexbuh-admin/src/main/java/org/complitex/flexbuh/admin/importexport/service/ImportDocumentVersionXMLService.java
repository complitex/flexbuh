package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.complitex.flexbuh.entity.Language;
import org.complitex.flexbuh.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.entity.dictionary.NormativeDocumentName;
import org.complitex.flexbuh.service.LanguageBean;
import org.complitex.flexbuh.service.dictionary.DocumentVersionBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ejb.*;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 27.08.11 17:44
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportDocumentVersionXMLService extends ImportDictionaryXMLService<DocumentVersion> {
	private final static Logger log = LoggerFactory.getLogger(ImportDocumentVersionXMLService.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	private DocumentVersionBean documentVersionBean;

	@EJB
	private LanguageBean languageBean;

	private Language ukLang = null;

	@Override
	public void process(Long sessionId, ImportListener listener, File importFile, Date beginDate, Date endDate) {
		initLang();
		super.process(sessionId, listener, importFile, beginDate, endDate);
	}

	@Override
	protected List<DocumentVersion> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate) throws ParseException {
		DocumentVersion documentVersion = new DocumentVersion();
		documentVersion.setUploadDate(importDate);
		//documentTerm.setBeginDate(beginDate);
		//documentTerm.setEndDate(endDate);
		for (int j = 0; j < contentNodeRow.getLength(); j++) {
			Node currentNode = contentNodeRow.item(j);
			if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_DOC")) {
				documentVersion.setCDoc(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_DOC_SUB")) {
				documentVersion.setCDocSub(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_DOC_VER")) {
				documentVersion.setCDocVer(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_BEGIN") &&
					StringUtils.isNotEmpty(currentNode.getTextContent())) {
				documentVersion.setBeginDate(parseDate(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_END") &&
					StringUtils.isNotEmpty(currentNode.getTextContent())) {
				documentVersion.setEndDate(parseDate(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NORM_DOC")) {
				if (documentVersion.getNormativeDocumentNames() == null) {
					documentVersion.setNormativeDocumentNames(Lists.<NormativeDocumentName>newArrayList());
				}
				NormativeDocumentName ukName = new NormativeDocumentName();
				ukName.setLanguage(ukLang);
				ukName.setValue(currentNode.getTextContent());
				documentVersion.getNormativeDocumentNames().add(ukName);
			}
		}
		Validate.isTrue(documentVersion.validate(), "Invalid processing document term: " + documentVersion);

		return Lists.newArrayList(documentVersion);
	}

    @Override
    public void create(DocumentVersion dictionary) {
        documentVersionBean.save(dictionary);
    }

    @NotNull
	private Date parseDate(@NotNull String stringDate) throws ParseException {
		return DATE_FORMAT.parse(stringDate);
	}

	private void initLang() {
		if (ukLang == null) {
			ukLang = languageBean.getLanguageByLangIsoCode("uk");
			Validate.notNull(ukLang, "'uk' language not find");
		}
	}
}
