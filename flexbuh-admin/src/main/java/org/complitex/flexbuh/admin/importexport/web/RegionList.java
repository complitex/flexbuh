package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.complitex.flexbuh.common.entity.dictionary.Region;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.dictionary.RegionBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 15:25
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class RegionList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(RegionList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	RegionBean regionBean;

	public RegionList() {

		final Form form = new Form("filter_form");
        form.setOutputMarkupId(true);
        add(form);

		//Модель
        final DataProvider<Region> dataProvider = new DataProvider<Region>() {

			@SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends Region> getData(int first, int count) {
                return regionBean.getRegions(first, count);
            }

            @Override
            protected int getSize() {
                return regionBean.getRegionsCount();
            }
        };
        dataProvider.setSort("type", SortOrder.ASCENDING);
        dataProvider.setSort("sub_type", SortOrder.ASCENDING);

		//Таблица
        DataView<Region> dataView = new DataView<Region>("dictionaries", dataProvider, 10) {

            @Override
            protected void populateItem(Item<Region> item) {
                Region region = item.getModelObject();

                item.add(new Label("code", Integer.toString(region.getCode())));
                item.add(new Label("begin_date", getStringDate(region.getBeginDate())));
                item.add(new Label("end_date", getStringDate(region.getEndDate())));
                item.add(new Label("name_uk", region.getNameUk()));
            }
        };
        form.add(dataView);

        //Постраничная навигация
        form.add(new PagingNavigator("paging", dataView, getClass().getName(), form));
	}

	private String getStringDate(Date date) {
		return date != null? DATE_FORMAT.format(date): "";
	}
}
