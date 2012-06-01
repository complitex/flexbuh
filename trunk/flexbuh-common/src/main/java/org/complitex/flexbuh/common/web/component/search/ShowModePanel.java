package org.complitex.flexbuh.common.web.component.search;

import org.apache.wicket.markup.html.form.EnumChoiceRenderer;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.flexbuh.common.entity.ShowMode;

import java.util.Arrays;
import java.util.List;

/**
 * @author Pavel Sknar
 *         Date: 17.05.12 10:12
 */
public class ShowModePanel extends Panel {

    public ShowModePanel(String id) {
        super(id);
        init();
    }

    private void init() {
        IChoiceRenderer renderer = new EnumChoiceRenderer(this);
        List<ShowMode> choices = Arrays.asList(ShowMode.values());
        RadioChoice showBooksMode = new RadioChoice("showMode", choices, renderer);
        showBooksMode.setSuffix("");
        add(showBooksMode);
    }
}
