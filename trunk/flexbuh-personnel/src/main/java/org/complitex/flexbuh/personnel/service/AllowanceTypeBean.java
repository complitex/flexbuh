package org.complitex.flexbuh.personnel.service;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.mybatis.Transactional;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.personnel.entity.AllowanceType;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 18.10.12 10:07
 */
@Stateless
public class AllowanceTypeBean extends AbstractBean {
    public static final String NS = AllowanceTypeBean.class.getName();

    public static final int SIZE = 10;

    @Transactional
    public void create(String type) {
        if (type != null && !isAllowanceTypeExist(type)) {
            sqlSession().insert(NS + ".insertAllowanceType", type);
        }
    }

    public List<AllowanceType> getAllowanceTypes(String start) {
        return sqlSession().selectList(NS + ".selectAllowanceTypes", FilterWrapper.of(new AllowanceType(start), 0, SIZE));
    }

    public boolean isAllowanceTypeExist(String start) {
        return (Boolean)sqlSession().selectOne(NS + ".isAllowanceTypeExist", start);
    }
}
