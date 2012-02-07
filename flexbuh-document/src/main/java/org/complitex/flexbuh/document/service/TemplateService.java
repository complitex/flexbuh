package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.template.TemplateXSD;
import org.complitex.flexbuh.common.service.TemplateBean;
import org.complitex.flexbuh.common.xml.LSInputImpl;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.Rule;
import org.complitex.flexbuh.document.exception.CreateDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public Unmarshaller getRuleUnmarshaller() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Rule.class);

        return context.createUnmarshaller();
    }

    public Map<String, Rule> getRules(String templateName, Unmarshaller unmarshaller)
            throws IOException, SAXException, ParserConfigurationException, JAXBException {
        Map<String, Rule> rules = new LinkedHashMap<>();

        Document controls = getTemplateControlDocument(templateName);

        NodeList ruleNodeList = controls.getElementsByTagName("rule");

        for (int i = 0; i < ruleNodeList.getLength(); ++i){
            Node ruleNode  = ruleNodeList.item(i);

            Rule rule = (Rule) unmarshaller.unmarshal(ruleNode);

            if ("=".equals(rule.getSign()) && rule.getExpression() != null) {
                rules.put(rule.getCDocRowC().replace("^", ""), rule);
            }
        }

        return rules;
    }

    public Map<String, Rule> getRules(String templateName) throws JAXBException, IOException, SAXException, ParserConfigurationException {
        return getRules(templateName, getRuleUnmarshaller());
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
