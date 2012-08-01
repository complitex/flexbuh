package org.complitex.flexbuh.personnel.entity;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.entity.HierarchicalTemporalDomainObject;
import org.complitex.flexbuh.common.entity.TemporalDomainObjectIterator;
import wickettree.ITreeProvider;

import java.util.Iterator;

/**
 * @author Pavel Sknar
 *         Date: 21.03.12 14:51
 */
public class HierarchicalTemporalDomainObjectProvider<T extends HierarchicalTemporalDomainObject<T>> implements ITreeProvider<T> {

    private TemporalDomainObjectIterator<T> iterator;

    public void setRoots(TemporalDomainObjectIterator<T> iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator<T> getRoots() {
        return iterator;
    }

    @Override
    public boolean hasChildren(T object) {
        return object != null && object.getChildes() != null && object.getChildes().hasNext();
    }

    @Override
    public Iterator<T> getChildren(T object) {
        return object.getChildes();
    }

    @Override
    public IModel<T> model(T object) {
        return new Model<>(object);
    }

    @Override
    public void detach() {
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
