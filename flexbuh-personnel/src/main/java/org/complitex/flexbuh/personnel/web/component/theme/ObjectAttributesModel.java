package org.complitex.flexbuh.personnel.web.component.theme;

import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.personnel.entity.ObjectAttributes;

import javax.validation.constraints.NotNull;

/**
 * @author Pavel Sknar
 *         Date: 16.10.12 12:01
 */
public class ObjectAttributesModel<T> implements IModel<T> {

    private PropertyModel<T> additionalModel;
    private PropertyModel<T> generalModel;

    public ObjectAttributesModel(final @NotNull ObjectAttributes objectAttributes, @NotNull final String expression) {

        IModel<Object> objectModel = new IModel<Object>() {
            @Override
            public Object getObject() {
                return objectAttributes.getObject();
            }

            @Override
            public void setObject(Object object) {

            }

            @Override
            public void detach() {

            }
        };
        IModel<Object> attributesModel = new IModel<Object>() {
            @Override
            public Object getObject() {
                return objectAttributes.getAttributes();
            }

            @Override
            public void setObject(Object object) {

            }

            @Override
            public void detach() {

            }
        };
        this.additionalModel = new PropertyModel<>(attributesModel, expression);
        this.generalModel = new PropertyModel<>(objectModel, expression);
    }

    @Override
    public T getObject() {
        T result = additionalModel.getTarget() != null? additionalModel.getObject(): null;
        return result != null? result: generalModel.getObject();
    }

    @Override
    public void setObject(T object) {
        if (additionalModel.getTarget() != null) {
            additionalModel.setObject(object);
            return;
        }
        generalModel.setObject(object);
    }

    @Override
    public void detach() {

    }
}
