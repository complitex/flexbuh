package org.complitex.flexbuh.common.entity.dictionary;

import org.apache.commons.lang.builder.ToStringBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 08.08.11 15:21
 */
@XmlType
@XmlAccessorType(value = XmlAccessType.FIELD)
public abstract class AbstractPeriodDictionary extends AbstractDictionary {
    public static final class DateAdapter extends XmlAdapter<String, Date> {
        private final static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("ddMMyyyy");

        @Override
        public Date unmarshal(String v) throws Exception {
            return DATE_FORMAT.parse(v);
        }

        @Override
        public String marshal(Date v) throws Exception {
            return DATE_FORMAT.format(v);
        }
    }

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(name = "D_BEGIN")
    private Date beginDate;

    @XmlJavaTypeAdapter(DateAdapter.class)
    @XmlElement(name = "D_END")
    private Date endDate;

    public Date getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(Date beginDate) {
        this.beginDate = beginDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean validate() {
        return super.validate() && (beginDate == null || endDate == null || beginDate.before(endDate));
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
