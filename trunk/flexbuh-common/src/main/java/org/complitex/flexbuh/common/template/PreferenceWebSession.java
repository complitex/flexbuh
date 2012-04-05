package org.complitex.flexbuh.common.template;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import org.apache.wicket.request.Request;
import org.complitex.flexbuh.common.entity.Preference;
import org.complitex.flexbuh.common.security.CookieWebSession;
import org.complitex.flexbuh.common.service.PreferenceBean;
import org.complitex.flexbuh.common.util.EjbUtil;

import java.util.Map;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 05.04.12 16:26
 */
public class PreferenceWebSession extends CookieWebSession{
    private Map<String, Preference> preferenceMap;

    public PreferenceWebSession(Request request, String login) {
        super(request, login);

        //load preferences
        preferenceMap = Maps.uniqueIndex(EjbUtil.getBean(PreferenceBean.class).getPreferences(getSessionId()),
                new Function<Preference, String>() {
                    @Override
                    public String apply(Preference preference) {
                        return preference.getKey();
                    }
                });
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
            preference.setValue(value);

            preferenceMap.put(key, preference);
        }

        EjbUtil.getBean(PreferenceBean.class).save(preference);
    }
}
