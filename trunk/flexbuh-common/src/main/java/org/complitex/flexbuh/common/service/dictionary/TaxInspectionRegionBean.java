package org.complitex.flexbuh.common.service.dictionary;

import org.complitex.flexbuh.common.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.common.entity.dictionary.TaxInspectionRegion;
import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.service.ICrudBean;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.04.12 14:52
 */
@Stateless
@LocalBean
public class TaxInspectionRegionBean extends AbstractBean implements ICrudBean<TaxInspectionRegion>{
    @EJB
    private TaxInspectionBean taxInspectionBean;

    @Override
    public Long getId(TaxInspectionRegion o) {
        return null;
    }

    @Override
    public void insert(TaxInspectionRegion taxInspectionRegion) {
        for (TaxInspection taxInspection : taxInspectionRegion.getTaxInspections()){
            taxInspection.setCReg(taxInspectionRegion.getCReg());
            taxInspection.setId(taxInspectionBean.getId(taxInspection));

            if (taxInspection.getId() == null) {
                taxInspectionBean.insert(taxInspection);
            }else {
                taxInspectionBean.update(taxInspection);
            }
        }
    }

    @Override
    public void update(TaxInspectionRegion taxInspectionRegion) {
        //insert only
    }

    @Override
    public TaxInspectionRegion load(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {
    }
}
