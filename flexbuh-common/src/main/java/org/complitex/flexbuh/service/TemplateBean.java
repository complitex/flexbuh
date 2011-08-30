package org.complitex.flexbuh.service;

import org.complitex.flexbuh.entity.AbstractTemplate;
import org.complitex.flexbuh.entity.TemplateControl;
import org.complitex.flexbuh.entity.TemplateXSD;
import org.complitex.flexbuh.entity.TemplateXSL;

import javax.ejb.Stateless;
import java.util.List;

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

    public TemplateXSL getTemplateXSL(String name){
        return (TemplateXSL) sqlSession().selectOne(NS + ".selectTemplateXSL", name);
    }

    public TemplateXSD getTemplateXSD(String name){
        return (TemplateXSD) sqlSession().selectOne(NS + ".selectTemplateXSD", name);
    }

    public TemplateControl getTemplateControl(String name){
        return  (TemplateControl) sqlSession().selectOne(NS + ".selectTemplateControl", name);
    }

    public TemplateControl getTemplateFO(String name){
        return  (TemplateControl) sqlSession().selectOne(NS + ".selectTemplateFO", name);
    }

	public boolean isExist(AbstractTemplate template) {
		return (Long)sqlSession().selectOne(NS + ".isExist", template) > 0;
	}

    @SuppressWarnings({"unchecked"})
    public List<String> getTemplateXSLNames(){
        return sqlSession().selectList(NS + ".selectTemplateXSLNames");
    }
}
