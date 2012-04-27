package org.complitex.flexbuh.common.security;

import org.apache.wicket.request.IRequestHandler;
import org.apache.wicket.request.Url;
import org.apache.wicket.request.cycle.IRequestCycleListener;
import org.apache.wicket.request.cycle.RequestCycle;
import org.complitex.flexbuh.common.service.TemplateSession;
import org.slf4j.MDC;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.04.12 15:45
 */
public class RequestCycleListener implements IRequestCycleListener {
    @Override
    public void onBeginRequest(RequestCycle cycle) {
        MDC.put("LOGIN", TemplateSession.get().getLogin());
        MDC.put("SESSION_ID", TemplateSession.get().getSessionId() + "");
    }

    @Override
    public void onEndRequest(RequestCycle cycle) {
    }

    @Override
    public void onDetach(RequestCycle cycle) {
    }

    @Override
    public void onRequestHandlerResolved(RequestCycle cycle, IRequestHandler handler) {
    }

    @Override
    public void onRequestHandlerScheduled(RequestCycle cycle, IRequestHandler handler) {
    }

    @Override
    public IRequestHandler onException(RequestCycle cycle, Exception ex) {
        return null;
    }

    @Override
    public void onExceptionRequestHandlerResolved(RequestCycle cycle, IRequestHandler handler, Exception exception) {
    }

    @Override
    public void onRequestHandlerExecuted(RequestCycle cycle, IRequestHandler handler) {
    }

    @Override
    public void onUrlMapped(RequestCycle cycle, IRequestHandler handler, Url url) {
    }
}
