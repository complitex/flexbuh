package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.complitex.flexbuh.common.entity.template.TemplateXSD;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.TemplateBean;
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
 *         Date: 31.08.11 11:13
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class TemplateXSDList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(TemplateXSDList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	@EJB
	TemplateBean templateBean;

	public TemplateXSDList() {

		final Form form = new Form("filter_form");
        form.setOutputMarkupId(true);
        add(form);

		//Модель
        final DataProvider<TemplateXSD> dataProvider = new DataProvider<TemplateXSD>() {

			@SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends TemplateXSD> getData(int first, int count) {
                return templateBean.getTemplateXSD(first, count);
            }

            @Override
            protected int getSize() {
                return templateBean.getTemplateXSDsCount();
            }
        };
        dataProvider.setSort("file_name", SortOrder.ASCENDING);

		//Таблица
        DataView<TemplateXSD> dataView = new DataView<TemplateXSD>("templates", dataProvider, 10) {

            @Override
            protected void populateItem(Item<TemplateXSD> item) {

                item.add(new Label("file_name", item.getModelObject().getName()));
                item.add(new Label("upload_date", getStringDate(item.getModelObject().getUploadDate())));
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