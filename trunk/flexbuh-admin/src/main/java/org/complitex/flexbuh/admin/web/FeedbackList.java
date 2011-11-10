package org.complitex.flexbuh.admin.web;

import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.entity.Feedback;
import org.complitex.flexbuh.service.FeedbackBean;
import org.complitex.flexbuh.template.TemplatePage;
import org.complitex.flexbuh.web.component.paging.PagingNavigator;

import javax.ejb.EJB;
import java.util.Iterator;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.11.11 17:31
 */
public class FeedbackList extends TemplatePage{
    @EJB
    private FeedbackBean feedbackBean;
    
    public FeedbackList() {
        SortableDataProvider<Feedback> dataProvider = new SortableDataProvider<Feedback>() {

            @Override
            public Iterator<? extends Feedback> iterator(int first, int count) {
                return feedbackBean.getFeedbacks(first, count).iterator();  
            }

            @Override
            public int size() {
                return feedbackBean.getFeedbacksCount();
            }

            @Override
            public IModel<Feedback> model(Feedback object) {
                return new Model<>(object);
            }
        };

        DataView dataView = new DataView<Feedback>("feedbacks", dataProvider) {
            @Override
            protected void populateItem(Item<Feedback> item) {
                Feedback feedback = item.getModelObject();
                
                item.add(DateLabel.forDateStyle("date", new Model<>(feedback.getDate()), "FF"));
                item.add(new Label("name", feedback.getName()));
                item.add(new Label("email", feedback.getEmail()));
                item.add(new Label("message", feedback.getMessage()));
            }
        };
        
        add(dataView);
        
        add(new PagingNavigator("paging", dataView, "FeedbackList"));
    }
}
