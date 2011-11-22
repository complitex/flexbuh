package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.complitex.flexbuh.common.entity.dictionary.AbstractPeriodDictionary;
import org.complitex.flexbuh.common.entity.dictionary.Currency;
import org.complitex.flexbuh.common.service.dictionary.CurrencyBean;
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
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 15.08.11 17:00
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportCurrencyXMLService extends ImportDictionaryXMLService<Currency> {
	private final static Logger log = LoggerFactory.getLogger(ImportCurrencyXMLService.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	private final static Date maxEndDate;// DateUtil.newDate(2999, 12, 31);

	static {
		Date maxEndDate1;
		try {
			maxEndDate1 = DATE_FORMAT.parse("31122999");
		} catch (ParseException e) {
			maxEndDate1 = null;
			log.error("Failed set max end date", e);
		}
		log.debug("set maxEndDate: {}", maxEndDate1);
		maxEndDate = maxEndDate1;
	}

	@EJB
	private CurrencyBean currencyBean;

	@Override
	protected List<Currency> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate,
												   MultiValueMap createdDictionaries, Map<Long, Currency> processedDictionaries) throws ParseException {
		Currency currency = new Currency();
		currency.setUploadDate(importDate);
		for (int j = 0; j < contentNodeRow.getLength(); j++) {
            Node currentNode = contentNodeRow.item(j);
            if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_CURRN_N")) {
                currency.setCodeNumber(Integer.parseInt(currentNode.getTextContent()));
            } else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_CURRN_C")) {
                currency.setCodeString(currentNode.getTextContent());
            } else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME_CUR")) {
                currency.setNameUk(currentNode.getTextContent());
            } else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME_RUS")) {
                currency.setNameRu(currentNode.getTextContent());
            } else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_BEGIN")) {
                currency.setBeginDate(parseDate(currentNode.getTextContent()));
            } else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_END")) {
                currency.setEndDate(parseDate(currentNode.getTextContent()));
            }
		}
		Validate.isTrue(currency.validate(), "Invalid processing currency: " + currency);
		processBeginAndEndDates(currency, createdDictionaries, processedDictionaries);
		return Lists.newArrayList(currency);
	}

    @Override
    public void create(Currency abstractDictionary) {
		currencyBean.save(abstractDictionary);
	}

	@Override
	public void update(Currency abstractDictionary) {
		currencyBean.update(abstractDictionary);
    }

	@SuppressWarnings("unchecked")
	private void processBeginAndEndDates(Currency currency, MultiValueMap createdDictionaries, Map<Long, Currency> processedDictionaries) {
		List<Currency> oldCurrencies = currencyBean.getCurrencyByCodeNumber(currency.getCodeNumber());
		if (createdDictionaries.containsKey(currency.getCodeNumber())) {
			oldCurrencies.addAll(createdDictionaries.getCollection(currency.getCodeNumber()));
		}
		for (Currency oldCurrency : oldCurrencies) {

			if (oldCurrency.getId() != null && processedDictionaries.containsKey(oldCurrency.getId())) {
				oldCurrency = processedDictionaries.get(oldCurrency.getId());
			}

			String errorMessage = printToLogInvalidDate(oldCurrency, currency);

			Validate.isTrue(!(isPositiveInfinityPoint(currency) && isNegativeInfinityPoint(currency) &&
					isPositiveInfinityPoint(oldCurrency) && isNegativeInfinityPoint(oldCurrency)), errorMessage);

			if (isPositiveInfinityPoint(currency) && isPositiveInfinityPoint(oldCurrency)) {
				Validate.isTrue(!isNegativeInfinityPoint(currency) && !isNegativeInfinityPoint(oldCurrency) &&
						currency.getBeginDate().compareTo(oldCurrency.getBeginDate()) > 0, errorMessage);
				oldCurrency.setEndDate(DateUtils.addDays(currency.getBeginDate(), -1));
				if (oldCurrency.getId() != null) {
					processedDictionaries.put(oldCurrency.getId(), oldCurrency);
				}
			} else if (isNegativeInfinityPoint(currency) && (isNegativeInfinityPoint(oldCurrency))) {
				Validate.isTrue(!isPositiveInfinityPoint(currency) && !isPositiveInfinityPoint(oldCurrency) &&
						currency.getEndDate().compareTo(oldCurrency.getEndDate()) < 0, errorMessage);
				oldCurrency.setBeginDate(DateUtils.addDays(currency.getEndDate(), 1));
				if (oldCurrency.getId() != null) {
					processedDictionaries.put(oldCurrency.getId(), oldCurrency);
				}
			} else if (isPositiveInfinityPoint(currency) && isNegativeInfinityPoint(oldCurrency)) {
				Validate.isTrue(currency.getEndDate().compareTo(oldCurrency.getBeginDate()) > 0, errorMessage);
			} else if (isPositiveInfinityPoint(oldCurrency) && isNegativeInfinityPoint(currency)) {
				Validate.isTrue(oldCurrency.getEndDate().compareTo(currency.getBeginDate()) > 0, errorMessage);
			} else if (isNegativeInfinityPoint(currency)) {
				pointOnSegment(oldCurrency, currency.getEndDate(), errorMessage);
			} else if (isNegativeInfinityPoint(oldCurrency) && currency.getBeginDate().before(oldCurrency.getEndDate())) {
				pointOnSegment(currency, oldCurrency.getEndDate(), errorMessage);
				oldCurrency.setBeginDate(DateUtils.addDays(currency.getEndDate(), 1));
				if (oldCurrency.getId() != null) {
					processedDictionaries.put(oldCurrency.getId(), oldCurrency);
				}
			} else if (isPositiveInfinityPoint(oldCurrency) && currency.getEndDate().after(oldCurrency.getBeginDate())) {
				pointOnSegment(currency, oldCurrency.getBeginDate(), errorMessage);
				oldCurrency.setEndDate(DateUtils.addDays(currency.getBeginDate(), -1));
				if (oldCurrency.getId() != null) {
					processedDictionaries.put(oldCurrency.getId(), oldCurrency);
				}
			} else {
				pointOnSegment(oldCurrency, currency.getBeginDate(), errorMessage);
				pointOnSegment(currency, oldCurrency.getBeginDate(), errorMessage);
			}
		}
	}

	private boolean isPositiveInfinityPoint(AbstractPeriodDictionary dictionary) {
		return dictionary.getEndDate() == null || DateUtils.isSameDay(dictionary.getEndDate(), maxEndDate);
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
		return new StringBuilder("Invalid processing currency date. ").
				append("New value: ").
				append(newDictionary).
				append(", old value: ").
				append(oldDictionary).toString();
	}

	private Date parseDate(@NotNull String stringDate) throws ParseException {
		return StringUtils.isEmpty(stringDate)? null: DATE_FORMAT.parse(stringDate);
	}
}
