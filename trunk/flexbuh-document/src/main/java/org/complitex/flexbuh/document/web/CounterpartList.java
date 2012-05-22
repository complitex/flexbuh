package org.complitex.flexbuh.document.web;

import com.google.common.io.ByteStreams;
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
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.IProcessListener;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.*;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.entity.CounterpartRowSet;
import org.complitex.flexbuh.document.service.CounterpartBean;
import org.complitex.flexbuh.document.service.CounterpartService;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
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
    private CounterpartService counterpartService;

    private Dialog uploadDialog;

    private Map<Long, IModel<Boolean>> selectMap = new HashMap<>();

    public CounterpartList() {
        add(new Label("title", getString("title")));
        final FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final CompoundPropertyModel<Counterpart> filterModel = new CompoundPropertyModel<>(new Counterpart(getSessionId()));

        final Form<Counterpart> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
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
                filterModel.setObject(new Counterpart(getSessionId()));
            }
        });

        //Модель
        SortableDataProvider<Counterpart> dataProvider = new SortableDataProvider<Counterpart>() {
            @Override
            public Iterator<? extends Counterpart> iterator(int first, int count) {
                selectMap.clear();

                FilterWrapper<Counterpart> filter = FilterWrapper.of(filterModel.getObject());

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());

                return counterpartBean.getCounterparts(filter).iterator();
            }

            @Override
            public int size() {
                filterModel.getObject().setPersonProfileId(getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID));

                return counterpartBean.getCounterpartCount(FilterWrapper.of(filterModel.getObject()));
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

        Form fileUploadForm = new Form("upload_form");
        final FileUploadField fileUploadField = new FileUploadField("upload_field");
        fileUploadForm.add(fileUploadField);

        fileUploadForm.add(new AjaxButton("upload") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                try {
                    Long personProfileId = getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID);

                    counterpartService.save(getSessionId(), personProfileId,
                            fileUploadField.getFileUpload().getInputStream(),
                            new IProcessListener<Counterpart>() {
                                @Override
                                public void onSuccess(Counterpart object) {
                                    info(getStringFormat("info_upload", object.getHname()));
                                }

                                @Override
                                public void onError(Counterpart object, Exception e) {
                                    error(getStringFormat("error_upload", e.getMessage()));
                                }
                            }
                    );
                } catch (IOException e) {
                    log.error("Ошибка загрузки контрагентов", e);
                    error("Ошибка загрузки контрагентов");
                }

                uploadDialog.close(target);
                target.add(feedbackPanel);
                target.add(filterForm);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //wtf
            }
        });

        uploadDialog.add(fileUploadForm);
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> list = new ArrayList<>();

        list.add(new UploadButton(id, true){
            @Override
            protected void onClick(AjaxRequestTarget target) {
                uploadDialog.open(target);
            }

            @Override
            public boolean isVisible() {
                return getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID) != null;
            }
        });

        list.add(new SaveButton(id, "export", false) {
            @Override
            protected void onClick() {
                final InputStream inputStream = counterpartService.getInputStream(getSessionId());

                if (inputStream != null) {
                    getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                            new AbstractResourceStreamWriter() {
                                @Override
                                public void write(Response output) {
                                    try {
                                        ByteStreams.copy(inputStream, ((HttpServletResponse) output.getContainerResponse())
                                                .getOutputStream());
                                    } catch (IOException e) {
                                        log.error("Ошибка выгрузки контрагентов", e);
                                    }
                                }

                                @Override
                                public String getContentType() {
                                    return "application/xml";
                                }
                            }, CounterpartRowSet.FILE_NAME));
                }else{
                    error(getString("error_export"));
                }
            }
        });

        list.add(new AddDocumentButton(id){
            @Override
            protected void onClick() {
                setResponsePage(CounterpartEdit.class);
            }

            @Override
            public boolean isVisible() {
                return getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID) != null;
            }
        });

        list.add(new DeleteItemButton(id){
            @Override
            protected void onClick() {
                for (Long id : selectMap.keySet()){
                    if (selectMap.get(id).getObject()){
                        Counterpart counterpart = counterpartBean.getCounterpart(id);

                        counterpartBean.delete(id);

                        info(getStringFormat("info_deleted", counterpart.getHname()));
                        log.info("Контрагент удален", new Event(EventCategory.REMOVE, counterpart));
                    }
                }
            }

            @Override
            public boolean isVisible() {
                return getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID) != null;
            }
        });

        return list;
    }
}
