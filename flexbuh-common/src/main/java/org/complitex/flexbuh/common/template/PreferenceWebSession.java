package org.complitex.flexbuh.common.template;

import org.apache.wicket.request.Request;
import org.complitex.flexbuh.common.entity.Preference;
import org.complitex.flexbuh.common.security.CookieWebSession;
import org.complitex.flexbuh.common.service.PreferenceBean;
import org.complitex.flexbuh.common.util.EjbUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.04.12 16:26
 */
public class PreferenceWebSession extends CookieWebSession{
    private Map<String, Preference> preferenceMap = new HashMap<>();

    public PreferenceWebSession(Request request, String login) {
        super(request, login);

        //load preferences
        List<Preference> preferences = EjbUtil.getBean(PreferenceBean.class).getPreferences(getSessionId());

        for (Preference preference : preferences){
            preferenceMap.put(preference.getKey(), preference);
        }
    }

    public String getPreferenceString(String key){
        Preference preference = preferenceMap.get(key);

        return preference != null ? preference.getValue() : null;
    }

    public Long getPreferenceLong(String key){
        try {
            return Long.valueOf(getPreferenceString(key));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public void putPreference(String key, String value){
        Preference preference = preferenceMap.get(key);

        if (preference == null){
            preference = new Preference(getSessionId(), key);

            preferenceMap.put(key, preference);
        }

        preference.setValue(value);

        EjbUtil.getBean(PreferenceBean.class).save(preference);
    }

    public void removePreference(String key){
        Preference preference = preferenceMap.remove(key);

        if (preference != null){
            EjbUtil.getBean(PreferenceBean.class).delete(preference.getId());
        }
    }
}
