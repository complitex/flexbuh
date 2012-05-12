package org.complitex.flexbuh.admin.dictionary.web;

import org.complitex.flexbuh.common.entity.dictionary.DictionaryType;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 20.03.12 16:27
 */
public class DictionaryImportListener implements Serializable {
    private String criticalErrorMessage;
    private boolean criticalError = false;

    private Map<DictionaryType, DictionaryImportChildListener> childListenerMap = new ConcurrentHashMap<>();

    public DictionaryImportChildListener getChildListener(DictionaryType dictionaryType, boolean create){
        DictionaryImportChildListener childListener = childListenerMap.get(dictionaryType);

        if (create && childListener == null){
            childListener = new DictionaryImportChildListener();

            childListenerMap.put(dictionaryType, childListener);
        }

        return childListener;
    }

    public boolean isEnded() {
        boolean childProcessed = true;

        for (DictionaryImportChildListener childListener : childListenerMap.values()){
            if (childListener.getStatus() == null || DictionaryImportChildListener.Status.PROCESSING.equals(childListener.getStatus())){
                childProcessed = false;
                break;
            }
        }

        return childProcessed || criticalError;
    }

    public void criticalError(String message){
        criticalError = true;
        criticalErrorMessage = message;
    }

    public String getCriticalErrorMessage() {
        return criticalErrorMessage;
    }

    public void clear(){
        criticalError = false;
        criticalErrorMessage = null;
        childListenerMap.clear();
    }

    public DictionaryType getProcessing(){
        for (DictionaryType type : childListenerMap.keySet()){
            if (DictionaryImportChildListener.Status.PROCESSING.equals(childListenerMap.get(type).getStatus())){
                return type;
            }
        }

        return null;
    }
}
