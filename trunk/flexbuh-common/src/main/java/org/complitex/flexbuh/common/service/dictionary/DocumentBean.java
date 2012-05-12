package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.Document;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.ICrudBean;
import org.complitex.flexbuh.common.util.DateUtil;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 22.08.11 12:15
 */
@Stateless
@LocalBean
public class DocumentBean extends AbstractBean implements ICrudBean<Document>{
    public static final String NS = DocumentBean.class.getName();

    @Override
    public Long getId(Document document) {
        return sqlSession().selectOne(NS + ".selectDocumentId", document);
    }

    @Override
    public void insert(Document document) {
        sqlSession().insert(NS + ".insertDocument", document);
    }

    @Override
    public void update(Document document) {
        sqlSession().update(NS + ".updateDocument", document);
    }

    @Override
    public Document load(Long id) {
        return getDocument(id);
    }

    @Override
    public void delete(Long id) {
        sqlSession().delete(NS + ".deleteDocument", id);
    }

    public Document getDocument(Long id) {
		return (Document)sqlSession().selectOne(NS + ".selectDocument", id);
	}

	public List<Document> getDocuments(FilterWrapper<Document> filter) {
		return sqlSession().selectList(NS + ".selectDocuments", filter);
	}

    public Integer getDocumentsCount(FilterWrapper<Document> filter){
        return (Integer)sqlSession().selectOne(NS + ".selectDocumentsCount", filter);
    }

    public List<Document> getLinkedDocuments(String cDoc, String cDocSub, Integer periodYear, Integer periodMonth){
        FilterWrapper<Document> filter = new FilterWrapper<>(new Document());

        filter.getObject().setCDoc(cDoc);
        filter.getObject().setCDocSub(cDocSub);

        filter.add("periodDate", DateUtil.getFirstDayOfMonth(periodYear, periodMonth-1));

        return sqlSession().selectList(NS + ".selectLinkedDocuments", filter);
    }

    public List<Document> getJuridicalDocuments(){
        return sqlSession().selectList(NS + ".selectJuridicalDocuments");
    }

    public List<Document> getPhysicalDocuments(){
        return sqlSession().selectList(NS + ".selectPhysicalDocuments");
    }
}
