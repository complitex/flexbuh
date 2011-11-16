package org.complitex.flexbuh.common.logging;

import java.io.Serializable;

/**
 * @author Pavel Sknar
 *         Date: 03.11.11 16:31
 */
public interface EventProperty extends Serializable {

	String getName();

	String getValue();
}
