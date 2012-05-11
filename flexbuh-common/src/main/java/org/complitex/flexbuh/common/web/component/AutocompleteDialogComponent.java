package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.flexbuh.common.entity.dictionary.Field;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;
import org.odlabs.wiquery.ui.autocomplete.Autocomplete;
import org.odlabs.wiquery.ui.autocomplete.AutocompleteAjaxComponent;

import javax.ejb.EJB;
import java.io.Serializable;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.11.11 15:53
 */
public abstract class AutocompleteDialogComponent<T extends Serializable> extends Panel {
    @EJB
    private FieldCodeBean fieldCodeBean;

    private AutocompleteAjaxComponent<T> autocompleteComponent;

    private String alias;
    private Field field;
   
    public AutocompleteDialogComponent(String id, final IModel<String> model, String code, String name, String sprName,
                                       final IAutocompleteDialog<T> dialog) {
        super(id);

        setOutputMarkupId(true);

        field = fieldCodeBean.getField(code, name, sprName);

        //Alias
        alias = field.getAlias() != null ? field.getAlias() : field.getName();
        alias = alias.replace(field.getPrefix(), "");

        autocompleteComponent = new AutocompleteAjaxComponent<T>("autocomplete", new Model<T>(), getChoiceRenderer()) {
            @Override
            public List<T> getValues(String term) {
                return AutocompleteDialogComponent.this.getValues(term);
            }

            @Override
            public T getValueOnSearchFail(String input) {
                model.setObject(input);

                return null;
            }

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                T object = getModelObject();

                if (object != null){
                    model.setObject(getAutocompleteField().getValue());

                    updateLinked(target, getModelObject());
                }

                target.add(getAutocompleteField());
            }
        };

        autocompleteComponent.getAutocompleteField().setModel(model);
//
        autocompleteComponent.setAutoUpdate(true);
        autocompleteComponent.setOutputMarkupId(true);
        add(autocompleteComponent);

        add(new AjaxButton("open") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialog.open(target,  AutocompleteDialogComponent.this);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //nothing
            }
        }.setDefaultFormProcessing(false));
    }

    protected abstract List<T> getValues(String tern);

    protected abstract void updateModel(T object);

    protected abstract IChoiceRenderer<T> getChoiceRenderer();

    public void updateModel(AjaxRequestTarget target, T object){
        updateModel(object);
        autocompleteComponent.setModelObject(object);
        autocompleteComponent.getAutocompleteField().updateModel();
        target.add(autocompleteComponent);
    }
   
    public void updateLinked(final AjaxRequestTarget target, final T object){
        getParent().visitChildren(getClass(), new IVisitor<AutocompleteDialogComponent<T>, Void>() {
            @Override
            public void component(AutocompleteDialogComponent<T> component, IVisit<Void> visit) {
                if (!AutocompleteDialogComponent.this.equals(component) && component.getId().contains(field.getPrefix())){
                    component.updateModel(target, object);
                }
            }
        });
    }

    public Autocomplete<String> getAutocompleteField() {
        return autocompleteComponent.getAutocompleteField();
    }

    public String getAlias() {
        return alias;
    }
}
