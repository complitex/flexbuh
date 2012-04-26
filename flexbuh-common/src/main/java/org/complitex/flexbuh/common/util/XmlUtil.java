package org.complitex.flexbuh.common.util;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
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

    private static final ThreadLocal<XStream> xStreamThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<XPath> xPathThreadLocal = new ThreadLocal<>();
    private static final ThreadLocal<XPath> schemaXPathThreadLocal = new ThreadLocal<>();

    public static XStream getXStream(){
        XStream xStream = xStreamThreadLocal.get();

        if (xStream == null){
            xStream = new XStream(new DomDriver());
            xStreamThreadLocal.set(xStream);
        }

        return xStream;
    }

    public static XPath getXPath(){
        XPath xPath = xPathThreadLocal.get();

        if (xPath == null){
            xPath = XPathFactory.newInstance().newXPath();
            xPathThreadLocal.set(xPath);
        }

        return xPath;
    }

    public static XPath getSchemaXPath(){
        XPath xPath = schemaXPathThreadLocal.get();

        if (xPath == null){
            xPath = XPathFactory.newInstance().newXPath();

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
            schemaXPathThreadLocal.set(xPath);
        }

        return xPath;
    }

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

    public static Element getElementByAttribute(String attributeName, String attributeValue, Node node){
        try {
            return (Element) getXPath().evaluate(".//*[@" + attributeName + "= '" + attributeValue + "']", node,
                    XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            log.error("Ошибка получения элемента по атрибуту", e);
        }

        return null;
    }

    public static Element getElementById(String id, Node node){
        return getElementByAttribute("id", id, node);
    }

    public static Element getElementByName(String name, Node node){
        return getElementByAttribute("name", name, node);
    }

    public static NodeList getElementsByAttribute(String attributeName, String attributeValue, Node node){
        try {
            return (NodeList) getXPath().evaluate(".//*[@" + attributeName + "= '" + attributeValue + "']", node,
                    XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.error("Ошибка получения элемента по атрибуту", e);
        }

        return null;
    }

    public static NodeList getElementsByAttribute(String attributeName, Node node){
        try {
            return (NodeList) getXPath().evaluate(".//*[@" + attributeName + "]", node, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.error("Ошибка получения элемента по атрибуту", e);
        }

        return null;
    }


    public static NodeList getElementsById(String id, Node node){
        return getElementsByAttribute("id", id, node);
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
