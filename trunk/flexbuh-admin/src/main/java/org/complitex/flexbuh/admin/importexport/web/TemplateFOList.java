package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.complitex.flexbuh.entity.template.TemplateFO;
import org.complitex.flexbuh.security.SecurityRole;
import org.complitex.flexbuh.service.TemplateBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.complitex.flexbuh.web.component.datatable.DataProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 30.08.11 19:42
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class TemplateFOList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(TemplateFOList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	@EJB
	TemplateBean templateBean;

	public TemplateFOList() {

		final Form form = new Form("filter_form");
        form.setOutputMarkupId(true);
        add(form);

		//Модель
        final DataProvider<TemplateFO> dataProvider = new DataProvider<TemplateFO>() {

			@SuppressWarnings("unchecked")
            @Override
            protected Iterable<? extends TemplateFO> getData(int first, int count) {
                return templateBean.getTemplateFO(first, count);
            }

            @Override
            protected int getSize() {
                return templateBean.getTemplateFOsCount();
            }
        };
        dataProvider.setSort("file_name", SortOrder.ASCENDING);

		//Таблица
        DataView<TemplateFO> dataView = new DataView<TemplateFO>("templates", dataProvider, 10) {

            @Override
            protected void populateItem(Item<TemplateFO> item) {

                item.add(new Label("file_name", item.getModelObject().getName()));
                item.add(new Label("upload_date", getStringDate(item.getModelObject().getUploadDate())));
            }
        };
        form.add(dataView);

        //Постраничная навигация
        form.add(new PagingNavigator("paging", dataView));
	}

	private String getStringDate(Date date) {
		return date != null? DATE_FORMAT.format(date): "";
	}
}
