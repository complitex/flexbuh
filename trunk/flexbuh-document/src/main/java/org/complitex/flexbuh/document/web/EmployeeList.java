package org.complitex.flexbuh.document.web;

import com.google.common.io.ByteStreams;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
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
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.IProcessListener;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.*;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeRowSet;
import org.complitex.flexbuh.document.service.EmployeeBean;
import org.complitex.flexbuh.document.service.EmployeeService;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.complitex.flexbuh.common.entity.PersonProfile.SELECTED_PERSON_PROFILE_ID;
import static org.complitex.flexbuh.common.logging.EventCategory.REMOVE;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 16:05
 */
public class EmployeeList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(EmployeeList.class);

    @EJB
    private EmployeeBean employeeBean;

    @EJB
    private EmployeeService employeeService;

    @EJB
    private PersonProfileBean personProfileBean;

    private Dialog uploadDialog;

    private Map<Long, IModel<Boolean>> selectMap = new HashMap<>();

    public EmployeeList() {
        add(new Label("title", getString("title")));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final CompoundPropertyModel<Employee> filterModel = new CompoundPropertyModel<>(new Employee(getSessionId()));

        final Form<Employee> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        filterForm.add(new TextField<>("htin")); //Идентификационный номер
        filterForm.add(new TextField<>("hname")); //ФИО
        filterForm.add(new DatePicker<>("hbirthday")); //Дата рождения
        filterForm.add(new DatePicker<>("hdateIn")); //Дата принятия на работу
        filterForm.add(new DatePicker<>("hdateOut")); //Дата увольнения

        //Все
        filterForm.add(new Button("reset"){
            @Override
            public void onSubmit() {
                filterModel.setObject(new Employee(getSessionId()));
            }
        });

        //Модель
        SortableDataProvider<Employee> dataProvider = new SortableDataProvider<Employee>() {
            @Override
            public Iterator<? extends Employee> iterator(int first, int count) {
                selectMap.clear();

                FilterWrapper<Employee> filter = FilterWrapper.of(filterModel.getObject());

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());

                return employeeBean.getEmployees(filter).iterator();
            }

            @Override
            public int size() {
                filterModel.getObject().setPersonProfileId(getPreferenceLong(SELECTED_PERSON_PROFILE_ID));

                return employeeBean.getEmployeesCount(FilterWrapper.of(filterModel.getObject()));
            }

            @Override
            public IModel<Employee> model(Employee object) {
                return new Model<>(object);
            }
        };
        dataProvider.setSort("id", SortOrder.DESCENDING);

        //Таблица
        DataView<Employee> dataView = new DataView<Employee>("list", dataProvider, 10) {
            @Override
            protected void populateItem(Item<Employee> item) {
                final Employee employee = item.getModelObject();

                IModel<Boolean> selectModel = new Model<>(false);

                selectMap.put(employee.getId(), selectModel);

                item.add(new CheckBox("select", selectModel)
                        .add(new AjaxFormComponentUpdatingBehavior("onchange") {
                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                //update
                            }
                        }));

                PageParameters pageParameters = new PageParameters();
                pageParameters.set("id", employee.getId());
                item.add(new BookmarkablePageLinkPanel<EmployeeEdit>("hname", employee.getHname(),
                        EmployeeEdit.class, pageParameters));

                item.add(new Label("htin", StringUtil.getString(employee.getHtin())));
                item.add(DateLabel.forDateStyle("hbirthday", new Model<>(employee.getHbirthday()), "M-"));
                item.add(DateLabel.forDateStyle("hdateIn", new Model<>(employee.getHdateIn()), "M-"));
                item.add(DateLabel.forDateStyle("hdateOut", new Model<>(employee.getHdateOut()), "M-"));
            }
        };
        dataView.setOutputMarkupId(true);
        filterForm.add(dataView);

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, "EmployeeList"));

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
                Long personProfileId = getPreferenceLong(SELECTED_PERSON_PROFILE_ID);

                try {
                    FileUpload fileUpload = fileUploadField.getFileUpload();

                    if (fileUpload.getClientFileName() != null) {
                        employeeService.save(getSessionId(), personProfileId, fileUpload.getClientFileName(),
                                fileUpload.getInputStream(),
                                new IProcessListener<Employee>() {
                                    @Override
                                    public void onSuccess(Employee object) {
                                        info(getStringFormat("info_upload", object.getHname()));
                                    }

                                    @Override
                                    public void onError(Employee object, Exception e) {
                                        error(getStringFormat("error_upload", e.getMessage()));
                                    }
                                });
                    }
                } catch (Exception e) {
                    log.error("Ошибка загрузки сотрудников", e);
                    error("Ошибка загрузки сотрудников");
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

        final Long sessionId = getSessionId();

        list.add(new UploadButton(id, true){
            @Override
            protected void onClick(AjaxRequestTarget target) {
                uploadDialog.open(target);
            }

            @Override
            public boolean isVisible() {
                return getPreferenceLong(SELECTED_PERSON_PROFILE_ID) != null;
            }
        });

        list.add(new SaveButton(id, "export", false) {
            @Override
            protected void onClick() {
                final InputStream inputStream = employeeService.getInputStream(getSessionId());

                if (inputStream != null) {
                    getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                            new AbstractResourceStreamWriter() {
                                @Override
                                public void write(Response output) {
                                    try {
                                        ByteStreams.copy(inputStream, ((HttpServletResponse) output.getContainerResponse())
                                                .getOutputStream());
                                    } catch (IOException e) {
                                        log.error("Ошибка экспорта профиля", e);
                                    }
                                }

                                @Override
                                public String getContentType() {
                                    return "application/xml";
                                }
                            }, EmployeeRowSet.FILE_NAME));
                }else {
                    error(getString("error_export"));
                }
            }
        });

        list.add(new AddDocumentButton(id){
            @Override
            protected void onClick() {
                setResponsePage(EmployeeEdit.class);
            }

            @Override
            public boolean isVisible() {
                return getPreferenceLong(SELECTED_PERSON_PROFILE_ID) != null;
            }
        });

        list.add(new DeleteItemButton(id){
            @Override
            protected void onClick() {
                for (Long id : selectMap.keySet()){
                    if (selectMap.get(id).getObject()){
                        Employee employee = employeeBean.getEmployee(id);

                        employeeBean.delete(id);

                        info(getStringFormat("info_deleted", employee.getHname()));
                        log.info("Сотрудник удален", new Event(REMOVE, employee));
                    }
                }
            }

            @Override
            public boolean isVisible() {
                return getPreferenceLong(SELECTED_PERSON_PROFILE_ID) != null;
            }
        });

        return list;
    }
}
