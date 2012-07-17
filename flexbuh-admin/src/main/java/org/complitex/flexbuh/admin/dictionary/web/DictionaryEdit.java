package org.complitex.flexbuh.admin.dictionary.web;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.markup.html.form.DateTextField;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.admin.dictionary.service.DictionaryFactory;
import org.complitex.flexbuh.common.annotation.Display;
import org.complitex.flexbuh.common.entity.dictionary.AbstractDictionary;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.ICrudBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.07.12 16:39
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class DictionaryEdit extends FormTemplatePage {
    private final static Logger log = LoggerFactory.getLogger(DictionaryEdit.class);

    public <T extends AbstractDictionary> DictionaryEdit(PageParameters parameters) {
        final String entityName = parameters.get("type").toString();
        final Long id = parameters.get("id").toOptionalLong();

        final Class<T> entityClass = DictionaryFactory.getEntity(entityName);
        ICrudBean<T> bean = DictionaryFactory.getCrudBean(entityName);

        final T object;
        final T oldObject;

        //load or create object
        if (id != null){
            object = bean.load(id);
            oldObject = bean.load(id);
        }else {
            try {
                object = entityClass.newInstance();
                oldObject = null;
            } catch (Exception e) {
                log.error("Ошибка создания объекта");
                throw new WicketRuntimeException(e);
            }
        }

        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        add(form);


        //create input text field
        ListView listView = new ListView<Field>("fields",
                new LoadableDetachableModel<List<? extends Field>>() {
                    @Override
                    protected List<? extends Field> load() {
                        return  ReflectionUtil.getAllFields(entityClass);
                    }
                }) {
            @Override
            protected void populateItem(ListItem<Field> item) {
                Field field = item.getModelObject();

                Class type = field.getType();

                //label
                Label label;
                item.add(label = new Label("label", getStringOrKey(field.getName())));

                final TextField textField;

                //input
                if (String.class.equals(type) || Integer.class.equals(type) || Long.class.equals(type)
                        || Boolean.class.equals(type)){
                    //noinspection unchecked
                    item.add(textField = new TextField<>("field", new PropertyModel<>(object, field.getName()), type));
                    textField.setLabel(Model.of(getStringOrKey(field.getName())));
                }else if (Date.class.equals(type)){
                    item.add(textField = DateTextField.forDatePattern("field", new PropertyModel<Date>(object, field.getName()),
                            "dd.MM.yyyy HH:mm:ss"));
                }else {
                    item.add(textField = new TextField("field"));
                    textField.setEnabled(false);
                }

                //display
                Display display = field.getAnnotation(Display.class);
                if (display != null){
                    textField.setEnabled(display.enable());
                    textField.setVisible(display.visible());
                    label.setVisible(display.visible());
                }

                //required
                NotNull notNull = field.getAnnotation(NotNull.class);
                if (notNull != null){
                    textField.setRequired(true);
                }
            }
        };
        listView.setReuseItems(true);
        form.add(listView);

        //save results
        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                ICrudBean<T> bean = DictionaryFactory.getCrudBean(entityName);

                if (id != null){
                    bean.update(object);

                    log.info("Запись справочника обновлена", new Event(EventCategory.EDIT, oldObject, object));
                    getSession().info(getString("updated"));
                }else {
                    bean.insert(object);

                    log.info("Запись справочника сохранена", new Event(EventCategory.EDIT, object));
                    getSession().info(getString("inserted"));
                }

                setResponsePage(DictionaryPages.getListPage(entityName));
            }
        });

        form.add(new Button("cancel"){
            @Override
            public void onSubmit() {
                setResponsePage(DictionaryPages.getListPage(entityName));
            }
        }.setDefaultFormProcessing(false));
    }
}
