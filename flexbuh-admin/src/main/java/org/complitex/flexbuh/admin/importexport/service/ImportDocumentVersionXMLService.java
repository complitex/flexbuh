package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.complitex.flexbuh.common.entity.dictionary.AbstractPeriodDictionary;
import org.complitex.flexbuh.common.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.common.service.ImportListener;
import org.complitex.flexbuh.common.service.dictionary.DocumentVersionBean;
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
import java.util.Map;

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

	@Override
	public void process(Long sessionId, ImportListener listener, File importFile, Date beginDate, Date endDate) {
		super.process(sessionId, listener, importFile, beginDate, endDate);
	}

	@Override
	protected List<DocumentVersion> processDictionaryNode(NodeList contentNodeRow,
														  Date importDate, Date beginDate, Date endDate,
														  MultiValueMap createdDictionaries,
														  Map<Long, DocumentVersion> processedDictionaries)
			throws ParseException {

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
                documentVersion.setNameUk(currentNode.getTextContent());
			}
		}
		Validate.isTrue(documentVersion.validate(), "Invalid processing document term: " + documentVersion);
		processBeginAndEndDates(documentVersion, createdDictionaries, processedDictionaries);

		return Lists.newArrayList(documentVersion);
	}

	@Override
    public void create(DocumentVersion dictionary) {
        documentVersionBean.save(dictionary);
    }

	@Override
	public void update(DocumentVersion dictionary) {
		documentVersionBean.update(dictionary);
	}
	
	@SuppressWarnings("unchecked")
	private void processBeginAndEndDates(DocumentVersion documentVersion, MultiValueMap createdDictionaries, Map<Long, DocumentVersion> processedDictionaries) {
		List<DocumentVersion> oldDocumentVersions = documentVersionBean.getDocumentVersionsByDocument(documentVersion.getCDoc(), documentVersion.getCDocSub());
		if (createdDictionaries.containsKey(documentVersion.hashCode())) {
			oldDocumentVersions.addAll(createdDictionaries.getCollection(documentVersion.hashCode()));
		}
		for (DocumentVersion oldDocumentVersion : oldDocumentVersions) {

			if (oldDocumentVersion.getId() != null && processedDictionaries.containsKey(oldDocumentVersion.getId())) {
				oldDocumentVersion = processedDictionaries.get(oldDocumentVersion.getId());
			}

			String errorMessage = printToLogInvalidDate(oldDocumentVersion, documentVersion);

			Validate.isTrue(!(isPositiveInfinityPoint(documentVersion) && isNegativeInfinityPoint(documentVersion) &&
					isPositiveInfinityPoint(oldDocumentVersion) && isNegativeInfinityPoint(oldDocumentVersion)), errorMessage);

			if (isPositiveInfinityPoint(documentVersion) && isPositiveInfinityPoint(oldDocumentVersion)) {
				Validate.isTrue(!isNegativeInfinityPoint(documentVersion) && !isNegativeInfinityPoint(oldDocumentVersion) &&
						documentVersion.getBeginDate().compareTo(oldDocumentVersion.getBeginDate()) > 0, errorMessage);
				oldDocumentVersion.setEndDate(DateUtils.addDays(documentVersion.getBeginDate(), -1));
				if (oldDocumentVersion.getId() != null) {
					processedDictionaries.put(oldDocumentVersion.getId(), oldDocumentVersion);
				}
			} else if (isNegativeInfinityPoint(documentVersion) && (isNegativeInfinityPoint(oldDocumentVersion))) {
				Validate.isTrue(!isPositiveInfinityPoint(documentVersion) && !isPositiveInfinityPoint(oldDocumentVersion) &&
						documentVersion.getEndDate().compareTo(oldDocumentVersion.getEndDate()) < 0, errorMessage);
				oldDocumentVersion.setBeginDate(DateUtils.addDays(documentVersion.getEndDate(), 1));
				if (oldDocumentVersion.getId() != null) {
					processedDictionaries.put(oldDocumentVersion.getId(), oldDocumentVersion);
				}
			} else if (isPositiveInfinityPoint(documentVersion) && isNegativeInfinityPoint(oldDocumentVersion)) {
				Validate.isTrue(documentVersion.getEndDate().compareTo(oldDocumentVersion.getBeginDate()) > 0, errorMessage);
			} else if (isPositiveInfinityPoint(oldDocumentVersion) && isNegativeInfinityPoint(documentVersion)) {
				Validate.isTrue(oldDocumentVersion.getEndDate().compareTo(documentVersion.getBeginDate()) > 0, errorMessage);
			} else if (isNegativeInfinityPoint(documentVersion)) {
				pointOnSegment(oldDocumentVersion, documentVersion.getEndDate(), errorMessage);
			} else if (isNegativeInfinityPoint(oldDocumentVersion) && documentVersion.getBeginDate().before(oldDocumentVersion.getEndDate())) {
				pointOnSegment(documentVersion, oldDocumentVersion.getEndDate(), errorMessage);
				oldDocumentVersion.setBeginDate(DateUtils.addDays(documentVersion.getEndDate(), 1));
				if (oldDocumentVersion.getId() != null) {
					processedDictionaries.put(oldDocumentVersion.getId(), oldDocumentVersion);
				}
			} else if (isPositiveInfinityPoint(oldDocumentVersion) && documentVersion.getEndDate().after(oldDocumentVersion.getBeginDate())) {
				pointOnSegment(documentVersion, oldDocumentVersion.getBeginDate(), errorMessage);
				oldDocumentVersion.setEndDate(DateUtils.addDays(documentVersion.getBeginDate(), -1));
				if (oldDocumentVersion.getId() != null) {
					processedDictionaries.put(oldDocumentVersion.getId(), oldDocumentVersion);
				}
			} else {
				pointOnSegment(oldDocumentVersion, documentVersion.getBeginDate(), errorMessage);
				pointOnSegment(documentVersion, oldDocumentVersion.getBeginDate(), errorMessage);
			}
		}
	}

	private boolean isPositiveInfinityPoint(AbstractPeriodDictionary dictionary) {
		return dictionary.getEndDate() == null;
	}

	private boolean isNegativeInfinityPoint(AbstractPeriodDictionary dictionary) {
		return dictionary.getBeginDate() == null;
	}

	private void pointOnSegment(AbstractPeriodDictionary dictionary, Date point, String errorMessage) {
		if (!isPositiveInfinityPoint(dictionary) && !isNegativeInfinityPoint(dictionary) && point != null) {
			//log.debug("Check positive infinity {}: {}", dictionary.getEndDate(), DateUtils.isSameDay(dictionary.getEndDate(), maxEndDate));
			//log.debug("Compare {} and {}: {}", new Object[] {dictionary.getBeginDate(), point, dictionary.getBeginDate().compareTo(point)});
			//log.debug("Compare {} and {}: {}", new Object[] {dictionary.getEndDate(), point, dictionary.getEndDate().compareTo(point)});
			//log.debug("Result composition: {}", dictionary.getBeginDate().compareTo(point) * dictionary.getEndDate().compareTo(point));
			Validate.isTrue((dictionary.getBeginDate().compareTo(point) * dictionary.getEndDate().compareTo(point)) > 0, errorMessage);
		}
	}

	private String printToLogInvalidDate(AbstractPeriodDictionary oldDictionary, AbstractPeriodDictionary newDictionary) {
		return new StringBuilder("Invalid processing documentVersion date. ").
				append("New value: ").
				append(newDictionary).
				append(", old value: ").
				append(oldDictionary).toString();
	}

	@NotNull
	private Date parseDate(@NotNull String stringDate) throws ParseException {
		return DATE_FORMAT.parse(stringDate);
	}
}
