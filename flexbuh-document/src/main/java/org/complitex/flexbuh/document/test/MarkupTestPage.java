package org.complitex.flexbuh.document.test;

import org.apache.wicket.markup.html.WebPage;
import org.complitex.flexbuh.document.web.DeclarationEditPanel;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 18:20
 */
public class MarkupTestPage extends WebPage{
    public MarkupTestPage() throws TransformerException, JAXBException, IOException, SAXException, ParserConfigurationException {
        add(new DeclarationEditPanel("component",  TransformationTestPage.getDeclaration()));
    }
}
