package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.ValidateMessage;
import org.complitex.flexbuh.document.service.DeclarationService;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 20.07.12 17:46
 */
public class DeclarationValidateMessagesDialog extends Panel{
    @EJB
    private DeclarationService declarationService;

    private Dialog dialog;
    private WebMarkupContainer container;

    private Declaration declaration;

    public DeclarationValidateMessagesDialog(String id) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setWidth(800);
        dialog.setHeight(492);
        add(dialog);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        dialog.add(container);

        container.add(new Label("name", new LoadableDetachableModel<Object>() {
            @Override
            protected Object load() {
                return declaration != null ? declaration.getName() : "";
            }
        }));

        container.add(new ListView<ValidateMessage>("list",
                new LoadableDetachableModel<List<ValidateMessage>>() {
                    @Override
                    protected List<ValidateMessage> load() {
                        return declaration != null ? declaration.getValidateMessages() : new ArrayList<ValidateMessage>();
                    }
                }) {
            @Override
            protected void populateItem(ListItem<ValidateMessage> item) {
                ValidateMessage validateMessage = item.getModelObject();

                item.add(new Label("row", validateMessage.getRow() + ""));
                item.add(new Label("col", validateMessage.getCol() + ""));
                item.add(new Label("message", validateMessage.getMessage()));
            }
        });
    }

    public void open(Declaration declaration, AjaxRequestTarget target) {
        this.declaration = declaration;
        declarationService.validate(declaration);

        target.add(container);

        dialog.open(target);
    }
}
