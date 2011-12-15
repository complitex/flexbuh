package org.complitex.flexbuh.document.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 15.12.11 14:33
 */
@XmlRootElement(name = "rowset")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CounterpartRowSet {
    @XmlElement(name = "row")
    private List<Counterpart> counterparts;

    public List<Counterpart> getCounterparts() {
        return counterparts;
    }

    public void setCounterparts(List<Counterpart> counterparts) {
        this.counterparts = counterparts;
    }
}
