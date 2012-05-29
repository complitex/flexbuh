package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.template.TemplateXML;
import org.complitex.flexbuh.common.exception.AbstractException;
import org.complitex.flexbuh.common.service.TemplateXMLBean;
import org.complitex.flexbuh.common.xml.LSInputImpl;
import org.complitex.flexbuh.document.entity.Declaration;
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

import static org.complitex.flexbuh.common.entity.template.TemplateXMLType.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.08.11 16:22
 */
@Stateless
public class TemplateXMLService {
    private final static Logger log = LoggerFactory.getLogger(TemplateXMLService.class);

    @EJB
    private TemplateXMLBean templateXMLBean;

    @EJB
    private DeclarationService declarationService;

    public Document getTemplate(String templateName, Declaration declaration){
        try {
            TemplateXML xsl = templateXMLBean.getTemplateXML(XSL, templateName);

            if (xsl == null){
                throw new AbstractException("Шаблон {0} не найден", templateName){};
            }

            return declarationService.getDocument(declaration, xsl);
        } catch (Exception e) {
            log.error("Ошибка загрузки шаблона", e);

            throw new RuntimeException(e);
        }
    }

    public Document createDocument(String data) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder.parse(new InputSource(new StringReader(data)));
    }

    public Document getTemplateXSDDocument(String templateName){
        try {
            return createDocument(templateXMLBean.getTemplateXML(XSD, templateName).getData());
        } catch (Exception e) {
            log.error("Шаблон документа не найден");

            throw new RuntimeException(e);
        }
    }

    public Document getTemplateControlDocument(String templateName) throws IOException, SAXException, ParserConfigurationException {
        return createDocument(templateXMLBean.getTemplateXML(CONTROL, templateName).getData());
    }

    public Schema getSchema(String templateName) throws SAXException {
        TemplateXML xsd = templateXMLBean.getTemplateXML(XSD, templateName);

        Source xsdSource = new StreamSource(new StringReader(xsd.getData()), templateName + ".xsd");

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        factory.setResourceResolver(new LSResourceResolver() {
            @Override
            public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
                if ("common_types.xsd".equals(systemId)){
                    TemplateXML common = templateXMLBean.getTemplateXML(XSD, "common_types");

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
