package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.entity.dictionary.Field;
import org.complitex.flexbuh.common.entity.dictionary.FieldCode;
import org.complitex.flexbuh.common.entity.dictionary.FieldCodeFilter;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;

import javax.ejb.EJB;
import java.util.Iterator;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 21.11.11 16:47
 */
public class FieldCodeList extends TemplatePage{
    @EJB
    private FieldCodeBean fieldCodeBean;

    public FieldCodeList() {
        Form filterForm = new Form<>("filter_form");
        add(filterForm);

        final FieldCodeFilter filter = new FieldCodeFilter();
        
        SortableDataProvider<FieldCode> dataProvider = new SortableDataProvider<FieldCode>() {
            @Override
            public Iterator<? extends FieldCode> iterator(int first, int count) {
                filter.setFirst(first);
                filter.setCount(count);
                filter.setAscending(getSort().isAscending());
                filter.setSortProperty(getSort().getProperty());
                
                return fieldCodeBean.getFieldCodes(filter).iterator();
            }

            @Override
            public int size() {
                return fieldCodeBean.getFieldCodesCount(filter); 
            }

            @Override
            public IModel<FieldCode> model(FieldCode object) {
                return new Model<>(object);
            }
        };
        dataProvider.setSort("id", SortOrder.ASCENDING);

        DataView dataView = new DataView<FieldCode>("field_codes", dataProvider) {
            @Override
            protected void populateItem(Item<FieldCode> item) {
                FieldCode fieldCode = item.getModelObject();
                
                String codes = "";
                
                for (String c : fieldCode.getCodes()){
                    codes += c + ", ";                    
                }
                                
                item.add(new Label("codes", codes.substring(0, codes.length()-2)));
                
                item.add(new ListView<Field>("fields", fieldCode.getFields()) {
                    @Override
                    protected void populateItem(ListItem<Field> itemField) {
                        Field field = itemField.getModelObject();
                        
                        itemField.add(new Label("name", field.getName()));
                        itemField.add(new Label("spr_name", field.getName()));
                        itemField.add(new Label("prefix", field.getName()));
                        itemField.add(new Label("alias", field.getName()));                                                 
                    }
                });
            }
        };
        dataView.setOutputMarkupId(true);
        filterForm.add(dataView);
        
        filterForm.add(new PagingNavigator("paging", dataView, "FieldCodeList", dataView));
    }
}
