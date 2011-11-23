package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.complitex.flexbuh.common.entity.dictionary.AbstractPeriodDictionary;
import org.complitex.flexbuh.common.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.common.service.dictionary.TaxInspectionBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 28.08.11 16:10
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportTaxInspectionXMLService extends ImportDictionaryXMLService<TaxInspection> {
	private final static Logger log = LoggerFactory.getLogger(ImportTaxInspectionXMLService.class);

	@EJB
	private TaxInspectionBean taxInspectionBean;

	@Override
	protected List<TaxInspection> processDictionaryNode(NodeList contentNodeRow,
														Date importDate, Date beginDate, Date endDate,
														MultiValueMap createdDictionaries,
														Map<Long, TaxInspection> processedDictionaries)
			throws ParseException {

		Integer regionCode = null;
		List<TaxInspection> taxInspections = Lists.newArrayList();
		for (int j = 0; j < contentNodeRow.getLength(); j++) {
			Node currentNode = contentNodeRow.item(j);
			if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_REG")) {
				regionCode = Integer.decode(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "SET_STI")) {
				processRowStiTags(taxInspections, currentNode.getChildNodes(), importDate, beginDate, endDate);
			}
		}
		Validate.notNull(regionCode, "Can not find region code");
		for (TaxInspection taxInspection : taxInspections) {
			taxInspection.setCReg(regionCode);
			Validate.isTrue(taxInspection.validate(), "Invalid processing document: " + taxInspection);
			processBeginAndEndDates(taxInspection, createdDictionaries, processedDictionaries);
		}
		return taxInspections;
	}

    @Override
    public void create(TaxInspection abstractDictionary) {
        taxInspectionBean.save(abstractDictionary);
    }

	@Override
	public void update(TaxInspection dictionary) {
		taxInspectionBean.update(dictionary);
	}

	private void processRowStiTags(List<TaxInspection> taxInspections, NodeList contentNodeRow,
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
				taxInspection.setCSti(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "C_RAJ")) {
				taxInspection.setCRaj(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "T_STI")) {
				taxInspection.setTSti(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME_STI")) {
                taxInspection.setNameUk(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME_RAJ")) {
                taxInspection.setNameRajUk(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_BEGIN")) {
				taxInspection.setBeginDate(beginDate);
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_END")) {
				taxInspection.setEndDate(endDate);
			}
		}
		return taxInspection;
	}
	
	@SuppressWarnings("unchecked")
	private void processBeginAndEndDates(TaxInspection taxInspection, MultiValueMap createdDictionaries, Map<Long, TaxInspection> processedDictionaries) {
		List<TaxInspection> oldTaxInspections = taxInspectionBean.getTaxInspectionByDistrict(taxInspection.getCSti(), taxInspection.getCRaj());
		if (createdDictionaries.containsKey(taxInspection.getCSti())) {
			oldTaxInspections.addAll(createdDictionaries.getCollection(taxInspection.getCSti()));
		}
		log.debug("Old tax inspections: {}", oldTaxInspections);
		for (TaxInspection oldTaxInspection : oldTaxInspections) {

			if (oldTaxInspection.getId() != null && processedDictionaries.containsKey(oldTaxInspection.getId())) {
				oldTaxInspection = processedDictionaries.get(oldTaxInspection.getId());
			}

			String errorMessage = printToLogInvalidDate(oldTaxInspection, taxInspection);

			Validate.isTrue(!(isPositiveInfinityPoint(taxInspection) && isNegativeInfinityPoint(taxInspection) &&
					isPositiveInfinityPoint(oldTaxInspection) && isNegativeInfinityPoint(oldTaxInspection)), errorMessage);

			if (isPositiveInfinityPoint(taxInspection) && isPositiveInfinityPoint(oldTaxInspection)) {
				Validate.isTrue(!isNegativeInfinityPoint(taxInspection) && !isNegativeInfinityPoint(oldTaxInspection) &&
						taxInspection.getBeginDate().compareTo(oldTaxInspection.getBeginDate()) > 0, errorMessage);
				oldTaxInspection.setEndDate(DateUtils.addDays(taxInspection.getBeginDate(), -1));
				if (oldTaxInspection.getId() != null) {
					processedDictionaries.put(oldTaxInspection.getId(), oldTaxInspection);
				}
			} else if (isNegativeInfinityPoint(taxInspection) && (isNegativeInfinityPoint(oldTaxInspection))) {
				Validate.isTrue(!isPositiveInfinityPoint(taxInspection) && !isPositiveInfinityPoint(oldTaxInspection) &&
						taxInspection.getEndDate().compareTo(oldTaxInspection.getEndDate()) < 0, errorMessage);
				oldTaxInspection.setBeginDate(DateUtils.addDays(taxInspection.getEndDate(), 1));
				if (oldTaxInspection.getId() != null) {
					processedDictionaries.put(oldTaxInspection.getId(), oldTaxInspection);
				}
			} else if (isPositiveInfinityPoint(taxInspection) && isNegativeInfinityPoint(oldTaxInspection)) {
				Validate.isTrue(taxInspection.getEndDate().compareTo(oldTaxInspection.getBeginDate()) > 0, errorMessage);
			} else if (isPositiveInfinityPoint(oldTaxInspection) && isNegativeInfinityPoint(taxInspection)) {
				Validate.isTrue(oldTaxInspection.getEndDate().compareTo(taxInspection.getBeginDate()) > 0, errorMessage);
			} else if (isNegativeInfinityPoint(taxInspection)) {
				pointOnSegment(oldTaxInspection, taxInspection.getEndDate(), errorMessage);
			} else if (isNegativeInfinityPoint(oldTaxInspection) && taxInspection.getBeginDate().before(oldTaxInspection.getEndDate())) {
				pointOnSegment(taxInspection, oldTaxInspection.getEndDate(), errorMessage);
				oldTaxInspection.setBeginDate(DateUtils.addDays(taxInspection.getEndDate(), 1));
				if (oldTaxInspection.getId() != null) {
					processedDictionaries.put(oldTaxInspection.getId(), oldTaxInspection);
				}
			} else if (isPositiveInfinityPoint(oldTaxInspection) && taxInspection.getEndDate().after(oldTaxInspection.getBeginDate())) {
				pointOnSegment(taxInspection, oldTaxInspection.getBeginDate(), errorMessage);
				oldTaxInspection.setEndDate(DateUtils.addDays(taxInspection.getBeginDate(), -1));
				if (oldTaxInspection.getId() != null) {
					processedDictionaries.put(oldTaxInspection.getId(), oldTaxInspection);
				}
			} else {
				pointOnSegment(oldTaxInspection, taxInspection.getBeginDate(), errorMessage);
				pointOnSegment(taxInspection, oldTaxInspection.getBeginDate(), errorMessage);
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
		return new StringBuilder("Invalid processing taxInspection date. ").
				append("New value: ").
				append(newDictionary).
				append(", old value: ").
				append(oldDictionary).toString();
	}
}

