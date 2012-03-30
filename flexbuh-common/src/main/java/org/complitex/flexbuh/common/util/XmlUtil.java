package org.complitex.flexbuh.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.08.11 16:55
 */
public class XmlUtil {
    private final static Logger log = LoggerFactory.getLogger(XmlUtil.class);

    public static String getString(Node node){
        try {
            DOMSource domSource = new DOMSource(node);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            return writer.toString();
        } catch (TransformerException e) {
            log.error("Ошибка получения строки по DOM");
            throw new RuntimeException(e);
        }
    }

    public static XPath newSchemaXPath() {
        XPathFactory factory = XPathFactory.newInstance();
        XPath xPath = factory.newXPath();

        xPath.setNamespaceContext(new NamespaceContext(){
            @Override
            public String getNamespaceURI(String prefix) {
                return XMLConstants.W3C_XML_SCHEMA_NS_URI;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return "xs";
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return Arrays.asList("xs").iterator();
            }
        });

        return xPath;
    }

    public static XPath newXPath(){
        return XPathFactory.newInstance().newXPath();
    }

    public static Element getElementByAttribute(String attributeName, String attributeValue, Node node, XPath xPath){
        try {
            return (Element) xPath.evaluate(".//*[@" + attributeName + "= '" + attributeValue + "']", node, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            log.error("Ошибка получения элемента по атрибуту", e);
        }

        return null;
    }

    public static Element getElementById(String id, Node node, XPath xPath){
        return getElementByAttribute("id", id, node, xPath);
    }

    public static Element getElementByName(String name, Node node, XPath xPath){
        return getElementByAttribute("name", name, node, xPath);
    }

    public static NodeList getElementsByAttribute(String attributeName, String attributeValue, Node node, XPath xPath){
        try {
            return (NodeList) xPath.evaluate(".//*[@" + attributeName + "= '" + attributeValue + "']", node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.error("Ошибка получения элемента по атрибуту", e);
        }

        return null;
    }

    public static NodeList getElementsByAttribute(String attributeName, Node node, XPath xPath){
        try {
            return (NodeList) xPath.evaluate(".//*[@" + attributeName + "]", node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.error("Ошибка получения элемента по атрибуту", e);
        }

        return null;
    }


    public static NodeList getElementsById(String id, Node node, XPath xPath){
        return getElementsByAttribute("id", id, node, xPath);
    }

    public static Node getParent(String tagName, Node node){
        Node parent = node.getParentNode();

        while (parent != null && !tagName.equalsIgnoreCase(parent.getNodeName())){
            parent = parent.getParentNode();
        }

        return parent;
    }

    public static Node getParentById(String id, Node node){
        Node parent = node.getParentNode();

        while (parent != null){
            if (parent instanceof Element && id.equalsIgnoreCase(((Element) parent).getAttribute("id"))) {
                return parent;
            } else {
                parent = parent.getParentNode();
            }
        }

        return parent;
    }

    public static <T> void writeXml(Class<T> _class, T jaxbElement, OutputStream outputStream, String encoding) throws JAXBException {
        JAXBContext context = JAXBContext.newInstance(_class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);

        marshaller.marshal(jaxbElement, outputStream);
    }

    public static <T> void writeXml(Class<T> _class, T jaxbElement, OutputStream outputStream) throws JAXBException {
        writeXml(_class, jaxbElement, outputStream, "UTF-8");
    }
}
