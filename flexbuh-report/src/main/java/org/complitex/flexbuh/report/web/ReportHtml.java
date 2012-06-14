package org.complitex.flexbuh.report.web;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.report.entity.Report;
import org.complitex.flexbuh.report.service.ReportBean;
import org.complitex.flexbuh.report.service.ReportService;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.06.12 17:23
 */
public class ReportHtml extends WebPage{
    @EJB
    private ReportService reportService;

    @EJB
    private ReportBean reportBean;

    public ReportHtml(PageParameters parameters) {
        Report report = reportBean.getReport(parameters.get("id").toLongObject());

        add(new Label("report", reportService.fillMarkup(report)).setEscapeModelStrings(false));
    }
}
