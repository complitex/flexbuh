package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.flexbuh.common.entity.dictionary.Field;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;
import org.odlabs.wiquery.ui.autocomplete.AutocompleteAjaxComponent;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.11.11 15:53
 */
public abstract class AutocompleteDialogComponent<T> extends Panel {
    @EJB
    private FieldCodeBean fieldCodeBean;

    private Dialog dialog;
    private FormComponent autocompleteComponent;

    private String alias;
    private IModel<T> selectModel;
    private IModel<String> model;

    public AutocompleteDialogComponent(String id, IModel<String> model, IModel<T> selectModel,
                                       String code, String name, String sprName) {
        super(id);

        this.selectModel = selectModel;
        this.model = model;

        //Alias
        Field field = fieldCodeBean.getField(code, name, sprName);
        alias = field.getAlias();

        alias = (alias != null && !alias.isEmpty()) ?  alias.replace(field.getPrefix(), "") : "WTF?";

        autocompleteComponent = new AutocompleteAjaxComponent<String>("autocomplete", model) {
            @Override
            public List<String> getValues(String term) {
                return AutocompleteDialogComponent.this.getValues(term);
            }

            @Override
            public String getValueOnSearchFail(String input) {
                return input;
            }

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                updateLinked(target);
            }
        };
        setOutputMarkupId(true);
        add(autocompleteComponent);

        dialog = new Dialog("dialog");
        add(dialog);

        add(new AjaxButton("open") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialog.open(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //nothing
            }
        }.setDefaultFormProcessing(false));
    }

    public Dialog getDialog() {
        return dialog;
    }

    protected abstract List<String> getValues(String tern);

    protected abstract void updateSelectModel(T object, IModel<String> model);

    protected void updateSelectModel(AjaxRequestTarget target){
        updateSelectModel(selectModel.getObject(), model);
        target.add(autocompleteComponent);
    }

    protected void updateSelectModel(T object){
        updateSelectModel(object, model);
    }
   
    protected void updateLinked(final AjaxRequestTarget target){
        getParent().visitChildren(getClass(), new IVisitor<AutocompleteDialogComponent<T>, Void>() {
            @Override
            public void component(AutocompleteDialogComponent<T> component, IVisit<Void> visit) {
                if (!AutocompleteDialogComponent.this.equals(component)){
                    component.updateSelectModel(selectModel.getObject());
                    target.add(component.getAutocompleteComponent());
                }
            }
        });
    }

    public FormComponent getAutocompleteComponent() {
        return autocompleteComponent;
    }

    public String getAlias() {
        return alias;
    }

    public IModel<T> getSelectModel() {
        return selectModel;
    }
}
