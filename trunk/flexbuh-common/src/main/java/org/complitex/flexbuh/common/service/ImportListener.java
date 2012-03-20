package org.complitex.flexbuh.common.service;

/**
 * @author Pavel Sknar
 *         Date: 15.08.11 11:33
 */
public interface ImportListener<T> {
	void begin();

    void processed(T object);

    void skip(T object);

	void completed();

	void error();
}
