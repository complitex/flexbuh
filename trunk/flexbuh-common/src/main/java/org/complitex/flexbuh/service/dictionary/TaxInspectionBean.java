package org.complitex.flexbuh.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.entity.AbstractFilter;
import org.complitex.flexbuh.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 28.08.11 15:55
 */
@Stateless
public class TaxInspectionBean extends AbstractBean {
    public static final String NS = TaxInspectionBean.class.getName();

    public void save(TaxInspection taxInspection) {
        sqlSession().insert(NS + ".insertTaxInspection", taxInspection);
    }

	public void update(TaxInspection taxInspection) {
        sqlSession().update(NS + ".updateTaxInspection", taxInspection);
    }

    public TaxInspection getTaxInspection(Long id) {
		return (TaxInspection)sqlSession().selectOne(NS + ".selectTaxInspection", id);
	}

	@SuppressWarnings("unchecked")
	public List<TaxInspection> getTaxInspectionByCode(Integer code) {
		return sqlSession().selectList(NS + ".selectTaxInspectionByCode", code);
	}

	@SuppressWarnings("unchecked")
	public List<TaxInspection> getTaxInspectionByCodeAndCodeArea(Integer code, Integer codeArea) {
		Map<String, Integer> params = Maps.newHashMap();
		params.put("code", code);
		params.put("codeArea", codeArea);
		return sqlSession().selectList(NS + ".selectTaxInspectionByCode", params);
	}

	@SuppressWarnings("unchecked")
	public List<TaxInspection> getTaxInspections() {
		return (List<TaxInspection>)sqlSession().selectList(NS + ".selectAllTaxInspections");
	}

	@SuppressWarnings("unchecked")
	public List<TaxInspection> getTaxInspectionsUniqueCodeWithName() {
		return (List<TaxInspection>)sqlSession().selectList(NS + ".selectAllTaxInspectionsCodeWithName");
	}

    public Integer getTaxInspectionsCount(){
        return (Integer) sqlSession().selectOne(NS + ".selectAllTaxInspectionsCount");
    }

	@SuppressWarnings("unchecked")
	public List<TaxInspection> getTaxInspections(int first, int count) {
		return sqlSession().selectList(NS + ".selectTaxInspections", new AbstractFilter(first, count));
	}
}
