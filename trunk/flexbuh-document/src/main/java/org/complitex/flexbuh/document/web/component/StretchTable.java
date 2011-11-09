package org.complitex.flexbuh.document.web.component;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.repeater.AbstractRepeater;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 07.11.11 17:50
 */
public class StretchTable extends AbstractRepeater {
    private List<WebMarkupContainer> stretchRows = new ArrayList<>();
    
    private int nextId = 0;

    public StretchTable(String id) {
        super(id);
        setOutputMarkupId(true);
        
        stretchRows.add(new WebMarkupContainer(++nextId + ""));
    }
    
    public WebMarkupContainer insertAfter(WebMarkupContainer row){
        WebMarkupContainer newRow = new WebMarkupContainer(++nextId + "");

        if (row != null) {
            stretchRows.add(stretchRows.indexOf(row) + 1, newRow);
        }else {
            stretchRows.add(newRow);
        }

        return newRow;
    }

    public void deleteRow(WebMarkupContainer row){
        stretchRows.remove(row);
    }

    @Override
    protected Iterator<? extends Component> renderIterator() {
        return stretchRows.iterator();
    }

    public WebMarkupContainer getFirstStretchRow(){
        return stretchRows.get(0);
    }

    @Override
    protected void onPopulate() {
        //remove
        for (Component component : this) {
            WebMarkupContainer c = (WebMarkupContainer) component;

            if (!stretchRows.contains(c)) {
                remove(c);
            }
        }

        //add
        for (Component row : stretchRows) {
            boolean found = false;

            for (Component component : this) {
                if (row.equals(component)) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                add(row);
            }
        }

        //delete visible
        if (stretchRows.size() == 1) {
            ((AddRowPanel) (stretchRows.get(0)).get("add_row_panel")).setDeleteVisible(false);
        }
    }

    public List<WebMarkupContainer> getStretchRows() {
        return stretchRows;
    }

    public void setStretchRows(List<WebMarkupContainer> stretchRows) {
        this.stretchRows = stretchRows;
    }
}
