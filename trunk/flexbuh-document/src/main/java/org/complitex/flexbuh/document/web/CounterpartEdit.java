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
import org.complitex.flexbuh.common.service.FIOBean;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.user.ShareBean;
import org.complitex.flexbuh.common.template.FormTemplatePage;
import org.complitex.flexbuh.common.web.component.FirstNameAutoCompleteTextField;
import org.complitex.flexbuh.common.web.component.LastNameAutoCompleteTextField;
import org.complitex.flexbuh.common.web.component.MiddleNameAutoCompleteTextField;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.service.CounterpartBean;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 16.11.11 16:03
 */
public class CounterpartEdit extends FormTemplatePage{
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
        Counterpart counterpart = id != null ? counterpartBean.getCounterpart(id) : new Counterpart(getSessionId());

        PersonProfile personProfile = personProfileBean.getPersonProfile(getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID));

        //Профиль
        if (id == null && personProfile != null){
            counterpart.setPersonProfileId(personProfile.getId());
            counterpart.setSessionId(personProfile.getSessionId());
        }
        
        if (counterpart == null){
            throw new WicketRuntimeException("Кажись нет записи по такому id в базе");
        }else if (!counterpart.getSessionId().equals(getSessionId())
                && !shareBean.isExist(new Share(counterpart.getSessionId(), getSessionId()))){
            throw new UnauthorizedInstantiationException(CounterpartEdit.class);
        }
        
        add(new Label("title", getString("title")));
        add(new FeedbackPanel("messages"));
        
        final Form<Counterpart> form = new Form<>("form", new CompoundPropertyModel<>(counterpart));
        add(form);
        
        form.add(new TextField("hk"));
        form.add(new LastNameAutoCompleteTextField("lastName").setRequired(true));
        form.add(new FirstNameAutoCompleteTextField("firstName").setRequired(true));
        form.add(new MiddleNameAutoCompleteTextField("middleName"));
        form.add(new TextField("hloc"));
        form.add(new TextField("htel"));
        form.add(new TextField("hnspdv"));
        
        form.add(new Button("submit"){
            @Override
            public void onSubmit() {
                counterpartBean.save(form.getModelObject());

                setResponsePage(CounterpartList.class);

                info(getString("info_saved"));
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
