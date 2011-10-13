package org.complitex.flexbuh.document.util;

import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.entity.template.AbstractTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.08.11 16:56
 */
public class DeclarationUtil {
    private static final Logger log = LoggerFactory.getLogger(DeclarationUtil.class);

    public static String getString(Declaration declaration) throws JAXBException {
        declaration.prepareXmlValues();

        JAXBContext context = JAXBContext.newInstance(Declaration.class, DeclarationValue.class);
        Marshaller m = context.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, declaration.getTemplateName() + ".xsd");

        StringWriter writer = new StringWriter();

        m.marshal(declaration, writer);

        return writer.toString();
    }
    
    public static String getString(Declaration declaration, AbstractTemplate template)
            throws TransformerException, JAXBException {
        //XSL transformation
        TransformerFactory transformerFactory = TransformerFactory.newInstance();

        StreamResult result = new StreamResult(new StringWriter());

        Transformer transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(template.getData())));

        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");

        transformer.transform(new StreamSource(new StringReader(DeclarationUtil.getString(declaration))), result);

        return result.getWriter().toString();
    }

    public static Document getDocument(Declaration declaration, AbstractTemplate template) throws TransformerException, JAXBException,
            ParserConfigurationException, IOException, SAXException {
        //Parse document
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder.parse(new InputSource(new StringReader(getString(declaration, template))));
    }

    public static Declaration getDeclaration(InputStream inputStream) throws JAXBException {
        Declaration declaration = (Declaration) JAXBContext
                .newInstance(Declaration.class, DeclarationValue.class)
                .createUnmarshaller()
                .unmarshal(inputStream);

        declaration.fillValuesFromXml();

        return declaration;
    }
}
