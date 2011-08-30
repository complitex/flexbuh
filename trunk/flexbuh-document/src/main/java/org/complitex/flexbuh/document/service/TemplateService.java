package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.Rule;
import org.complitex.flexbuh.document.util.DeclarationUtil;
import org.complitex.flexbuh.entity.TemplateXSL;
import org.complitex.flexbuh.service.TemplateBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
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

    public Document getDocument(String templateName, Declaration declaration)
            throws TransformerException, JAXBException, ParserConfigurationException, IOException, SAXException {
        TemplateXSL templateXSL = templateBean.getTemplateXSL(templateName);

        //XSL transformation
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        StreamResult result = new StreamResult(new StringWriter());

        Transformer transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(templateXSL.getData())));

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        transformer.transform(new StreamSource(new StringReader(DeclarationUtil.getDeclarationXML(declaration))), result);

        //Parse document
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder.parse(new InputSource(new StringReader(result.getWriter().toString())));
    }

    public Document getDocument(String data) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder.parse(new InputSource(new StringReader(data)));
    }

    public Document getSchema(String templateName) throws ParserConfigurationException, IOException, SAXException {
        return getDocument(templateBean.getTemplateXSD(templateName).getData());
    }

    public Document getControl(String templateName) throws IOException, SAXException, ParserConfigurationException {
        return getDocument(templateBean.getTemplateControl(templateName).getData());
    }

    public Unmarshaller getRuleUnmarshaller() throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(Rule.class);

        return context.createUnmarshaller();
    }

    public Map<String, Rule> getRules(String templateName, Unmarshaller unmarshaller)
            throws IOException, SAXException, ParserConfigurationException, JAXBException {
        Map<String, Rule> rules = new LinkedHashMap<>();

        Document controls = getControl(templateName);

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
}
