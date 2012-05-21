package org.complitex.flexbuh.logging.service;

import org.apache.commons.lang.time.DateUtils;
import org.complitex.flexbuh.common.entity.AbstractFilter;
import org.complitex.flexbuh.common.logging.EventCategory;
import org.complitex.flexbuh.common.util.DateUtil;
import org.complitex.flexbuh.logging.entity.Log;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Pavel Sknar
 *         Date: 08.11.11 17:20
 */
public class LogFilter extends AbstractFilter {
	// event attributes
	private Date timestmp;
	private String loggerName;
	private String formattedMessage;
	private Log.LEVEL level;

	// event properties
	private String login;
	private String module;
	private String modelClass;
	private String objectId;
	private EventCategory eventCategory;


	public Long getAfterTime() {
		return timestmp != null? DateUtils.truncate(timestmp, Calendar.DATE).getTime(): null;
	}

	public Long getBeforeTime() {
		return timestmp != null? DateUtil.getEndOfDay(timestmp).getTime(): null;
	}

    public Date getTimestmp() {
        return timestmp;
    }

    public void setTimestmp(Date timestmp) {
        this.timestmp = timestmp;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public void setLoggerName(String loggerName) {
        this.loggerName = loggerName;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public void setFormattedMessage(String formattedMessage) {
        this.formattedMessage = formattedMessage;
    }

    public Log.LEVEL getLevel() {
        return level;
    }

    public void setLevel(Log.LEVEL level) {
        this.level = level;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getModelClass() {
        return modelClass;
    }

    public void setModelClass(String modelClass) {
        this.modelClass = modelClass;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public EventCategory getEventCategory() {
        return eventCategory;
    }

    public void setEventCategory(EventCategory eventCategory) {
        this.eventCategory = eventCategory;
    }
}
