package org.complitex.flexbuh.document.web;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.TemplateXSD;
import org.complitex.flexbuh.document.entity.TemplateXSL;
import org.complitex.flexbuh.document.service.TemplateBean;
import org.complitex.flexbuh.document.web.model.DeclarationBooleanModel;
import org.complitex.flexbuh.document.web.model.DeclarationChoiceModel;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;
import org.complitex.flexbuh.web.component.RadioSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.ejb.EJB;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.NamespaceContext;
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
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:58
 */
public class DeclarationFormComponent extends WebMarkupContainer implements IMarkupResourceStreamProvider{
    private final static Logger log = LoggerFactory.getLogger(DeclarationFormComponent.class);

    @EJB
    private TemplateBean templateBean;

    private MarkupResourceStream markupResourceStream;

    private Map<String, RadioSet<String>> radioSetMap = new HashMap<String, RadioSet<String>>();

    public DeclarationFormComponent(String id, String templateName, Declaration declaration){
        super(id);

        try {
            init(templateName, declaration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void init(String templateName, Declaration declaration)
            throws TransformerException, JAXBException, ParserConfigurationException, IOException, SAXException, XPathFactoryConfigurationException {
        Document template = getDocument(templateName, declaration);
        Document schema = getSchema(templateName);
        XPath xPath = getSchemaXPath();

        NodeList bodyNodeList = template.getElementsByTagName("body");

        //Form
        Element div = (Element) template.renameNode(bodyNodeList.item(0), null, "div");
        div.setAttribute("xmlns:wicket", "http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd");
        div.setAttribute("wicket:id", "container");

        WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        //Input
        NodeList inputList = div.getElementsByTagName("input");

        for (int i=0; i < inputList.getLength(); ++i){
            addFormComponent((Element) inputList.item(i), declaration, container, template, schema, xPath);
        }

        //Markup
        StringResourceStream stringResourceStream = new StringResourceStream(getString(div), "text/html");
        stringResourceStream.setCharset(Charset.forName("UTF-8"));

        markupResourceStream = new MarkupResourceStream(stringResourceStream);
    }

    private Document getDocument(String templateName, Declaration declaration) throws TransformerException, JAXBException, ParserConfigurationException, IOException, SAXException {
        TemplateXSL templateXSL = templateBean.getTemplateXSL(templateName);

        //XSL transformation
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        StreamResult result = new StreamResult(new StringWriter());

        Transformer transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(templateXSL.getData())));

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        transformer.transform(new StreamSource(new StringReader(getDeclarationXML(declaration))), result);

        //Parse document
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder.parse(new InputSource(new StringReader(result.getWriter().toString())));
    }

    private Document getSchema(String templateName) throws ParserConfigurationException, IOException, SAXException {
        TemplateXSD templateXSD = templateBean.getTemplateXSD(templateName);

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder.parse(new InputSource(new StringReader(templateXSD.getData())));
    }

    private XPath getSchemaXPath() {
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

    private String getDeclarationXML(Declaration declaration) throws JAXBException {
        declaration.prepareXmlValues();

        JAXBContext context = JAXBContext.newInstance(Declaration.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        StringWriter writer = new StringWriter();

        m.marshal(declaration, writer);

        return writer.toString();
    }

    public String getString(Node node) throws TransformerException {
        DOMSource domSource = new DOMSource(node);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.transform(domSource, result);
        return writer.toString();
    }

    @Override
    protected void onComponentTagBody(MarkupStream markupStream, ComponentTag openTag) {
        //todo cache
        try {
            Markup markup = getApplication().getMarkupSettings().getMarkupParserFactory().newMarkupParser(markupResourceStream).parse();

            MarkupStream associatedMarkupStream = new MarkupStream(markup);

            renderComponentTagBody(associatedMarkupStream, (ComponentTag) associatedMarkupStream.get());
        } catch (Exception e) {
            log.error("Ошибка создания страницы", e);
        }
    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        return markupResourceStream;
    }

    private void addFormComponent(Element input, Declaration declaration, WebMarkupContainer container, Document template, Document schema,
                                  XPath xPath){
        try {
            String id = input.getAttribute("id");

            //set wicket id markup
            input.setAttribute("wicket:id", id);

            //lookup schema rules
            Element schemaElement = (Element) xPath.evaluate("//xs:element[@name='" + id + "']", schema, XPathConstants.NODE);
            String schemaType = schemaElement.getAttribute("type");

            if ("DGchk".equals(schemaType)){
                Element schemaChoice = (Element) schemaElement.getParentNode();

                if ("xs:choice".equals(schemaChoice.getTagName())){
                    //find parent for component
                    RadioSet<String> radioSet = radioSetMap.get(id);

                    //create choice
                    if (radioSet == null){
                        String rId = "radio_"+id;

                        //create radio set markup
                        Element radioSetElement = template.createElement("span");
                        radioSetElement.setAttribute("wicket:id", rId);
                        input.getParentNode().appendChild(radioSetElement);

                        //add radio set component
                        radioSet = new RadioSet<String>(rId, new DeclarationChoiceModel(id, declaration));
                        container.add(radioSet);

                        //child nodes to radio set
                        NodeList children = schemaChoice.getElementsByTagName("xs:element");
                        for (int i=0; i < children.getLength() ; ++i){
                            Element el = (Element) children.item(i);
                            radioSetMap.put(el.getAttribute("name"), radioSet);
                        }
                    }

                    input.setAttribute("type", "radio");

                    Radio<String> radio = new Radio<String>(id, new Model<String>(id), radioSet);
                    radioSet.addRadio(radio);

                    container.add(radio);
                }else{
                    input.setAttribute("type", "checkbox");
                    container.add(new CheckBox(id, new DeclarationBooleanModel(id, declaration)));
                }
            }else{
                input.setAttribute("type", "text");
                container.add(new TextField<String>(id, new DeclarationStringModel(id, declaration)));
            }
        } catch (XPathExpressionException e) {
            log.error("Ошибка добавления компонента формы ввода декларации", e);
        }
    }

    private Element getElementById(String id, Node node, XPath xPath) throws XPathExpressionException {
        return (Element) xPath.evaluate("//[@id = '" + id + "']", node, XPathConstants.NODE);
    }
}
