package org.complitex.flexbuh.logging.web.component;

import com.google.common.base.Joiner;
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
import org.complitex.flexbuh.common.util.StringUtil;
import org.complitex.flexbuh.logging.entity.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.*;

/**
 * @author Pavel Sknar
 *         Date: 17.11.11 15:21
 */
public class LogChangePanel extends Panel {
    private final static Logger log = LoggerFactory.getLogger(LogChangePanel.class);

    public LogChangePanel(String id, Log log){
        super(id);

        List<DiffObject> diffObjects = Lists.newArrayList();

        MapDifference<String, String> diffMap = Maps.difference(
                getFields(log.get(EventKey.OLD_OBJECT)),
                getFields(log.get(EventKey.NEW_OBJECT)));

        for (Map.Entry<String, MapDifference.ValueDifference<String>> de : diffMap.entriesDiffering().entrySet()) {
            diffObjects.add(new DiffObject(de.getKey(), de.getValue().leftValue(), de.getValue().rightValue()));
        }

        for (Map.Entry<String, String> de : diffMap.entriesOnlyOnLeft().entrySet()) {
            diffObjects.add(new DiffObject(de.getKey(), de.getValue(), ""));
        }

        for (Map.Entry<String, String> de : diffMap.entriesOnlyOnRight().entrySet()) {
            diffObjects.add(new DiffObject(de.getKey(), "", de.getValue()));
        }

        Collections.sort(diffObjects, new Comparator<DiffObject>() {
            @Override
            public int compare(DiffObject o1, DiffObject o2) {
                return o1.getFieldName().compareTo(o2.getFieldName());
            }
        });

        ListView<DiffObject> listView = new ListView<DiffObject>("diffs", diffObjects) {
            @Override
            protected void populateItem(ListItem<DiffObject> item) {
                DiffObject diff = item.getModelObject();

                item.add(new Label("field_name", diff.getFieldName()));
                item.add(new Label("old_value", diff.getOldValue()));
                item.add(new Label("new_value", diff.getNewValue()));
            }
        };

        add(listView);
    }

    private Map<String, String> getFields(String xmlObject) {
        if (StringUtils.isEmpty(xmlObject)) {
            return Collections.emptyMap();
        }

        Map<String, String> fields = Maps.newHashMap();

        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

            InputStream is = new ByteArrayInputStream(xmlObject.getBytes("UTF-8"));

            org.w3c.dom.Document doc = documentBuilder.parse(is);

            NodeList propertyNodeList = doc.getDocumentElement().getChildNodes();

            for (int i = 0; i < propertyNodeList.getLength(); i++) {
                Node node = propertyNodeList.item(i);

                if (node instanceof Element) {
                    Element element = (Element) node;

                    NodeList subNodeList = element.getChildNodes();

                    int subLength = subNodeList.getLength();

                    if (subLength > 3){
                        for (int k = 0; k < subLength; ++k){
                            Node subNode = subNodeList.item(k);

                            if (subNode instanceof Element) {
                                Element subElement = (Element) subNode;

                                String id = ":";

                                NodeList idNodeList = subElement.getElementsByTagName("id");
                                if (idNodeList.getLength() > 0){
                                    id = ":" + idNodeList.item(0).getTextContent().trim() + ":";
                                }

                                fields.put(element.getTagName() + id  + StringUtil.getLastPacketName(subElement.getTagName()),
                                        getString(subElement));
                            }
                        }
                    } else {
                        fields.put(element.getTagName(), element.getTextContent().trim());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка разбора xml", e);
        }

        return fields;
    }

    private String getString(Node node){
        NodeList childNodeList = node.getChildNodes();

        int childLength = childNodeList.getLength();

        if (childLength > 3){
            List<String> list = new ArrayList<>();

            for (int i = 0; i < childLength; ++i){
                Node childNode = childNodeList.item(i);

                if (childNode instanceof Element){
                    Element childElement = (Element) childNode;

                    list.add(childElement.getTagName() + ": " + childElement.getTextContent().trim());
                }
            }

            return Joiner.on(", ").join(list);
        }

        return node.getTextContent().trim();
    }

    private static class DiffObject implements Serializable {
        private String fieldName;
        private String oldValue;
        private String newValue;

        private DiffObject(String fieldName, String oldValue, String newValue) {
            this.fieldName = fieldName;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getOldValue() {
            return oldValue;
        }

        public void setOldValue(String oldValue) {
            this.oldValue = oldValue;
        }

        public String getNewValue() {
            return newValue;
        }

        public void setNewValue(String newValue) {
            this.newValue = newValue;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}
