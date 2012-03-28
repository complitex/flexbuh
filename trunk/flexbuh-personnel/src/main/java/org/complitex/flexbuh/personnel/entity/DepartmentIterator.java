package org.complitex.flexbuh.personnel.entity;

import org.complitex.flexbuh.personnel.entity.Department;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Pavel Sknar
 *         Date: 23.03.12 13:44
 */
public class DepartmentIterator implements Iterator<Department>, Serializable {

    private List<Department> departments;
    private int i = 0;

    public DepartmentIterator(List<Department> departments) {
        this.departments = departments;
    }

    @Override
    public boolean hasNext() {
        return i < departments.size();
    }

    @Override
    public Department next() {
        if (i >= departments.size())
                throw new NoSuchElementException();

        return departments.get(i++);
    }

    @Override
    public void remove() {

    }
}
