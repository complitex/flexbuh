package org.complitex.flexbuh.personnel.service;

import com.google.common.collect.Lists;
import org.complitex.flexbuh.common.entity.HierarchicalTemporalDomainObject;
import org.complitex.flexbuh.common.entity.TemporalDomainObjectIterator;
import org.complitex.flexbuh.common.mybatis.Transactional;

import javax.ejb.ObjectNotFoundException;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;

/**
 * @author Pavel Sknar
 *         Date: 12.07.12 16:40
 */
public abstract class HierarchicalTemporalDomainObjectBean<T extends HierarchicalTemporalDomainObject<T>> extends TemporalDomainObjectBean<T>  {
    protected HierarchicalTemporalDomainObjectBean(String NS) {
        super(NS);
    }

    /**
     * Delete hierarchical object. Change link on child item.
     * File `*.xml` must content `delete` function.
     * @param id hierarchical object id
     */
    @Transactional
    public void delete(@NotNull Long id) throws ObjectNotFoundException {
        T object = getTDObject(id);
        if (object == null) {
            throw new ObjectNotFoundException("Can not deleted object " + id);
        }
        delete(object);
    }

    /**
     * Delete hierarchical object. Change link on child item.
     * File `*.xml` must content `delete` function.
     * @param object hierarchical object
     */
    @Transactional
    public void delete(@NotNull T object) {
        object.setDeleted(true);
        if (object.getCompletionDate() == null) {
            object.setCompletionDate(new Date());
        }
        sqlSession().update(NS + ".delete", object);
        TemporalDomainObjectIterator<T> childes = object.getChildes();
        if (childes != null) {
            while (childes.hasNext()) {
                T item = childes.next();
                item.setEntryIntoForceDate(object.getCompletionDate());
                item.setMaster(object.getMaster());
                save(item);
            }
        }
    }
}
