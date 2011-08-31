package org.complitex.flexbuh.service;

import org.complitex.flexbuh.entity.*;

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

	/**
	 * Get all templates without data
	 *
	 * @return xsl templates
	 */
	@SuppressWarnings("unchecked")
	public List<TemplateXSL> getAllTemplateXSL(){
        return sqlSession().selectList(NS + ".selectAllTemplateXSL");
    }

	/**
	 * Get all templates without data
	 *
	 * @return xsd templates
	 */
	@SuppressWarnings("unchecked")
    public List<TemplateXSD> getAllTemplateXSD(){
        return sqlSession().selectList(NS + ".selectAllTemplateXSD");
    }

	/**
	 * Get all templates without data
	 *
	 * @return control templates
	 */
	@SuppressWarnings("unchecked")
    public List<TemplateControl> getAllTemplateControl(){
        return  sqlSession().selectList(NS + ".selectAllTemplateControl");
    }

	/**
	 * Get all templates without data
	 *
	 * @return fo templates
	 */
	@SuppressWarnings("unchecked")
    public List<TemplateFO> getAllTemplateFO(){
        return  sqlSession().selectList(NS + ".selectAllTemplateFO");
    }

	public boolean isExist(AbstractTemplate template) {
		return (Long)sqlSession().selectOne(NS + ".isExist", template) > 0;
	}

    @SuppressWarnings({"unchecked"})
    public List<String> getTemplateXSLNames(){
        return sqlSession().selectList(NS + ".selectTemplateXSLNames");
    }
}
