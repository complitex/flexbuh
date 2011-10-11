package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.complitex.flexbuh.entity.dictionary.Region;
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
	protected List<Region> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate) throws ParseException {
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
}
