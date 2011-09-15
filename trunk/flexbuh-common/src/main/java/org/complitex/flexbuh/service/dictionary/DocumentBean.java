package org.complitex.flexbuh.service.dictionary;

import org.complitex.flexbuh.entity.AbstractFilter;
import org.complitex.flexbuh.entity.dictionary.Document;
import org.complitex.flexbuh.entity.dictionary.DocumentName;
import org.complitex.flexbuh.service.AbstractBean;

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

		for (DocumentName documentName : document.getNames()) {
            documentName.setDocumentId(document.getId());

			sqlSession().insert(NS + ".insertDocumentName", documentName);
		}
    }

    public Document getDocument(Long id) {
		return (Document)sqlSession().selectOne(NS + ".selectDocument", id);
	}

	@SuppressWarnings("unchecked")
	public List<Document> getDocuments() {
		return (List<Document>)sqlSession().selectList(NS + ".selectAllDocuments");
	}

    public Integer getDocumentsCount(){
        return (Integer)sqlSession().selectOne(NS + ".selectAllDocumentsCount");
    }

	@SuppressWarnings("unchecked")
	public List<Document> getDocuments(int first, int count) {
		return sqlSession().selectList(NS + ".selectDocuments", new AbstractFilter(first, count));
	}

    @SuppressWarnings("unchecked")
    public List<Document> getLinkedDocuments(Document parent){
        return sqlSession().selectList(NS + ".selectLinkedDocuments", parent);
    }
}
