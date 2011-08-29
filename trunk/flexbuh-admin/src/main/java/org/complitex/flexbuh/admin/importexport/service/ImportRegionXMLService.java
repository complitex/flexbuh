package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.complitex.flexbuh.entity.Language;
import org.complitex.flexbuh.entity.dictionary.Dictionary;
import org.complitex.flexbuh.entity.dictionary.Region;
import org.complitex.flexbuh.entity.dictionary.RegionName;
import org.complitex.flexbuh.service.LanguageBean;
import org.complitex.flexbuh.service.dictionary.DictionaryBean;
import org.complitex.flexbuh.service.dictionary.RegionBean;
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
 *         Date: 28.08.11 15:22
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportRegionXMLService extends ImportDictionaryXMLService {
	private final static Logger log = LoggerFactory.getLogger(ImportRegionXMLService.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	private LanguageBean languageBean;

	@EJB
	private RegionBean regionBean;

	private Language ukLang = null;

	@Override
	public void process(ImportListener listener, File importFile, Date beginDate, Date endDate) {
		initLang();
		super.process(listener, importFile, beginDate, endDate);
	}

	@Override
	protected List<Dictionary> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate) throws ParseException {
		Region region = new Region();
		region.setUploadDate(importDate);
		region.setBeginDate(beginDate);
		region.setEndDate(endDate);
		for (int j = 0; j < contentNodeRow.getLength(); j++) {
			Node currentNode = contentNodeRow.item(j);
			if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "CODE")) {
				region.setCode(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME")) {
				if (region.getNames() == null) {
					region.setNames(Lists.<RegionName>newArrayList());
				}
				RegionName ruName = new RegionName();
				ruName.setLanguage(ukLang);
				ruName.setValue(currentNode.getTextContent());
				region.getNames().add(ruName);
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_BEGIN") &&
					StringUtils.isNotEmpty(currentNode.getTextContent())) {
				region.setBeginDate(parseDate(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_END") &&
					StringUtils.isNotEmpty(currentNode.getTextContent())) {
				region.setEndDate(parseDate(currentNode.getTextContent()));
			}
		}
		Validate.isTrue(region.validate(), "Invalid processing document: " + region);
		return Lists.newArrayList((Dictionary)region);
	}

	private void initLang() {
		if (ukLang == null) {
			ukLang = languageBean.find("uk");
			Validate.notNull(ukLang, "'uk' language not find");
		}
	}

	@NotNull
	private Date parseDate(@NotNull String stringDate) throws ParseException {
		return DATE_FORMAT.parse(stringDate);
	}

	@Override
	protected DictionaryBean getDictionaryBean() {
		return regionBean;
	}
}
