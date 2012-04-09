package org.complitex.flexbuh.document.web;

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
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.*;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.common.util.XmlUtil;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.document.entity.Employee;
import org.complitex.flexbuh.document.entity.EmployeeRowSet;
import org.complitex.flexbuh.document.service.EmployeeBean;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 17.11.11 16:05
 */
public class EmployeeList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(CounterpartList.class);

    @EJB
    private EmployeeBean employeeBean;

    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private ConfigBean configBean;

    private Dialog uploadDialog;

    private Map<Long, IModel<Boolean>> selectMap = new HashMap<>();

    public EmployeeList() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        final FilterWrapper<Employee> filter = new FilterWrapper<>(new Employee(getSessionId()));

        final Form filterForm = new Form("filter_form");
        add(filterForm);

        filterForm.add(new TextField<>("htin", new PropertyModel<>(filter, "object.htin"))); //Идентификационный номер
        filterForm.add(new TextField<>("hname", new PropertyModel<>(filter, "object.hname"))); //ФИО
        filterForm.add(new TextField<>("hbirthday", new PropertyModel<>(filter, "object.hbirthday"))); //Дата рождения
        filterForm.add(new TextField<>("hdateIn", new PropertyModel<>(filter, "object.hdateIn"))); //Дата принятия на работу
        filterForm.add(new TextField<>("hdateOut", new PropertyModel<>(filter, "object.hdateOut"))); //Дата увольнения

        //Все
        filterForm.add(new Button("reset"){
            @Override
            public void onSubmit() {
                filter.setObject(new Employee(getSessionId()));
            }
        });

        //Модель
        SortableDataProvider<Employee> dataProvider = new SortableDataProvider<Employee>() {
            @Override
            public Iterator<? extends Employee> iterator(int first, int count) {
                selectMap.clear();

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());

                return employeeBean.getEmployees(filter).iterator();
            }

            @Override
            public int size() {
                filter.getObject().setPersonProfileId(getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID));

                return employeeBean.getEmployeesCount(filter);
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

        final IModel<List<FileUpload>> fileUploadModel = new ListModel<>();

        Form fileUploadForm = new Form("upload_form");

        fileUploadForm.add(new AjaxButton("upload") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                List<FileUpload> fileUploads = fileUploadModel.getObject();

                Long personProfileId = getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID);

                int count = 0;

                try {
                    for (FileUpload fileUpload : fileUploads){
                        count += employeeBean.save(getSessionId(), personProfileId, fileUpload.getInputStream());
                    }

                    uploadDialog.close(target);

                    setResponsePage(EmployeeList.class);

                    getSession().info(getStringFormat("info_employee_loaded", count));
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
                return getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID) != null;
            }
        });

        list.add(new SaveButton(id, "export", false) {
            @Override
            protected void onClick() {
                getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                        new AbstractResourceStreamWriter() {
                            @Override
                            public void write(Response output) {
                                try {
                                    List<Employee> employees = employeeBean.getAllEmployees(getSessionId());

                                    //jaxb preprocess, set not null values
                                    for (Employee employee : employees){
                                        if (employee.getHtin() == null){
                                            employee.setHtin(0);
                                        }
                                    }

                                    OutputStream os = ((HttpServletResponse) output.getContainerResponse()).getOutputStream();

                                    XmlUtil.writeXml(EmployeeRowSet.class, new EmployeeRowSet(employees, true), os, "windows-1251");
                                } catch (Exception e) {
                                    log.error("Cannot export employee to xml: {}", new Object[]{e, EventCategory.EXPORT});
                                }
                            }

                            @Override
                            public String getContentType() {
                                return "application/xml";
                            }
                        }, EmployeeRowSet.FILE_NAME));
            }
        });

        list.add(new AddDocumentButton(id){
            @Override
            protected void onClick() {
                setResponsePage(EmployeeEdit.class);
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
                        employeeBean.delete(id);
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
