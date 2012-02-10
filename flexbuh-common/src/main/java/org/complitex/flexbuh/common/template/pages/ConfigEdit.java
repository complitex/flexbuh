package org.complitex.flexbuh.common.template.pages;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.entity.IConfig;
import org.complitex.flexbuh.common.security.SecurityRole;
import org.complitex.flexbuh.common.service.ConfigBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;

import javax.ejb.EJB;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.10.2010 13:21:37
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class ConfigEdit extends FormTemplatePage {
    @EJB(name = "ConfigBean")
    private ConfigBean configBean;

    public ConfigEdit() {
        super();

        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));

        Form form = new Form("form");
        add(form);

        final List<IConfig> configs = new ArrayList<IConfig>(configBean.getConfigs());

        final Map<IConfig, IModel<String>> model = new HashMap<IConfig, IModel<String>>();

        for (IConfig config : configs){
            model.put(config, new Model<String>(configBean.getString(config, true)));
        }

        //add localization bundles
        addAllResourceBundle(configBean.getResourceBundles());

        final Map<String, List<IConfig>> configGroupMap = configBean.getConfigGroups();

        ListView<String> groupNames = new ListView<String>("groupNames", new ArrayList<String>(configGroupMap.keySet())){

            @Override
            protected void populateItem(ListItem<String> item) {
                String groupKey = item.getModelObject();

                item.add(new Label("groupName", getStringOrKey(groupKey)));

                item.add(new ListView<IConfig>("listView", configGroupMap.get(groupKey)){
                    {
                        setReuseItems(true);
                    }

                    @Override
                    protected void populateItem(ListItem<IConfig> item) {
                        IConfig config = item.getModelObject();

                        item.add(new Label("label", getStringOrKey(config.name())));
                        if (config.getAllowedValues() != null && config.getAllowedValues().size() > 0) {
                            item.add(new DropDownChoice<String>("config_select", model.get(config), config.getAllowedValues()) {
                                @Override
                                protected boolean localizeDisplayValues() {
                                    return true;
                                }
                            });
                            item.add(new TextField<String>("config_text").setVisible(false));
                        } else {
                            item.add(new DropDownChoice<>("config_select").setVisible(false));
                            item.add(new TextField<String>("config_text", model.get(config)));
                        }
                    }
                });
            }
        };
        groupNames.setReuseItems(true);
        form.add(groupNames);

        Button save = new Button("save"){
            @Override
            public void onSubmit() {
                for (IConfig configName : configs){
                    String value = model.get(configName).getObject();

                    if (!configBean.getString(configName, true).equals(value)){
                        configBean.update(configName, value);
                    }
                }
                info(getString("saved"));
            }
        };
        form.add(save);
    }
}
