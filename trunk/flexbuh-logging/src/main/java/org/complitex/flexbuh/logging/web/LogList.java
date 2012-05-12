package org.complitex.flexbuh.logging.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxSubmitLink;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.complitex.flexbuh.common.web.component.datatable.DataProvider;
import org.complitex.flexbuh.common.web.component.paging.PagingNavigator;
import org.complitex.flexbuh.logging.entity.Log;
import org.complitex.flexbuh.logging.service.LogFilter;
import org.complitex.flexbuh.logging.service.LogListBean;
import org.complitex.flexbuh.logging.web.component.LogChangePanel;
import org.odlabs.wiquery.ui.datepicker.DatePicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.complitex.flexbuh.common.logging.EventKey.*;

/**
 * @author Pavel Sknar
 *         Date: 10.11.11 17:15
 */
@AuthorizeInstantiation(SecurityRole.LOG_VIEW)
public class LogList extends TemplatePage {

	private final static Logger logger = LoggerFactory.getLogger(LogList.class);

    private final static String IMAGE_ARROW_TOP = "images/arrow2top.gif";
    private final static String IMAGE_ARROW_BOTTOM = "images/arrow2bot.gif";

	@EJB
    private LogListBean logListBean;

    public LogList() {
        super();
        init();
    }

    private void init() {
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        //Фильтр модель
        LogFilter filterObject = new LogFilter();

        final IModel<LogFilter> filterModel = new CompoundPropertyModel<>(filterObject);

        //Фильтр форма
        final Form<LogFilter> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        final Set<Long> expandModel = new HashSet<>();

        Link filter_reset = new Link("filter_reset") {

            @Override
            public void onClick() {
                filterForm.clearInput();
                filterModel.setObject(new LogFilter());
            }
        };
        filterForm.add(filter_reset);

        //Date
        DatePicker<Date> timestmp = new DatePicker<>("timestmp");
        filterForm.add(timestmp);

        //Login
        filterForm.add(new TextField<String>("login"));

        //Module
        filterForm.add(new DropDownChoice<>("module", logListBean.getModules(),
                new IChoiceRenderer<String>() {

                    @Override
                    public Object getDisplayValue(String object) {
                        return getStringOrKey(object);
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return object;
                    }
                }));


        //Controller Class
        filterForm.add(new DropDownChoice<>("caller_class", logListBean.getControllers(),
                new IChoiceRenderer<String>() {

                    @Override
                    public Object getDisplayValue(String object) {
                        return getStringOrKey(object);
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return object;
                    }
                }));

        //Model Class
        filterForm.add(new DropDownChoice<>("model", logListBean.getModels(),
                new IChoiceRenderer<String>() {

                    @Override
                    public Object getDisplayValue(String object) {
                        return getStringOrKey(object);
                    }

                    @Override
                    public String getIdValue(String object, int index) {
                        return object;
                    }
                }));

        //Object Id
        filterForm.add(new TextField<String>("objectId"));

        //Category
        filterForm.add(new DropDownChoice<>("category", Arrays.asList(EventCategory.values()),
                new IChoiceRenderer<EventCategory>() {

                    @Override
                    public Object getDisplayValue(EventCategory object) {
                        return getStringOrKey(object.name());
                    }

                    @Override
                    public String getIdValue(EventCategory object, int index) {
                        return String.valueOf(object.ordinal());
                    }
				}));

        //Level
        filterForm.add(new DropDownChoice<>("level_string", Arrays.asList(Log.LEVEL.values()),
                new IChoiceRenderer<Log.LEVEL>() {

                    @Override
                    public Object getDisplayValue(Log.LEVEL object) {
                        return getStringOrKey(object.name());
                    }

                    @Override
                    public String getIdValue(Log.LEVEL object, int index) {
                        return String.valueOf(object.ordinal());
                    }
                }));

        //Description
        filterForm.add(new TextField<String>("formatted_message"));

        //Модель данных списка элементов журнала событий
        final DataProvider<Log> dataProvider = new DataProvider<Log>() {

            @Override
            protected Iterable<? extends Log> getData(int first, int count) {
                LogFilter filter = filterModel.getObject();
                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());
                return logListBean.getLogs(filterModel.getObject());
            }

            @Override
            protected int getSize() {
                return logListBean.getLogsCount(filterModel.getObject());
            }
        };
        dataProvider.setSort("timestmp", SortOrder.DESCENDING);

        //Таблица журнала событий
        DataView<Log> dataView = new DataView<Log>("logs", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Log> item) {
                final Log log = item.getModelObject();

                item.add(DateLabel.forDatePattern("timestmp", new Model<>(new Date(log.getTime())), "dd.MM.yy HH:mm:ss"));
				//item.add(LogManager.get().getLinkComponent("objectId", log));
				item.add(new Label("objectId", log.get(OBJECT_ID)));
				item.add(new Label("caller_class", getStringOrKey(log.getController())));
				item.add(new Label("formatted_message", log.getDescription()));
				item.add(new Label("login", log.get(LOGIN)));
				item.add(new Label("module", getStringOrKey(log.getModuleName())));
				item.add(new Label("model", getStringOrKey(log.get(MODEL_CLASS))));
				item.add(new Label("category", getStringOrKey(log.get(CATEGORY))));
				item.add(new Label("level_string", getStringOrKey(log.getLevel())));


					LogChangePanel logChangePanel = new LogChangePanel("log_changes", log);
					logChangePanel.setVisible((log.containsKey(OLD_OBJECT) || log.containsKey(NEW_OBJECT))
                            && expandModel.contains(log.getId()));
					item.add(logChangePanel);


                ContextImage expandImage = new ContextImage("expand_image",
                        expandModel.contains(log.getId()) ? IMAGE_ARROW_TOP : IMAGE_ARROW_BOTTOM);

                AjaxSubmitLink expandLink = new AjaxSubmitLink("expand_link") {

                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        if (expandModel.contains(log.getId())) {
                            expandModel.remove(log.getId());
                        } else {
                            expandModel.add(log.getId());
                        }
                        target.add(filterForm);
                    }

					@Override
					protected void onError(AjaxRequestTarget target, Form<?> form) {

					}
				};
                expandLink.setDefaultFormProcessing(false);
                expandLink.setVisible(log.containsKey(OLD_OBJECT) || log.containsKey(NEW_OBJECT));
                expandLink.add(expandImage);
                item.add(expandLink);
            }
        };
        filterForm.add(dataView);

        //Сортировка
        filterForm.add(new OrderByBorder("header.timestmp", "timestmp", dataProvider));
        filterForm.add(new OrderByBorder("header.login", "login", dataProvider));
        filterForm.add(new OrderByBorder("header.module", "module", dataProvider));
        filterForm.add(new OrderByBorder("header.caller_class", "caller_class", dataProvider));
        filterForm.add(new OrderByBorder("header.model", "model", dataProvider));
        filterForm.add(new OrderByBorder("header.objectId", "objectId", dataProvider));
        filterForm.add(new OrderByBorder("header.category", "category", dataProvider));
        filterForm.add(new OrderByBorder("header.level_string", "level_string", dataProvider));
        filterForm.add(new OrderByBorder("header.formatted_message", "formatted_message", dataProvider));

        //Постраничная навигация
        filterForm.add(new PagingNavigator("paging", dataView, getClass().getName(), filterForm));
    }
}
