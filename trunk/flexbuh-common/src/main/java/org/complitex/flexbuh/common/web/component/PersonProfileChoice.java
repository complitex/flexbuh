package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.util.StringUtil;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.12.11 15:56
 */
public class PersonProfileChoice extends DropDownChoice<PersonProfile>{
    @EJB
    private PersonProfileBean personProfileBean;
    
    private Long sessionId;
    
    public PersonProfileChoice(String id, Long sessionId) {
        super(id);
        
        this.sessionId = sessionId;

        List<PersonProfile> profiles = personProfileBean.getAllPersonProfiles(sessionId);
        PersonProfile selectedPersonProfile = personProfileBean.getSelectedPersonProfile(sessionId);
        
        setModel(new Model<PersonProfile>());

        if (selectedPersonProfile != null) {
            for (PersonProfile pp : profiles){
                if (pp.getId().equals(selectedPersonProfile.getId())){
                    setModelObject(pp);
                    break;
                }
            }
        }

        setChoices(profiles);
        
        setChoiceRenderer(new IChoiceRenderer<PersonProfile>() {
            @Override
            public Object getDisplayValue(PersonProfile object) {
                return StringUtil.getString(object.getTin()) + " " +object.getProfileName();
            }

            @Override
            public String getIdValue(PersonProfile object, int index) {
                return object.getId() + "";
            }
        });

        setNullValid(true);
    }

    @Override
    protected void onSelectionChanged(PersonProfile newSelection) {
        personProfileBean.deselectAllPersonProfile(sessionId);

        if (newSelection != null){
            personProfileBean.setSelectedPersonProfile(newSelection);
        }
    }

    @Override
    protected boolean wantOnSelectionChangedNotifications() {
        return true;
    }
}
