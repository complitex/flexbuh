package org.complitex.flexbuh.personnel.web.component;

import com.google.common.collect.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.personnel.entity.Allowance;
import org.complitex.flexbuh.personnel.entity.ObjectAttributes;
import org.complitex.flexbuh.personnel.entity.Payment;
import org.complitex.flexbuh.personnel.web.component.theme.ObjectAttributesModel;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

/**
 * @author Pavel Sknar
 *         Date: 15.10.12 16:48
 */
public abstract class PaymentPanel<T extends TemporalDomainObject> extends Panel {

    protected PaymentPanel(@NotNull String id, @NotNull final ObjectAttributes objectAttributes,
                           @NotNull TemporalHistoryPanelState<T> state) {
        super(id);

        TemporalObjectWebUtil.addHistoryFieldToForm(this, getHistoryPanelFactory(), state, "payment_salary",
                new NumberTextField<>("payment.salary",
                        new ObjectAttributesModel<Float>(objectAttributes, "payment.salary"), Float.class));

        TemporalObjectWebUtil.addHistoryFieldToForm(this, getHistoryPanelFactory(), state, "payment_number",
                new NumberTextField<>("payment.number",
                        new ObjectAttributesModel<Integer>(objectAttributes, "payment.number"), Integer.class));

        TemporalObjectWebUtil.addHistoryFieldToForm(this, getHistoryPanelFactory(), state, "payment_currency_unit",
                new DropDownChoice<String>("payment.currencyUnit",
                        new ObjectAttributesModel<String>(objectAttributes, "payment.currencyUnit"), Payment.CURRENCY_UNIT) {
                    @Override
                    protected boolean localizeDisplayValues() {
                        return true;
                    }
                });

        TemporalObjectWebUtil.addHistoryFieldToForm(this, getHistoryPanelFactory(), state, "payment_type",
                new DropDownChoice<String>("payment.type",
                        new ObjectAttributesModel<String>(objectAttributes, "payment.type"), Payment.TYPE) {
                    @Override
                    protected boolean localizeDisplayValues() {
                        return true;
                    }
                });

        // Show enabled user allowances
        final WebMarkupContainer allowancesContainer = new WebMarkupContainer("allowancesContainer");
        allowancesContainer.setOutputMarkupId(true);
        this.add(allowancesContainer);

        final Map<Allowance, IModel<Boolean>> selectedMap = newHashMap();

        ListView<Allowance> allowances = new ListView<Allowance>("allowances") {

            @Override
            protected void populateItem(ListItem<Allowance> item) {
                final Allowance allowance = item.getModelObject();

                IModel<Boolean> selectedModel;
                if (selectedMap.containsKey(allowance)) {
                    selectedModel = selectedMap.get(allowance);
                } else {
                    selectedModel = new Model<>(false);
                    selectedMap.put(allowance, selectedModel);
                }

                AjaxCheckBox selected = new AjaxCheckBox("selected", selectedModel) {

                    @Override
                    protected void onUpdate(AjaxRequestTarget target) {

                    }
                };
                item.add(selected);
                item.add(new Label("allowanceType", allowance.getType()));
                item.add(new Label("allowanceValue", Float.toString(allowance.getValue())));
                item.add(new Label("allowanceCalculationUnit", getString(allowance.getCalculationUnit())));

            }
        };
        allowancesContainer.add(allowances);

        // Dialog add allowances
        final Dialog addAllowancesDialog = new Dialog("add_allowances_dialog");
        addAllowancesDialog.setTitle(getString("add_allowances"));
        //addAllowancesDialog.setWidth(500);
        //addAllowancesDialog.setHeight(100);

        add(addAllowancesDialog);

        Form selectAllowancesForm = new Form("select_allowances_form");

        final ArrayList<Allowance> selectedNewAllowances = new ArrayList<>();
        final List<Allowance> selectAllowances = getSelectAllowances();
        final ListMultipleChoice<Allowance> selectAllowancesChoice = new ListMultipleChoice<>("select_allowances",
                new Model<>(selectedNewAllowances), selectAllowances, new IChoiceRenderer<Allowance>() {
            @Override
            public Object getDisplayValue(Allowance object) {
                return String.format("%1$s (%2$s %3$s)", object.getType(), object.getValue(), getString(object.getCalculationUnit()));
            }

            @Override
            public String getIdValue(Allowance object, int index) {
                return object.getId().toString();
            }
        });
        selectAllowancesChoice.setMaxRows(10);
        selectAllowancesChoice.setOutputMarkupId(true);
        selectAllowancesForm.add(selectAllowancesChoice);

        // Button add allowances on form. Show dialog
        final AjaxSubmitLink addAllowances = new AjaxSubmitLink("addAllowances") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                addAllowancesDialog.open(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }

            @Override
            public boolean isEnabled() {
                return selectAllowances.size() > 0;
            }
        };
        addAllowances.setDefaultFormProcessing(false);
        add(addAllowances);

        // Button remove allowances on form
        final AjaxButton remove = new AjaxButton("removeAllowances") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                update(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                update(target);
            }

            private void update(AjaxRequestTarget target) {
                List<Allowance> allowances = new PropertyModel<List<Allowance>>(objectAttributes.getObject(), "allowances").getObject();
                for (Map.Entry<Allowance, IModel<Boolean>> entry : selectedMap.entrySet()) {
                    if (entry.getValue().getObject()) {
                        selectAllowances.add(entry.getKey());
                        allowances.remove(entry.getKey());
                    }
                }
                selectedMap.clear();
                for (Allowance allowance : allowances) {
                    selectedMap.put(allowance, new Model<>(false));
                }

                target.add(selectAllowancesChoice);
                target.add(allowancesContainer);
                target.add(this);
                target.add(addAllowances);

                //target.add(userOrganizationContainer);
            }

            @Override
            public boolean isEnabled() {
                return true;//user.getAllowances().size() > 0;
            }
        };
        remove.setDefaultFormProcessing(false);
        add(remove);

        // Button add allowances on dialog
        selectAllowancesForm.add(new AjaxButton("add") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<Allowance> allowances = new PropertyModel<List<Allowance>>(objectAttributes.getObject(), "allowances").getObject();
                selectAllowances.removeAll(selectedNewAllowances);
                allowances.addAll(selectedNewAllowances);
                for (Allowance newAllowance : selectedNewAllowances) {
                    selectedMap.put(newAllowance, new Model<>(false));
                }

                target.add(selectAllowancesChoice);
                target.add(allowancesContainer);
                target.add(remove);
                target.add(addAllowances);

                addAllowancesDialog.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }
        });

        addAllowancesDialog.add(selectAllowancesForm);
    }

    protected abstract List<Allowance> getSelectAllowances();

    protected abstract HistoryPanelFactory<T> getHistoryPanelFactory();
}
