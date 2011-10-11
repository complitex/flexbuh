package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.complitex.flexbuh.entity.Language;
import org.complitex.flexbuh.entity.dictionary.AbstractDictionary;
import org.complitex.flexbuh.entity.dictionary.Currency;
import org.complitex.flexbuh.service.dictionary.CurrencyBean;
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
    private CurrencyBean currencyBean;

    private Language ukLang = null;
    private Language ruLang = null;

    @Override
    public void process(Long sessionId, ImportListener listener, File importFile, Date beginDate, Date endDate) {
        super.process(sessionId, listener, importFile, beginDate, endDate);
    }

    @Override
    protected List<AbstractDictionary> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate) throws ParseException {
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

        return Lists.newArrayList((AbstractDictionary)currency);
    }

    @Override
    public void create(AbstractDictionary abstractDictionary) {
        currencyBean.save((Currency) abstractDictionary);
    }

    @NotNull
    private Date parseDate(@NotNull String stringDate) throws ParseException {
        return DATE_FORMAT.parse(stringDate);
    }
}
