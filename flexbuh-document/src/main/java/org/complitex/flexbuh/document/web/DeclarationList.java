package org.complitex.flexbuh.document.web;

import com.google.common.collect.Ordering;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.time.Time;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.template.toolbar.AddDocumentButton;
import org.complitex.flexbuh.common.template.toolbar.DeleteItemButton;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.template.toolbar.UploadButton;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.common.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.common.web.component.IAjaxUpdate;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationFilter;
import org.complitex.flexbuh.document.entity.LinkedDeclaration;
import org.complitex.flexbuh.document.entity.Period;
import org.complitex.flexbuh.document.exception.DeclarationZipException;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.complitex.flexbuh.document.service.DeclarationService;
import org.complitex.flexbuh.document.web.component.*;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.09.11 18:51
 */
public class DeclarationList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(DeclarationList.class);

    @EJB
    private DeclarationBean declarationBean;

    @EJB
    private DeclarationService declarationService;

    @EJB
    private PersonProfileBean personProfileBean;

    private DeclarationUploadDialog declarationUploadDialog;

    private Map<Long, IModel<Boolean>> selectMap = new ConcurrentHashMap<>();

    private DeclarationFilter declarationFilter;

    public DeclarationList(PageParameters parameters) {
        this();

        declarationFilter.getPeriods().add(
                new Period(
                        parameters.get("period_month").toInt(1),
                        parameters.get("period_type").toInt(1),
                        parameters.get("period_year").toInt(1)));
    }

    public DeclarationList() {
        add(new Label("title", getString("title")));
        final FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final WebMarkupContainer periodYearContainer = new WebMarkupContainer("period_year_container");
        periodYearContainer.setOutputMarkupId(true);
        add(periodYearContainer);

        final Long sessionId = getSessionId();

        //Фильтр
        declarationFilter = new DeclarationFilter(sessionId);

        //Default period
        declarationFilter.getPeriods().add(new Period(DateUtil.getCurrentMonth()+1, 1, DateUtil.getCurrentYear()));

        //Таблица
        final WebMarkupContainer tableContainer = new WebMarkupContainer("table_container");
        tableContainer.setOutputMarkupId(true);
        add(tableContainer);

        //Periods
        final IModel<Map<Integer, List<Period>>> periodMapModel = new LoadableDetachableModel<Map<Integer, List<Period>>>() {
            @Override
            protected Map<Integer, List<Period>> load() {
                Long personProfileId = getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID);

                return declarationBean.getPeriodMap(sessionId, personProfileId);
            }
        };

        //Дерево - годы
        ListView yearList = new ListView<Integer>("year_list", new ListModel<Integer>() {
            @Override
            public List<Integer> getObject() {
                return Ordering.natural().sortedCopy(periodMapModel.getObject().keySet());
            }

            @Override
            public void detach() {
                periodMapModel.detach();
            }
        }) {
            @Override
            protected void populateItem(final ListItem<Integer> item) {
                final Integer year = item.getModelObject();

                item.add((new Label("year_header", year + "")));

                //Дерево - периоды
                ListView periodList = new ListView<Period>("period_list", periodMapModel.getObject().get(year)) {
                    @Override
                    protected void populateItem(final ListItem<Period> periodItem) {
                        periodItem.setOutputMarkupId(true);

                        final Period period = periodItem.getModelObject();

                        periodItem.add(new Behavior() {
                            @Override
                            public void onComponentTag(Component component, ComponentTag tag) {
                                if (declarationFilter.getPeriods().contains(period)){
                                    tag.getAttributes().put("class", "period_block period_selected");
                                }else {
                                    tag.getAttributes().put("class", "period_block");
                                }
                            }
                        });

                        AjaxLink action = new AjaxLink("action_select") {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                Set<Period> filterPeriods = declarationFilter.getPeriods();

                                if (filterPeriods.contains(period)){
                                    filterPeriods.remove(period);

                                    //remove selected on change period
                                    List<Long> list = declarationBean.getAllDeclarationIds(declarationFilter);
                                    for (Long id : selectMap.keySet()) {
                                        if (!list.contains(id)) {
                                            selectMap.remove(id);
                                        }
                                    }
                                }else{
                                    filterPeriods.add(period);
                                }

                                target.add(periodItem);
                                target.add(tableContainer);
                            }
                        };
                        periodItem.add(action);

                        String periodString = getString("period_" + period.getType() + "_" + period.getMonth());

                        action.add(new Label("period_header", periodString));
                    }
                };
                item.add(periodList);
            }
        };
        periodYearContainer.add(yearList);

        //Форма
        Form filterForm = new Form("filter_form");
        tableContainer.add(filterForm);

        //Выбор всех документов в периоде
        final Label selectedCount = new Label("selected_count", new LoadableDetachableModel<Object>() {
            @Override
            protected Object load() {
                int count = 0;

                for (IModel<Boolean> s : selectMap.values()){
                    if (s.getObject()){
                        count++;
                    }
                }

                return count > 0 ? "(" + count + ")" : "";
            }
        });
        selectedCount.setOutputMarkupId(true);
        filterForm.add(selectedCount);

        final CheckBox selectAllPeriod = new CheckBox("select_all_period", new Model<>(Boolean.FALSE));
        selectAllPeriod.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (selectAllPeriod.getModelObject()){
                    List<Long> list = declarationBean.getAllDeclarationIds(declarationFilter);

                    for (Long id : list){
                        selectMap.put(id, Model.of(Boolean.TRUE));
                    }
                }else {
                    selectMap.clear();
                }

                target.add(tableContainer);
                target.add(selectedCount);
            }
        });
        filterForm.add(selectAllPeriod);

        //Название
        filterForm.add(new TextField<>("name", new PropertyModel<String>(declarationFilter, "name")));

        //Дата
        filterForm.add(new DatePicker<>("date", new PropertyModel<Date>(declarationFilter, "date")));

        //Все
        filterForm.add(new Button("reset"){
            @Override
            public void onSubmit() {
                declarationFilter.clear();
            }
        });

        //Модель
        SortableDataProvider<Declaration> dataProvider = new SortableDataProvider<Declaration>() {
            @Override
            public Iterator<? extends Declaration> iterator(int first, int count) {
                declarationFilter.setFirst(first);
                declarationFilter.setCount(count);
                declarationFilter.setSortProperty(getSort().getProperty());
                declarationFilter.setAscending(getSort().isAscending());

                return declarationBean.getDeclarations(declarationFilter).iterator();
            }

            @Override
            public int size() {
                //предполагается что size() вызывается до iterator()
                declarationFilter.setPersonProfileId(getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID));

                return declarationBean.getDeclarationsCount(declarationFilter);
            }

            @Override
            public IModel<Declaration> model(Declaration object) {
                return new Model<>(object);
            }
        };
        dataProvider.setSort("date", SortOrder.DESCENDING);

        //Validate message dialog
        final DeclarationValidateMessagesDialog validateMessages = new DeclarationValidateMessagesDialog("validate_messages");
        filterForm.add(validateMessages);

        //Таблица
        final DataView<Declaration> dataView = new DataView<Declaration>("declarations", dataProvider) {
            @Override
            protected void populateItem(Item<Declaration> item) {
                final Declaration declaration = item.getModelObject();

                //Select
                IModel<Boolean> selectModel = selectMap.get(declaration.getId());
                if (selectModel == null){
                    selectModel = Model.of(Boolean.FALSE);
                    selectMap.put(declaration.getId(), selectModel);
                }

                item.add(new CheckBox("select", selectModel)
                        .add(new AjaxFormComponentUpdatingBehavior("onchange") {
                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                target.add(selectedCount);
                            }
                        }));

                //Name
                PageParameters pageParameters = new PageParameters();
                pageParameters.set("id", declaration.getId());

                item.add(new BookmarkablePageLinkPanel<>("name", declaration.getTemplateName() + " " + declaration.getName(),
                        DeclarationEditPage.class, pageParameters, !declaration.isValidated() ? null : "color:#273540"));

                //Date
                item.add(DateLabel.forDatePattern("date", new Model<>(declaration.getDate()),
                        DateUtil.isCurrentDay(declaration.getDate()) ? "HH:mm" : "dd.MM.yyyy HH:mm"));

                //HFILL HNUM
                item.add(new Label("hfill",  declaration.getValue("HFILL")));
                item.add(new Label("hnum",  declaration.getValue("HNUM")));

                //Version
                Integer version = declaration.getHead().getCDocType();
                item.add(new Label("version",  version != null && version > 0 ? version.toString() : ""));

                //Action
                item.add(new DeclarationXmlLink("action_xml", declaration));
                item.add(new DeclarationPdfLink("action_pdf", declaration));
                item.add(new DeclarationCheckLink("action_check", declaration));
                item.add(new DeclarationClarifyLink("action_clarify", declaration));
                item.add(new AjaxLink("action_errors") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        validateMessages.open(declaration, target);
                    }
                }.setVisible(!declaration.isValidated()));

                //Attach
                item.add(new Link("action_attach") {
                    @Override
                    public void onClick() {
                        Declaration parent = declarationBean.getPossibleDeclarationParent(declaration.getId());

                        declaration.setParentId(parent.getId());
                        declarationBean.save(declaration);

                        info(getStringFormat("info_attached", declaration.getFullName(), parent.getFullName()));
                    }
                }.setVisible(declaration.getPossibleParentId() != null));

                //Linked
                final WebMarkupContainer linkedContainer = new WebMarkupContainer("linked_container");
                linkedContainer.setOutputMarkupId(true);
                item.add(linkedContainer);

                final ListView linkedDeclarations = new ListView<LinkedDeclaration>("linked_declarations",
                        declaration.getHead().getLinkedDeclarations() != null
                                ? declaration.getHead().getLinkedDeclarations()
                                : new ArrayList<LinkedDeclaration>()) {
                    @Override
                    protected void populateItem(ListItem<LinkedDeclaration> linkedItem) {
                        final Declaration linkedDeclaration = linkedItem.getModelObject().getDeclaration();

                        PageParameters pageParameters = new PageParameters();
                        pageParameters.set("id", linkedDeclaration.getId());

                        //Select
                        IModel<Boolean> selectModel = selectMap.get(linkedDeclaration.getId());
                        if (selectModel == null){
                            selectModel = Model.of(Boolean.FALSE);
                            selectMap.put(linkedDeclaration.getId(), selectModel);
                        }

                        linkedItem.add(new CheckBox("select", selectModel)
                                .add(new AjaxFormComponentUpdatingBehavior("onchange") {
                                    @Override
                                    protected void onUpdate(AjaxRequestTarget target) {
                                        target.add(selectedCount);
                                    }
                                }));

                        linkedItem.add(new BookmarkablePageLinkPanel<>("name", linkedDeclaration.getTemplateName()
                                + " " + linkedDeclaration.getName(), DeclarationEditPage.class, pageParameters,
                                !linkedDeclaration.isValidated() ? null : "color:#273540"));

                        linkedItem.add(DateLabel.forDatePattern("date", new Model<>(linkedDeclaration.getDate()),
                                DateUtil.isCurrentDay(linkedDeclaration.getDate()) ? "HH:mm" : "dd.MM.yyyy HH:mm"));

                        //HFILL HNUM
                        linkedItem.add(new Label("hfill",  linkedDeclaration.getValue("HFILL")));
                        linkedItem.add(new Label("hnum",  linkedDeclaration.getValue("HNUM")));

                        //Version
                        Integer version = declaration.getHead().getCDocType();
                        linkedItem.add(new Label("version",  version != null && version > 0 ? version.toString() : ""));

                        linkedItem.add(new DeclarationXmlLink("action_xml", linkedDeclaration));
                        linkedItem.add(new DeclarationPdfLink("action_pdf", linkedDeclaration));
                        linkedItem.add(new DeclarationCheckLink("action_check", linkedDeclaration));
                        linkedItem.add(new DeclarationClarifyLink("action_clarify", linkedDeclaration));
                        linkedItem.add(new AjaxLink("action_errors") {
                            @Override
                            public void onClick(AjaxRequestTarget target) {
                                validateMessages.open(linkedDeclaration, target);
                            }
                        }.setVisible(!linkedDeclaration.isValidated()));

                        //Detach
                        linkedItem.add(new Link("action_detach") {
                            @Override
                            public void onClick() {
                                linkedDeclaration.setParentId(null);
                                declarationBean.save(linkedDeclaration);

                                info(getStringFormat("info_detached", linkedDeclaration.getFullName()));
                            }
                        });

                    }
                };
                linkedDeclarations.setVisible(false);
                linkedContainer.add(linkedDeclarations);

                //Expand
                final Label expandLabel = new Label("label", new LoadableDetachableModel<String>() {
                    @Override
                    protected String load() {
                        return linkedDeclarations.isVisible() ? "-" : "+";
                    }
                });
                expandLabel.setOutputMarkupId(true);

                AjaxLink expand = new AjaxLink("expand") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        linkedDeclarations.setVisible(!linkedDeclarations.isVisible());
                        target.add(linkedContainer);
                        target.add(expandLabel);
                    }
                };
                expand.setVisible(declaration.hasLinkedDeclarations());
                item.add(expand);

                expand.add(expandLabel);
            }
        };
        filterForm.add(dataView);

        //Названия колонок и сортировка
        filterForm.add(new OrderByBorder("header.name", "name", dataProvider));
        filterForm.add(new OrderByBorder("header.date", "date", dataProvider));

        //Сохранение архива
        filterForm.add(new Button("download_xml_zip"){
            @Override
            public void onSubmit() {
                List<Long> selectedDeclarations = getSelectedDeclarationIds();

                if (selectedDeclarations.isEmpty()){
                    info(getString("info_select_declarations"));
                    return;
                }

                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                try {
                    declarationService.writeXmlZip(declarationBean.getDeclarations(selectedDeclarations), outputStream);

                    log.info("Документы выгружены, {0}", selectedDeclarations,
                            new Event(EventCategory.EXPORT, Declaration.class));
                } catch (DeclarationZipException e) {
                    log.error("Ошибка выгрузки документов", e);
                    error(getString("error_download_xml_zip"));
                }

                getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                        new AbstractResourceStreamWriter() {

                            @Override
                            public void write(Response output) {
                                output.write(outputStream.toByteArray());
                            }

                            @Override
                            public Bytes length() {
                                return Bytes.bytes(outputStream.size());
                            }

                            @Override
                            public String getContentType() {
                                return "application/zip";
                            }

                            @Override
                            public Time lastModifiedTime() {
                                return Time.now();
                            }
                        }, "declaration_xml_" + DateUtil.getStringDate(DateUtil.getCurrentDate()).replace(".", "") + ".zip"));
            }
        });

        filterForm.add(new Button("download_pdf_zip"){
            @Override
            public void onSubmit() {
                List<Long> selectedDeclarationIds = getSelectedDeclarationIds();

                if (selectedDeclarationIds.isEmpty()){
                    info(getString("info_select_declarations"));
                    return;
                }

                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                try {
                    declarationService.writePdfZip(declarationBean.getDeclarations(selectedDeclarationIds), outputStream);

                    log.info("Документы в печатной форме выгружены: {0} ", selectedDeclarationIds,
                            new Event(EventCategory.EXPORT, Declaration.class));
                } catch (DeclarationZipException e) {
                    log.error("Ошибка выгрузки документов в печатной форме", e);
                    error(getString("error_download_pdf_zip"));
                }

                getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                        new AbstractResourceStreamWriter() {

                            @Override
                            public void write(Response output) {
                                output.write(outputStream.toByteArray());
                            }

                            @Override
                            public Bytes length() {
                                return Bytes.bytes(outputStream.size());
                            }

                            @Override
                            public String getContentType() {
                                return "application/zip";
                            }

                            @Override
                            public Time lastModifiedTime() {
                                return Time.now();
                            }
                        }, "declaration_pdf_" + DateUtil.getStringDate(DateUtil.getCurrentDate()).replace(".", "") + ".zip"));
            }
        });

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, DeclarationList.class.getName(), filterForm));

        IAjaxUpdate update =  new IAjaxUpdate() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                target.add(feedbackPanel);
                target.add(periodYearContainer);
                target.add(tableContainer);
            }
        };

        //Загрузка файлов
        declarationUploadDialog = new DeclarationUploadDialog("upload_dialog", declarationFilter, update);
        add(declarationUploadDialog);

        //Отображение пустого списка
        filterForm.add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(dataView.getItemCount() > 0);
            }
        });

        WebMarkupContainer emptyDeclarationListContainer = new WebMarkupContainer("empty_declaration_list_container");
        emptyDeclarationListContainer.setOutputMarkupId(true);
        tableContainer.add(emptyDeclarationListContainer);

        emptyDeclarationListContainer.add(new Behavior() {
            @Override
            public void onConfigure(Component component) {
                component.setVisible(dataView.getItemCount() == 0);
            }
        });

        emptyDeclarationListContainer.add(new Link("add_declaration") {
            @Override
            public void onClick() {
                setResponsePage(DeclarationCreate.class);
            }
        });
    }

    private List<Long> getSelectedDeclarationIds() {
        List<Long> selectedDeclarations = new ArrayList<>();

        for (Long declarationId : selectMap.keySet()){
            if (selectMap.get(declarationId).getObject()){
                selectedDeclarations.add(declarationId);
            }
        }
        return selectedDeclarations;
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        List<ToolbarButton> list = new ArrayList<>();

        list.add(new UploadButton(id, true){
            @Override
            protected void onClick(AjaxRequestTarget target) {
                declarationUploadDialog.open(target);
            }
        });

        list.add(new AddDocumentButton(id){
            @Override
            protected void onClick() {
                setResponsePage(DeclarationCreate.class);
            }
        });

        list.add(new DeleteItemButton(id){
            @Override
            protected void onClick() {
                for (Long id : selectMap.keySet()){
                    if (selectMap.get(id).getObject()){
                        declarationBean.deleteDeclaration(id);

                        log.info("Документ удален", new Event(EventCategory.REMOVE, Declaration.class, id));
                    }
                }
            }
        });

        return list;
    }
}
