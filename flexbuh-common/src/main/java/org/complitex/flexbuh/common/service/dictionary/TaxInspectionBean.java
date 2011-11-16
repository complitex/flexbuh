package org.complitex.flexbuh.common.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.common.entity.AbstractFilter;
import org.complitex.flexbuh.common.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.common.service.AbstractBean;

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
	public List<TaxInspection> getTaxInspectionByCode(Integer cSti) {
		return sqlSession().selectList(NS + ".selectTaxInspectionByCode", cSti);
	}

	@SuppressWarnings("unchecked")
	public List<TaxInspection> getTaxInspectionByDistrict(Integer cSti, Integer cRaj) {
		Map<String, Integer> params = Maps.newHashMap();
		params.put("cSti", cSti);
		params.put("cRaj", cRaj);
		return sqlSession().selectList(NS + ".selectTaxInspectionsByDistrict", params);
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

    @SuppressWarnings("unchecked")
    public List<String> getTaxInspectionDistrictNames(){
        return sqlSession().selectList(NS + ".selectTaxInspectionDistrictNames");
    }

    @SuppressWarnings("unchecked")
    public List<TaxInspection> getTaxInspectionsByDistrictName(String districtName){
        return sqlSession().selectList(NS + ".selectTaxInspectionsByDistrictName", districtName);
    }
}
