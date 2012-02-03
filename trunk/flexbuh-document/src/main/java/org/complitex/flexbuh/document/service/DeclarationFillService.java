package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.dictionary.TaxInspection;
import org.complitex.flexbuh.common.service.dictionary.TaxInspectionBean;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationHead;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.complitex.flexbuh.common.entity.PersonType.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.10.11 18:06
 */
@Stateless
public class DeclarationFillService {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");
    
    @EJB
    private TaxInspectionBean taxInspectionBean;

    public void autoFillHeader(Declaration declaration){
        PersonProfile personProfile = declaration.getPersonProfile();

        //skip if profile is not linked
        if (personProfile == null){
            return;
        }

        DeclarationHead head = declaration.getHead();
        
        TaxInspection taxInspection = taxInspectionBean.getTaxInspection(personProfile.getTaxInspectionId());

        declaration.fillValueByType("DGHTINJ", personProfile.getTin());
        declaration.fillValueByType("DGHTINF", personProfile.getTin());
        declaration.fillValueByType("DGHTINSD", personProfile.getTin());
        declaration.fillValueByType("DGHTINNR", personProfile.getTin());
        declaration.fillValueByType("DGHTINUM", personProfile.getTin());

        declaration.fillValueByType("DGHNAME", personProfile.getName());

        if (taxInspection != null) {
            declaration.fillValueByType("DGHSTI", taxInspection.getCSti() + " " + taxInspection.getNameRajUk());
        }

        declaration.fillValueByType("DGYear", head.getPeriodYear());
        declaration.fillValueByType("DGKv", head.getPeriodMonth() / 3);
        declaration.fillValueByType("DGMonth", head.getPeriodMonth());

        declaration.fillValueByType("DGHLOC", personProfile.getAddress());
        declaration.fillValueByType("DGHZIP", personProfile.getZipCode());
        declaration.fillValueByType("DGHTEL", personProfile.getPhone());
        declaration.fillValueByType("DGHFAX", personProfile.getFax());
        declaration.fillValueByType("DGkved", personProfile.getKved());

        declaration.fillValueByType("DGHBUH", personProfile.getBFio());
        declaration.fillValueByType("DGHBOS", personProfile.getDFio());

        if (PHYSICAL_PERSON.equals(personProfile.getPersonType())) {
            declaration.fillValueByType("DGHFO", personProfile.getName());
        }

        declaration.fillValueByType("DGHEMAIL", personProfile.getEmail());

        declaration.fillValueByType("DGDate", head.getDFill());

        declaration.fillValueByType("DGHNSPDV", personProfile.getNumPdvSvd());

        if (JOINT_AGREEMENT.equals(personProfile.getPersonType())){
            declaration.fillValueByType("DGHDDGVSD", getString(personProfile.getContractDate()));
            declaration.fillValueByType("DGHNDGVSD", personProfile.getContractNumber());
        }

        if (PROPERTY_AGREEMENT.equals(personProfile.getPersonType())){
            declaration.fillValueByType("DGHDDGVUM", getString(personProfile.getContractDate()));
            declaration.fillValueByType("DGHNDGVUM", personProfile.getContractNumber());
        }

        autoFillNakladnaHeader("SEL", declaration);
        autoFillNakladnaHeader("BUY", declaration);

        //else

        declaration.fillValue("E_MAIL", personProfile.getEmail());
        declaration.fillValue("HKSEL", personProfile.getIpn());
        //todo C_DOC_CNT

        declaration.fillValue("TIN", personProfile.getTin());

        if (taxInspection != null) {
            declaration.fillValue("C_REG", StringUtil.getString(taxInspection.getCReg()));
            declaration.fillValue("C_RAJ", StringUtil.getString(taxInspection.getCRaj()));
        }

        //AutoFillMainParameters
        declaration.fillValue("HZ", "1");
        declaration.fillValue("HCOPY", "1");
        declaration.fillValue("HNPDV", personProfile.getIpn());

        if (head.getPeriodType() == 2 && head.getPeriodMonth() == 3){
            declaration.fillValue("H1KV", "1");
        }
        if (head.getPeriodType() == 3){
            declaration.fillValue("HHY", "1");
        }
        if (head.getPeriodMonth() == 2 && head.getPeriodMonth() == 9){
            declaration.fillValue("H3KV", "1");
        }

        //todo HY

        if (personProfile.getPersonType().getCode() == 1){
            declaration.fillValue("HJ", "1");
        }
        if (personProfile.getPersonType().getCode() == 2){
            declaration.fillValue("HF", "1");
        }
        if (personProfile.getPersonType().getCode() != 1){
            declaration.fillValue("HKP", "1");
        }

        //todo HNPDV

        declaration.fillValue("HKBUH", personProfile.getBInn());
        declaration.fillValue("HKBOS", personProfile.getDInn());
        declaration.fillValue("HKOATUU", personProfile.getKoatuu());
        declaration.fillValue("HTINSTI", personProfile.getCStiTin());
    }
    
    private void autoFillNakladnaHeader(String type, Declaration declaration){
        PersonProfile personProfile = declaration.getPersonProfile();

        declaration.fillValue("HNAME" + type, personProfile.getName());
        declaration.fillValue("HK" + type, personProfile.getIpn());
        declaration.fillValue("HTEL" + type, personProfile.getPhone());
        declaration.fillValue("HNSPDV" + type, personProfile.getNumPdvSvd());
        declaration.fillValue("HLOC" + type, personProfile.getAddress());
    }

    private String getString(Date date){
        if (date != null){
            return DATE_FORMAT.format(date);
        }

        return null;
    }


}
