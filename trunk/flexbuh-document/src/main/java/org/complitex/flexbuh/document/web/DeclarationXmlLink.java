package org.complitex.flexbuh.document.web;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.time.Time;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.util.DeclarationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;

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
            final byte[] bytes  = DeclarationUtil.getString(declaration).getBytes();  
            
            getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                    new AbstractResourceStreamWriter(){
    
                        @Override
                        public void write(Response output) {                        
                            output.write(bytes);
                        }
    
                        @Override
                        public Bytes length() {
                            return Bytes.bytes(bytes.length);
                        }
    
                        @Override
                        public String getContentType() {
                            return "application/xml";
                        }
    
                        @Override
                        public Time lastModifiedTime() {
                            return Time.now();
                        }
                    }, declaration.getFileName() + ".xml"));
        } catch (JAXBException e) {
            log.error("Ошибка генерации xml документа");
            
            throw new WicketRuntimeException(e);
        }
    }
}
