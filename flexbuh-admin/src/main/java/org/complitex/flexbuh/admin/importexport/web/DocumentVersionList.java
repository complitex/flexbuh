package org.complitex.flexbuh.admin.importexport.web;

import org.apache.wicket.ajax.markup.html.navigation.paging.AjaxPagingNavigator;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.PageableListView;
import org.complitex.flexbuh.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.entity.dictionary.NormativeDocumentName;
import org.complitex.flexbuh.service.dictionary.DocumentVersionBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.SimpleDateFormat;

/**
 * @author Pavel Sknar
 *         Date: 29.08.11 14:55
 */
public class DocumentVersionList extends TemplatePage {
	private final static Logger log = LoggerFactory.getLogger(DocumentVersion.class);

	private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

	@EJB
	DocumentVersionBean documentVersionBean;

	public DocumentVersionList() {

		WebMarkupContainer datacontainer = new WebMarkupContainer("data");
        datacontainer.setOutputMarkupId(true);
        add(datacontainer);

        PageableListView<DocumentVersion> listview = new PageableListView<DocumentVersion>("rows", documentVersionBean.readAll(), 20) {
            @Override
            protected void populateItem(ListItem<DocumentVersion> item) {
                item.add(new Label("type", item.getModelObject().getDocumentType()));
                item.add(new Label("sub_type", item.getModelObject().getDocumentSubType()));
                item.add(new Label("version", Integer.toString(item.getModelObject().getVersion())));
                item.add(new Label("begin_date", DATE_FORMAT.format(item.getModelObject().getBeginDate())));
                item.add(new Label("end_date", DATE_FORMAT.format(item.getModelObject().getEndDate())));
				for (NormativeDocumentName documentName : item.getModelObject().getNormativeDocumentNames()) {
					if ("uk".equals(documentName.getLanguage().getLangIsoCode())) {
						item.add(new Label("name_uk", documentName.getValue()));
					}
				}
            }
        };

        datacontainer.add(listview);
        datacontainer.add(new AjaxPagingNavigator("navigator", listview));
        datacontainer.setVersioned(false);
	}
}
