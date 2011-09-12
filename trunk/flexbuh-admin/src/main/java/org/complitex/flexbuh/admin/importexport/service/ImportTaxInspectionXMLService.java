package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.complitex.flexbuh.entity.Language;
import org.complitex.flexbuh.entity.dictionary.AreaName;
import org.complitex.flexbuh.entity.dictionary.Dictionary;
import org.complitex.flexbuh.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.entity.dictionary.TaxInspectionName;
import org.complitex.flexbuh.service.LanguageBean;
import org.complitex.flexbuh.service.dictionary.DictionaryBean;
import org.complitex.flexbuh.service.dictionary.TaxInspectionBean;
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
 *         Date: 28.08.11 16:10
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportTaxInspectionXMLService extends ImportDictionaryXMLService {
	private final static Logger log = LoggerFactory.getLogger(ImportTaxInspectionXMLService.class);

	@EJB
	private LanguageBean languageBean;

	@EJB
	private TaxInspectionBean taxInspectionBean;

	private Language ukLang = null;

	@Override
	public void process(Long sessionId, ImportListener listener, File importFile, Date beginDate, Date endDate) {
		initLang();
		super.process(sessionId, listener, importFile, beginDate, endDate);
	}

	@Override
	protected List<Dictionary> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate) throws ParseException {
		Integer regionCode = null;
		List<Dictionary> taxInspections = Lists.newArrayList();
		for (int j = 0; j < contentNodeRow.getLength(); j++) {
			Node currentNode = contentNodeRow.item(j);
			if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_REG")) {
				regionCode = Integer.decode(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "SET_STI")) {
				processRowStiTags(taxInspections, currentNode.getChildNodes(), importDate, beginDate, endDate);
			}
		}
		Validate.notNull(regionCode, "Can not find region code");
		for (Dictionary taxInspection : taxInspections) {
			((TaxInspection)taxInspection).setRegionCode(regionCode);
			Validate.isTrue(taxInspection.validate(), "Invalid processing document: " + taxInspection);
		}
		return taxInspections;
	}

	private void processRowStiTags(List<Dictionary> taxInspections, NodeList contentNodeRow,
								   Date importDate, Date beginDate, Date endDate) throws ParseException {
		for (int j = 0; j < contentNodeRow.getLength(); j++) {
			Node currentNode = contentNodeRow.item(j);
			if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "ROW_STI")) {
				taxInspections.add(processTaxInspectionNode(currentNode.getChildNodes(), importDate, beginDate, endDate));
			}
		}
	}

	private TaxInspection processTaxInspectionNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate) throws ParseException {
		TaxInspection taxInspection = new TaxInspection();
		taxInspection.setUploadDate(importDate);
		taxInspection.setBeginDate(beginDate);
		taxInspection.setEndDate(endDate);
		for (int j = 0; j < contentNodeRow.getLength(); j++) {
			Node currentNode = contentNodeRow.item(j);
			if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_STI")) {
				taxInspection.setCode(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_RAJ")) {
				taxInspection.setCodeArea(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "T_STI")) {
				taxInspection.setCodeTaxInspectionType(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME_STI")) {
				if (taxInspection.getNames() == null) {
					taxInspection.setNames(Lists.<TaxInspectionName>newArrayList());
				}
				TaxInspectionName ruName = new TaxInspectionName();
				ruName.setLanguage(ukLang);
				ruName.setValue(currentNode.getTextContent());
				taxInspection.getNames().add(ruName);
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME_RAJ")) {
				if (taxInspection.getAreaNames() == null) {
					taxInspection.setAreaNames(Lists.<AreaName> newArrayList());
				}
				AreaName ruName = new AreaName();
				ruName.setLanguage(ukLang);
				ruName.setValue(currentNode.getTextContent());
				taxInspection.getAreaNames().add(ruName);
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_BEGIN")) {
				taxInspection.setBeginDate(beginDate);
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_END")) {
				taxInspection.setEndDate(endDate);
			}
		}
		return taxInspection;
	}

	private void initLang() {
		if (ukLang == null) {
			ukLang = languageBean.find("uk");
			Validate.notNull(ukLang, "'uk' language not find");
		}
	}

	@Override
	protected DictionaryBean getDictionaryBean() {
		return taxInspectionBean;
	}
}

