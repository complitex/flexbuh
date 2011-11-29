package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.util.convert.IConverter;
import org.complitex.flexbuh.common.web.component.CssStyleTextField;
import org.complitex.flexbuh.document.web.converter.DecimalConverter;
import org.complitex.flexbuh.document.web.converter.DeclarationDateConverter;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.10.11 18:06
 */
public class DeclarationTextField extends CssStyleTextField<String> implements IDeclarationStringComponent{
    private DeclarationStringModel model;

    public DeclarationTextField(String id, DeclarationStringModel model) {
        super(id, model);

        this.model = model;

        setType(String.class);
    }

    public DeclarationStringModel getDeclarationModel(){
        return (DeclarationStringModel) getModel();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C> IConverter<C> getConverter(Class<C> classType) {
        if (model.getType() != null){
            switch (model.getType()){
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

    public String getMask(){
        return getDeclarationModel().getMask();
    }
}
