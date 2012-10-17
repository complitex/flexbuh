package org.complitex.flexbuh.personnel.web.component;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.NumberTextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.personnel.entity.ObjectAttributes;
import org.complitex.flexbuh.personnel.entity.Payment;
import org.complitex.flexbuh.personnel.web.component.theme.ObjectAttributesModel;

import javax.validation.constraints.NotNull;

/**
 * @author Pavel Sknar
 *         Date: 15.10.12 16:48
 */
public abstract class PaymentPanel<T extends TemporalDomainObject> extends Panel {

    protected PaymentPanel(@NotNull String id, @NotNull ObjectAttributes objectAttributes,
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
    }

    protected abstract HistoryPanelFactory<T> getHistoryPanelFactory();
}
