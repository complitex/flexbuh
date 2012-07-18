package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.DocumentTerm;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.ICrudBean;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 27.08.11 10:05
 */
@Stateless
@LocalBean
public class DocumentTermBean extends AbstractBean implements ICrudBean<DocumentTerm>{
    public static final String NS = DocumentTermBean.class.getName();

    @Override
    public Long getId(DocumentTerm documentTerm) {
        return sqlSession().selectOne(NS + ".selectDocumentTermId", documentTerm);
    }

    @Override
    public void insert(DocumentTerm documentTerm) {
        sqlSession().insert(NS + ".insertDocumentTerm", documentTerm);
    }

    @Override
    public void update(DocumentTerm documentTerm) {
        sqlSession().update(NS + ".updateDocumentTerm", documentTerm);
    }

    @Override
    public DocumentTerm load(Long id) {
        return getDocumentTerm(id);
    }

    @Override
    public void delete(Long id) {
        sqlSession().delete(NS + ".deleteDocumentTerm", id);
    }

    public DocumentTerm getDocumentTerm(Long id) {
        return (DocumentTerm)sqlSession().selectOne(NS + ".selectDocumentTerm", id);
    }

    public List<DocumentTerm> getDocumentTerms(FilterWrapper<DocumentTerm> filter) {
        return sqlSession().selectList(NS + ".selectDocumentTerms", filter);
    }

    public Integer getDocumentTermsCount(FilterWrapper<DocumentTerm> filter){
        return (Integer)sqlSession().selectOne(NS + ".selectDocumentTermsCount", filter);
    }

	public List<Integer> getPeriodTypes() {
		return sqlSession().selectList(NS + ".selectPeriodTypes");
	}

	public List<Integer> getPeriodMonths() {
		return sqlSession().selectList(NS + ".selectPeriodMonths");
	}
}
