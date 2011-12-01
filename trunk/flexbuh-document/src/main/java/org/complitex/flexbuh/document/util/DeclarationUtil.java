package org.complitex.flexbuh.document.util;

import org.complitex.flexbuh.common.entity.template.AbstractTemplate;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
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
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.08.11 16:56
 */
public class DeclarationUtil {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

    private final static int[] BEST_BUF = {0x0E8, 0x0D5, 1, 3, 0x0C3, 0x0C1, 0x83, 0x3D, 0x0B7, 0x0F0, 0x41, 5, 7, 0x72, 0x10, 0x0E8};

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

        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        
        String xml = getString(declaration, template);

        //head-on fix
        xml = xml.replace("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">", "");

        return documentBuilder.parse(new InputSource(new StringReader(xml)));
    }

    public static Declaration getDeclarationByXml(InputStream inputStream) throws JAXBException {
        Declaration declaration = (Declaration) JAXBContext
                .newInstance(Declaration.class, DeclarationValue.class)
                .createUnmarshaller()
                .unmarshal(inputStream);

        declaration.fillValuesFromXml();

        return declaration;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Declaration getDeclarationByBest(InputStream inputStream) throws Exception {
        //fix to lzma
        ByteArrayOutputStream lzmaStream = new ByteArrayOutputStream();

        inputStream.skip(15);
        int b;
        int index = -1;

        while ((b = inputStream.read()) != -1){
            lzmaStream.write(++index < 160 ? b ^ BEST_BUF[index%16] : b);
        }

        //lzma decode
        ByteArrayInputStream inStream = new ByteArrayInputStream(lzmaStream.toByteArray());

        inStream.skip(1);

        ByteArrayOutputStream decodeOutStream = new ByteArrayOutputStream();
       
        int propertiesSize = 5;
        byte[] properties = new byte[propertiesSize];
        
        if (inStream.read(properties, 0, propertiesSize) != propertiesSize){
            throw new Exception("input .lzma file is too short");            
        }
            
        SevenZip.Compression.LZMA.Decoder decoder = new SevenZip.Compression.LZMA.Decoder();
        
        if (!decoder.SetDecoderProperties(properties)){
            throw new Exception("Incorrect stream properties");
        }
        
        long outSize = 0;
        for (int i = 0; i < 8; i++){
            int v = inStream.read();
            if (v < 0){
                throw new Exception("Can't read stream size");
            }
            
            outSize |= ((long)v) << (8 * i);
        }
        
        if (!decoder.Code(inStream, decodeOutStream, outSize)){
            throw new Exception("Error in data stream");
        }
        
        return getDeclarationByXml(new ByteArrayInputStream(decodeOutStream.toByteArray()));
    }
    
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Declaration getDeclaration(InputStream inputStream) throws Exception {
        byte[] header = new byte[15];
        
        inputStream.read(header);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(header);
        
        int b;
        while ((b = inputStream.read()) != -1){
            outputStream.write(b);
        }

        if (new String(header).contains("PACKED_XML")){
            return getDeclarationByBest(new ByteArrayInputStream(outputStream.toByteArray()));
        }else{
            return getDeclarationByXml(new ByteArrayInputStream(outputStream.toByteArray()));
        }
    }

    public static String getString(Date date){
        if (date != null){
            return DATE_FORMAT.format(date);
        }

        return null;
    }
}
