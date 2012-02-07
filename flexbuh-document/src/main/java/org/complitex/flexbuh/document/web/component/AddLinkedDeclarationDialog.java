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
import org.complitex.flexbuh.common.entity.dictionary.Document;
import org.complitex.flexbuh.common.service.dictionary.DocumentBean;
import org.complitex.flexbuh.common.web.component.IAjaxUpdate;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.LinkedDeclaration;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.01.12 16:31
 */
public class AddLinkedDeclarationDialog extends Panel {
    @EJB
    private DocumentBean documentBean;

    private Declaration declaration;

    private Dialog dialog;
    private WebMarkupContainer container;

    private IModel<Document> model;

    public AddLinkedDeclarationDialog(String id, final IAjaxUpdate update) {
        super(id);

        dialog = new Dialog("dialog");
        dialog.setWidth(760);
        dialog.setTitle(getString("title"));
        add(dialog);

        container = new WebMarkupContainer("container");
        container.setOutputMarkupId(true);
        dialog.add(container);

        Form form = new Form<>("form");
        container.add(form);

        model = new Model<>(new Document());

        RadioGroup<Document> radioGroup = new RadioGroup<>("radio_group", model);
        form.add(radioGroup);

        final IModel<List<Document>> documentModel = new LoadableDetachableModel<List<Document>>() {
            @Override
            protected List<Document> load() {
                List<Document> docs = new ArrayList<>();

                if (declaration != null && declaration.getLinkedDeclarations() != null){
                    List<Document> list = documentBean.getLinkedDocuments(declaration.getHead().getCDoc(),
                            declaration.getHead().getCDocSub());

                    for (Document doc : list){
                        boolean found = false;

                        for (LinkedDeclaration ld : declaration.getLinkedDeclarations()){
                            if (doc.getCDoc().equals(ld.getСDoc()) && doc.getCDocSub().equals(ld.getСDocSub())){
                                found = true;

                                break;
                            }
                        }

                        if (!found){
                            docs.add(doc);
                        }
                    }
                }

                return docs;
            }
        };

        container.add(new Label("info", new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                return documentModel.getObject().isEmpty() ? getString("info_empty") : "";
            }
        }));

        ListView documents = new ListView<Document>("list", documentModel) {
            @Override
            protected void populateItem(ListItem<Document> item) {
                Document document = item.getModelObject();

                item.add(new Radio<>("select", new Model<>(document)));
                item.add(new Label("label", document.getFullName(getLocale())));
            }
        };
        radioGroup.add(documents);

        form.add(new AjaxButton("add") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                Document document = model.getObject();

                if (document != null) {
                    dialog.close(target);

                    declaration.addLinkedDeclaration(document);

                    update.onUpdate(target);

                    getSession().info(getString("info_linked_declaration_added") + ": " + document.getFullName(getLocale()));
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //idiots dropped satellite
            }

            @Override
            public boolean isVisible() {
                return !documentModel.getObject().isEmpty();
            }
        });
    }

    public void open(AjaxRequestTarget target, Declaration declaration){
        this.declaration = declaration;
        model.setObject(new Document());

        target.add(container);

        dialog.open(target);
    }
}


