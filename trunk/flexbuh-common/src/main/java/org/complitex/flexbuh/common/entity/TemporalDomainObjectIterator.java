package org.complitex.flexbuh.common.entity;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Pavel Sknar
 *         Date: 23.03.12 13:44
 */
public class TemporalDomainObjectIterator<T extends TemporalDomainObject> implements Iterator<T>, Serializable {

    private List<T> collection;
    private int i = 0;

    public TemporalDomainObjectIterator(List<T> collection) {
        this.collection = collection == null? Collections.<T>emptyList(): collection;
    }

    public TemporalDomainObjectIterator(T item) {
        if (item == null) {
            collection = Collections.emptyList();
        } else {
            collection = Lists.newArrayList();
            collection.add(item);
        }
    }

    @Override
    public boolean hasNext() {
        return i < collection.size();
    }

    @Override
    public T next() {
        if (i >= collection.size())
                throw new NoSuchElementException();

        return collection.get(i++);
    }

    @Override
    public void remove() {

    }
}
