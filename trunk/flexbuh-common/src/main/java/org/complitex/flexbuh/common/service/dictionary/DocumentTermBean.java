package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.dictionary.DocumentTerm;
import org.complitex.flexbuh.common.entity.dictionary.DocumentTermFilter;
import org.complitex.flexbuh.common.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 27.08.11 10:05
 */
@Stateless
public class DocumentTermBean extends AbstractBean {
    public static final String NS = DocumentTermBean.class.getName();

    public void save(DocumentTerm documentTerm) {
        sqlSession().insert(NS + ".insertDocumentTerm", documentTerm);
    }

    public DocumentTerm getDocumentTerm(Long id) {
        return (DocumentTerm)sqlSession().selectOne(NS + ".selectDocumentTerm", id);
    }

    public List<DocumentTerm> getDocumentTerms(DocumentTermFilter filter) {
        return sqlSession().selectList(NS + ".selectDocumentTerms", filter);
    }

    public Integer getDocumentTermsCount(DocumentTermFilter filter){
        return (Integer)sqlSession().selectOne(NS + ".selectDocumentTermsCount", filter);
    }

	public List<Integer> getPeriodTypes() {
		return sqlSession().selectList(NS + ".selectPeriodTypes");
	}

	public List<Integer> getPeriodMonths() {
		return sqlSession().selectList(NS + ".selectPeriodMonths");
	}
}
