package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.complitex.flexbuh.entity.dictionary.DocumentTerm;
import org.complitex.flexbuh.service.dictionary.DocumentTermBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ejb.*;
import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 27.08.11 10:28
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportDocumentTermXMLService extends ImportDictionaryXMLService<DocumentTerm> {
	private final static Logger log = LoggerFactory.getLogger(ImportDocumentTermXMLService.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	private DocumentTermBean documentTermBean;

	@Override
	protected List<DocumentTerm> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate) throws ParseException {
		DocumentTerm documentTerm = new DocumentTerm();
		documentTerm.setUploadDate(importDate);
		documentTerm.setBeginDate(beginDate);
		documentTerm.setEndDate(endDate);
		for (int j = 0; j < contentNodeRow.getLength(); j++) {
			Node currentNode = contentNodeRow.item(j);
			if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_DOC")) {
				documentTerm.setCDoc(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_DOC_SUB")) {
				documentTerm.setCDocSub(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_DOC_VER")) {
				documentTerm.setCDocVer(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_TERM")) {
				documentTerm.setDateTerm(parseDate(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "PERIOD_MONTH")) {
				documentTerm.setPeriodMonth(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "PERIOD_TYPE")) {
				documentTerm.setPeriodType(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "PERIOD_YEAR")) {
				documentTerm.setPeriodYear(Integer.decode(currentNode.getTextContent()));
			}
		}
		Validate.isTrue(documentTerm.validate(), "Invalid processing document term: " + documentTerm);

		return Lists.newArrayList(documentTerm);
	}

	@NotNull
	private Date parseDate(@NotNull String stringDate) throws ParseException {
		return DATE_FORMAT.parse(stringDate);
	}

    @Override
    public void create(DocumentTerm dictionary) {
        documentTermBean.save(dictionary);
    }
}
