package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.common.entity.PersonProfile;
import org.complitex.flexbuh.common.service.PersonProfileBean;
import org.complitex.flexbuh.common.service.TemplateSession;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 27.12.11 15:56
 */
public class PersonProfileChoice extends DropDownChoice<PersonProfile>{
    @EJB
    private PersonProfileBean personProfileBean;

    public PersonProfileChoice(String id) {
        super(id);

        List<PersonProfile> profiles = personProfileBean.getAllPersonProfiles(TemplateSession.getSessionId());

        Long selectedPersonProfileId = TemplateSession.getPreferenceLong(PersonProfile.SELECTED_PERSON_PROFILE_ID);

        setModel(new Model<PersonProfile>());

        if (selectedPersonProfileId != null) {
            for (PersonProfile pp : profiles){
                if (pp.getId().equals(selectedPersonProfileId)){
                    setModelObject(pp);
                    break;
                }
            }
        }

        setChoices(profiles);
        
        setChoiceRenderer(new IChoiceRenderer<PersonProfile>() {
            @Override
            public Object getDisplayValue(PersonProfile personProfile) {
                String s = !personProfile.getSessionId().equals(TemplateSession.getSessionId())
                        ? "(" + personProfile.getUserName()  + ") " : "";
                s += personProfile.getTin() != null ? personProfile.getTin() + " " : "";
                s += personProfile.getProfileName();

                return s;
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
        if (newSelection != null){
            TemplateSession.putPreference(PersonProfile.SELECTED_PERSON_PROFILE_ID, newSelection.getId().toString());
        }else{
            TemplateSession.removePreference(PersonProfile.SELECTED_PERSON_PROFILE_ID);
        }
    }

    @Override
    protected boolean wantOnSelectionChangedNotifications() {
        return true;
    }
}
