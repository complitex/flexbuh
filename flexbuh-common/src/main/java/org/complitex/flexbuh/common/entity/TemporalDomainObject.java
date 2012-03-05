package org.complitex.flexbuh.common.entity;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 01.03.12 10:23
 */
public abstract class TemporalDomainObject extends DomainObject {

    private Long version;

    private boolean deleted;

    private Date entryIntoForceDate;

    private Date completionDate;

    @XmlTransient
    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @XmlTransient
    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @XmlTransient
    public Date getEntryIntoForceDate() {
        return entryIntoForceDate;
    }

    public void setEntryIntoForceDate(Date entryIntoForceDate) {
        this.entryIntoForceDate = entryIntoForceDate;
    }

    @XmlTransient
    public Date getCompletionDate() {
        return completionDate;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TemporalDomainObject that = (TemporalDomainObject) o;

        if (deleted != that.deleted) return false;
        if (completionDate != null ? !completionDate.equals(that.completionDate) : that.completionDate != null)
            return false;
        if (entryIntoForceDate != null ? !entryIntoForceDate.equals(that.entryIntoForceDate) : that.entryIntoForceDate != null)
            return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (deleted ? 1 : 0);
        result = 31 * result + (entryIntoForceDate != null ? entryIntoForceDate.hashCode() : 0);
        result = 31 * result + (completionDate != null ? completionDate.hashCode() : 0);
        return result;
    }
}
