package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.dictionary.Document;
import org.complitex.flexbuh.common.entity.dictionary.DocumentFilter;
import org.complitex.flexbuh.common.service.AbstractBean;

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

	@SuppressWarnings("unchecked")
	public List<Document> getDocuments(DocumentFilter filter) {
		return (List<Document>)sqlSession().selectList(NS + ".selectDocuments", filter);
	}

    public Integer getDocumentsCount(DocumentFilter filter){
        return (Integer)sqlSession().selectOne(NS + ".selectDocumentsCount", filter);
    }

    @SuppressWarnings("unchecked")
    public List<Document> getLinkedDocuments(String cDoc, String cDocSub){
        return sqlSession().selectList(NS + ".selectLinkedDocuments", new Document(cDoc, cDocSub));
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> getJuridicalDocuments(){
        return sqlSession().selectList(NS + ".selectJuridicalDocuments");
    }

    @SuppressWarnings({"unchecked"})
    public List<Document> getPhysicalDocuments(){
        return sqlSession().selectList(NS + ".selectPhysicalDocuments");
    }
}
