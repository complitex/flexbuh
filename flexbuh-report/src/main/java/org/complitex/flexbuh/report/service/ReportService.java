package org.complitex.flexbuh.report.service;

import org.complitex.flexbuh.report.entity.Report;
import org.complitex.flexbuh.report.entity.ReportSql;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 13.06.12 17:54
 */
@Stateless
public class ReportService {
    @EJB
    private ReportBean reportBean;

    public String fillMarkup(Report report){
        String s = report.getMarkup();

        for (ReportSql reportSql : report.getReportSqlList()){
            List<Map<String, String>> list = reportBean.getSqlList(reportSql);

            if (list == null || list.isEmpty()){
                continue;
            }

            if (list.size() == 1){
                Map<String, String> map = list.get(0);

                for (String key : map.keySet()){
                    s = s.replace("${" + key + "}", map.get(key));
                }
            }else {
                //todo table view
            }
        }

        return s;
    }
}
