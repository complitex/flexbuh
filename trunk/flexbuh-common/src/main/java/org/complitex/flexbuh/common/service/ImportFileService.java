package org.complitex.flexbuh.common.service;

import java.io.File;
import java.io.InputStream;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 15.08.11 11:16
 */
public interface ImportFileService {

	void process(Long sessionId, ImportListener listener, File importFile, Date beginDate, Date endDate);

	void process(Long sessionId, ImportListener listener, String fileName, InputStream inputStream, Date beginDate, Date endDate);

}
