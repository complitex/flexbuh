package org.complitex.flexbuh.personnel.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.datetime.PatternDateConverter;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.convert.IConverter;
import org.complitex.flexbuh.common.entity.TemporalDomainObject;
import org.complitex.flexbuh.common.web.component.IAjaxUpdate;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.odlabs.wiquery.ui.dialog.Dialog;

import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 06.04.12 11:25
 */
public class TemporalObjectEditDialog extends Panel {

    private Dialog dialog;
    private DatePicker<Date> datePicker;

    public TemporalObjectEditDialog(String id, final TemporalDomainObject object, final IAjaxUpdate update) {
        super(id);

        Form form = new Form("form");

        // Дата создания организации
        datePicker = new DatePicker<Date>("entryIntoForceDate",
                new PropertyModel<Date>(object, "entryIntoForceDate"), Date.class) {

            @Override
            public <Date> IConverter<Date> getConverter(Class<Date> type) {
                return (IConverter<Date>)new PatternDateConverter("dd.MM.yyyy", true);
            }
        };
        datePicker.setDateFormat("dd.mm.yy");
        form.add(datePicker);

        form.add(new AjaxButton("currentDate") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                object.setEntryIntoForceDate(new Date());
                target.add(datePicker);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }
        });

        dialog = new Dialog("dialog");
        dialog.setWidth(220);
        dialog.setHeight(130);

        add(dialog);

        form.add(new AjaxButton("submit") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                update.onUpdate(target);

                datePicker.disable(target);
                dialog.close(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {

            }
        });

        dialog.add(form);
        /*
        dialog.setOpenEvent(new DefaultJsScopeUiEvent(
                "var elements = document.getElementsByName(\"currentDate\");\n" +
                "   elements[0].focus();"));
                */

    }

    public void open(AjaxRequestTarget target) {
        datePicker.disable(target);
        dialog.open(target);
        datePicker.enable(target);
    }
}
