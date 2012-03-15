package org.complitex.flexbuh.common.web.component;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.util.string.Strings;
import org.complitex.flexbuh.common.entity.Address;
import org.complitex.flexbuh.common.entity.CityType;
import org.complitex.flexbuh.common.entity.StreetType;
import org.complitex.flexbuh.common.service.CityTypeBean;
import org.complitex.flexbuh.common.service.StreetTypeBean;

import javax.ejb.EJB;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 14.03.12 10:17
 */
public class AddressPanel extends Panel {

    private final static int LIST_INIT_SIZE = 10;

    @EJB
    private StreetTypeBean streetTypeBean;

    @EJB
    private CityTypeBean cityTypeBean;

    public AddressPanel(String id, Address address, IModel<String> cityModel, IModel<String> streetModel) {
        super(id);
        init(address, cityModel, streetModel);
    }

    private void init(Address address, IModel<String> cityModel, IModel<String> streetModel) {

        // Zip code
        add(new TextField<>("zipCode", new PropertyModel<String>(address, "zipCode")));

        // Country
        add(new TextField<>("country", new PropertyModel<String>(address, "country")));

        // Region
        add(new TextField<>("region", new PropertyModel<String>(address, "region")));

        // Area
        add(new TextField<>("area", new PropertyModel<String>(address, "area")));

        // City
        if (address != null && StringUtils.isNotEmpty(address.getCityType()) &&
                StringUtils.isNotEmpty(address.getCity())) {
            cityModel.setObject(address.getCityType() + " " + address.getCity());
        } else if (address != null && address.getCity() != null) {
            cityModel.setObject(address.getCity());
        }
        final org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField<String> cityField = new org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField<String>("city", cityModel) {
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }

                List<String> choices = Lists.newArrayListWithCapacity(LIST_INIT_SIZE);

                for (CityType cityType : cityTypeBean.getCityTypes(input, getLocale())) {
                    choices.add(cityType.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        add(cityField);

        // Street
        if (address != null && StringUtils.isNotEmpty(address.getStreetType()) &&
                StringUtils.isNotEmpty(address.getStreet())) {
            streetModel.setObject(address.getStreetType() + " " + address.getStreet());
        } else if (address != null && address.getStreet() != null) {
            streetModel.setObject(address.getStreet());
        }
        final org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField<String> streetField = new org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField<String>("street", streetModel) {
            @Override
            protected Iterator<String> getChoices(String input) {
                if (Strings.isEmpty(input)) {
                    List<String> emptyList = Collections.emptyList();
                    return emptyList.iterator();
                }

                List<String> choices = Lists.newArrayListWithCapacity(LIST_INIT_SIZE);

                for (StreetType streetType : streetTypeBean.getStreetTypes(input, getLocale())) {
                    choices.add(streetType.getName(getLocale()));
                }

                return choices.iterator();
            }
        };
        add(streetField);

        // Building
        add(new TextField<>("building", new PropertyModel<String>(address, "building")));

        // Apartment
        add(new TextField<>("apartment", new PropertyModel<String>(address, "apartment")));

    }
}
