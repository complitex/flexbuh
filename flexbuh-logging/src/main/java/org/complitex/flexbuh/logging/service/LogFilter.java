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
	private String caller_class;
	private String formatted_message;
	private Log.LEVEL level_string;

	// event properties
	private String login;
	private String module;
	private String model;
	private String objectId;
	private EventCategory category;

	public int getCountSignificantProperties() {
		int count = 0;

		if (login != null) {
			count++;
		}
		if (module != null) {
			count++;
		}
		if (model != null) {
			count++;
		}
		if (category != null) {
			count++;
		}
		if (objectId != null) {
			count++;
		}
		return count;
	}

	public Date getTimestmp() {
		return timestmp;
	}

	public void setTimestmp(Date timestmp) {
		this.timestmp = timestmp;
	}

	public Long getAfterTime() {
		return timestmp != null? DateUtils.truncate(timestmp, Calendar.DATE).getTime(): null;
	}

	public Long getBeforeTime() {
		return timestmp != null? DateUtil.getEndOfDay(timestmp).getTime(): null;
	}

	public String getCaller_class() {
		return caller_class;
	}

	public void setCaller_class(String caller_class) {
		this.caller_class = caller_class;
	}

	public String getFormatted_message() {
		return formatted_message;
	}

	public void setFormatted_message(String formatted_message) {
		this.formatted_message = formatted_message;
	}

	public Log.LEVEL getLevel_string() {
		return level_string;
	}

	public void setLevel_string(Log.LEVEL level_string) {
		this.level_string = level_string;
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

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public EventCategory getCategory() {
		return category;
	}

	public void setCategory(EventCategory category) {
		this.category = category;
	}
}
