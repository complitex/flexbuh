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
    public final static String FILE_NAME = "spr_contragents.xml";

    @XmlElement(name = "header")
    private RowSetHeader header;

    @XmlElement(name = "row")
    private List<Counterpart> counterparts;

    public CounterpartRowSet() {
        header = new RowSetHeader("spr_contragents", "Довідник контрагентів");

        header.addColumn("HK", "Індивідуальний податковий номер", "right", "yes");
        header.addColumn("HNAME", "Назва (Прізвище, ім'я, по-батькові)", "left", "yes");
        header.addColumn("HLOC", "Місцезнаходження", "left");
        header.addColumn("HTEL", "Номер телефона", "left");
        header.addColumn("HNSPDV", "Номер свідоцтва ПДВ", "left");
    }

    public CounterpartRowSet(List<Counterpart> counterparts, boolean clearLocalId) {
        this();

        this.counterparts = counterparts;

        for (int i = 0, size = counterparts.size(); i < size; i++){
            Counterpart counterpart = counterparts.get(i);

            counterpart.setNum(i + 1);

            if (clearLocalId){
                counterpart.setId(null);
                counterpart.setPersonProfileId(null);
            }
        }
    }

    public CounterpartRowSet(List<Counterpart> counterparts) {
        this(counterparts, false);
    }

    public RowSetHeader getHeader() {
        return header;
    }

    public void setHeader(RowSetHeader header) {
        this.header = header;
    }

    public List<Counterpart> getCounterparts() {
        return counterparts;
    }

    public void setCounterparts(List<Counterpart> counterparts) {
        this.counterparts = counterparts;
    }
}
