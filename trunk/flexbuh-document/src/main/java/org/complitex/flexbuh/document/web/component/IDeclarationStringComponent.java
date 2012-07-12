package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.util.convert.IConverter;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 29.11.11 14:11
 */
public interface IDeclarationStringComponent{
    DeclarationStringModel getDeclarationModel();
    
    String getValue();

    <C> IConverter<C> getConverter(Class<C> classType);
}
