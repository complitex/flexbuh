package org.complitex.flexbuh.report.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.report.entity.Report;
import org.complitex.flexbuh.report.entity.ReportSql;
import org.complitex.flexbuh.report.service.ReportBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wicket.contrib.tinymce.TinyMceBehavior;
import wicket.contrib.tinymce.ajax.TinyMceAjaxButton;
import wicket.contrib.tinymce.settings.*;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

import static wicket.contrib.tinymce.settings.Button.*;
import static wicket.contrib.tinymce.settings.TinyMCESettings.Position.after;
import static wicket.contrib.tinymce.settings.TinyMCESettings.Position.before;
import static wicket.contrib.tinymce.settings.TinyMCESettings.Toolbar.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.06.12 18:35
 */
public class ReportEdit extends FormTemplatePage{
    private final static Logger log = LoggerFactory.getLogger(ReportEdit.class);

    @EJB
    private ReportBean reportBean;

    public ReportEdit() {
        init(null);
    }

    public ReportEdit(PageParameters parameters) {
        init(parameters.get("id").toLongObject());
    }

    private void init(Long id){
        add(new Label("title", getString("title")));

        final FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        add(feedbackPanel);

        final Report report;
        if (id != null){
            report = reportBean.getReport(id);
        }
        else{
            report = new Report();

            List<ReportSql> reportSqlList = new ArrayList<>();
            reportSqlList.add(new ReportSql());
            report.setReportSqlList(reportSqlList);
        }

        Form form = new Form<>("form");
        add(form);

        form.add(new TextField<>("name", new PropertyModel<>(report, "name")).setRequired(true));

        TextArea markup = new TextArea<>("markup", new PropertyModel<>(report, "markup"));
        markup.setRequired(true);
        markup.add(new TinyMceBehavior(getSettings()));
        form.add(markup);

        ListView reportSqlList = new ListView<ReportSql>("report_sql_list",
                new LoadableDetachableModel<List<? extends ReportSql>>() {
                    @Override
                    protected List<? extends ReportSql> load() {
                        return report.getReportSqlList();
                    }
                }) {
            @Override
            protected void populateItem(ListItem<ReportSql> item) {
                item.add(new TextArea<>("sql", new PropertyModel<>(item.getModel(), "sql")));
            }
        };
        reportSqlList.setReuseItems(true);
        form.add(reportSqlList);

        form.add(new TinyMceAjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                reportBean.save(report);

                info(getStringFormat("saved", DateUtil.getCurrentDate()));

                target.add(feedbackPanel);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });

        form.add(new org.apache.wicket.markup.html.form.Button("cancel"){
            @Override
            public void onSubmit() {
                setResponsePage(ReportList.class);
            }
        });
    }

    private TinyMCESettings getSettings(){
        TinyMCESettings settings = new TinyMCESettings(TinyMCESettings.Theme.advanced);

        ContextMenuPlugin contextMenuPlugin = new ContextMenuPlugin();
        settings.register(contextMenuPlugin);

        // first toolbar
        settings.add(fontselect, first, after);
        settings.add(fontsizeselect, first, after);

        // second toolbar
        PastePlugin pastePlugin = new PastePlugin();
        SearchReplacePlugin searchReplacePlugin = new SearchReplacePlugin();
        DateTimePlugin dateTimePlugin = new DateTimePlugin();
        dateTimePlugin.setDateFormat("Date: %m-%d-%Y");
        dateTimePlugin.setTimeFormat("Time: %H:%M");
        PreviewPlugin previewPlugin = new PreviewPlugin();
        settings.add(cut, second, before);
        settings.add(copy, second, before);
        settings.add(pastePlugin.getPasteButton(), second, before);
        settings.add(pastePlugin.getPasteTextButton(), second, before);
        settings.add(pastePlugin.getPasteWordButton(), second, before);
        settings.add(separator, second, before);
        settings.add(searchReplacePlugin.getSearchButton(), second, before);
        settings.add(searchReplacePlugin.getReplaceButton(), second, before);
        settings.add(separator, second, before);
        settings.add(Button.separator, second, after);
        settings.add(dateTimePlugin.getDateButton(), second, after);
        settings.add(dateTimePlugin.getTimeButton(), second, after);
        settings.add(separator, second, after);
        settings.add(previewPlugin.getPreviewButton(), second, after);
        settings.add(separator, second, after);
        settings.add(forecolor, second, after);
        settings.add(backcolor, second, after);

        // third toolbar
        TablePlugin tablePlugin = new TablePlugin();
        PrintPlugin printPlugin = new PrintPlugin();
        FullScreenPlugin fullScreenPlugin = new FullScreenPlugin();
        DirectionalityPlugin directionalityPlugin = new DirectionalityPlugin();
        settings.add(tablePlugin.getTableControls(), third, before);
        settings.add(Button.separator, third, after);
        settings.add(printPlugin.getPrintButton(), third, after);
        settings.add(Button.separator, third, after);
        settings.add(directionalityPlugin.getLtrButton(), third, after);
        settings.add(directionalityPlugin.getRtlButton(), third, after);
        settings.add(Button.separator, third, after);
        settings.add(fullScreenPlugin.getFullscreenButton(), third, after);

        // other settings
        settings.setToolbarAlign(TinyMCESettings.Align.left);
        settings.setToolbarLocation(TinyMCESettings.Location.top);
        settings.setStatusbarLocation(TinyMCESettings.Location.bottom);
        settings.setResizing(true);

        //skin
        settings.addCustomSetting("skin: \"o2k7\"");
        settings.addCustomSetting("skin_variant : \"silver\"");

        return settings;
    }
}
