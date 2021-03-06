package org.complitex.flexbuh.common.service.dictionary;

import com.google.common.collect.Maps;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.ICrudBean;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 28.08.11 15:55
 */
@Stateless
@LocalBean
public class TaxInspectionBean extends AbstractBean implements ICrudBean<TaxInspection>{
    public static final String NS = TaxInspectionBean.class.getName();

    @Override
    public Long getId(TaxInspection taxInspection) {
        return sqlSession().selectOne(NS + ".selectTaxInspectionId", taxInspection);
    }

    @Override
    public void insert(TaxInspection taxInspection) {
        sqlSession().insert(NS + ".insertTaxInspection", taxInspection);
    }

    @Override
    public void update(TaxInspection taxInspection) {
        sqlSession().update(NS + ".updateTaxInspection", taxInspection);
    }

    @Override
    public TaxInspection load(Long id) {
        return getTaxInspection(id);
    }

    @Override
    public void delete(Long id) {
        sqlSession().delete(NS + ".deleteTaxInspection", id);
    }

    public TaxInspection getTaxInspection(Long id) {
        return (TaxInspection)sqlSession().selectOne(NS + ".selectTaxInspection", id);
    }

    public List<TaxInspection> getTaxInspectionByCode(Integer cSti) {
        return sqlSession().selectList(NS + ".selectTaxInspectionByCode", cSti);
    }

    public List<TaxInspection> getTaxInspectionByDistrict(Integer cSti, Integer cRaj) {
        Map<String, Integer> params = Maps.newHashMap();
        params.put("cSti", cSti);
        params.put("cRaj", cRaj);
        return sqlSession().selectList(NS + ".selectTaxInspectionsByDistrict", params);
    }

    public List<TaxInspection> getTaxInspections() {
        return sqlSession().selectList(NS + ".selectAllTaxInspections");
    }

    public List<TaxInspection> getTaxInspectionsUniqueCodeWithName() {
        return sqlSession().selectList(NS + ".selectAllTaxInspectionsCodeWithName");
    }

    public List<TaxInspection> getTaxInspections(FilterWrapper<TaxInspection> filter) {
        return sqlSession().selectList(NS + ".selectTaxInspections", filter);
    }

    public Integer getTaxInspectionsCount(FilterWrapper<TaxInspection> filter){
        return (Integer) sqlSession().selectOne(NS + ".selectTaxInspectionsCount", filter);
    }

    public List<String> getTaxInspectionDistrictNames(){
        return sqlSession().selectList(NS + ".selectTaxInspectionDistrictNames");
    }

    public List<TaxInspection> getTaxInspectionsByDistrictName(String districtName){
        return sqlSession().selectList(NS + ".selectTaxInspectionsByDistrictName", districtName);
    }
}
