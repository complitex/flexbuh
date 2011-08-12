package org.complitex.flexbuh.document.web;

import org.apache.wicket.MarkupContainer;
import org.apache.wicket.markup.*;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.TemplateXSL;
import org.complitex.flexbuh.document.service.TemplateBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
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
import java.nio.charset.Charset;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.08.11 15:58
 */
public class DeclarationFormComponent extends WebMarkupContainer implements IMarkupResourceStreamProvider{
    @EJB
    private TemplateBean templateBean;

    private MarkupResourceStream markupResourceStream;

    public DeclarationFormComponent(String id, String templateName, Declaration declaration, Map<String, IModel<String>> model){
        super(id);

        try {
            init(templateName, declaration, model);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void init(String templateName, Declaration declaration, Map<String, IModel<String>> model)
            throws TransformerException, JAXBException, ParserConfigurationException, IOException, SAXException {
        Document document = getDocument(templateName, declaration);

        NodeList bodyNodeList = document.getElementsByTagName("body");

        //Form
        Element div = (Element) document.renameNode(bodyNodeList.item(0), null, "div");
        div.setAttribute("xmlns:wicket", "http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd");
        div.setAttribute("wicket:id", "container");

        WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);

        //Input
        NodeList inputList = div.getElementsByTagName("input");

        for (int i=0; i < inputList.getLength(); ++i){
            Element input = (Element) inputList.item(i);
            String id = input.getAttribute("id");

            input.setAttribute("wicket:id", id);

            //fix type
            if (input.getAttribute("type").equals("textbox")){
                input.setAttribute("type", "text");
            }

            IModel<String> m = new Model<String>();
            model.put(id, m);

            container.add(new TextField<String>(id, m));
        }

        //Markup
        StringResourceStream stringResourceStream = new StringResourceStream(getString(div), "text/html");
        stringResourceStream.setCharset(Charset.forName("UTF-8"));

        markupResourceStream = new MarkupResourceStream(stringResourceStream);

//        getApplication().getMarkupSettings().getMarkupCache().removeMarkup()
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
            Markup markup = getApplication().getMarkupSettings().getMarkupParserFactory().newMarkupParser(
                    markupResourceStream).parse();

            MarkupStream associatedMarkupStream =  new MarkupStream(markup);

            renderComponentTagBody(associatedMarkupStream, (ComponentTag) associatedMarkupStream.get());

        } catch (Exception e) {
            //wtf
        }

    }

    @Override
    public IResourceStream getMarkupResourceStream(MarkupContainer container, Class<?> containerClass) {
        return markupResourceStream;
    }
}
