package org.complitex.flexbuh.common.logging;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 03.11.11 16:25
 */
public class EventCategory implements EventProperty {

	public static final EventCategory USER_LOGIN = new EventCategory("USER_LOGIN");
	public static final EventCategory USER_LOGOFF = new EventCategory("USER_LOGOFF");
	public static final EventCategory LIST = new EventCategory("LIST");
	public static final EventCategory VIEW = new EventCategory("VIEW");
	public static final EventCategory CREATE = new EventCategory("CREATE");
	public static final EventCategory EDIT = new EventCategory("EDIT");
	public static final EventCategory REMOVE = new EventCategory("REMOVE");
	public static final EventCategory IMPORT = new EventCategory("IMPORT");
	public static final EventCategory EXPORT = new EventCategory("EXPORT");
    public static final EventCategory GETTING_DATA = new EventCategory("GETTING_DATA");
	public static final EventCategory SETTING_PERMISSION = new EventCategory("SETTING_PERMISSION");

	private static final List<EventCategory> CATEGORIES = Lists.newArrayList(USER_LOGIN, USER_LOGOFF, LIST, VIEW,
			CREATE, EDIT, REMOVE, IMPORT, EXPORT, GETTING_DATA, SETTING_PERMISSION);

	private String categoryName;

	private EventCategory() {
	}

	public EventCategory(String categoryName) {
		this.categoryName = categoryName;
	}

	@Override
	public String getName() {
		return "category";
	}

	@Override
	public String getValue() {
		return categoryName;
	}

	public static List<EventCategory> getCategories() {
		return CATEGORIES;
	}
}
