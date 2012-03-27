package org.complitex.flexbuh.resources;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 *
 * @author Artem
 */
public final class WebCommonResourceInitializer implements IInitializer {

    /* css resources */
    public static final String CSS_STYLE = "css/style.css";
    public static final String CSS_FLEXBUH = "css/flexbuh.css";

    /* js resources */
    public static final String JS_COMMON = "js/common.js";
    public static final String JS_IE_SELECT_FIX = "js/ie_select_fix.js";
    public static final String JS_HIGHLIHT = "js/jquery-ui-1.7.3.highlight.min.js";
    public static final String JS_SCROLL = "js/jquery.scrollTo-1.4.2-min.js";
    public static final String JS_PLACEHOLDER = "js/jquery.placeholder.js";

    @Override
    public void init(Application application) {
        mount(CSS_STYLE, application);
        mount(CSS_FLEXBUH, application);

        mount(JS_COMMON, application);
        mount(JS_IE_SELECT_FIX, application);
        mount(JS_HIGHLIHT, application);
        mount(JS_SCROLL, application);
        mount(JS_PLACEHOLDER, application);
    }

    @Override
    public void destroy(Application application) {
        //implement me if you wish
    }

    private void mount(String path, Application application){
        ((WebApplication)application).mountResource(path, new PackageResourceReference(getClass(), path));
    }
}
