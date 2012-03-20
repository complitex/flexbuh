package org.complitex.flexbuh.common.service;

import java.io.InputStream;
import java.util.Date;
import java.util.Locale;

/**
 * @author Pavel Sknar
 *         Date: 15.08.11 11:16
 */
public interface ImportFileService<T> {
    void process(Long sessionId, ImportListener<T> listener, String fileName, InputStream inputStream, Locale locale,
                 Date beginDate, Date endDate);

}
