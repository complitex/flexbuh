package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.document.entity.*;

import javax.ejb.Stateless;
import java.util.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.09.11 15:07
 */
@Stateless
public class DeclarationBean extends AbstractBean{
    public final static String NS = DeclarationBean.class.getName();

    public void save(Declaration declaration){
        declaration.setDate(DateUtil.getCurrentDate());

        if (declaration.getId() != null){
            Declaration old = getDeclaration(declaration.getId());

            //remove values
            for (DeclarationValue vOld : old.getDeclarationValues()){
                boolean contain = false;

                for (DeclarationValue v : declaration.getDeclarationValues()){
                    if (vOld.getId().equals(v.getId())){
                        contain = true;
                        break;
                    }
                }

                if (!contain){
                    sqlSession().delete(NS + ".deleteDeclarationValue", vOld.getId());
                }
            }

            //save values
            for (DeclarationValue v : declaration.getDeclarationValues()){
                if (v.getId() != null){ //to improve skip update unchanged value
                    sqlSession().update("updateDeclarationValue", v);
                }else{
                    v.setDeclarationId(declaration.getId());
                    sqlSession().insert(NS + ".insertDeclarationValue", v);
                }
            }

            //update declaration
            sqlSession().update(NS + ".updateDeclaration", declaration);
        }else{
            sqlSession().insert(NS + ".insertDeclaration", declaration);

            for (DeclarationValue value : declaration.getDeclarationValues()){
                value.setDeclarationId(declaration.getId());
                sqlSession().insert(NS + ".insertDeclarationValue", value);
            }
        }

        //linked declaration
        if (declaration.getHead().getLinkedDeclarations() != null){
            for (LinkedDeclaration linkedDeclaration : declaration.getHead().getLinkedDeclarations()){
                Declaration d = linkedDeclaration.getDeclaration();
                d.setParentId(declaration.getId());
                d.setPersonProfileId(declaration.getPersonProfileId());
                d.setSessionId(declaration.getSessionId());
                d.getHead().setTin(declaration.getHead().getTin());

                save(d);
            }
        }
    }

    public Declaration getDeclaration(Long id){
        return (Declaration) sqlSession().selectOne(NS + ".selectDeclaration", id);
    }

    @SuppressWarnings({"unchecked"})
    public List<Declaration> getDeclarations(DeclarationFilter filter){
        return sqlSession().selectList(NS + ".selectDeclarations", filter);
    }

    @SuppressWarnings("unchecked")
    public List<Declaration> getAllDeclarations(Long sessionId){
        return sqlSession().selectList(NS + ".selectAllDeclarations", sessionId);
    }

    public Integer getDeclarationsCount(DeclarationFilter filter){
        return (Integer) sqlSession().selectOne(NS + ".selectDeclarationsCount", filter);
    }

    public void deleteDeclaration(Long id){
        sqlSession().delete(NS + ".deleteDeclaration", id);
    }

    public void deleteDeclarationValue(Long id){
        sqlSession().delete(NS + ".deleteDeclarationValue", id);
    }

    @SuppressWarnings("unchecked")
    public List<Declaration> getDeclarations(List<Long> ids){
        return sqlSession().selectList(NS + ".selectDeclarationsByIds", ids);
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getYears(Long sessionId){
        return sqlSession().selectList(NS + ".selectDeclarationYears", sessionId);
    }

    @SuppressWarnings("unchecked")
    public List<Period> getPeriods(final Long sessionId, final Integer year){
        return sqlSession().selectList(NS + ".selectDeclarationPeriods", new HashMap<String, Object>(){{
            put("sessionId", sessionId);
            put("year", year);
        }});
    }

    public Map<Integer, List<Period>> getPeriodMap(Long sessionId){
        Map<Integer, List<Period>> periodMap = new HashMap<>();

        List<Period> allPeriods = getPeriods(sessionId, null);

        for (Period period : allPeriods){
            List<Period> list = periodMap.get(period.getYear());

            if (list == null){
                list = new ArrayList<>();

                periodMap.put(period.getYear(), list);
            }

            list.add(period);
        }

        //Sort
        for (List<Period> periods : periodMap.values()){
            Collections.sort(periods);
        }

        return periodMap;
    }

    public Declaration getPossibleDeclarationParent(Long childId){
        return (Declaration) sqlSession().selectOne(NS + ".selectPossibleDeclarationParent", childId);
    }
}
