package org.complitex.flexbuh.common.entity;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 11.07.12 18:20
 */
public abstract class HierarchicalTemporalDomainObject<HierarchicalTDOClass extends HierarchicalTemporalDomainObject> extends TemporalDomainObject {

    abstract public void setChildes(List<HierarchicalTDOClass> childes);

    abstract public void setMaster(HierarchicalTDOClass master);

    abstract public TemporalDomainObjectIterator<HierarchicalTDOClass> getChildes();

    abstract public HierarchicalTDOClass getMaster();
}
