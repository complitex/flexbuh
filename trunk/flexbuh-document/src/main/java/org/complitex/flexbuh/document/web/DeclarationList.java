package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.EmptyPanel;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.time.Time;
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
import org.complitex.flexbuh.document.web.component.DeclarationUploadDialog;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.ByteArrayOutputStream;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.09.11 18:51
 */
public class DeclarationList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(DeclarationList.class);

    private final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(getLocale());

    @EJB
    private DeclarationBean declarationBean;

    @EJB
    private DeclarationService declarationService;

    @EJB
    private PersonProfileBean personProfileBean;

    private DeclarationUploadDialog declarationUploadDialog;

    private Map<Declaration, IModel<Boolean>> selectMap = new HashMap<>();

    final String[] MONTH = dateFormatSymbols.getMonths();
    final List<Period> PERIODS = Arrays.asList(
            new Period(1, 0, MONTH[0]), new Period(1, 1, MONTH[1]), new Period(1, 2, MONTH[2]),
            new Period(2, 2, "1 " + getString("period_type_2")),
            new Period(1, 2, MONTH[3]), new Period(1, 4, MONTH[4]), new Period(1, 5, MONTH[5]),
            new Period(2, 5, "2 " + getString("period_type_2")),
            new Period(3, 5, "1 " + getString("period_type_3")),
            new Period(1, 6, MONTH[6]), new Period(1, 7, MONTH[7]), new Period(1, 8, MONTH[8]),
            new Period(2, 8, "3 " + getString("period_type_2")),
            new Period(1, 8, MONTH[9]), new Period(1, 10, MONTH[10]), new Period(1, 11, MONTH[11]),
            new Period(2, 11, "4 " + getString("period_type_2")),
            new Period(3, 11, "1 " + getString("period_type_3")),
            new Period(4, 8, getString("period_type_4")),
            new Period(5, 11, getString("period_type_5"))
    );

    public DeclarationList() {
        add(new Label("title", getString("title")));
        final FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final WebMarkupContainer yearContainer = new WebMarkupContainer("year_container");
        yearContainer.setOutputMarkupId(true);

        add(yearContainer);

        final Long sessionId = getSessionId();

        //Фильтр
        final DeclarationFilter declarationFilter = new DeclarationFilter(sessionId);
        declarationFilter.setPeriodYear(DateUtil.getCurrentYear());
        declarationFilter.setPeriodType(1);
        declarationFilter.setPeriodMonth(DateUtil.getCurrentMonth() + 1);

        //Таблица
        final WebMarkupContainer filterContainer = new WebMarkupContainer("filter_container");
        filterContainer.setOutputMarkupId(true);

        //Форма
        Form filterForm = new Form("filter_form");
        filterContainer.add(filterForm);

        IModel<List<? extends Integer>> yearModel = new LoadableDetachableModel<List<? extends Integer>>() {
            @Override
            protected List<? extends Integer> load() {
                return declarationBean.getYears(sessionId);
            }
        };

        //Дерево - годы
        ListView yearList = new ListView<Integer>("year_list", yearModel) {
            @Override
            protected void populateItem(final ListItem<Integer> item) {
                final Integer year = item.getModelObject();
                final boolean selectedYear = year.equals(declarationFilter.getPeriodYear());
                item.setRenderBodyOnly(true);

                AjaxLink action = new AjaxLink("action_select") {
                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        declarationFilter.setPeriodYear(year);

                        target.add(yearContainer);
                    }

                    @Override
                    public boolean isEnabled() {
                        return !selectedYear;
                    }
                };
                item.add(action);

                action.add((new Label("year_header", year + "")));

                final WebMarkupContainer periodContainer = new WebMarkupContainer("period_container");
                periodContainer.setOutputMarkupId(true);
                item.add(periodContainer);

                //Load periods for year and set labels
                List<Period> periods = declarationBean.getPeriods(sessionId, year);
                for (Period p : periods){
                    for (Period label : PERIODS){
                        if (p.getMonth().equals(label.getMonth()) && p.getType().equals(label.getType())){
                            p.setLabel(label.getLabel());
                        }
                    }
                }

                //Дерево - периоды
                if (selectedYear) {
                    ListView periodList = new ListView<Period>("period_list", periods) {
                        @Override
                        protected void populateItem(ListItem<Period> periodItem) {
                            periodItem.setRenderBodyOnly(true);

                            final Period period = periodItem.getModelObject();

                            final boolean selectedPeriod = period.getMonth().equals(declarationFilter.getPeriodMonth() - 1)
                                    && period.getType().equals(declarationFilter.getPeriodType());

                            if (selectedPeriod){
                                periodItem.add(filterContainer);
                            }else {
                                WebMarkupContainer emptyDiv = new WebMarkupContainer("filter_container");
                                emptyDiv.add(new Form("filter_form").setVisible(false));

                                periodItem.add(emptyDiv);
                            }

                            AjaxLink action = new AjaxLink("action_select") {
                                @Override
                                public void onClick(AjaxRequestTarget target) {
                                    declarationFilter.setPeriodYear(year);
                                    declarationFilter.setPeriodType(period.getType());
                                    declarationFilter.setPeriodMonth(period.getMonth() + 1);

                                    target.add(yearContainer);
                                }

                                @Override
                                public boolean isEnabled() {
                                    return !selectedPeriod;
                                }
                            };
                            periodItem.add(action);

                            action.add(new Label("period_header", period.getLabel()));
                        }
                    };
                    periodContainer.add(periodList);
                }else {
                    periodContainer.add(new EmptyPanel("period_list").setVisible(false));
                }
            }
        };
        yearContainer.add(yearList);

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
                selectMap.clear();

                declarationFilter.setFirst(first);
                declarationFilter.setCount(count);
                declarationFilter.setSortProperty(getSort().getProperty());
                declarationFilter.setAscending(getSort().isAscending());

                return declarationBean.getDeclarations(declarationFilter).iterator();
            }

            @Override
            public int size() {
                return declarationBean.getDeclarationsCount(declarationFilter);
            }

            @Override
            public IModel<Declaration> model(Declaration object) {
                return new Model<>(object);
            }
        };
        dataProvider.setSort("date", SortOrder.DESCENDING);

        //Таблица
        DataView<Declaration> dataView = new DataView<Declaration>("declarations", dataProvider) {
            @Override
            protected void populateItem(Item<Declaration> item) {
                final Declaration declaration = item.getModelObject();

                //Select
                IModel<Boolean> selectModel = new Model<>(false);

                selectMap.put(declaration, selectModel);

                item.add(new CheckBox("select", selectModel)
                        .add(new AjaxFormComponentUpdatingBehavior("onchange") {
                            @Override
                            protected void onUpdate(AjaxRequestTarget target) {
                                //update
                            }
                        }));

                //Name
                PageParameters pageParameters = new PageParameters();
                pageParameters.set("id", declaration.getId());

                item.add(new BookmarkablePageLinkPanel<>("name", declaration.getTemplateName() + " " + declaration.getName(),
                        DeclarationFormPage.class, pageParameters));

                //Date
                item.add(DateLabel.forDatePattern("date", new Model<>(declaration.getDate()), "dd.MM.yyyy HH:mm"));

                //Action
                item.add(new DeclarationXmlLink("action_xml", declaration));
                item.add(new DeclarationPdfLink("action_pdf", declaration));

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
                        declaration.getLinkedDeclarations()) {
                    @Override
                    protected void populateItem(ListItem<LinkedDeclaration> linkedItem) {
                        final Declaration linkedDeclaration = linkedItem.getModelObject().getDeclaration();

                        PageParameters pageParameters = new PageParameters();
                        pageParameters.set("id", linkedDeclaration.getId());

                        linkedItem.add(new BookmarkablePageLinkPanel<>("name", linkedDeclaration.getTemplateName()
                                + " " + linkedDeclaration.getName(), DeclarationFormPage.class, pageParameters));

                        linkedItem.add(DateLabel.forDatePattern("date", new Model<>(linkedDeclaration.getDate()), "dd.MM.yyyy HH:mm"));

                        linkedItem.add(new DeclarationXmlLink("action_xml", linkedDeclaration));
                        linkedItem.add(new DeclarationPdfLink("action_pdf", linkedDeclaration));

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
                List<Declaration> selectedDeclarations = getSelectedDeclaration();

                if (selectedDeclarations.isEmpty()){
                    info(getString("info_select_declarations"));
                    return;
                }

                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                try {
                    declarationService.writeXmlZip(selectedDeclarations, outputStream);
                } catch (DeclarationZipException e) {
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
                        }, "declaration_xml" + ".zip"));
            }
        });

        filterForm.add(new Button("download_pdf_zip"){
            @Override
            public void onSubmit() {
                List<Declaration> selectedDeclarations = getSelectedDeclaration();

                if (selectedDeclarations.isEmpty()){
                    info(getString("info_select_declarations"));
                    return;
                }

                final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

                try {
                    declarationService.writePdfZip(selectedDeclarations, outputStream);
                } catch (DeclarationZipException e) {
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
                        }, "declaration_pdf" + ".zip"));
            }
        });

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, DeclarationList.class.getName(), filterForm));

        IAjaxUpdate update =  new IAjaxUpdate() {
            @Override
            public void onUpdate(AjaxRequestTarget target) {
                target.add(feedbackPanel);
                target.add(yearContainer);
            }
        };

        //Загрузка файлов
        declarationUploadDialog = new DeclarationUploadDialog("upload_dialog", update);
        add(declarationUploadDialog);
    }

    private List<Declaration> getSelectedDeclaration() {
        List<Declaration> selectedDeclarations = new ArrayList<>();

        for (Declaration declaration : selectMap.keySet()){
            if (selectMap.get(declaration).getObject()){
                selectedDeclarations.add(declaration);
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
                for (Declaration declaration : selectMap.keySet()){
                    if (selectMap.get(declaration).getObject()){
                        declarationBean.deleteDeclaration(declaration.getId());
                    }
                }
            }
        });

        return list;
    }
}
