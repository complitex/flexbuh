package org.complitex.flexbuh.personnel.web.component;

import org.apache.wicket.model.PropertyModel;
import org.complitex.flexbuh.common.web.component.AutoCompleteTextField;
import org.complitex.flexbuh.personnel.entity.OrganizationType;
import org.complitex.flexbuh.personnel.service.OrganizationTypeBean;

import javax.ejb.EJB;
import java.util.List;
import java.util.Locale;

import static org.complitex.flexbuh.common.service.FIOBean.SIZE;

/**
 * @author Pavel Sknar
 *         Date: 07.03.12 15:00
 */
public class OrganizationTypeAutoCompleteTextField extends AutoCompleteTextField<OrganizationType> {

    @EJB
    private OrganizationTypeBean organizationTypeBean;

    public OrganizationTypeAutoCompleteTextField(String id) {
        super(id);
    }

    public OrganizationTypeAutoCompleteTextField(String id, PropertyModel<String> model) {
        super(id, model);
    }

    @Override
    protected int getCapacity() {
        return SIZE;
    }

    @Override
    protected List<OrganizationType> getLocalizedDomainObjects(String input, Locale locale) {
        return organizationTypeBean.getOrganizationTypes(input, locale);
    }
}
