package org.complitex.flexbuh.common.service;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import javax.ejb.Singleton;
import java.io.OutputStream;
import java.io.Writer;

/**
 * @author Pavel Sknar
 *         Date: 17.11.11 14:09
 */
@Singleton(name = "Marshaller")
public class Marshaller {

	private static final XStream xstream = new XStream(new DomDriver());

	public String marshal(Object obj) {
		return xstream.toXML(obj);
	}

	public void marshal(Object obj, Writer out) {
		xstream.toXML(obj, out);
	}

	public void marshal(Object obj, OutputStream out) {
		xstream.toXML(obj, out);
	}
}
