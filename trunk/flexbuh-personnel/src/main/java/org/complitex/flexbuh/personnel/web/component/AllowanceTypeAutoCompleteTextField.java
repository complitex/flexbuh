package org.complitex.flexbuh.personnel.web.component;

import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteSettings;
import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.common.web.component.AutoCompleteTextField;
import org.complitex.flexbuh.personnel.entity.AllowanceType;
import org.complitex.flexbuh.personnel.service.AllowanceTypeBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

import static org.complitex.flexbuh.common.service.FIOBean.SIZE;

/**
 * @author Pavel Sknar
 *         Date: 18.10.12 14:16
 */
public class AllowanceTypeAutoCompleteTextField extends AutoCompleteTextField<AllowanceType> {

    @EJB
    private AllowanceTypeBean organizationTypeBean;

    public AllowanceTypeAutoCompleteTextField(String id) {
        super(id, new AutoCompleteSettings().setShowListOnEmptyInput(true));
    }

    public AllowanceTypeAutoCompleteTextField(String id, PropertyModel<String> model) {
        super(id, model, new AutoCompleteSettings().setShowListOnEmptyInput(true));
    }

    @Override
    protected int getCapacity() {
        return SIZE;
    }

    @Override
    protected List<AllowanceType> getDomainObjects(String input, Locale locale) {
        return organizationTypeBean.getAllowanceTypes(input);
    }

    @Override
    protected String getDomainObjectName(AllowanceType object, Locale locale) {
        return object.getName();
    }
}
