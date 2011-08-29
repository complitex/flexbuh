package org.complitex.flexbuh.admin.importexport.service;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Pavel Sknar
 *         Date: 15.08.11 12:01
 */
public abstract class ImportXMLService implements ImportFileService {

	protected Document getDocument(File file) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		return documentBuilder.parse(new FileInputStream(file));
    }

}
