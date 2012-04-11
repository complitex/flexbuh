package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.Radio;
import org.apache.wicket.markup.html.form.RadioGroup;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.common.service.dictionary.TaxInspectionBean;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.12.11 16:53
 */
public class TaxInspectionDialog extends Panel {
    @EJB
    TaxInspectionBean taxInspectionBean;

    private Dialog dialog;
    private IAjaxUpdate ajaxUpdate;

    public TaxInspectionDialog(String id, IModel<TaxInspection> model) {
        super(id, model);

        dialog = new Dialog("dialog");
        dialog.setWidth(875);
        dialog.setHeight(525);
        dialog.setTitle(getString("title"));
        add(dialog);

        //Фильтр модель
        final IModel<TaxInspection> filterModel = new CompoundPropertyModel<>(new TaxInspection());

        final Form<TaxInspection> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        dialog.add(filterForm);

        AjaxLink filterReset = new AjaxLink("filter_reset") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                filterModel.setObject(new TaxInspection());
                target.add(filterForm);
            }
        };
        filterForm.add(filterReset);

        AjaxButton filterSubmit = new AjaxButton("filter_submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(filterForm);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //achieving error-free
            }
        };
        filterForm.add(filterSubmit);

        //Tax inspection code
        filterForm.add(new TextField<String>("cSti"));

        //Region code
        filterForm.add(new TextField<String>("cReg"));

        //Area code
        filterForm.add(new TextField<String>("cRaj"));

        //Tax inspection type
        filterForm.add(new TextField<String>("tSti"));

        //Ukrainian name
        filterForm.add(new TextField<String>("nameUk"));

        //Ukrainian region name
        filterForm.add(new TextField<String>("nameRajUk"));

        //Модель
        final DataProvider<TaxInspection> dataProvider = new DataProvider<TaxInspection>() {

            @SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends TaxInspection> getData(int first, int count) {
                FilterWrapper<TaxInspection> filter = FilterWrapper.of(filterModel.getObject());

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());

                return taxInspectionBean.getTaxInspections(filter);
            }

            @Override
            protected int getSize() {
                return taxInspectionBean.getTaxInspectionsCount(FilterWrapper.of(filterModel.getObject()));
            }
        };
        dataProvider.setSort("c_sti", SortOrder.ASCENDING);
        
        //Radio
        RadioGroup<TaxInspection> radioGroup = new RadioGroup<>("radio_group", model);
        filterForm.add(radioGroup);

        //Таблица
        DataView<TaxInspection> dataView = new DataView<TaxInspection>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<TaxInspection> item) {
                TaxInspection taxInspection = item.getModelObject();
                                                                                                       
                item.add(new Radio<>("radio_select", new Model<>(taxInspection)));
                item.add(new Label("code", Integer.toString(taxInspection.getCSti())));
                item.add(new Label("region_code", Integer.toString(taxInspection.getCReg())));
                item.add(new Label("area_code", Integer.toString(taxInspection.getCRaj())));
                item.add(new Label("tax_inspection_type_code", Integer.toString(taxInspection.getTSti())));
                item.add(new Label("name_uk",taxInspection.getNameUk()));
                item.add(new Label("area_name_uk", taxInspection.getNameRajUk()));
            }
        };
        radioGroup.add(dataView);

        //Сортировка
        filterForm.add(new OrderByBorder("header.c_sti", "c_sti", dataProvider));
        filterForm.add(new OrderByBorder("header.c_reg", "c_reg", dataProvider));
        filterForm.add(new OrderByBorder("header.c_raj", "c_raj", dataProvider));
        filterForm.add(new OrderByBorder("header.t_sti", "t_sti", dataProvider));
        filterForm.add(new OrderByBorder("header.name_uk", "name_uk", dataProvider));
        filterForm.add(new OrderByBorder("header.name_raj_uk", "name_raj_uk", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, getClass().getName(), filterForm));
        
        //Выбор
        AjaxButton select = new AjaxButton("select") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                dialog.close(target);
                if (ajaxUpdate != null) {
                    ajaxUpdate.onUpdate(target);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //make goals, no errors
            }
        };
        filterForm.add(select);
    }

    public void open(AjaxRequestTarget target){
        dialog.open(target);
    }

    public IAjaxUpdate getAjaxUpdate() {
        return ajaxUpdate;
    }

    public void setAjaxUpdate(IAjaxUpdate ajaxUpdate) {
        this.ajaxUpdate = ajaxUpdate;
    }
}
