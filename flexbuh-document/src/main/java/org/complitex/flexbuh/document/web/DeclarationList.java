package org.complitex.flexbuh.document.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.*;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationFilter;
import org.complitex.flexbuh.document.service.DeclarationBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.complitex.flexbuh.util.StringUtil;
import org.complitex.flexbuh.web.component.BookmarkablePageLinkPanel;
import org.complitex.flexbuh.web.component.declaration.PeriodTypeChoice;
import org.complitex.flexbuh.web.component.paging.PagingNavigator;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.text.DateFormatSymbols;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.09.11 18:51
 */
public class DeclarationList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(DeclarationList.class);

    private final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(getLocale());

    private final static int MIN_YEAR = 1990;
    private final static int MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR);

    @EJB
    private DeclarationBean declarationBean;

    public DeclarationList() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        Form filterForm = new Form("filter_form");
        add(filterForm);

        //Фильтр
        final DeclarationFilter declarationFilter = new DeclarationFilter(getSessionId(false));

        //Название
        filterForm.add(new TextField<>("name", new PropertyModel<String>(declarationFilter, "name")));

        //Месяц (для 1,2,3,4 кварталов это 3,6,9,12 месяц соответственно, для года - 12)
        final DropDownChoice periodMonthChoice = new DropDownChoice<>("period_month",
                new PropertyModel<Integer>(declarationFilter, "periodMonth"),
                new LoadableDetachableModel<List<Integer>>() {
                    @Override
                    protected List<Integer> load() {
                        Integer type = declarationFilter.getPeriodType();

                        if (type == null || type == 1){
                            return Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
                        }

                        switch (declarationFilter.getPeriodType()){
                            case 2: return Arrays.asList(3, 6, 9, 12);
                            case 3: return Arrays.asList(6);
                            case 4: return Arrays.asList(9);
                            case 5: return Arrays.asList(12);
                        }

                        return Collections.emptyList();
                    }
                }, new IChoiceRenderer<Integer>() {
            @Override
            public Object getDisplayValue(Integer object) {
                return dateFormatSymbols.getMonths()[object - 1];
            }

            @Override
            public String getIdValue(Integer object, int index) {
                return object.toString();
            }
        });
        periodMonthChoice.setOutputMarkupId(true);
        periodMonthChoice.setNullValid(true);
        filterForm.add(periodMonthChoice);

        //Период (1-месяц, 2-квартал, 3-полугодие, 4-9 месяцев, 5-год)
        PeriodTypeChoice periodTypeChoice = new PeriodTypeChoice("period_type", new PropertyModel<Integer>(declarationFilter, "periodType"));
        periodTypeChoice.setOutputMarkupId(true);
        periodTypeChoice.setNullValid(true);
        filterForm.add(periodTypeChoice);
        periodTypeChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(periodMonthChoice);
            }
        });

        //Год
        List<Integer> years = new ArrayList<>();
        for (int i = MIN_YEAR; i <= MAX_YEAR; ++i){
            years.add(i);
        }
        filterForm.add(new DropDownChoice<>("period_year", new PropertyModel<Integer>(declarationFilter, "periodYear"),
                years).setNullValid(true));

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

                item.add(new Label("name", declaration.getTemplateName() + " " + declaration.getName()));
                item.add(new Label("period_type", getString("period_type_" + declaration.getHead().getPeriodType())));
                item.add(new Label("period_month", dateFormatSymbols.getMonths()[declaration.getHead().getPeriodMonth()-1]));
                item.add(new Label("period_year", StringUtil.getString(declaration.getHead().getPeriodYear())));
                item.add(DateLabel.forDatePattern("date", new Model<>(declaration.getDate()), "dd.MM.yyyy HH:mm"));

                PageParameters pageParameters = new PageParameters();
                pageParameters.set("id", declaration.getId());

                item.add(new BookmarkablePageLinkPanel<>("action_edit", getString("action_edit"), DeclarationFormPage.class,
                        pageParameters));

                item.add(new DeclarationXmlLink("action_xml", declaration));
                item.add(new DeclarationPdfLink("action_pdf", declaration));
            }
        };

        filterForm.add(dataView);

        //Названия колонок и сортировка
        filterForm.add(new OrderByBorder("header.name", "name", dataProvider));
        filterForm.add(new OrderByBorder("header.period_type", "period_type", dataProvider));
        filterForm.add(new OrderByBorder("header.period_month", "period_month", dataProvider));
        filterForm.add(new OrderByBorder("header.period_year", "period_year", dataProvider));
        filterForm.add(new OrderByBorder("header.date", "date", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, DeclarationList.class.getName(), filterForm));

        //Загрузка файлов
        final IModel<List<FileUpload>> fileUploadModel = new ListModel<>();
        
        Form fileUploadForm = new Form("upload_form"){
            @Override
            protected void onSubmit() {
                List<FileUpload> fileUploads = fileUploadModel.getObject();

                try {
                    for (FileUpload fileUpload : fileUploads){
                        declarationBean.save(getSessionId(true), fileUpload.getInputStream());
                    }

                    info("Документы успешно загружены");
                } catch (Exception e) {
                    log.error("Ошибка загрузки файла", e);
                    error("Ошибка загрузки файла");
                }
            }
        };
        
        add(fileUploadForm);
        
        fileUploadForm.add(new FileUploadField("upload_field", fileUploadModel));
    }
}
