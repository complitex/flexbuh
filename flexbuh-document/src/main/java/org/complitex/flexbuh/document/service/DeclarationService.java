package org.complitex.flexbuh.document.service;

import org.apache.fop.apps.Driver;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.dictionary.DocumentTerm;
import org.complitex.flexbuh.common.entity.template.TemplateXML;
import org.complitex.flexbuh.common.entity.template.TemplateXMLType;
import org.complitex.flexbuh.common.service.TemplateXMLBean;
import org.complitex.flexbuh.common.service.dictionary.DocumentTermBean;
import org.complitex.flexbuh.common.util.ZipUtil;
import org.complitex.flexbuh.document.entity.*;
import org.complitex.flexbuh.document.exception.DeclarationParseException;
import org.complitex.flexbuh.document.exception.DeclarationZipException;
import org.complitex.flexbuh.document.fop.FopConfiguration;
import org.complitex.sevenzip.Compression.LZMA.Decoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;
import java.io.*;
import java.util.ArrayList;
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

    private final static int[] BEST_BUF = {0x0E8, 0x0D5, 1, 3, 0x0C3, 0x0C1, 0x83, 0x3D, 0x0B7, 0x0F0, 0x41, 5, 7, 0x72,
            0x10, 0x0E8};

    private final static int BUFFER = 8192;

    @EJB
    private DeclarationBean declarationBean;

    @EJB
    private TemplateXMLBean templateXMLBean;

    @EJB
    private TemplateXMLService templateXMLService;

    @EJB
    private RuleService ruleService;

    @EJB
    private DocumentTermBean documentTermBean;

    public String getString(Declaration declaration, String encoding, boolean validate) throws DeclarationParseException {
        try {
            List<String> names = getTemplateXSDNames(declaration);

            //sort
            sortDeclarationValues(declaration, names);

            //remove redundant
            clearRedundantDeclarationValue(declaration, names);

            //prepare xml value for jaxb marshal
            declaration.prepareXmlValues();

            JAXBContext context = JAXBContext.newInstance(Declaration.class, DeclarationValue.class);

            Marshaller m = context.createMarshaller();

            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, declaration.getTemplateName() + ".xsd");
            m.setProperty(Marshaller.JAXB_ENCODING, encoding);

            if (validate){
                m.setSchema(templateXMLService.getSchema(declaration.getTemplateName()));
            }

            StringWriter writer = new StringWriter();

            m.marshal(declaration, writer);

            //clear xml values
            declaration.clearXmlValues();

            return writer.toString();
        } catch (Exception e) {
            throw new DeclarationParseException("Ошибка преобразования в xml", e);
        }
    }

    public String getString(Declaration declaration) throws DeclarationParseException{
        return getString(declaration, "UTF-8", false);
    }

    public void validate(final Declaration declaration){
        try {
            Schema schema = templateXMLService.getSchema(declaration.getTemplateName());
            Validator validator = schema.newValidator();

            validator.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) throws SAXException {
                    throw exception;
                }

                @Override
                public void error(SAXParseException e) throws SAXException {
                    declaration.addValidateMessage(e.getColumnNumber(), e.getLineNumber(), e.getLocalizedMessage());
                }

                @Override
                public void fatalError(SAXParseException exception) throws SAXException {
                    throw exception;
                }
            });

            StreamSource streamSource = new StreamSource(new StringReader(getString(declaration)));

            validator.validate(streamSource);

            declaration.setValidated(!declaration.hasValidateMessages());
        } catch (Exception e) {
            declaration.setValidated(false);
            log.error("Ошибка проверки документа", e);
        }
    }

    public void validateAndSave(Declaration declaration){
        validate(declaration);

        if (declaration.getHead().getLinkedDeclarations() != null){
            for (LinkedDeclaration ld : declaration.getHead().getLinkedDeclarations()){
                validate(ld.getDeclaration());
            }
        }

        declarationBean.save(declaration);
    }

    public void check(Declaration declaration){
        ruleService.check(declaration);
    }

    public void sortDeclarationValues(Declaration declaration, List<String> names) {
        List<DeclarationValue> sortedDeclarationValues = new ArrayList<>();

        for (String name : names){
            for (DeclarationValue declarationValue : declaration.getDeclarationValues()) {
                if (name.equals(declarationValue.getName())) {
                    sortedDeclarationValues.add(declarationValue);

                    if (!name.contains("XXXX")) {
                        break;
                    }
                }
            }
        }

        declaration.setDeclarationValues(sortedDeclarationValues);
    }

    public void clearRedundantDeclarationValue(Declaration declaration, List<String> names){
        List<DeclarationValue> declarationValues = declaration.getDeclarationValues();

        for (int i = 0, declarationValuesSize = declarationValues.size(); i < declarationValuesSize; i++) {
            DeclarationValue declarationValue = declarationValues.get(i);

            if (!names.contains(declarationValue.getName())){
                declaration.removeDeclarationValue(declarationValue.getName());

                i--;
            }
        }
    }

    public List<String> getTemplateXSDNames(Declaration declaration){

        List<String> names = new ArrayList<>();

        NodeList nodeList = templateXMLService.getTemplateXSDDocument(declaration.getTemplateName())
                .getElementsByTagName("xs:element");

        for (int i=0, size = nodeList.getLength(); i < size; ++i){
            names.add(((Element) nodeList.item(i)).getAttribute("name"));
        }

        return names;
    }

    public String getString(Declaration declaration, TemplateXML xsl) throws DeclarationParseException{
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();

            StreamResult result = new StreamResult(new StringWriter());

            Transformer transformer = transformerFactory.newTransformer(new StreamSource(new StringReader(xsl.getData())));

            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.VERSION, "1.0");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            transformer.transform(new StreamSource(new StringReader(getString(declaration))), result);

            return result.getWriter().toString();
        } catch (Exception e) {
            throw new DeclarationParseException("Ошибка преобразования в xml по шаблону", e);
        }
    }

    public Document getDocument(Declaration declaration, TemplateXML xsl) throws DeclarationParseException {
        try {
            //Parse document
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            String xml = getString(declaration, xsl);

            //head-on fix
            xml = xml.replace("<!DOCTYPE html PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">", "");

            return documentBuilder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            log.error("Ошибка преобразования документа по шаблону", e);

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

        Decoder decoder = new Decoder();

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
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream))){
            writeXmlZip(declarations, zipOutputStream, null);
        } catch (Exception e) {
            throw new DeclarationZipException("Ошибка создания архива XML документов", e);
        }
    }

    public void writeXmlZip(List<Declaration> declarations, ZipOutputStream zipOutputStream, String dir)
            throws DeclarationParseException, IOException {
        for (Declaration declaration : declarations){
            declaration.getHead().setCDocCnt(declaration.getId().intValue());

            ZipUtil.writeZip(new ByteArrayInputStream(getString(declaration, "windows-1251", false).getBytes("windows-1251")),
                    zipOutputStream, dir, declaration.getFileName() + ".xml");
        }
    }

    public void writeXmlZip(Long sessionId, ZipOutputStream zipOutputStream, String dir)
            throws IOException, DeclarationParseException {
        writeXmlZip(declarationBean.getAllDeclarations(sessionId), zipOutputStream, dir);
    }

    public void writePdfZip(List<Declaration> declarations, OutputStream outputStream) throws DeclarationZipException {
        try {
            ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(outputStream));

            byte data[] = new byte[BUFFER];

            for (Declaration declaration : declarations){
                ByteArrayOutputStream pdf = new ByteArrayOutputStream();
                writePdf(declaration, pdf);

                BufferedInputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(pdf.toByteArray()),
                        BUFFER);

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
            TemplateXML templateFO = templateXMLBean.getTemplateXML(TemplateXMLType.FO, declaration.getTemplateName());

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

    public boolean hasStoredDeclaration(Declaration declaration){
        DeclarationHead head = declaration.getHead();
        DeclarationFilter filter = new DeclarationFilter(declaration.getSessionId());

        filter.setPeriodType(head.getPeriodType());
        filter.setPeriodMonth(head.getPeriodMonth());
        filter.setPeriodYear(head.getPeriodYear());
        filter.setCDoc(head.getCDoc());
        filter.setCDocSub(head.getCDocSub());
        filter.setPersonProfileId(declaration.getPersonProfileId());

        return declarationBean.getDeclarationsCount(filter) > 0;
    }

    public Declaration parse(Long sessionId, List<PersonProfile> personProfiles, String fileName, InputStream inputStream)
            throws DeclarationParseException {

        Declaration declaration = getDeclaration(inputStream);

        declaration.setSessionId(sessionId);

        //Person profile by file name
        declaration.setPersonProfileId(null);

        try{
            Integer tin = Integer.valueOf(fileName.substring(4, 14));

            for (PersonProfile pp : personProfiles){
                if (tin.equals(pp.getTin())){
                    declaration.setPersonProfile(pp);
                    declaration.setPersonProfileId(pp.getId());
                    break;
                }
            }
        }catch (Exception e){
            //no tin in file name
        }

        return declaration;
    }

    public boolean checkPeriod(Declaration declaration){
        DeclarationHead head = declaration.getHead();

        DocumentTerm documentTerm = new DocumentTerm();
        documentTerm.setCDoc(head.getCDoc());
        documentTerm.setCDocSub(head.getCDocSub());
        documentTerm.setCDocVer(head.getCDocVer());
        documentTerm.setPeriodMonth(head.getPeriodMonth());
        documentTerm.setPeriodType(head.getPeriodType());
        documentTerm.setPeriodYear(head.getPeriodYear());

        return documentTermBean.getId(documentTerm) != null;
    }
}
