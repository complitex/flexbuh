package org.complitex.flexbuh.common.service;

import org.complitex.flexbuh.common.entity.AbstractFilter;
import org.complitex.flexbuh.common.entity.template.TemplateControl;
import org.complitex.flexbuh.common.entity.template.TemplateFO;
import org.complitex.flexbuh.common.entity.template.TemplateXSD;
import org.complitex.flexbuh.common.entity.template.TemplateXSL;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.07.11 15:59
 */
@Stateless
public class TemplateBean extends AbstractBean {
    public static final String NS = TemplateBean.class.getName();

    public void save(TemplateXSL templateXSL){
        sqlSession().insert(NS + ".insertTemplateXSL", templateXSL);
    }

    public void save(TemplateXSD templateXSD){
        sqlSession().insert(NS + ".insertTemplateXSD", templateXSD);
    }

    public void save(TemplateFO templateFO){
        sqlSession().insert(NS + ".insertTemplateFO", templateFO);
    }

    public void save(TemplateControl templateControl){
        sqlSession().insert(NS + ".insertTemplateControl", templateControl);
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

    public TemplateFO getTemplateFO(String name){
        return  (TemplateFO) sqlSession().selectOne(NS + ".selectTemplateFO", name);
    }

	/**
	 * Get all templates without data
	 *
	 * @return xsl templates
	 */
	public List<TemplateXSL> getTemplateXSLs(){
        return sqlSession().selectList(NS + ".selectAllTemplateXSLs");
    }

	public List<TemplateXSL> getTemplateXSL(int first, int count){
        return sqlSession().selectList(NS + ".selectTemplateXSLs", new AbstractFilter(first, count));
    }

	public int getTemplateXSLsCount() {
		return (Integer)sqlSession().selectOne(NS + ".selectAllTemplateXSLsCount");
	}

	/**
	 * Get all templates without data
	 *
	 * @return xsd templates
	 */
    public List<TemplateXSD> getTemplateXSDs(){
        return sqlSession().selectList(NS + ".selectAllTemplateXSDs");
    }

	public List<TemplateXSD> getTemplateXSD(int first, int count){
        return sqlSession().selectList(NS + ".selectTemplateXSDs", new AbstractFilter(first, count));
    }

	public int getTemplateXSDsCount() {
		return (Integer)sqlSession().selectOne(NS + ".selectAllTemplateXSDsCount");
	}

	/**
	 * Get all templates without data
	 *
	 * @return control templates
	 */
    public List<TemplateControl> getTemplateControls(){
        return  sqlSession().selectList(NS + ".selectAllTemplateControl");
    }

	public List<TemplateControl> getTemplateControl(int first, int count){
        return sqlSession().selectList(NS + ".selectTemplateControls", new AbstractFilter(first, count));
    }

	public int getTotalCountTemplateControl() {
		return (Integer)sqlSession().selectOne(NS + ".selectAllTemplateControlsCount");
	}

	/**
	 * Get all templates without data
	 *
	 * @return fo templates
	 */
    public List<TemplateFO> getAllTemplateFO(){
        return  sqlSession().selectList(NS + ".selectAllTemplateFOs");
    }

	public List<TemplateFO> getTemplateFO(int first, int count){
        return sqlSession().selectList(NS + ".selectTemplateFOs", new AbstractFilter(first, count));
    }

	public int getTemplateFOsCount() {
		return (Integer)sqlSession().selectOne(NS + ".selectAllTemplateFOsCount");
	}

    public List<String> getTemplateXSLNames(){
        return sqlSession().selectList(NS + ".selectTemplateXSLNames");
    }
}
