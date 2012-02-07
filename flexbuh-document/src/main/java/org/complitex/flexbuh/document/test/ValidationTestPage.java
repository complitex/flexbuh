package org.complitex.flexbuh.document.test;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.complitex.flexbuh.document.exception.CreateDocumentException;
import org.complitex.flexbuh.document.service.TemplateService;
import org.complitex.flexbuh.document.web.validation.RestrictionValidator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.ejb.EJB;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 23.08.11 16:40
 */
public class ValidationTestPage extends WebPage{
    @EJB
    private TemplateService templateService;

    public ValidationTestPage() throws IOException, SAXException, ParserConfigurationException, CreateDocumentException {
//        add(CSSPackageResource.getHeaderContribution(TooltipBehavior.CSS));

        add(new FeedbackPanel("message"));

        Form form = new Form("form");
        add(form);

        Document commonTypes = templateService.getTemplateXSDDocument("common_types");

        List<Element> simpleTypes = new ArrayList<>();

        NodeList nodes = commonTypes.getElementsByTagName("xs:simpleType");
        for (int i = 0; i < nodes.getLength(); ++i){
            simpleTypes.add((Element) nodes.item(i));
        }

        ListView listView = new ListView<Element>("list", simpleTypes) {

            @Override
            protected void populateItem(ListItem<Element> item) {
                Element element = item.getModelObject();
                String name = element.getAttribute("name");

                item.add(new Label("label", name));

                TextField<String> input = new TextField<>("input", new Model<String>());
                input.add(new RestrictionValidator<String>(element));
//                input.add(new TooltipBehavior().setTip("#validation"));

                item.add(input);
            }
        };

        form.add(listView);
    }
}
