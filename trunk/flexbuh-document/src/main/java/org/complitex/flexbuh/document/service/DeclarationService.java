package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.entity.DeclarationHead;
import org.complitex.flexbuh.document.entity.PersonProfile;
import org.complitex.flexbuh.service.dictionary.TaxInspectionBean;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import static org.complitex.flexbuh.document.entity.PersonType.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 18.10.11 18:06
 */
@Stateless
public class DeclarationService {
    @EJB
    private TaxInspectionBean taxInspectionBean;

    public void autoFillHeader(Declaration declaration){
        PersonProfile personProfile = declaration.getPersonProfile();

        // if (docType.substr(1, 5) != "12010" && docType.substr(1, 5) != "12012" && docType.substr(0, 6) != "R12015")
        switch (personProfile.getPersonType()) {
            case JURIDICAL_PERSON:
                declaration.fillValue("DGHTINJ", personProfile.getTin());
                break;
            case PHYSICAL_PERSON:
                declaration.fillValue("DGHTINF", personProfile.getTin());
                break;
            case JOINT_AGREEMENT:
                declaration.fillValue("DGHTINSD", personProfile.getTin());
                break;
            case REPRESENTATIVE:
                declaration.fillValue("DGHTINNR", personProfile.getTin());
                break;
            case PROPERTY_AGREEMENT:
                declaration.fillValue("DGHTINUM", personProfile.getTin());
                break;
        }

        declaration.fillValue("DGHNAME", personProfile.getName());

        if (personProfile.getCSti() != null){
            int cRaj = personProfile.getCSti() % 100;
            int cReg = personProfile.getCSti() / 100; //todo Math.floor

//            TaxInspection taxInspection = taxInspectionBean.getTaxInspectionByDistrict()

        }

        //todo DGHSTI
        //todo DGYear
        //todo DGKv
        //todo DGMonth

        declaration.fillValue("DGHLOC", personProfile.getAddress());
        declaration.fillValue("DGHZIP", personProfile.getZipCode());
        declaration.fillValue("DGHTEL", personProfile.getPhone());
        declaration.fillValue("DGHFAX", personProfile.getFax());
        declaration.fillValue("DGkved", personProfile.getKved());

        declaration.fillValue("DGHBUH", personProfile.getBFio());
        declaration.fillValue("DGHBOS", personProfile.getDFio());

        if (PHYSICAL_PERSON.equals(personProfile.getPersonType())) {
            declaration.fillValue("DGHFO", personProfile.getName());
        }

        declaration.fillValue("DGHEMAIL", personProfile.getEmail());

        //todo DGDate
        //todo DGHNPDV

        declaration.fillValue("DGHNSPDV", personProfile.getNumPdvSvd());

        if (JOINT_AGREEMENT.equals(personProfile.getPersonType())){
//            declaration.fillValue("DGHDDGVSD", personProfile.getContractDate()); //todo date format
            declaration.fillValue("DGHNDGVSD", personProfile.getContractNumber());
        }

        if (PROPERTY_AGREEMENT.equals(personProfile.getPersonType())){
//            declaration.fillValue("DGHDDGVUM", personProfile.getContractDate().toString());  //todo date format
            declaration.fillValue("DGHNDGVUM", personProfile.getContractNumber());
        }

        autoFillNakladnaHeader("SEL", declaration);
        autoFillNakladnaHeader("BUY", declaration);

        //else

        declaration.fillValue("E_MAIL", personProfile.getEmail());
        declaration.fillValue("HKSEL", personProfile.getIpn());
        //todo C_DOC_CNT

        declaration.fillValue("TIN", personProfile.getTin());
        //todo c_sti

        //AutoFillMainParameters
        DeclarationHead head = declaration.getHead();

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

        //todo pType
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

}
