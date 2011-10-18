package org.complitex.flexbuh.service;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Pavel Sknar
 *         Date: 15.08.11 12:01
 */
public abstract class ImportXMLService implements ImportFileService {

	protected Document getDocument(InputStream inputStream) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return documentBuilder.parse(inputStream);
    }

}
