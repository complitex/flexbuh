package org.complitex.flexbuh.admin.dictionary.web;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.entity.template.TemplateXML;
import org.complitex.flexbuh.common.entity.template.TemplateXMLType;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.TemplateXMLBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 31.08.11 11:19
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class TemplateXMLList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(TemplateXMLList.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy HH:mm");

	@EJB
    private TemplateXMLBean templateXMLBean;

	public TemplateXMLList(PageParameters parameters) {
        final TemplateXMLType type = parameters.get("type").toEnum(TemplateXMLType.class);

        add(new Label("title", getString("title_" + type.name())));

		final Form form = new Form("filter_form");
        form.setOutputMarkupId(true);
        add(form);

		//Модель
        final DataProvider<TemplateXML> dataProvider = new DataProvider<TemplateXML>() {
            @Override
            protected Iterable<? extends TemplateXML> getData(int first, int count) {
                return templateXMLBean.getTemplateXML(type, first, count);
            }

            @Override
            protected int getSize() {
                return templateXMLBean.getTemplateXMLsCount(type);
            }
        };
        dataProvider.setSort("file_name", SortOrder.ASCENDING);

		//Таблица
        DataView<TemplateXML> dataView = new DataView<TemplateXML>("templates", dataProvider, 10) {

            @Override
            protected void populateItem(Item<TemplateXML> item) {
                TemplateXML templateXML = item.getModelObject();

                item.add(new Label("file_name", templateXML.getName()));
                item.add(new Label("upload_date", getStringDate(templateXML.getUploadDate())));


                PageParameters pageParameters = new PageParameters();
                pageParameters.add("type", type);
                pageParameters.add("name", templateXML.getName());

                item.add(new BookmarkablePageLinkPanel<>("edit", getString("edit"), TemplateXMLEdit.class, pageParameters));
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