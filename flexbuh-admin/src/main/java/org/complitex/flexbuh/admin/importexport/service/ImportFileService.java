package org.complitex.flexbuh.admin.importexport.service;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 15.08.11 11:16
 */
public interface ImportFileService {

	void process(ImportListener listener, File importFile, Date beginDate, Date endDate);

	void process(ImportListener listener, String fileName, InputStream inputStream, Date beginDate, Date endDate);

}
