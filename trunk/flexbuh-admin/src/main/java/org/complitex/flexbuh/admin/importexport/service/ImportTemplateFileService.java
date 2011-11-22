package org.complitex.flexbuh.admin.importexport.service;

import org.complitex.flexbuh.common.entity.template.*;
import org.complitex.flexbuh.common.service.ImportFileService;
import org.complitex.flexbuh.common.service.ImportListener;
import org.complitex.flexbuh.common.service.TemplateBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import javax.validation.constraints.NotNull;
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

    public static final String DEFAULT_FILE_ENCODING = "CP1251";

    @EJB
    private TemplateBean templateBean;

    @Resource
    protected UserTransaction userTransaction;

	@Override
	public void process(Long sessionId, ImportListener listener, String fileName, InputStream inputStream, Date beginDate, Date endDate) {
        listener.begin();
        try{
            userTransaction.begin();
            save(fileName, getData(inputStream));
            userTransaction.commit();
            listener.completed();
        } catch (Throwable th) {
            listener.cancel();
            try {
                userTransaction.rollback();
            } catch (SystemException e) {
            }
            log.error("Cancel create template: " + fileName, th);
        }
    }

    protected String getData(InputStream inputStream) throws IOException {
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        BufferedReader br = new BufferedReader(new InputStreamReader(bufferedInputStream));

        bufferedInputStream.mark(16384);

        if (br.readLine().toUpperCase().contains("UTF-8")) {
            bufferedInputStream.reset();

            return getData(bufferedInputStream, "UTF-8");
        }

        bufferedInputStream.reset();

        return getData(bufferedInputStream, DEFAULT_FILE_ENCODING);
    }

	protected String getData(InputStream inputStream, String encoding) throws IOException {
        StringBuilder data = new StringBuilder();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, encoding));

        String line;

        while ((line = br.readLine()) != null){
            data.append(line).append('\n');
        }

        br.close();

        return data.toString();
    }

    public AbstractTemplate save(@NotNull String fileName, String data) {
		String extension = getFileExtension(fileName).toLowerCase();
		String name = getName(fileName);
		Date uploadDate = new Date();

		AbstractTemplate template;

        switch (extension){
            case "xml":
                template = new TemplateControl(name, data, uploadDate);
                templateBean.save((TemplateControl) template);
                break;
            case "xsd":
                template = new TemplateXSD(name, data, uploadDate);
                templateBean.save((TemplateXSD) template);
                break;
            case "xsl":
                template = new TemplateXSL(name, data, uploadDate);
                templateBean.save((TemplateXSL) template);
                break;
            case "fo":
                template = new TemplateFO(name, data, uploadDate);
                templateBean.save((TemplateFO) template);
                break;
            default:
                return null;
        }

		return template;
	}

	private String getFileExtension(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.') + 1);
	}

	private String getName(@NotNull String fileName){
        return fileName.substring(0, fileName.lastIndexOf('.')).toUpperCase();
    }
}
