package org.complitex.flexbuh.service.dictionary;

import org.complitex.flexbuh.entity.AbstractFilter;
import org.complitex.flexbuh.entity.dictionary.AreaName;
import org.complitex.flexbuh.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.entity.dictionary.TaxInspectionName;
import org.complitex.flexbuh.service.AbstractBean;

import javax.ejb.Stateless;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 28.08.11 15:55
 */
@Stateless
public class TaxInspectionBean extends AbstractBean {
    public static final String NS = TaxInspectionBean.class.getName();

    public void save(TaxInspection taxInspection) {
        sqlSession().insert(NS + ".insertTaxInspection", taxInspection);

		for (TaxInspectionName taxInspectionName : taxInspection.getNames()) {
            taxInspectionName.setTaxInspectionId(taxInspection.getId());

			sqlSession().insert(NS + ".insertTaxInspectionName", taxInspectionName);
		}

		for (AreaName areaName : taxInspection.getAreaNames()) {
            areaName.setTaxInspectionId(taxInspection.getId());

			sqlSession().insert(NS + ".insertAreaName", areaName);
		}
    }

    public TaxInspection getTaxInspection(Long id) {
		return (TaxInspection)sqlSession().selectOne(NS + ".selectTaxInspection", id);
	}

	@SuppressWarnings("unchecked")
	public TaxInspection getTaxInspectionByCode(Integer code) {
		List<TaxInspection> resultCollection = sqlSession().selectList(NS + ".selectTaxInspectionByCode", code);
		return resultCollection.isEmpty()? null: resultCollection.get(0);
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
