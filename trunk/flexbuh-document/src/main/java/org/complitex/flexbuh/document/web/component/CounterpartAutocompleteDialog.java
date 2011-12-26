package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.complitex.flexbuh.common.entity.dictionary.FieldCode;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.common.web.component.AutocompleteDialogComponent;
import org.complitex.flexbuh.common.web.component.IAutocompleteDialog;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.entity.CounterpartFilter;
import org.complitex.flexbuh.document.service.CounterpartBean;
import org.complitex.flexbuh.document.service.PersonProfileBean;
import org.complitex.flexbuh.document.web.model.DeclarationStringModel;

import javax.ejb.EJB;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 28.11.11 12:35
 */
public class CounterpartAutocompleteDialog extends AutocompleteDialogComponent<Counterpart> implements IDeclarationStringComponent{
    @EJB
    private FieldCodeBean fieldCodeBean;

    @EJB
    private CounterpartBean counterpartBean;

    @EJB
    private PersonProfileBean personProfileBean;

    private Long sessionId;

    private DeclarationStringModel model;

    public CounterpartAutocompleteDialog(String id, DeclarationStringModel model, Long sessionId, IAutocompleteDialog<Counterpart> dialog) {
        super(id, model, model.getDeclaration().getTemplateName(), model.getName(), FieldCode.COUNTERPART_SPR_NAME, dialog);

        this.sessionId = sessionId;
        this.model = model;
    }

    protected void updateModel(Counterpart counterpart){
        switch (getAlias()){
            case "HK":
                model.setObject(counterpart.getHk());
                break;
            case "HNAME":
                model.setObject(counterpart.getHname());
                break;
            case "HLOC":
                model.setObject(counterpart.getHloc());
                break;
            case "HTEL":
                model.setObject(counterpart.getHtel());
                break;
            case "HNSPDV":
                model.setObject(counterpart.getHnspdv());
                break;
        }
    }

    @Override
    protected List<Counterpart> getValues(String tern) {
        try {
            CounterpartFilter filter = new CounterpartFilter(sessionId, personProfileBean.getSelectedPersonProfileId(sessionId));

            switch (getAlias()){
                case "HK":
                    filter.setHk(tern);
                    break;
                case "HNAME":
                    filter.setHname(tern);
                    break;
                case "HLOC":
                    filter.setHloc(tern);
                    break;
                case "HTEL":
                    filter.setHtel(tern);
                    break;
                case "HNSPDV":
                    filter.setHnspdv(tern);
                    break;
            }

            return counterpartBean.getCounterparts(filter);

        } catch (Exception e) {
            //error can happen
        }

        return null;
    }

    @Override
    protected IChoiceRenderer<Counterpart> getChoiceRenderer() {
        return new IChoiceRenderer<Counterpart>() {
            @Override
            public Object getDisplayValue(Counterpart object) {
                switch (getAlias()){
                    case "HK":
                        return StringUtil.getString(object.getHk());
                    case "HNAME":
                        return StringUtil.getString(object.getHname());
                    case "HLOC":
                        return StringUtil.getString(object.getHloc());
                    case "HTEL":
                        return StringUtil.getString(object.getHtel());
                    case "HNSPDV":
                        return StringUtil.getString(object.getHnspdv());
                }

                return null;
            }

            @Override
            public String getIdValue(Counterpart object, int index) {
                return object.getId() != null ? object.getId().toString() : null;
            }
        };
    }

    @Override
    public DeclarationStringModel getDeclarationModel() {
        return model;
    }

    @Override
    public String getValue() {
        return getAutocompleteField().getValue();
    }
}
