package org.complitex.flexbuh.admin.importexport.service;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import java.io.*;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 16:30
 */
@Stateless
@ConcurrencyManagement(ConcurrencyManagementType.BEAN)
@TransactionManagement(TransactionManagementType.BEAN)
public class ImportTemplateFileService implements ImportFileService {
	private final static Logger log = LoggerFactory.getLogger(ImportTemplateFileService.class);

    public static final String FILE_ENCODING = "CP1251";

    @EJB
    private TemplateService templateService;

	@Resource
    protected UserTransaction userTransaction;

	@Override
	public void process(Long sessionId, ImportListener listener, File importFile, Date beginDate, Date endDate) {
		listener.begin();
		try{
			userTransaction.begin();
			templateService.save(importFile.getName(), getData(importFile));
			userTransaction.commit();
			listener.completed();
		} catch (Throwable th) {
			listener.cancel();
			try {
				userTransaction.rollback();
			} catch (SystemException e) {
			}
			log.error("Cancel create template: " + importFile.getName(), th);
		}
	}

	@Override
	public void process(Long sessionId, ImportListener listener, String fileName, InputStream inputStream, Date beginDate, Date endDate) {
		 throw new NotImplementedException("use process(ImportListener listener, File importFile, Date beginDate, Date endDate)");
	}

	protected String getData(File file) throws IOException {
        StringBuilder data = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), FILE_ENCODING));

        String line;

        while ((line = br.readLine()) != null){
            data.append(line).append('\n');
        }

        br.close();

        return data.toString();
    }
}
