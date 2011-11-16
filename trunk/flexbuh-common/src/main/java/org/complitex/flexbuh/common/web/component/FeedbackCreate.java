package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.complitex.flexbuh.common.entity.Feedback;
import org.complitex.flexbuh.common.service.FeedbackBean;
import org.complitex.flexbuh.common.template.TemplatePage;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.11.11 16:27
 */
public class FeedbackCreate extends Panel {
    @EJB
    private FeedbackBean feedbackBean;

    public FeedbackCreate(String id) {
        super(id);

        final Dialog dialog = new Dialog("feedbackDialog");
        dialog.setWidth(530);
        dialog.setTitle("Оставить отзыв или сообщить об ошибке");
        add(dialog);

        final FeedbackPanel feedbackPanel = new FeedbackPanel("messages");
        feedbackPanel.setOutputMarkupId(true);
        dialog.add(feedbackPanel);
        
        add(new AjaxLink("feedback") {
            @Override
            public void onClick(AjaxRequestTarget target) {
                dialog.open(target);
            }
        });
        
        final Form<Feedback> form = new Form<>("form", new CompoundPropertyModel<>(new Feedback()));
        form.setOutputMarkupId(true);
        dialog.add(form);
        
        form.add(new TextField<>("name").setRequired(true));
        form.add(new TextField<>("email"));
        form.add(new TextArea<>("message").setRequired(true));
        
        form.add(new AjaxButton("submit") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> f) {
                Feedback feedback = form.getModelObject();
                feedback.setSessionId(((TemplatePage)getPage()).getSessionId(true));

                feedbackBean.save(feedback);

                info("Спасибо за отзыв!");
                
                target.add(feedbackPanel);

                target.add(form.setVisible(false));
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(feedbackPanel);
            }
        });
    }
}
                                