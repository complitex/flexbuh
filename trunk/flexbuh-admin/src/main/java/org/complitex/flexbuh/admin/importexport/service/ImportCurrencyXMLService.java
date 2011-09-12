package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.complitex.flexbuh.entity.Language;
import org.complitex.flexbuh.entity.dictionary.Currency;
import org.complitex.flexbuh.entity.dictionary.CurrencyName;
import org.complitex.flexbuh.entity.dictionary.Dictionary;
import org.complitex.flexbuh.service.LanguageBean;
import org.complitex.flexbuh.service.dictionary.CurrencyBean;
import org.complitex.flexbuh.service.dictionary.DictionaryBean;
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
 *         Date: 15.08.11 17:00
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportCurrencyXMLService extends ImportDictionaryXMLService {
	private final static Logger log = LoggerFactory.getLogger(ImportCurrencyXMLService.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	private LanguageBean languageBean;

	@EJB
	private CurrencyBean currencyBean;

	private Language ukLang = null;
	private Language ruLang = null;

	@Override
	public void process(Long sessionId, ImportListener listener, File importFile, Date beginDate, Date endDate) {
		initLang();
		super.process(sessionId, listener, importFile, beginDate, endDate);
	}

	@Override
	protected List<Dictionary> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate) throws ParseException {
		Currency currency = new Currency();
		currency.setUploadDate(importDate);
		for (int j = 0; j < contentNodeRow.getLength(); j++) {
			Node currentNode = contentNodeRow.item(j);
			if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_CURRN_N")) {
				currency.setCodeNumber(Integer.parseInt(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_CURRN_C")) {
				currency.setCodeString(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME_CUR")) {
				if (currency.getNames() == null) {
					currency.setNames(Lists.<CurrencyName>newArrayList());
				}
				CurrencyName ukName = new CurrencyName();
				ukName.setLanguage(ukLang);
				ukName.setValue(currentNode.getTextContent());
				currency.getNames().add(ukName);
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME_RUS")) {
				if (currency.getNames() == null) {
					currency.setNames(Lists.<CurrencyName>newArrayList());
				}
				CurrencyName ruName = new CurrencyName();
				ruName.setLanguage(ruLang);
				ruName.setValue(currentNode.getTextContent());
				currency.getNames().add(ruName);
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_BEGIN")) {
				currency.setBeginDate(parseDate(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_END")) {
				currency.setEndDate(parseDate(currentNode.getTextContent()));
			}
		}
		Validate.isTrue(currency.validate(), "Invalid processing currency: " + currency);
		return Lists.newArrayList((Dictionary)currency);
	}

	private void initLang() {
		if (ukLang == null) {
			ukLang = languageBean.find("uk");
			Validate.notNull(ukLang, "'uk' language not find");
		}
		if (ruLang == null) {
			ruLang = languageBean.find("ru");
			Validate.notNull(ukLang, "'ru' language not find");
		}
	}

	@NotNull
	private Date parseDate(@NotNull String stringDate) throws ParseException {
		return DATE_FORMAT.parse(stringDate);
	}

	@Override
	protected DictionaryBean getDictionaryBean() {
		return currencyBean;
	}
}
