package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Lists;
import org.complitex.flexbuh.entity.dictionary.Dictionary;
import org.complitex.flexbuh.service.dictionary.DictionaryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.*;
import java.io.File;
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
public abstract class ImportDictionaryXMLService extends ImportXMLService {
	private final static Logger log = LoggerFactory.getLogger(ImportDictionaryXMLService.class);

	private final static int OBJECTS_TRANSACTION_SIZE = 1000;

	@Resource
    protected UserTransaction userTransaction;

	@Override
	public void process(ImportListener listener, File importFile, Date beginDate, Date endDate) {
		listener.begin();

		Date importDate = new Date();
		List<Dictionary> docDictionaries = Lists.newArrayList();
		try {
			org.w3c.dom.Document document = getDocument(importFile);
			processDocument("ROW", beginDate, endDate, importDate, docDictionaries, document);
			processDocument("row", beginDate, endDate, importDate, docDictionaries, document);
			commit(docDictionaries, true);
			activateUploadedRecords(importDate);

			listener.completed();
		} catch (Throwable th) {
			log.warn("Cancel import currency: " + importFile, th);
			listener.cancel();
			deleteUploadedRecords(importDate);
		}
	}

	private void processDocument(String tagName, Date beginDate, Date endDate, Date importDate, List<Dictionary> docDictionaries, Document document) throws ParseException, SystemException, NotSupportedException, RollbackException, HeuristicRollbackException, HeuristicMixedException {
		NodeList nodeRows = document.getElementsByTagName(tagName);
		for (int i = 0; i < nodeRows.getLength(); i++) {
			List<Dictionary> dictionaries = processDictionaryNode(nodeRows.item(i).getChildNodes(), importDate, beginDate, endDate);
			docDictionaries.addAll(dictionaries);
			commit(docDictionaries, false);
		}
	}

	protected abstract List<Dictionary> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate) throws ParseException;

	protected void activateUploadedRecords(Date importDate) {
		try {
			userTransaction.begin();
			getDictionaryBean().activate(importDate);
			userTransaction.commit();
		} catch (Exception e) {
			try{
				userTransaction.rollback();
			} catch (Exception e2) {
			}
			log.error("Failed remove uploaded records. Import date: " + importDate, e);
		}
	}

	protected void deleteUploadedRecords(Date importDate) {
		try {
			userTransaction.begin();
			getDictionaryBean().delete(importDate);
			userTransaction.commit();
		} catch (Exception e) {
			try{
				userTransaction.rollback();
			} catch (Exception e2) {
			}
			log.error("Failed remove uploaded records. Import date: " + importDate, e);
		}
	}

	private void commit(List<Dictionary> dictionaries, boolean finalTransaction)
			throws SystemException, NotSupportedException, RollbackException,
			HeuristicRollbackException, HeuristicMixedException {

		if (dictionaries.size() < OBJECTS_TRANSACTION_SIZE && !finalTransaction) {
			return;
		}
		userTransaction.begin();
		try{
			for (Dictionary dictionary : dictionaries) {
				getDictionaryBean().create(dictionary);
			}
		} catch (Throwable th) {
			log.error("Rollback user transaction");
			userTransaction.rollback();
			throw th;
		}
		userTransaction.commit();
		log.debug("Commit documents");
		dictionaries.clear();
	}

	abstract protected DictionaryBean getDictionaryBean();
}
