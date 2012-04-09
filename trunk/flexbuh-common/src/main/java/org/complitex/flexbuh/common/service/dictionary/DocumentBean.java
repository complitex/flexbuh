package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.dictionary.Document;
import org.complitex.flexbuh.common.entity.dictionary.DocumentFilter;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.util.DateUtil;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 22.08.11 12:15
 */
@Stateless
public class DocumentBean extends AbstractBean {
    public static final String NS = DocumentBean.class.getName();

    public void save(Document document) {
        sqlSession().insert(NS + ".insertDocument", document);
    }

    public Document getDocument(Long id) {
		return (Document)sqlSession().selectOne(NS + ".selectDocument", id);
	}

	public List<Document> getDocuments(DocumentFilter filter) {
		return sqlSession().selectList(NS + ".selectDocuments", filter);
	}

    public Integer getDocumentsCount(DocumentFilter filter){
        return (Integer)sqlSession().selectOne(NS + ".selectDocumentsCount", filter);
    }

    public List<Document> getLinkedDocuments(String cDoc, String cDocSub, Integer periodYear, Integer periodMonth){
        DocumentFilter filter = new DocumentFilter(cDoc, cDocSub, DateUtil.getFirstDayOfMonth(periodYear, periodMonth-1));

        return sqlSession().selectList(NS + ".selectLinkedDocuments", filter);
    }

    public List<Document> getJuridicalDocuments(){
        return sqlSession().selectList(NS + ".selectJuridicalDocuments");
    }

    public List<Document> getPhysicalDocuments(){
        return sqlSession().selectList(NS + ".selectPhysicalDocuments");
    }
}
