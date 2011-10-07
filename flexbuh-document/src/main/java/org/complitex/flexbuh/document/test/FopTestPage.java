package org.complitex.flexbuh.document.test;

import org.apache.fop.apps.Driver;
import org.apache.fop.apps.Options;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.form.Form;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.fop.FopConfiguration;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.complitex.flexbuh.document.util.DeclarationUtil;
import org.complitex.flexbuh.entity.template.TemplateFO;
import org.complitex.flexbuh.service.TemplateBean;

import javax.ejb.EJB;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 06.10.11 15:34
 */
public class FopTestPage extends WebPage{
    @EJB
    private TemplateBean templateBean;
    
    @EJB
    private DeclarationBean declarationBean;

    public FopTestPage() {
        add(new Form("form"){
            @Override
            protected void onSubmit() {
                fopTest();
            }
        });
    }

    private void fopTest(){
        TemplateFO templateFO = templateBean.getTemplateFO("J0110102");
        Declaration declaration = declarationBean.getDeclaration(1L);

        try {
            FopConfiguration.init();

            new Options(new File("C:\\OPZ\\fop\\conf\\userconfig.xml"));

            Driver driver = new Driver();
            //Setup logging here: driver.setLogger(...
            driver.setRenderer(Driver.RENDER_PDF);

            //Setup the OutputStream for FOP
            FileOutputStream fos = new java.io.FileOutputStream(new File("C:/fop_test.pdf"));

            driver.setOutputStream(fos);

            //Make sure the XSL transformation's result is piped through to FOP
            Result res = new SAXResult(driver.getContentHandler());

//            // Step 1: Construct a FopFactory
//            // (reuse if you plan to render multiple documents!)
//            FopFactory fopFactory = FopFactory.newInstance();
//
//
//            // Step 2: Set up output stream.
//            // Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).
//            OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("C:/fop_test.pdf")));
//
//            // Step 3: Construct fop with desired output format
//            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

            // Step 4: Setup JAXP using identity transformer
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(templateFO.getData())));

            // Step 5: Setup input and output for XSLT transformation
            // Setup input stream
            Source src = new StreamSource(new StringReader(DeclarationUtil.getString(declaration)));

//            // Resulting SAX events (the generated FO) must be piped through to FOP
//            Result res = new SAXResult(fop.getDefaultHandler());

            // Step 6: Start XSLT transformation and FOP processing
            transformer.transform(src, res);

            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            
            throw new WicketRuntimeException(e);
        }
    }
}
