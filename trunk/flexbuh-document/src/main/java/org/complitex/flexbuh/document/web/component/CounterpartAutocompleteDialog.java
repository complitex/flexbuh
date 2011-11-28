package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;
import org.complitex.flexbuh.common.web.component.AutocompleteDialogComponent;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.entity.CounterpartFilter;
import org.complitex.flexbuh.document.service.CounterpartBean;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.11.11 12:35
 */
public class CounterpartAutocompleteDialog extends AutocompleteDialogComponent<Counterpart> {
    private final static String SPR_NAME = "spr_contragents";

    @EJB
    private FieldCodeBean fieldCodeBean;

    @EJB
    private CounterpartBean counterpartBean;

    private Long sessionId;

    public CounterpartAutocompleteDialog(String id, DeclarationStringModel model, Long sessionId) {
        super(id, model, new Model<>(new Counterpart()), model.getDeclaration().getTemplateName(), model.getName(), SPR_NAME);

        this.sessionId = sessionId;

        final Dialog dialog = getDialog();
        dialog.setTitle(getString("title"));
        dialog.setWidth(870);

        //Form
        Form form = new Form<Counterpart>("form");
        dialog.add(form);

        form.add(new AjaxButton("select") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                updateSelectModel(target);
                updateLinked(target);

                dialog.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //no more error
            }
        });

        final RadioGroup<Counterpart> radioGroup = new RadioGroup<>("radio_group", getSelectModel());
        form.add(radioGroup);

        List<Counterpart> list = counterpartBean.getCounterparts(new CounterpartFilter(sessionId));

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

    protected void updateSelectModel(Counterpart counterpart, IModel<String> model){
        switch (getAlias()){
            case "HK":
                model.setObject(counterpart.getHk());
                break;
            case "HNAME":
                model.setObject(counterpart.getHname());
                break;
            case "HLOC":
                model.setObject(counterpart.getHloc());
                break;
            case "HTEL":
                model.setObject(counterpart.getHtel());
                break;
            case "HNSPDV":
                model.setObject(counterpart.getHnspdv());
                break;
        }

    }

    @Override
    protected List<String> getValues(String tern) {
        List<String> list = new ArrayList<>();

        try {
            CounterpartFilter filter = new CounterpartFilter(sessionId);

            switch (getAlias()){
                case "HK":
                    filter.setHk(tern);
                    break;
                case "HNAME":
                    filter.setHname(tern);
                    break;
                case "HLOC":
                    filter.setHloc(tern);
                    break;
                case "HTEL":
                    filter.setHtel(tern);
                    break;
                case "HNSPDV":
                    filter.setHnspdv(tern);
                    break;
            }

            List<Counterpart> counterparts = counterpartBean.getCounterparts(filter);

            switch (getAlias()){
                case "HK":
                    for (Counterpart counterpart : counterparts){
                        list.add(counterpart.getHk());
                    }
                    break;
                case "HNAME":
                    for (Counterpart counterpart : counterparts){
                        list.add(counterpart.getHname());
                    }
                    break;
                case "HLOC":
                    for (Counterpart counterpart : counterparts){
                        list.add(counterpart.getHloc());
                    }
                    break;
                case "HTEL":
                    for (Counterpart counterpart : counterparts){
                        list.add(counterpart.getHtel());
                    }
                    break;
                case "HNSPDV":
                    for (Counterpart counterpart : counterparts){
                        list.add(counterpart.getHnspdv());
                    }
                    break;

            }
        } catch (Exception e) {
            //error can happen
        }

        return list;
    }
}
