package org.complitex.flexbuh.common.web.component;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.resource.PackageResourceReference;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.07.12 18:16
 */
public class HtmlTextArea<T> extends TextArea<T> {
    public HtmlTextArea(String id, IModel<T> model) {
        super(id, model);
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        response.renderCSSReference(new PackageResourceReference(HtmlTextArea.class, "codemirror/codemirror.css"));
        response.renderJavaScriptReference(new PackageResourceReference(HtmlTextArea.class, "codemirror/codemirror.js"));
        response.renderJavaScriptReference(new PackageResourceReference(HtmlTextArea.class, "codemirror/xml.js"));

        String js_xml = "var editor = CodeMirror.fromTextArea(document.getElementById(\""+ getMarkupId() +"\")," +
                "      {  mode: {name: \"xml\", alignCDATA: true}," +
                "        lineNumbers: true\n, lineWrapping: true " +
                "      });";

        response.renderOnDomReadyJavaScript(js_xml);
    }

    @Override
    protected void onComponentTag(ComponentTag tag) {
        tag.getAttributes().put("id", getMarkupId());
        tag.put("name", getId());
    }
}
