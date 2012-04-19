package org.complitex.flexbuh.common.template.toolbar;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.image.ContextImage;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;

import java.util.Random;

/**
 *
 * @author Artem
 */
public abstract class ToolbarButton extends Panel {
    private String tagId;
    private static final String LINK_MARKUP_ID = "link";
    private boolean useAjax;

    public ToolbarButton(String id, String path, String titleKey) {
       this(id, path, titleKey, false);
    }

    public ToolbarButton(String id, String path, String titleKey, boolean useAjax) {
        super(id);

        this.useAjax = useAjax;

        AbstractLink link = addLink();
        add(link);

        link.add(addImage(path, titleKey));
    }

    public ToolbarButton(String id, String path, String titleKey, String tagId) {
        this(id, path, titleKey, false);
        this.tagId = tagId;
    }

    protected void onClick(){
    }

    protected void onClick(AjaxRequestTarget target){
    }

    protected class ToolbarButtonLink extends Link<Void> {
        private Random random = new Random();

        public ToolbarButtonLink() {
            super(LINK_MARKUP_ID);
        }

        @Override
        public void onClick() {
            ToolbarButton.this.onClick();
        }

        @Override
        protected CharSequence getURL() {
            return super.getURL() + "&" + random.nextInt();
        }
    }

    protected AbstractLink addLink() {
        if (useAjax) {
            return new AjaxLink(LINK_MARKUP_ID){

                @Override
                public void onClick(AjaxRequestTarget target) {
                    ToolbarButton.this.onClick(target);
                }
            };
        }else{
            return new ToolbarButtonLink();
        }
    }

    protected ContextImage addImage(String path, final String titleKey) {
        return new ContextImage("image", path) {

            @Override
            protected void onComponentTag(ComponentTag tag) {
                super.onComponentTag(tag);
                tag.put("title", getString(titleKey));

                if (tagId != null) {
                    tag.setId(tagId);
                }
            }
        };
    }
}
