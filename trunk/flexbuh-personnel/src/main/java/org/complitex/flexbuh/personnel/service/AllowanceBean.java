package org.complitex.flexbuh.personnel.service;

import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.personnel.entity.Allowance;
import org.complitex.flexbuh.personnel.entity.AllowanceFilter;
import org.complitex.flexbuh.personnel.entity.TemporalDomainObjectFilter;

import javax.ejb.Stateless;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.util.Date;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 18.10.12 10:06
 */
@Stateless
public class AllowanceBean extends TemporalDomainObjectBean<Allowance> {

    public static final String NS = AllowanceBean.class.getName();

    public AllowanceBean() {
        super(NS);
    }

    @Transactional
    @Override
    public void save(Allowance allowance) {
        if (allowance.getId() != null) {
            update(allowance);
        } else {
            create(allowance);
        }
    }

    @Transactional
    public void create(Allowance allowance) {
        allowance.setVersion(1L);
        sqlSession().insert(NS + ".insertAllowance", allowance);
    }

    @Transactional
    public void update(Allowance allowance) {
        sqlSession().update(NS + ".updateAllowanceNullCompletionDate", allowance);
        sqlSession().insert(NS + ".updateAllowance", allowance);
    }

    @Transactional
    public void updateCompletionDate(Allowance allowance) {
        sqlSession().update(NS + ".updateAllowanceCompletionDate", allowance);
    }

    @Override
    @NotNull
    public <A extends TemporalDomainObjectFilter> List<Allowance> getTDOObjects(@Null A f) {

        AllowanceFilter filter = (AllowanceFilter)f;
        if (filter == null) {
            filter = new AllowanceFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }

        return sqlSession().selectList(NS + ".selectCurrentAllowances", filter);
    }

    public int getAllowancesCount(AllowanceFilter filter) {
        if (filter == null) {
            filter = new AllowanceFilter();
            filter.setCurrentDate(new Date());
            filter.setCount(Integer.MAX_VALUE);
        }

        return (Integer)sqlSession().selectOne(NS + ".selectCurrentAllowancesCount", filter);
    }

    @Transactional
    public void deleteAllowance(Allowance position) {
        position.setDeleted(true);
        if (position.getCompletionDate() == null) {
            position.setCompletionDate(new Date());
        }
        sqlSession().update(NS + ".delete", position);
    }
}