package org.complitex.flexbuh.common.web.component;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
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

    AutoCompleteTextField<String> cityField;
    AutoCompleteTextField<String> streetField;

    public AddressPanel(String id, IModel<Address> addressModel, IModel<String> cityModel, IModel<String> streetModel) {
        super(id, addressModel);
        init(addressModel, cityModel, streetModel);
    }

    private void init(IModel<Address> addressModel, IModel<String> cityModel, IModel<String> streetModel) {

        Address address = addressModel.getObject();

        // Zip code
        add(new TextField<>("zipCode"));

        // Country
        add(new TextField<>("country"));

        // Region
        add(new TextField<>("region"));

        // Area
        add(new TextField<>("area"));

        // City
        if (address != null) {
            initCompoundModel(cityModel, address.getCityType(), address.getCity());
        }
        cityField = new AutoCompleteTextField<String>("city", cityModel) {
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
        if (address != null) {
            initCompoundModel(streetModel, address.getStreetType(), address.getStreet());
        }
        streetField = new AutoCompleteTextField<String>("street", streetModel) {
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
        add(new TextField<>("building"));

        // Apartment
        add(new TextField<>("apartment"));

    }

    public void updateModel(IModel<Address> addressModel, IModel<String> cityModel, IModel<String> streetModel) {
        this.setDefaultModel(addressModel);
        Address address = addressModel.getObject();
        if (address != null) {
            initCompoundModel(cityModel, address.getCityType(), address.getCity());
            initCompoundModel(streetModel, address.getStreetType(), address.getStreet());
        } else {
            cityModel.setObject(null);
            streetModel.setObject(null);
        }
    }

    private static void initCompoundModel(IModel<String> model, String type, String name) {
        if (StringUtils.isNotEmpty(type) &&
                StringUtils.isNotEmpty(name)) {
            model.setObject(type + " " + name);
        } else if (name != null) {
            model.setObject(name);
        }
    }
}
