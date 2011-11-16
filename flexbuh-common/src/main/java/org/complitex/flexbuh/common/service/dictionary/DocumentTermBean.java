package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.AbstractFilter;
import org.complitex.flexbuh.common.entity.dictionary.DocumentTerm;
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

    @SuppressWarnings("unchecked")
    public List<DocumentTerm> getDocumentTerms() {
        return (List<DocumentTerm>)sqlSession().selectList(NS + ".selectAllDocumentTerms");
    }

    public Integer getAllDocumentTermsCount(){
        return (Integer)sqlSession().selectOne(NS + ".selectAllDocumentTermsCount");
    }

    @SuppressWarnings("unchecked")
    public List<DocumentTerm> getDocumentTerms(int first, int count) {
        return sqlSession().selectList(NS + ".selectDocumentTerms", new AbstractFilter(first, count));
    }
}
