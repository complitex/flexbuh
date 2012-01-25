package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.AddDocumentButton;
import org.complitex.flexbuh.common.template.toolbar.DeleteItemButton;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.template.toolbar.UploadButton;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.entity.CounterpartFilter;
import org.complitex.flexbuh.document.service.CounterpartBean;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 15:37
 */
public class CounterpartList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(CounterpartList.class);

    @EJB
    private CounterpartBean counterpartBean;

    @EJB
    private PersonProfileBean personProfileBean;

    private Dialog uploadDialog;

    private Map<Long, IModel<Boolean>> selectMap = new HashMap<>();

    public CounterpartList() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        CounterpartFilter filter = new CounterpartFilter(getSessionId());

        final Form<CounterpartFilter> filterForm = new Form<>("filter_form", new CompoundPropertyModel<>(filter));
        add(filterForm);

        filterForm.add(new TextField<>("hk")); //ИНН
        filterForm.add(new TextField<>("hname")); //Название
        filterForm.add(new TextField<>("hloc")); //Адрес
        filterForm.add(new TextField<>("htel")); //Телефон
        filterForm.add(new TextField<>("hnspdv")); //Страховой номер

        //Все
        filterForm.add(new Button("reset"){
            @Override
            public void onSubmit() {
                filterForm.getModelObject().clear();
            }
        });

        //Модель
        SortableDataProvider<Counterpart> dataProvider = new SortableDataProvider<Counterpart>() {
            @Override
            public Iterator<? extends Counterpart> iterator(int first, int count) {
                selectMap.clear();

                CounterpartFilter counterpartFilter = filterForm.getModelObject();

                counterpartFilter.setFirst(first);
                counterpartFilter.setCount(count);
                counterpartFilter.setSortProperty(getSort().getProperty());
                counterpartFilter.setAscending(getSort().isAscending());

                return counterpartBean.getCounterparts(counterpartFilter).iterator();
            }

            @Override
            public int size() {
                return counterpartBean.getCounterpartCount(filterForm.getModelObject());
            }

            @Override
            public IModel<Counterpart> model(Counterpart object) {
                return new Model<>(object);
            }
        };
        dataProvider.setSort("id", SortOrder.DESCENDING);

        //Таблица
        final DataView<Counterpart> dataView = new DataView<Counterpart>("list", dataProvider, 10) {
            @Override
            protected void populateItem(Item<Counterpart> item) {
                final Counterpart counterpart = item.getModelObject();

                IModel<Boolean> selectModel = new Model<>(false);

                selectMap.put(counterpart.getId(), selectModel);

                item.add(new CheckBox("select", selectModel)
                        .add(new AjaxFormComponentUpdatingBehavior("onchange") {
                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                //update
                            }
                        }));

                PageParameters pageParameters = new PageParameters();
                pageParameters.set("id", counterpart.getId());
                item.add(new BookmarkablePageLinkPanel<CounterpartEdit>("hname", counterpart.getHname(),
                        CounterpartEdit.class, pageParameters));

                item.add(new Label("hk", counterpart.getHk()));
                item.add(new Label("hloc", counterpart.getHloc()));
                item.add(new Label("htel", counterpart.getHtel()));
                item.add(new Label("hnspdv", counterpart.getHnspdv()));
            }
        };
        dataView.setOutputMarkupId(true);
        filterForm.add(dataView);

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, "CounterpartList"));

        //Загрузка файлов
        uploadDialog = new Dialog("upload_dialog");
        uploadDialog.setTitle(getString("upload_title"));
        uploadDialog.setWidth(500);
        uploadDialog.setHeight(100);

        add(uploadDialog);

        final IModel<List<FileUpload>> fileUploadModel = new ListModel<>();

        Form fileUploadForm = new Form("upload_form");

        fileUploadForm.add(new AjaxButton("upload") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<FileUpload> fileUploads = fileUploadModel.getObject();
                
                int count = 0;

                try {
                    for (FileUpload fileUpload : fileUploads){
                        count += counterpartBean.save(getSessionId(true), fileUpload.getInputStream());
                    }

                    uploadDialog.close(target);

                    setResponsePage(CounterpartList.class);

                    getSession().info(getStringFormat("info_counterparts_loaded", count));
                } catch (Exception e) {
                    log.error("Ошибка загрузки файла", e);
                    getSession().error("Ошибка загрузки файла");
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //wtf
            }
        });

        uploadDialog.add(fileUploadForm);

        fileUploadForm.add(new FileUploadField("upload_field", fileUploadModel));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> list = new ArrayList<>();

        final Long sessionId = getSessionId();

        list.add(new UploadButton(id, true){
            @Override
            protected void onClick(AjaxRequestTarget target) {
                uploadDialog.open(target);
            }

            @Override
            public boolean isVisible() {
                //todo cache selected profile id
                return personProfileBean.getSelectedPersonProfileId(sessionId) != null;
            }
        });

        list.add(new AddDocumentButton(id){
            @Override
            protected void onClick() {
                setResponsePage(CounterpartEdit.class);
            }

            @Override
            public boolean isVisible() {
                return personProfileBean.getSelectedPersonProfileId(sessionId) != null;
            }
        });

        list.add(new DeleteItemButton(id){
            @Override
            protected void onClick() {
                for (Long id : selectMap.keySet()){
                    if (selectMap.get(id).getObject()){
                        counterpartBean.delete(id);
                    }
                }
            }

            @Override
            public boolean isVisible() {
                return personProfileBean.getSelectedPersonProfileId(sessionId) != null;
            }
        });

        return list;
    }
}
