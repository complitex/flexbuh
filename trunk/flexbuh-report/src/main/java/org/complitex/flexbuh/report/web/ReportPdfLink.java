package org.complitex.flexbuh.report.web;

import org.apache.wicket.request.Response;
import org.apache.wicket.request.handler.resource.ResourceStreamRequestHandler;
import org.apache.wicket.util.lang.Bytes;
import org.apache.wicket.util.resource.AbstractResourceStreamWriter;
import org.apache.wicket.util.time.Time;
import org.complitex.flexbuh.common.web.component.NoCacheLink;
import org.complitex.flexbuh.report.entity.Report;
import org.complitex.flexbuh.report.service.ReportBean;
import org.complitex.flexbuh.report.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xhtmlrenderer.pdf.ITextRenderer;

import javax.ejb.EJB;
import java.io.ByteArrayOutputStream;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 14.06.12 17:54
 */
public class ReportPdfLink extends NoCacheLink{
    private final static Logger log = LoggerFactory.getLogger(ReportPdfLink.class);

    @EJB
    private ReportService reportService;

    @EJB
    private ReportBean reportBean;

    private Long reportId;

    public ReportPdfLink(String id, Long reportId) {
        super(id);

        this.reportId = reportId;
    }

    @Override
    public void onClick() {
        try {
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            final Report report = reportBean.getReport(reportId);

            String html = reportService.fillMarkup(report);

            html = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
                    + "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" "
                    + "\"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">"
                    + "<html><body>"
                    + html
                    + "</body></html>";

            ITextRenderer renderer = new ITextRenderer();
            renderer.getFontResolver().addFont("c:/LiberationSans-Regular.ttf", "UTF-8", true);

            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            outputStream.close();

            getRequestCycle().scheduleRequestHandlerAfterCurrent(new ResourceStreamRequestHandler(
                    new AbstractResourceStreamWriter(){

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
                            return "application/pdf";
                        }

                        @Override
                        public Time lastModifiedTime() {
                            return Time.now();
                        }
                    }, report.getName() + ".pdf"));
        } catch (Exception e) {
            log.error("Ошибка создания Отчета в формате PDF");
        }
    }
}
