package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.document.entity.AbstractTemplate;
import org.complitex.flexbuh.service.AbstractBean;

import javax.ejb.Stateless;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 15:59
 */
@Stateless
public class TemplateBean extends AbstractBean {
    public static final String NS = TemplateBean.class.getName();

    public void save(AbstractTemplate template){
        sqlSession().insert(NS + ".insertTemplate", template);
    }
}
