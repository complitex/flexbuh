package org.complitex.flexbuh.report.service;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.util.ListUtil;
import org.complitex.flexbuh.report.entity.Report;
import org.complitex.flexbuh.report.entity.ReportSql;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.06.12 15:40
 */
@Stateless
public class ReportBean extends AbstractBean{
    public static final String NS = ReportBean.class.getName();

    public ReportSql getReportSql(Long id){
        return sqlSession().selectOne(NS + ".selectReportSql", id);
    }

    public Report getReport(Long id){
        return sqlSession().selectOne(NS + ".selectReport", id);
    }

    public List<Report> getReportList(FilterWrapper<Report> filterWrapper){
        return sqlSession().selectList(NS + ".selectReportList", filterWrapper);
    }

    public Integer getReportListCount(FilterWrapper<Report> filterWrapper){
        return sqlSession().selectOne(NS + ".selectReportListCount", filterWrapper);
    }

    public void save(final Report report){
        if (report.getId() == null){
            //insert report
            sqlSession().insert(NS + ".insertReport", report);

            //update report_sql
            for (ReportSql reportSql : report.getReportSqlList()){
                reportSql.setReportId(report.getId());
                sqlSession().insert(NS + ".insertReportSql", reportSql);
            }
        }else{
            //delete report_sql
            List<ReportSql> dbList = getReport(report.getId()).getReportSqlList();
            Iterable<ReportSql> toDelete = ListUtil.getDiffById(dbList, report.getReportSqlList());
            for (ReportSql db : toDelete){
                sqlSession().delete(NS + ".deleteReportSql", db.getId());
            }

            //update report_sql
            for (ReportSql reportSql : report.getReportSqlList()){
                if (reportSql.getId() == null){
                    reportSql.setReportId(report.getId());
                    sqlSession().insert(NS + ".insertReportSql", reportSql);
                }else {
                    sqlSession().update(NS + ".updateReportSql", reportSql);
                }
            }

            //update report
            sqlSession().update(NS + ".updateReport", report);
        }
    }
}
