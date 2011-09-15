package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.complitex.flexbuh.entity.dictionary.AbstractDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 26.08.11 14:44
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public abstract class ImportDictionaryXMLService<T extends AbstractDictionary> extends ImportXMLService {
	private final static Logger log = LoggerFactory.getLogger(ImportDictionaryXMLService.class);

	@Resource
    protected UserTransaction userTransaction;

	@Override
	public void process(Long sessionId, ImportListener listener, File importFile, Date beginDate, Date endDate) {
		try {
			process(sessionId, listener, importFile.getName(), new FileInputStream(importFile), beginDate, endDate);
		} catch (FileNotFoundException e) {
			listener.begin();
			log.warn("Can not find file: " + importFile, e);
			listener.cancel();
		}
	}

	@Override
	public void process(Long sessionId, ImportListener listener, String name, InputStream inputStream, Date beginDate, Date endDate) {
		listener.begin();

		Date importDate = new Date();
		List<T> docDictionaries = Lists.newArrayList();
		try {
			org.w3c.dom.Document document = getDocument(inputStream);
			processDocument("ROW", beginDate, endDate, importDate, docDictionaries, document);
			processDocument("row", beginDate, endDate, importDate, docDictionaries, document);

            //save
            userTransaction.begin();
            try{
                for (T dictionary : docDictionaries) {
                    create(dictionary);
                }
            } catch (Throwable th) {
                log.error("Rollback user transaction");
                userTransaction.rollback();
                throw th;
            }
            userTransaction.commit();

			listener.completed();
		} catch (Throwable th) {
			log.warn("Cancel import dictionary: " + name, th);
			listener.cancel();
		}
	}

	private void processDocument(String tagName, Date beginDate, Date endDate, Date importDate, List<T> docDictionaries, Document document) throws ParseException, SystemException, NotSupportedException, RollbackException, HeuristicRollbackException, HeuristicMixedException {
		NodeList nodeRows = document.getElementsByTagName(tagName);
		for (int i = 0; i < nodeRows.getLength(); i++) {
			List<T> dictionaries = processDictionaryNode(nodeRows.item(i).getChildNodes(), importDate, beginDate, endDate);
			docDictionaries.addAll(dictionaries);
		}
	}

	protected abstract List<T> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate) throws ParseException;

    public abstract void create(T dictionary);
}
