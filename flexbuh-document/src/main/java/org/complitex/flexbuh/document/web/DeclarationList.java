package org.complitex.flexbuh.document.web;

import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationFilter;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.complitex.flexbuh.template.TemplatePage;

import javax.ejb.EJB;
import java.util.Iterator;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.09.11 18:51
 */
public class DeclarationList extends TemplatePage{
    @EJB
    private DeclarationBean declarationBean;

    public DeclarationList() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        final DeclarationFilter filter = new DeclarationFilter(getSessionId(false));

        Form filterForm = new Form("filter_form");
        add(filterForm);

        SortableDataProvider<Declaration> dataProvider = new SortableDataProvider<Declaration>() {
            @Override
            public Iterator<? extends Declaration> iterator(int first, int count) {
                filter.setFirst(first);
                filter.setCount(count);

                return declarationBean.getDeclarations(filter).iterator();
            }

            @Override
            public int size() {
                return declarationBean.getDeclarationsCount(filter);
            }

            @Override
            public IModel<Declaration> model(Declaration object) {
                return new Model<>(object);
            }
        };

        DataView<Declaration> dataView = new DataView<Declaration>("declarations", dataProvider) {
            @Override
            protected void populateItem(Item<Declaration> item) {
                Declaration declaration = item.getModelObject();

                item.add(new Label("name", "[name] - " + declaration.getId()));
            }
        };

        filterForm.add(dataView);
    }
}
