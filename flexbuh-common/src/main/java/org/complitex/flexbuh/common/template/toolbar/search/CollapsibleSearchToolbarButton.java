package org.complitex.flexbuh.common.template.toolbar.search;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.request.resource.ResourceReference;
import org.complitex.flexbuh.common.template.toolbar.ToolbarButton;
import org.complitex.flexbuh.common.web.component.search.ShowModePanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Pavel Sknar
 *         Date: 01.06.12 11:15
 */
public class CollapsibleSearchToolbarButton extends ToolbarButton {

    private static final Logger log = LoggerFactory.getLogger(CollapsibleSearchToolbarButton.class);

    private static final String IMAGE_SRC = "images/gear_blue.png";
    private static final String TITLE_KEY = "image.title.collapsible_search";

    private final ShowModePanel showModePanel;

    public CollapsibleSearchToolbarButton(String id, ShowModePanel showModePanel) {

        super(id, IMAGE_SRC, TITLE_KEY, true);

        this.showModePanel = showModePanel;
        showModePanel.setOutputMarkupPlaceholderTag(true);
        showModePanel.setVisible(false);
    }

    @Override
    protected void onClick(AjaxRequestTarget target) {
        log.debug("click on CollapsibleSearchToolbarButton");
        showModePanel.setVisible(!showModePanel.isVisible());
        target.add(showModePanel);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        response.renderCSSReference(new PackageResourceReference(CollapsibleSearchToolbarButton.class,
                "CollapsibleSearchToolbarButton.css"));

        response.renderJavaScriptReference(new PackageResourceReference(CollapsibleSearchToolbarButton.class,
                "CollapsibleSearchToolbarButton.js"));
    }
}
