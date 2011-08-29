package org.complitex.flexbuh.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.Stub;
import org.complitex.flexbuh.entity.dictionary.AreaName;
import org.complitex.flexbuh.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.entity.dictionary.TaxInspectionName;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 28.08.11 15:55
 */
@Stateless
public class TaxInspectionBean extends DictionaryBean<TaxInspection> {
    public static final String NS = TaxInspectionBean.class.getName();

	@Override
    public void create(TaxInspection taxInspection) {
        sqlSession().insert(NS + ".create", taxInspection);

		Map<String, Object> params = Maps.newHashMap();
		params.put("taxInspectionId", taxInspection.getId());
		for (TaxInspectionName taxInspectionName : taxInspection.getNames()) {
			params.put("languageId", taxInspectionName.getLanguage().getId());
			params.put("val", taxInspectionName.getValue());
			sqlSession().insert(TaxInspectionName.class.getName() + ".create", params);
		}

		params = Maps.newHashMap();
		params.put("taxInspectionId", taxInspection.getId());
		for (AreaName areaName : taxInspection.getAreaNames()) {
			params.put("languageId", areaName.getLanguage().getId());
			params.put("val", areaName.getValue());
			sqlSession().insert(AreaName.class.getName() + ".create", params);
		}
    }

	@SuppressWarnings("unchecked")
	public List<TaxInspection> readAll() {
		Map<String, Object> params = Maps.newHashMap();
		params.put("table", getTable());
		return (List<TaxInspection>)sqlSession().selectList(NS + ".readAll", params);
	}

	public TaxInspection read(long id) {
		return (TaxInspection)sqlSession().selectOne(NS + ".findById", new Stub(id, getTable()));
	}

	@Override
	protected String getTable() {
		return TaxInspection.TABLE;
	}
}
