package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.util.convert.IConverter;
import org.complitex.flexbuh.document.web.converter.DeclarationDateConverter;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;
import org.complitex.flexbuh.web.component.CssStyleTextField;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.10.11 18:06
 */
public class DeclarationTextField extends CssStyleTextField<String> {    
    private String schemaType;
    
    public DeclarationTextField(String id, DeclarationStringModel model, String schemaType) {
        super(id, model);
        
        this.schemaType = schemaType;
        
        setType(String.class);
    }

    public DeclarationStringModel getDeclarationModel(){
        return (DeclarationStringModel) getModel();
    }

    @Override
    public <C> IConverter<C> getConverter(Class<C> classType) {
        if ("DGDate".equals(schemaType)){
            //noinspection unchecked
            return (IConverter<C>) new DeclarationDateConverter();
        }
        
        return super.getConverter(classType);
    }
}
