package org.complitex.flexbuh.logging.web.component;

import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.complitex.flexbuh.common.logging.EventKey;
import org.complitex.flexbuh.logging.entity.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Pavel Sknar
 *         Date: 17.11.11 15:21
 */
public class LogChangePanel extends Panel {
    private final static Logger logger = LoggerFactory.getLogger(LogChangePanel.class);

    public LogChangePanel(String id, Log log){
        super(id);

        List<DiffObject> diffs = Lists.newArrayList();

        String oldObject = log.get(EventKey.OLD_OBJECT);
        String newObject = log.get(EventKey.NEW_OBJECT);

        MapDifference<String, String> diff = Maps.difference(getFields(oldObject), getFields(newObject));
        for (Map.Entry<String, MapDifference.ValueDifference<String>> differenceEntry : diff.entriesDiffering().entrySet()) {
            diffs.add(new DiffObject(differenceEntry.getKey(), differenceEntry.getValue().leftValue(), differenceEntry.getValue().rightValue()));
        }
        for (Map.Entry<String, String> differenceEntry : diff.entriesOnlyOnLeft().entrySet()) {
            diffs.add(new DiffObject(differenceEntry.getKey(), differenceEntry.getValue(), ""));
        }
        for (Map.Entry<String, String> differenceEntry : diff.entriesOnlyOnRight().entrySet()) {
            diffs.add(new DiffObject(differenceEntry.getKey(), "", differenceEntry.getValue()));
        }

        //ListView<String> listView = new ListView<String>("log_diffs", DiffUtils.generateUnifiedDiff("", "", Collections.<String>emptyList(), patch, 0)) {
        ListView<DiffObject> listView2 = new ListView<DiffObject>("diffs", diffs) {

            @Override
            protected void populateItem(ListItem<DiffObject> item) {
                DiffObject diff = item.getModelObject();

                item.add(new Label("field_name", diff.fieldName));
                item.add(new Label("old_value", diff.oldValue));
                item.add(new Label("new_value", diff.newValue));
            }
        };

        add(listView2);
    }

    private Map<String, String> getFields(String xmlObject) {
        if (StringUtils.isEmpty(xmlObject)) {
            return Collections.emptyMap();
        }

        Map<String, String> fields = Maps.newHashMap();

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            InputStream is = new ByteArrayInputStream(xmlObject.getBytes());


            org.w3c.dom.Document doc = documentBuilder.parse(is);
            NodeList childNodes = doc.getDocumentElement().getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                org.w3c.dom.Node node = childNodes.item(i);
                if (!StringUtils.equals(node.getNodeName(), "#text")) {
                    fields.put(node.getNodeName(), node.getTextContent());
                }
            }
        } catch (Exception e) {
            //todo Invalid byte 1 of 1-byte UTF-8 sequence
        }

        return fields;
    }

    private class DiffObject implements Serializable {
        private String fieldName;
        private String oldValue;
        private String newValue;

        private DiffObject(String fieldName, String oldValue, String newValue) {
            this.fieldName = fieldName;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
