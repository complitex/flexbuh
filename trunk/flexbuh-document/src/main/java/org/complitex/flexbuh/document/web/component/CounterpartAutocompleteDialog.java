package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.complitex.flexbuh.common.entity.FilterWrapper;
import org.complitex.flexbuh.common.entity.dictionary.FieldCode;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.common.web.component.AutocompleteDialogComponent;
import org.complitex.flexbuh.common.web.component.IAutocompleteDialog;
import org.complitex.flexbuh.document.entity.Counterpart;
import org.complitex.flexbuh.document.service.CounterpartBean;
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

    private DeclarationStringModel model;

    private Long sessionId;
    private Long personProfileId;

    public CounterpartAutocompleteDialog(String id, Long sessionId, Long personProfileId, DeclarationStringModel model,
                                         IAutocompleteDialog<Counterpart> dialog) {
        super(id, model, model.getDeclaration().getTemplateName(), model.getName(), FieldCode.COUNTERPART_SPR_NAME, dialog);

        this.sessionId = sessionId;
        this.personProfileId  = personProfileId;
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
            FilterWrapper<Counterpart> filter = new FilterWrapper<>(new Counterpart(sessionId, personProfileId));

            switch (getAlias()){
                case "HK":
                    filter.getObject().setHk(tern);
                    break;
                case "HNAME":
                    filter.getObject().setHname(tern);
                    break;
                case "HLOC":
                    filter.getObject().setHloc(tern);
                    break;
                case "HTEL":
                    filter.getObject().setHtel(tern);
                    break;
                case "HNSPDV":
                    filter.getObject().setHnspdv(tern);
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
