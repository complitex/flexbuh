package org.complitex.flexbuh.document.web;

import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.authorization.UnauthorizedInstantiationException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.entity.user.Share;
import org.complitex.flexbuh.common.logging.Event;
import org.complitex.flexbuh.common.service.FIOBean;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.user.ShareBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.service.CounterpartBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;

import static org.complitex.flexbuh.common.entity.PersonProfile.SELECTED_PERSON_PROFILE_ID;
import static org.complitex.flexbuh.common.logging.EventCategory.CREATE;
import static org.complitex.flexbuh.common.logging.EventCategory.EDIT;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 16:03
 */
public class CounterpartEdit extends FormTemplatePage{
    private final static Logger log = LoggerFactory.getLogger(CounterpartEdit.class);

    @EJB    
    private CounterpartBean counterpartBean;
    
    @EJB
    private PersonProfileBean personProfileBean;

    @EJB
    private FIOBean fioBean;

    @EJB
    private ShareBean shareBean;
    
    public CounterpartEdit() {
        init(null);
    }

    public CounterpartEdit(PageParameters pageParameters) {
        init(pageParameters.get("id").toLongObject());
    }
    
    private void init(Long id){
        final boolean edit = id != null;
        final Counterpart oldCounterpart = edit ? counterpartBean.getCounterpart(id) : null;
        final Counterpart counterpart = edit ? counterpartBean.getCounterpart(id) : new Counterpart(getSessionId());

        //Профиль
        PersonProfile personProfile = personProfileBean.getPersonProfile(getPreferenceLong(SELECTED_PERSON_PROFILE_ID));

        if (id == null && personProfile != null){
            counterpart.setPersonProfileId(personProfile.getId());
            counterpart.setSessionId(personProfile.getSessionId());
        }
        
        if (counterpart == null){
            log.error("Контрагент по id={} не найден", id);

            throw new WicketRuntimeException("Кажись нет записи по такому id в базе");
        }else if (!counterpart.getSessionId().equals(getSessionId())
                && !shareBean.isExist(new Share(counterpart.getSessionId(), getSessionId()))){
            log.error("Доступ запрещен", new Event(EDIT, counterpart));

            throw new UnauthorizedInstantiationException(CounterpartEdit.class);
        }
        
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));
        
        final Form<Counterpart> form = new Form<>("form", new CompoundPropertyModel<>(counterpart));
        add(form);
        
        form.add(new TextField("hk"));
        form.add(new TextField("hname").setRequired(true));
        form.add(new TextField("hloc"));
        form.add(new TextField("htel"));
        form.add(new TextField("hnspdv"));
        
        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                counterpartBean.save(counterpart);

                log.info("Контрагент {}", edit? "изменен":"добавлен", new Event(edit? EDIT:CREATE, oldCounterpart, counterpart));
                info(getStringFormat("info_saved", counterpart.getHname()));

                setResponsePage(CounterpartList.class);
            }
        });
        
        form.add(new Button("cancel"){
            @Override
            public void onSubmit() {
                setResponsePage(CounterpartList.class);
            }
        }.setDefaultFormProcessing(false));
    }
}
