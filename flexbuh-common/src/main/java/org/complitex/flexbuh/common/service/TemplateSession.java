package org.complitex.flexbuh.common.service;

import org.apache.wicket.Session;
import org.complitex.flexbuh.common.template.PreferenceWebSession;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 06.04.12 13:11
 */
public class TemplateSession {
    public static PreferenceWebSession get(){
        return (PreferenceWebSession) Session.get();
    }

    //Session Id

    public static Long getSessionId(){
        return get().getSessionId();
    }

    //Preference

    public static String getPreferenceString(String key){
        return get().getPreferenceString(key);
    }

    public static  Long getPreferenceLong(String key){
        return get().getPreferenceLong(key);
    }

    public static  void putPreference(String key, String value){
        get().putPreference(key, value);
    }

    public static  void removePreference(String key){
        get().removePreference(key);
    }
}
