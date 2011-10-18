package org.complitex.flexbuh.service;

/**
 * @author Pavel Sknar
 *         Date: 15.08.11 11:33
 */
public interface ImportListener {

	void begin();

	void completed();

	void completedWithError();

	void cancel();

	ImportListener getChildImportListener(Object o);

}
