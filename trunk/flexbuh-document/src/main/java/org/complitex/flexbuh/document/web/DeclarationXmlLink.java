package org.complitex.flexbuh.document.web;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.time.Time;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.util.DeclarationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.nio.charset.Charset;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 13.10.11 16:18
 */
public class DeclarationXmlLink extends Link{  
    private final static Logger log = LoggerFactory.getLogger(DeclarationXmlLink.class);
    
    private Declaration declaration;
    
    public DeclarationXmlLink(String id, Declaration declaration) {
        super(id);
        
        this.declaration = declaration;
    }

    @Override
    public void onClick() {
        try {
            String data = DeclarationUtil.getString(declaration);
            StringResourceStream stringResourceStream = new StringResourceStream(data, "application/xml");
            stringResourceStream.setCharset(Charset.forName("UTF-8"));
            stringResourceStream.setLastModified(Time.now());

            getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                    stringResourceStream, declaration.getFileName() + ".xml"));
        } catch (JAXBException e) {
            log.error("Ошибка генерации xml документа");
            
            throw new WicketRuntimeException(e);
        }
    }
}
