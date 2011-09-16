package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.complitex.flexbuh.entity.Language;
import org.complitex.flexbuh.entity.dictionary.Document;
import org.complitex.flexbuh.entity.dictionary.DocumentName;
import org.complitex.flexbuh.service.LanguageBean;
import org.complitex.flexbuh.service.dictionary.DocumentBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ejb.*;
import java.io.File;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 22.08.11 12:12
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportDocumentXMLService extends ImportDictionaryXMLService<Document> {
	private final static Logger log = LoggerFactory.getLogger(ImportDocumentXMLService.class);

	@EJB
	private LanguageBean languageBean;

	@EJB
	private DocumentBean documentBean;

	private Language ukLang = null;

	@Override
	public void process(Long sessionId, ImportListener listener, File importFile, Date beginDate, Date endDate) {
		initLang();
		super.process(sessionId, listener, importFile, beginDate, endDate);
	}

	@Override
	protected List<Document> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate) throws ParseException {
		Document document = new Document();
		document.setUploadDate(importDate);
		document.setBeginDate(beginDate);
		document.setEndDate(endDate);

		for (int j = 0; j < contentNodeRow.getLength(); j++) {
			Node currentNode = contentNodeRow.item(j);
			if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_DOC")) {
				document.setCDoc(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_DOC_SUB")) {
				document.setCDocSub(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME")) {
				if (document.getNames() == null) {
					document.setNames(Lists.<DocumentName>newArrayList());
				}
				DocumentName ukName = new DocumentName();
				ukName.setLanguage(ukLang);
				ukName.setValue(currentNode.getTextContent());
				document.getNames().add(ukName);
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_DOC_CNT_SET")) {
				if ("0".equals(currentNode.getTextContent())) {
					document.setCntSet(Boolean.FALSE);
				} else if ("1".equals(currentNode.getTextContent())) {
					document.setCntSet(Boolean.TRUE);
				} else {
					throw new IllegalArgumentException("C_DOC_CNT_SET must be '0' or '1'");
				}
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "PARENT_C_DOC")) {
				document.setParentCDoc(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "PARENT_C_DOC_SUB")) {
				document.setParentCDocSub(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "SELECTED")) {  //todo такого нет тега в Spr_doc.xml
				document.setSelected(Boolean.parseBoolean(currentNode.getTextContent()));
			}
		}

        //todo improve validation
//		Validate.isTrue(document.validate(), "Invalid processing document: " + document);

		return Lists.newArrayList(document);
	}

	private void initLang() {
		if (ukLang == null) {
			ukLang = languageBean.getLanguageByLangIsoCode("uk");
			Validate.notNull(ukLang, "'uk' language not find");
		}
	}

    @Override
    public void create(Document dictionary) {
        documentBean.save(dictionary);
    }
}
