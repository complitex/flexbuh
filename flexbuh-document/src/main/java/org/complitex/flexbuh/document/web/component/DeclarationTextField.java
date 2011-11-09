package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.util.convert.IConverter;
import org.complitex.flexbuh.document.web.converter.DecimalConverter;
import org.complitex.flexbuh.document.web.converter.DeclarationDateConverter;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;
import org.complitex.flexbuh.web.component.CssStyleTextField;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.10.11 18:06
 */
public class DeclarationTextField extends CssStyleTextField<String> {    
    private String schema;
    
    public DeclarationTextField(String id, DeclarationStringModel model, String schema) {
        super(id, model);
        
        this.schema = schema;
        
        setType(String.class);
    }

    public DeclarationStringModel getDeclarationModel(){
        return (DeclarationStringModel) getModel();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> IConverter<C> getConverter(Class<C> classType) {
        if (schema != null){
            switch (schema){
                case "DGDate":
                case "DateColumn":
                    return (IConverter<C>) new DeclarationDateConverter();
                case "DGdecimal1":
                case "Decimal1Column":
                    return (IConverter<C>) new DecimalConverter(1);
                case "DGdecimal2":
                case "Decimal2Column":
                    return (IConverter<C>) new DecimalConverter(2);
            }
        }

        return super.getConverter(classType);
    }

    public String getSchema() {
        return schema;
    }
    
    public String getMask(){
        return getDeclarationModel().getMask();
    }
}
