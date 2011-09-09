package org.complitex.flexbuh.template.pages;

import org.apache.wicket.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.complitex.flexbuh.web.component.scroll.ScrollListBehavior;

/**
 * @author Pavel Sknar
 *         Date: 08.09.11 12:04
 */
public class ScrollListPage extends ListPage {

    public static final String SCROLL_PARAMETER = "idToScroll";

    public ScrollListPage() {
    }

    public ScrollListPage(PageParameters params) {
        super(params);
        String idToScroll = params.getString(SCROLL_PARAMETER);
        if (!Strings.isEmpty(idToScroll)) {
            add(new ScrollListBehavior(idToScroll));
        }
    }
}
