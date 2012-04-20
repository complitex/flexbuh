package org.complitex.flexbuh.common.service;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.template.TemplateXML;
import org.complitex.flexbuh.common.entity.template.TemplateXMLType;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 15:59
 */
@Stateless
public class TemplateXMLBean extends AbstractBean {
    public static final String NS = TemplateXMLBean.class.getName();

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void save(TemplateXML templateXML){
        if (templateXML.getId() != null) {
            sqlSession().update(NS + ".updateTemplateXML", templateXML);
        }else{
            sqlSession().insert(NS + ".insertTemplateXML", templateXML);
        }
    }

    public TemplateXML getTemplateXML(TemplateXMLType type, String name){
        Map<String, Object> map = new HashMap<>();
        map.put("type", type);
        map.put("name", name);

        return sqlSession().selectOne(NS + ".selectTemplateXML", map);
    }

    public List<TemplateXML> getTemplateXML(TemplateXMLType type, int first, int count){
        return sqlSession().selectList(NS + ".selectTemplateXMLs", FilterWrapper.of(type, first, count));
    }

    public int getTemplateXMLsCount(TemplateXMLType type) {
        return sqlSession().selectOne(NS + ".selectTemplateXMLsCount", type);
    }

    public Long getTemplateId(TemplateXML templateXML){
        return sqlSession().selectOne(NS + ".selectTemplateXMLId", templateXML);
    }
}
