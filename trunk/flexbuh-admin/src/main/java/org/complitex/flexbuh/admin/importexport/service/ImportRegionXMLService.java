package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.time.DateUtils;
import org.complitex.flexbuh.common.entity.dictionary.AbstractPeriodDictionary;
import org.complitex.flexbuh.common.entity.dictionary.Region;
import org.complitex.flexbuh.common.service.ImportListener;
import org.complitex.flexbuh.common.service.dictionary.RegionBean;
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
 *         Date: 28.08.11 15:22
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportRegionXMLService extends ImportDictionaryXMLService<Region> {
	private final static Logger log = LoggerFactory.getLogger(ImportRegionXMLService.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	private RegionBean regionBean;

	@Override
	public void process(Long sessionId, ImportListener listener, File importFile, Date beginDate, Date endDate) {
		super.process(sessionId, listener, importFile, beginDate, endDate);
	}

	@Override
	protected List<Region> processDictionaryNode(NodeList contentNodeRow,
												 Date importDate, Date beginDate, Date endDate,
												 MultiValueMap createdDictionaries,
												 Map<Long, Region> processedDictionaries)
			throws ParseException {

		Region region = new Region();
		region.setUploadDate(importDate);
		region.setBeginDate(beginDate);
		region.setEndDate(endDate);
		for (int j = 0; j < contentNodeRow.getLength(); j++) {
			Node currentNode = contentNodeRow.item(j);
			if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "CODE")) {
				region.setCode(Integer.decode(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "NAME")) {
                region.setNameUk(currentNode.getTextContent());
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_BEGIN") &&
					StringUtils.isNotEmpty(currentNode.getTextContent())) {
				region.setBeginDate(parseDate(currentNode.getTextContent()));
			} else if (StringUtils.equalsIgnoreCase(currentNode.getNodeName(), "D_END") &&
					StringUtils.isNotEmpty(currentNode.getTextContent())) {
				region.setEndDate(parseDate(currentNode.getTextContent()));
			}
		}
		Validate.isTrue(region.validate(), "Invalid processing document: " + region);
		processBeginAndEndDates(region, createdDictionaries, processedDictionaries);
		return Lists.newArrayList(region);
	}

	@NotNull
	private Date parseDate(@NotNull String stringDate) throws ParseException {
		return DATE_FORMAT.parse(stringDate);
	}

    @Override
    public void create(Region dictionary) {
        regionBean.save(dictionary);
    }

	@Override
	public void update(Region dictionary) {
		regionBean.update(dictionary);
	}
	
	@SuppressWarnings("unchecked")
	private void processBeginAndEndDates(Region region, MultiValueMap createdDictionaries, Map<Long, Region> processedDictionaries) {
		List<Region> oldRegions = regionBean.getRegionByCode(region.getCode());
		if (createdDictionaries.containsKey(region.getCode())) {
			oldRegions.addAll(createdDictionaries.getCollection(region.getCode()));
		}
		for (Region oldRegion : oldRegions) {

			if (oldRegion.getId() != null && processedDictionaries.containsKey(oldRegion.getId())) {
				oldRegion = processedDictionaries.get(oldRegion.getId());
			}

			String errorMessage = printToLogInvalidDate(oldRegion, region);

			Validate.isTrue(!(isPositiveInfinityPoint(region) && isNegativeInfinityPoint(region) &&
					isPositiveInfinityPoint(oldRegion) && isNegativeInfinityPoint(oldRegion)), errorMessage);

			if (isPositiveInfinityPoint(region) && isPositiveInfinityPoint(oldRegion)) {
				Validate.isTrue(!isNegativeInfinityPoint(region) && !isNegativeInfinityPoint(oldRegion) &&
						region.getBeginDate().compareTo(oldRegion.getBeginDate()) > 0, errorMessage);
				oldRegion.setEndDate(DateUtils.addDays(region.getBeginDate(), -1));
				if (oldRegion.getId() != null) {
					processedDictionaries.put(oldRegion.getId(), oldRegion);
				}
			} else if (isNegativeInfinityPoint(region) && (isNegativeInfinityPoint(oldRegion))) {
				Validate.isTrue(!isPositiveInfinityPoint(region) && !isPositiveInfinityPoint(oldRegion) &&
						region.getEndDate().compareTo(oldRegion.getEndDate()) < 0, errorMessage);
				oldRegion.setBeginDate(DateUtils.addDays(region.getEndDate(), 1));
				if (oldRegion.getId() != null) {
					processedDictionaries.put(oldRegion.getId(), oldRegion);
				}
			} else if (isPositiveInfinityPoint(region) && isNegativeInfinityPoint(oldRegion)) {
				Validate.isTrue(region.getEndDate().compareTo(oldRegion.getBeginDate()) > 0, errorMessage);
			} else if (isPositiveInfinityPoint(oldRegion) && isNegativeInfinityPoint(region)) {
				Validate.isTrue(oldRegion.getEndDate().compareTo(region.getBeginDate()) > 0, errorMessage);
			} else if (isNegativeInfinityPoint(region)) {
				pointOnSegment(oldRegion, region.getEndDate(), errorMessage);
			} else if (isNegativeInfinityPoint(oldRegion) && region.getBeginDate().before(oldRegion.getEndDate())) {
				pointOnSegment(region, oldRegion.getEndDate(), errorMessage);
				oldRegion.setBeginDate(DateUtils.addDays(region.getEndDate(), 1));
				if (oldRegion.getId() != null) {
					processedDictionaries.put(oldRegion.getId(), oldRegion);
				}
			} else if (isPositiveInfinityPoint(oldRegion) && region.getEndDate().after(oldRegion.getBeginDate())) {
				pointOnSegment(region, oldRegion.getBeginDate(), errorMessage);
				oldRegion.setEndDate(DateUtils.addDays(region.getBeginDate(), -1));
				if (oldRegion.getId() != null) {
					processedDictionaries.put(oldRegion.getId(), oldRegion);
				}
			} else {
				pointOnSegment(oldRegion, region.getBeginDate(), errorMessage);
				pointOnSegment(region, oldRegion.getBeginDate(), errorMessage);
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
			Validate.isTrue((dictionary.getBeginDate().compareTo(point) * dictionary.getEndDate().compareTo(point)) > 0, errorMessage);
		}
	}

	private String printToLogInvalidDate(AbstractPeriodDictionary oldDictionary, AbstractPeriodDictionary newDictionary) {
		return new StringBuilder("Invalid processing region date. ").
				append("New value: ").
				append(newDictionary).
				append(", old value: ").
				append(oldDictionary).toString();
	}
}
