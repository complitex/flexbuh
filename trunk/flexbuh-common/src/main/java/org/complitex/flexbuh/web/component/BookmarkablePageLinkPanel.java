package org.complitex.flexbuh.web.component;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * @author Pavel Sknar
 *         Date: 08.09.11 18:09
 */
public class BookmarkablePageLinkPanel <T> extends Panel {

    public <C extends Page> BookmarkablePageLinkPanel(String id, String label, Class<C> page, PageParameters parameters) {
        super(id);
        Link link = new BookmarkablePageLink<T>("link", page, parameters);
        link.add(new Label("label", label));
        add(link);
    }

    public <C extends Page> BookmarkablePageLinkPanel(String id, String label, final String markupId, Class<C> page, PageParameters parameters) {
        super(id);

        Link link = new BookmarkablePageLink<T>("link", page, parameters);
        link.add(new Label("label", label));
        add(link);
    }
}
