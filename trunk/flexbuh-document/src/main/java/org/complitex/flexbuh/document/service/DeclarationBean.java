package org.complitex.flexbuh.document.service;

import org.complitex.flexbuh.common.service.AbstractBean;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.document.entity.*;

import javax.ejb.Stateless;
import java.util.HashMap;
import java.util.List;

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
        if (declaration.getLinkedDeclarations() != null){
            for (LinkedDeclaration linkedDeclaration : declaration.getLinkedDeclarations()){
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
    public List<Period> getPeriods(final Long sessionId, final int year){
        return sqlSession().selectList(NS + ".selectDeclarationPeriods", new HashMap<String, Object>(){{
            put("sessionId", sessionId);
            put("year", year);
        }});
    }

    public Declaration getPossibleDeclarationParent(Long childId){
        return (Declaration) sqlSession().selectOne(NS + ".selectPossibleDeclarationParent", childId);
    }
}
