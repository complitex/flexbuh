package org.complitex.flexbuh.common.web;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.entity.AbstractTemporalEntity;
import org.complitex.flexbuh.common.entity.TemporalEntityFilter;
import org.complitex.flexbuh.common.service.TemporalEntityBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.12.11 15:37
 */
public class TemporalEntityListPage extends TemplatePage{
    @EJB
    private TemporalEntityBean temporalEntityBean;
    
    public TemporalEntityListPage(PageParameters parameters) {
        try {
            init(parameters.get("entity").toString());
        } catch (Exception e) {
            throw new WicketRuntimeException(e);
        }
    }
    
    private void init(String className) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        add(new FeedbackPanel("messages"));
        
        final Class _class = getClass().getClassLoader().loadClass(className);
        final AbstractTemporalEntity entity = (AbstractTemporalEntity) _class.newInstance();
        
        @SuppressWarnings("unchecked")
        final TemporalEntityFilter<AbstractTemporalEntity> filter = new TemporalEntityFilter(getSessionId(true), _class);

        //Форма
        final Form filterForm = new Form("filter_form");
        add(filterForm);
        
        //Все
        filterForm.add(new Button("reset"){
            @Override
            public void onSubmit() {
//                filterForm.getModelObject().clear();
            }
        });
        
        //todo add sort settings
        final Map<String, Class> fieldTypeMap = entity.getFieldTypeMap();
        final List<String> fields = new ArrayList<>(fieldTypeMap.keySet());
        
        //Название колонок
        ListView headerLabels = new ListView<String>("header_labels", fields) {
            @Override
            protected void populateItem(ListItem<String> item) {
                String key = item.getModelObject();
                
                item.add(new Label("label", TemporalEntityListPage.this.getString(_class, key)));
            }
        };
        filterForm.add(headerLabels);
        
        //Фильтр
        ListView headerInput = new ListView<String>("header_inputs", fields) {
            @Override
            protected void populateItem(ListItem<String> item) {
                String key = item.getModelObject();
                
                item.add(new Label("input", "[" + fieldTypeMap.get(key).getSimpleName() + "]"));
            }
        };
        filterForm.add(headerInput);

        DataProvider<AbstractTemporalEntity> dataProvider = new DataProvider<AbstractTemporalEntity>() {
            @Override
            protected Iterable<AbstractTemporalEntity> getData(int first, int count) {
                return temporalEntityBean.getTemporalEntities(filter);
            }

            @Override
            protected int getSize() {
                return temporalEntityBean.getTemporalEntitiesCount(filter);
            }
        };

        DataView dataView = new DataView<AbstractTemporalEntity>("list", dataProvider) {
            @Override
            protected void populateItem(Item<AbstractTemporalEntity> item) {
                final Map<String, Object> fieldMap = item.getModelObject().getFieldMap();
                
                item.add(new ListView<String>("fields", fields) {
                    @Override
                    protected void populateItem(ListItem<String> item) {
                        String key = item.getModelObject();
                        
                        item.add(new Label("value", "[" + StringUtil.getString(fieldMap.get(key)) + "]"));
                    }
                });
            }
        };
        filterForm.add(dataView);

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, "CounterpartList"));
    }
}
