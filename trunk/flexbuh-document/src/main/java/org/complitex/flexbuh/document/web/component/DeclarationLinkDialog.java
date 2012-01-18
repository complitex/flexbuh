package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.web.component.IAjaxUpdate;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 12.01.12 18:00
 */
public class DeclarationLinkDialog extends Panel {
    @EJB
    private DeclarationBean declarationBean;
    
    private List<Long> linkedDeclarationIds;
    private List<Declaration> declarations;
    private Dialog dialog; 
    private WebMarkupContainer container;
    private IModel<Declaration> model;

    public DeclarationLinkDialog(String id, final IAjaxUpdate update) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setTitle(getString("title"));
        dialog.setWidth(600);
        add(dialog);
        
        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        dialog.add(container);
                
        Form form = new Form<>("form");
        container.add(form);

        model = new Model<>(new Declaration());
        
        RadioGroup radioGroup = new RadioGroup<>("radio_group", model);
        radioGroup.setOutputMarkupId(true);
        form.add(radioGroup);
        
        form.add(new Label("info", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return declarations == null || declarations.isEmpty() ? getString("info_not_found") : "";
            }
        }));
        
        radioGroup.add(new ListView<Declaration>("declarations", new LoadableDetachableModel<List<? extends Declaration>>() {
            @Override
            protected List<? extends Declaration> load() {
                return declarations;
            }
        }) {
            @Override
            protected void populateItem(ListItem<Declaration> item) {
                Declaration declaration = item.getModelObject();
                
                item.add(new Radio<>("select", new Model<>(declaration)));
                item.add(new Label("label", declaration.getTemplateName() + " " + declaration.getName()));
            }
        });
        
        form.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Declaration declaration = model.getObject();

                if (declaration != null) {
                    declarationBean.updateDeclarationParent(linkedDeclarationIds, declaration.getId());

                    dialog.close(target);

                    getSession().info(getString("info_linked") + " " + declaration.getFullName());

                    if (update != null) {
                        update.onUpdate(target);
                    }
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //errors must die
            }

            @Override
            public boolean isVisible() {
                return declarations != null && !declarations.isEmpty();
            }
        });
    }

    public void open(AjaxRequestTarget target, List<Long> linkedDeclarationIds){
        if (linkedDeclarationIds != null && !linkedDeclarationIds.isEmpty()) {
            this.linkedDeclarationIds = linkedDeclarationIds;
            declarations = declarationBean.getPossibleParentDeclarations(linkedDeclarationIds);
            model.setObject(new Declaration());

            target.add(container);
            dialog.open(target);
        }
    }
}
