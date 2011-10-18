package org.complitex.flexbuh.admin.importexport.service;

import com.google.common.collect.Maps;
import org.apache.commons.collections.map.MultiValueMap;
import org.complitex.flexbuh.entity.dictionary.AbstractDictionary;
import org.complitex.flexbuh.service.ImportListener;
import org.complitex.flexbuh.service.ImportXMLService;
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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

	@SuppressWarnings("unchecked")
	@Override
	public void process(Long sessionId, ImportListener listener, String name, InputStream inputStream, Date beginDate, Date endDate) {
		listener.begin();

		Date importDate = new Date();
		MultiValueMap createdDictionaries = new MultiValueMap();
		Map<Long, T> processedDictionaries = Maps.newHashMap();
		try {
			org.w3c.dom.Document document = getDocument(inputStream);
			processDocument("ROW", beginDate, endDate, importDate, createdDictionaries, processedDictionaries, document);
			processDocument("row", beginDate, endDate, importDate, createdDictionaries, processedDictionaries, document);

            //save
            userTransaction.begin();
            try{
                for (T dictionary : (Collection<T>)createdDictionaries.values()) {
                    save(dictionary);
                }
				for (T dictionary : processedDictionaries.values()) {
					save(dictionary);
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

	private void processDocument(String tagName, Date beginDate, Date endDate, Date importDate, MultiValueMap createdDictionaries, Map<Long, T> processedDictionaries, Document document) throws ParseException, SystemException, NotSupportedException, RollbackException, HeuristicRollbackException, HeuristicMixedException {
		NodeList nodeRows = document.getElementsByTagName(tagName);
		for (int i = 0; i < nodeRows.getLength(); i++) {
			List<T> dictionaries = processDictionaryNode(nodeRows.item(i).getChildNodes(), importDate, beginDate, endDate, createdDictionaries, processedDictionaries);
			for (T dictionary : dictionaries) {
				createdDictionaries.put(dictionary.hashCode(), dictionary);
			}
		}
	}

	protected abstract List<T> processDictionaryNode(NodeList contentNodeRow, Date importDate, Date beginDate, Date endDate, MultiValueMap createdDictionaries, Map<Long, T> processedDictionaries) throws ParseException;

	private void save(T dictionary) {
		if (dictionary.getId() == null) {
        	create(dictionary);
		} else {
			update(dictionary);
		}
	}

    public abstract void create(T dictionary);

    public abstract void update(T dictionary);
}
