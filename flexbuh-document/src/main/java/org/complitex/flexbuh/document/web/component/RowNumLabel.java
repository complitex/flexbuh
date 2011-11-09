package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.LoadableDetachableModel;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 08.11.11 15:26
 */
public class RowNumLabel extends Label {
    private final static String ABC = "АБВГҐДЕЄЖЗИІЇЙКЛМНОПРСТУФХЦЧШЩЮЯ";

    private boolean letter;

    public RowNumLabel(String id, final boolean letter, final WebMarkupContainer row, final StretchTable stretchTable) {
        super(id, new LoadableDetachableModel<String>() {
            @Override
            protected String load() {
                int index = stretchTable.getStretchRows().indexOf(row);

                return letter ? ABC.substring(index, index + 1) : String.valueOf(index + 1);
            }
        });

        this.letter = letter;

        setOutputMarkupId(true);
    }

    public boolean isLetter() {
        return letter;
    }
}
