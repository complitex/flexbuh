package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.dictionary.DictionaryType;
import org.complitex.flexbuh.common.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 17.08.11 12:40
 */
@Stateless
public class DictionaryTypeBean extends AbstractBean {
	public static final String NS = DictionaryTypeBean.class.getName();

	@SuppressWarnings("unchecked")
	public List<DictionaryType> getDictionaryTypes() {
		return sqlSession().selectList(NS + ".selectAllDictionaryTypes");
	}

	public DictionaryType getDictionaryTypesByCode(String code) {
		return (DictionaryType)sqlSession().selectOne(NS + ".selectDictionaryTypeByCode", code);
	}
}
