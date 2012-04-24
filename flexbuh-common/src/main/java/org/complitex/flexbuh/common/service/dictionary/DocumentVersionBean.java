package org.complitex.flexbuh.common.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.DocumentVersion;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.ICrudBean;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 27.08.11 17:46
 */
@Stateless
@LocalBean
public class DocumentVersionBean extends AbstractBean implements ICrudBean<DocumentVersion> {
    public static final String NS = DocumentVersionBean.class.getName();

    public Long getId(DocumentVersion documentVersion){
        return sqlSession().selectOne(NS + ".selectDocumentVersionId", documentVersion);
    }

    @Override
    public void save(DocumentVersion documentVersion) {
        if (documentVersion.getId() == null){
            sqlSession().insert(NS + ".insertDocumentVersion", documentVersion);
        }else {
            sqlSession().update(NS + ".updateDocumentVersion", documentVersion);
        }
    }

    @Override
    public DocumentVersion load(Long id) {
        return getDocumentVersion(id);
    }

    @Override
    public void delete(Long id) {
        sqlSession().delete(NS + ".deleteDocumentVersion", id);
    }

    public DocumentVersion getDocumentVersion(Long id) {
        return (DocumentVersion) sqlSession().selectOne(NS + ".selectDocumentVersion", id);
    }

    public List<DocumentVersion> getDocumentVersionsByDocument(String cDoc, String cDocSub) {
        Map<String, String> params = Maps.newHashMap();
        params.put("cDoc", cDoc);
        params.put("cDocSub", cDocSub);
        return sqlSession().selectList(NS + ".selectDocumentVersionsByDocument");
    }

    public DocumentVersion getDocumentVersion(String cDoc, String cDocSub, Integer cDocVer){
        return sqlSession().selectOne(NS + ".findDocumentVersion", new DocumentVersion(cDoc, cDocSub, cDocVer));

    }

    public List<DocumentVersion> getDocumentVersions(FilterWrapper<DocumentVersion> filter) {
        return sqlSession().selectList(NS + ".selectDocumentVersions", filter);
    }

    public Integer getDocumentVersionsCount(FilterWrapper<DocumentVersion> filter){
        return (Integer) sqlSession().selectOne(NS + ".selectDocumentVersionsCount", filter);
    }
}
