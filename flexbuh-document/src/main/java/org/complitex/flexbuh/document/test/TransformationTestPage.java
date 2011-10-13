package org.complitex.flexbuh.document.test;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.entity.template.TemplateXSL;
import org.complitex.flexbuh.service.TemplateBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ejb.EJB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.08.11 16:21
 */
public class TransformationTestPage extends WebPage {
    @EJB
    private TemplateBean templateBean;

    public TransformationTestPage() throws TransformerException, ParserConfigurationException, IOException, SAXException {
        TemplateXSL templateXSL = templateBean.getTemplateXSL("F0100203");

        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        StreamResult result = new StreamResult(new StringWriter());
        Transformer transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(templateXSL.getData())));
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        transformer.transform(new StreamSource(new StringReader(getDeclarationXML())), result);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        Document document = documentBuilder.parse(new InputSource(new StringReader(result.getWriter().toString())));

        Element root = document.getDocumentElement();
        root.setAttribute("xmlns:wicket", "http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd");

        NodeList nodeList = document.getElementsByTagName("input");

        for (int i=0; i < nodeList.getLength(); ++i){
            Element element = (Element) nodeList.item(i);

            String id = element.getAttribute("id");

            element.setAttribute("wicket:id", id);
        }

        add(new Label("template", "hello"));

//        getRequestCycle().setRequestTarget(new StringRequestTarget("text/html",  "UTF-8", getStringFromDocument(document)));
    }

    public static String getStringFromDocument(Document doc) throws TransformerException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    public static Declaration getDeclaration(){
        Declaration declaration = new Declaration();

        declaration.getHead().setTin(11);
        declaration.getHead().setCDoc("c_doc_1");
        declaration.getHead().setCDocCnt(1);
        declaration.getHead().setCDocStan(2);
        declaration.getHead().setCDocSub("c_doc_sub_4");
        declaration.getHead().setCDocType(3);
        declaration.getHead().setCDocVer(4);
        declaration.getHead().setCRaj(4);
        declaration.getHead().setCReg(5);
        declaration.getHead().setCStiOrig(33);
        declaration.getHead().setDFill("d_fill_10");
        declaration.getHead().setPeriodMonth(6);
        declaration.getHead().setPeriodType(7);
        declaration.getHead().setPeriodYear(8);
        declaration.getHead().setSoftware("software_14");

        declaration.addDeclarationValue(new DeclarationValue(1, "name1", null));
        declaration.addDeclarationValue(new DeclarationValue(null, "H001G1I", "P1"));
        declaration.addDeclarationValue(new DeclarationValue(null, "HTIN", "P2"));
        declaration.addDeclarationValue(new DeclarationValue(null, "HLOC", "P3"));

        return declaration;
    }

    private String getDeclarationXML(){
        Declaration declaration = getDeclaration();

        declaration.prepareXmlValues();

        try {
            JAXBContext context = JAXBContext.newInstance(Declaration.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            StringWriter writer = new StringWriter();

            m.marshal(declaration, writer);

            return writer.toString();

        } catch (JAXBException e) {
            return "";
        }
    }


}
