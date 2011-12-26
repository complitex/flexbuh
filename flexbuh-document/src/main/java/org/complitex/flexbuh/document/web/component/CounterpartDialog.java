package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.web.component.AutocompleteDialogComponent;
import org.complitex.flexbuh.common.web.component.IAutocompleteDialog;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.entity.CounterpartFilter;
import org.complitex.flexbuh.document.service.CounterpartBean;
import org.complitex.flexbuh.document.service.PersonProfileBean;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 30.11.11 15:07
 */
public class CounterpartDialog extends Panel implements IAutocompleteDialog<Counterpart> {
    @EJB
    private CounterpartBean counterpartBean;

    @EJB
    private PersonProfileBean personProfileBean;

    private AutocompleteDialogComponent<Counterpart> component;
    
    private Dialog dialog;

    public CounterpartDialog(String id, Long sessionId) {
        super(id);
        
        dialog = new Dialog("dialog");
        add(dialog);

        dialog.setTitle(getString("title"));
        dialog.setWidth(870);

        //Form
        Form form = new Form<Counterpart>("form");
        dialog.add(form);

        final IModel<Counterpart> model = new Model<>(new Counterpart());

        form.add(new AjaxButton("select") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                if (model.getObject() != null && component != null){

                    component.updateModel(target, model.getObject());
                    component.updateLinked(target, model.getObject());

                    dialog.close(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //no more error
            }
        });

        final RadioGroup<Counterpart> radioGroup = new RadioGroup<>("radio_group", model);
        form.add(radioGroup);

        List<Counterpart> list = counterpartBean.getCounterparts(new CounterpartFilter(sessionId,
                personProfileBean.getSelectedPersonProfileId(sessionId)));

        ListView listView = new ListView<Counterpart>("counterparts", list) {
            @Override
            protected void populateItem(ListItem<Counterpart> item) {
                Counterpart counterpart = item.getModelObject();

                item.add(new Radio<>("select", new Model<>(counterpart)));
                item.add(new Label("hk", counterpart.getHk()));
                item.add(new Label("hname", counterpart.getHname()));
                item.add(new Label("hloc", counterpart.getHloc()));
                item.add(new Label("htel", counterpart.getHtel()));
                item.add(new Label("hnspdv", counterpart.getHnspdv()));
            }
        };
        radioGroup.add(listView);
    }

    @Override
    public void open(AjaxRequestTarget target, AutocompleteDialogComponent<Counterpart> component) {
        this.component = component;

        dialog.open(target);
    }
}
