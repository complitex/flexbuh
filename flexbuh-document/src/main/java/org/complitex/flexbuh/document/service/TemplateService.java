package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.template.TemplateXSD;
import org.complitex.flexbuh.common.service.TemplateBean;
import org.complitex.flexbuh.common.xml.LSInputImpl;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.exception.CreateDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.08.11 16:22
 */
@Stateless
public class TemplateService {
    private final static Logger log = LoggerFactory.getLogger(TemplateService.class);

    @EJB
    private TemplateBean templateBean;

    @EJB
    private DeclarationService declarationService;

    public Document getTemplate(String templateName, Declaration declaration) throws CreateDocumentException {
        try {
            return declarationService.getDocument(declaration, templateBean.getTemplateXSL(templateName));
        } catch (Exception e) {
            throw new CreateDocumentException(e);
        }
    }

    public Document createDocument(String data) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder.parse(new InputSource(new StringReader(data)));
    }

    public Document getTemplateXSDDocument(String templateName) throws CreateDocumentException {
        try {
            return createDocument(templateBean.getTemplateXSD(templateName).getData());
        } catch (Exception e) {
            throw new CreateDocumentException(e);
        }
    }

    public Document getTemplateControlDocument(String templateName) throws IOException, SAXException, ParserConfigurationException {
        return createDocument(templateBean.getTemplateControl(templateName).getData());
    }

    public Schema getSchema(String templateName) throws SAXException {
        final TemplateXSD common = templateBean.getTemplateXSD("common_types");
        TemplateXSD xsd = templateBean.getTemplateXSD(templateName);

        Source xsdSource = new StreamSource(new StringReader(xsd.getData()), templateName + ".xsd");

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        factory.setResourceResolver(new LSResourceResolver() {
            @Override
            public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
                if ("common_types.xsd".equals(systemId)){
                    LSInputImpl lsInput = new LSInputImpl();
                    lsInput.setStringData(common.getData());

                    return lsInput;
                }

                return null;
            }
        });

        return factory.newSchema(xsdSource);
    }
}
