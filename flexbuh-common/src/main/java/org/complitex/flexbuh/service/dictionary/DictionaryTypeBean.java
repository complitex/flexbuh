package org.complitex.flexbuh.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.dictionary.*;
import org.complitex.flexbuh.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 17.08.11 12:40
 */
@Stateless
public class DictionaryTypeBean extends AbstractBean {
	public static final String NS = DictionaryTypeBean.class.getName();

	public static final Map<String, Class<? extends Dictionary>> dictionaries = Maps.newHashMap();

	static {
		dictionaries.put(Currency.TABLE, Currency.class);
		dictionaries.put(Document.TABLE, Document.class);
		dictionaries.put(DocumentTerm.TABLE, DocumentTerm.class);
		dictionaries.put(DocumentVersion.TABLE, DocumentVersion.class);
		dictionaries.put(Region.TABLE, Region.class);
		dictionaries.put(TaxInspection.TABLE, TaxInspection.class);
	}

	public Class<? extends Dictionary> classDictionaryByType(DictionaryType dictionaryType) {
		if (!dictionaries.containsKey(dictionaryType.getCode())) {
			throw new RuntimeException("Do not support this dictionary type " +
					dictionaryType.getCode() +". Class did not find");
		}
		return dictionaries.get(dictionaryType.getCode());
	}

	public DictionaryType typeByClassDictionary(Class<? extends Dictionary> dictionaryClass) {

		for (Map.Entry<String, Class<? extends Dictionary>> dictionaryTypeEntry : dictionaries.entrySet()) {
			if (dictionaryClass.equals(dictionaryTypeEntry.getValue())) {
				return findByCode(dictionaryTypeEntry.getKey());
			}
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public List<DictionaryType> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", DictionaryType.TABLE);
		return (List<DictionaryType>)sqlSession().selectList(NS + ".readAll", params);
	}

	public DictionaryType findByCode(String code) {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", DictionaryType.TABLE);
		params.put("code", code);
		return (DictionaryType)sqlSession().selectOne(NS + ".findByCode", params);
	}
}
