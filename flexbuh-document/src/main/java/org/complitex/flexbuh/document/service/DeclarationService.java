package org.complitex.flexbuh.document.service;

import org.apache.fop.apps.Driver;
import org.complitex.flexbuh.common.entity.template.TemplateFO;
import org.complitex.flexbuh.common.entity.template.TemplateXSD;
import org.complitex.flexbuh.common.entity.template.TemplateXSL;
import org.complitex.flexbuh.common.service.TemplateBean;
import org.complitex.flexbuh.common.xml.LSInputImpl;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationValue;
import org.complitex.flexbuh.document.exception.DeclarationParseException;
import org.complitex.flexbuh.document.exception.DeclarationSaveException;
import org.complitex.flexbuh.document.exception.DeclarationValidateException;
import org.complitex.flexbuh.document.exception.DeclarationZipException;
import org.complitex.flexbuh.document.fop.FopConfiguration;
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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.11.11 16:04
 */
@Stateless
public class DeclarationService {
    private final static Logger log = LoggerFactory.getLogger(DeclarationService.class);

    private final static int[] BEST_BUF = {0x0E8, 0x0D5, 1, 3, 0x0C3, 0x0C1, 0x83, 0x3D, 0x0B7, 0x0F0, 0x41, 5, 7, 0x72, 0x10, 0x0E8};

    static final int BUFFER = 2048;

    @EJB
    private DeclarationBean declarationBean;

    @EJB
    private TemplateBean templateBean;

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

    public String getString(Declaration declaration, boolean validate) throws DeclarationParseException {
        try {
            declaration.prepareXmlValues();

            JAXBContext context = JAXBContext.newInstance(Declaration.class, DeclarationValue.class);

            Marshaller m = context.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, declaration.getTemplateName() + ".xsd");

            if (validate){
                m.setSchema(getSchema(declaration.getTemplateName()));
            }

            StringWriter writer = new StringWriter();

            m.marshal(declaration, writer);

            return writer.toString();
        } catch (Exception e) {
            throw new DeclarationParseException("Ошибка преобразования в xml", e);
        }
    }

    public String getString(Declaration declaration) throws DeclarationParseException{
        return getString(declaration, false);
    }
    
    public void validate(Declaration declaration) throws DeclarationValidateException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            Document document = factory.newDocumentBuilder().parse(new InputSource(new StringReader(getString(declaration))));

            Schema schema = getSchema(declaration.getTemplateName());
            Validator validator = schema.newValidator();

//            StreamSource streamSource = new StreamSource(new StringReader(getString(declaration)));

            validator.validate(new DOMSource(document));
        } catch (Exception e) {
            log.error("Упс!", e);

            throw new DeclarationValidateException("Ошибка проверки структуры данных", e);
        }
    }
    
    public String getString(Declaration declaration, TemplateXSL xsl) throws DeclarationParseException{
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            StreamResult result = new StreamResult(new StringWriter());

            Transformer transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(xsl.getData())));

            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            transformer.transform(new StreamSource(new StringReader(getString(declaration, false))), result);

            return result.getWriter().toString();
        } catch (Exception e) {
            throw new DeclarationParseException("Ошибка преобразования в xml по шаблону", e);
        }
    }

    public Document getDocument(Declaration declaration, TemplateXSL xsl) throws DeclarationParseException {
        try {
            //Parse document
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            String xml = getString(declaration, xsl);

            //head-on fix
            xml = xml.replace("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">", "");

            return documentBuilder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            throw new DeclarationParseException("Ошибка преобразования документа по шаблону", e);
        }
    }

    public Declaration getDeclarationByXml(InputStream inputStream) throws DeclarationParseException {
        try {
            Declaration declaration = (Declaration) JAXBContext
                    .newInstance(Declaration.class, DeclarationValue.class)
                    .createUnmarshaller()
                    .unmarshal(inputStream);

            declaration.fillValuesFromXml();

            return declaration;
        } catch (JAXBException e) {
            throw new DeclarationParseException("Ошибка создания документа по xml", e);
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public Declaration getDeclarationByBest(InputStream inputStream) throws Exception {
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
    public Declaration getDeclaration(InputStream inputStream) throws DeclarationParseException {
        try {
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
        } catch (Exception e) {
            throw new DeclarationParseException("Ошибка создания документа", e);
        }
    }

    public boolean isSamePeriod(Declaration d1, Declaration d2){
        return d1.getHead().getPeriodType().equals(d2.getHead().getPeriodType())
                && d1.getHead().getPeriodMonth().equals(d2.getHead().getPeriodMonth())
                && d1.getHead().getPeriodYear().equals(d2.getHead().getPeriodYear());
    }

    public void writeXmlZip(List<Declaration> declarations, OutputStream outputStream) throws DeclarationZipException {
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream));

            byte data[] = new byte[BUFFER];

            for (Declaration declaration : declarations){
                String xml = getString(declaration);

                BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(xml.getBytes("UTF-8")), BUFFER);

                ZipEntry zipEntry = new ZipEntry(declaration.getFileName() + ".xml");

                zipOutputStream.putNextEntry(zipEntry);
                int count;
                while((count = inputStream.read(data, 0, BUFFER)) != -1) {
                    zipOutputStream.write(data, 0, count);
                }
                inputStream.close();
            }

            zipOutputStream.close();
        } catch (Exception e) {
            throw new DeclarationZipException("Ошибка создания архива XML документов", e);
        }
    }

    public void writePdfZip(List<Declaration> declarations, OutputStream outputStream) throws DeclarationZipException {
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream));

            byte data[] = new byte[BUFFER];

            for (Declaration declaration : declarations){
                ByteArrayOutputStream pdf = new ByteArrayOutputStream();
                writePdf(declaration, pdf);

                BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(pdf.toByteArray()), BUFFER);

                ZipEntry zipEntry = new ZipEntry(declaration.getFileName() + ".pdf");

                zipOutputStream.putNextEntry(zipEntry);
                int count;
                while((count = inputStream.read(data, 0, BUFFER)) != -1) {
                    zipOutputStream.write(data, 0, count);
                }
                inputStream.close();
            }

            zipOutputStream.close();
        } catch (Exception e) {
            throw new DeclarationZipException("Ошибка создания архива PDF документов", e);
        }
    }

    public void writePdf(Declaration declaration, OutputStream outputStream){
        try {
            TemplateFO templateFO = templateBean.getTemplateFO(declaration.getTemplateName());

            FopConfiguration.init();

            Driver driver = new Driver();

            driver.setRenderer(Driver.RENDER_PDF);


            driver.setOutputStream(outputStream);

            Result res = new SAXResult(driver.getContentHandler());

            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(templateFO.getData())));

            Source src = new StreamSource(new StringReader(getString(declaration)));

            transformer.transform(src, res);
        } catch (Exception e) {
            log.error("Ошибка создания PDF");
        }
    }

    public Declaration parseAndSave(Long sessionId, Long personProfileId, InputStream inputStream)
            throws DeclarationSaveException, DeclarationParseException {
        Declaration declaration = getDeclaration(inputStream);
        declaration.setSessionId(sessionId);
        declaration.setPersonProfileId(personProfileId);

        declarationBean.save(declaration);

        return declaration;
    }
}
