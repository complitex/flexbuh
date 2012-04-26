package org.complitex.flexbuh.document.service;

import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupParser;
import org.apache.wicket.markup.MarkupResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.resource.StringResourceStream;
import org.apache.wicket.util.time.Time;
import org.complitex.flexbuh.common.entity.dictionary.Field;
import org.complitex.flexbuh.common.entity.dictionary.FieldCode;
import org.complitex.flexbuh.common.service.dictionary.FieldCodeBean;
import org.complitex.flexbuh.common.util.XmlUtil;
import org.complitex.flexbuh.document.entity.Declaration;
import org.complitex.flexbuh.document.exception.CreateDocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 02.11.11 17:14
 */
@Singleton
public class DeclarationMarkupService {
    private static final Logger log = LoggerFactory.getLogger(DeclarationMarkupService.class);

    private final static String WICKET_NAMESPACE_URI = "http://wicket.apache.org/dtds.data/wicket-xhtml1.4-strict.dtd";

    @EJB
    private TemplateXMLService templateService;

    @EJB
    private FieldCodeBean fieldCodeBean;

    private Map<String, Markup> markupMap = new ConcurrentHashMap<>();

    public Markup getMarkup(String templateName){
        Markup markup = markupMap.get(templateName);

        if (markup == null){
            try {
                markup = createMarkup(templateName);
                markupMap.put(templateName, markup);
            } catch (Exception e) {
                log.error("Ошибка создания шаблона страницы", e);

                throw new RuntimeException(e);
            }
        }

        return markup;
    }

    private Markup createMarkup(String templateName) throws IOException, ResourceStreamNotFoundException, CreateDocumentException {
        StringResourceStream stringResourceStream = new StringResourceStream(getMarkupString(templateName), "text/html");
        stringResourceStream.setCharset(Charset.forName("UTF-8"));
        stringResourceStream.setLastModified(Time.now());

        return new MarkupParser(new MarkupResourceStream(stringResourceStream)).parse();
    }

    private String getMarkupString(String templateName) throws CreateDocumentException {
        //Xml
        Document template = templateService.getTemplate(templateName, new Declaration(templateName));
        Document schema = templateService.getTemplateXSDDocument(templateName);

        //Fields
        List<Field> fields = fieldCodeBean.getFields(templateName);
        Set<String> sprNameSet = new HashSet<>();

        //Body
        NodeList bodyNodeList = template.getElementsByTagName("body");
        if (bodyNodeList.getLength() < 1){
            bodyNodeList = template.getElementsByTagName("BODY");
        }

        Element panel = template.createElement("wicket:panel");
        panel.setAttribute("xmlns:wicket", WICKET_NAMESPACE_URI);

        Element containerElement = (Element) template.renameNode(bodyNodeList.item(0), null, "div");
        containerElement.setAttribute("wicket:id", "container");
        panel.appendChild(containerElement);

        //Input
        NodeList inputList = containerElement.getElementsByTagName("input");

        for (int i=0; i < inputList.getLength(); ++i){
            Element inputElement = (Element) inputList.item(i);

            if (XmlUtil.getParentById("StretchTable", inputElement) == null){
                //type
                Element schemaElement = XmlUtil.getElementByName(inputElement.getAttribute("id"), schema);
                String type = schemaElement.getAttribute("type");
                inputElement.setAttribute("schema", type);

                //id
                final String id = inputElement.getAttribute("id");
                inputElement.setAttribute("wicket:id", id);
                inputElement.removeAttribute("id");

                //create form component
                if ("DGchk".equals(type)){
                    Element choiceElement = (Element) schemaElement.getParentNode();

                    if ("xs:choice".equals(choiceElement.getNodeName())){
                        inputElement.setAttribute("type", "radio");

                        //child nodes to radio set
                        NodeList children = choiceElement.getElementsByTagName("xs:element");

                        if (((Element)children.item(0)).getAttribute("name").equals(id)){
                            Element radioSetElement = template.createElement("span");
                            radioSetElement.setAttribute("wicket:id", "radio_set_" + id);

                            inputElement.getParentNode().insertBefore(radioSetElement, inputElement);
                        }
                    }else{
                        inputElement.setAttribute("type", "checkbox");
                    }
                }else{
                    //Field Code
                    for (Field field : fields){
                        String sprName = field.getSprName();

                        if (FieldCode.IMPLEMENTED.contains(sprName) && id.equals(field.getName())){
                            template.renameNode(inputElement, null, "span");
                            i--; //inputList size changed after rename

                            inputElement.setAttribute("field", sprName);

                            //dialog
                            if (!sprNameSet.contains(sprName)){
                                Element sprDivElement = template.createElement("div");
                                sprDivElement.setAttribute("wicket:id", "dialog_" + sprName);
                                
                                containerElement.insertBefore(sprDivElement, containerElement.getFirstChild());
                                
                                sprNameSet.add(sprName);                                
                            }

                            break;
                        }
                    }

                    //TextField
                    inputElement.setAttribute("type", "text");

                    inputElement.setAttribute("mask", ((Element) XmlUtil.getParent("td", inputElement)).getAttribute("mask"));
                }
            }
        }

        //Dynamic table input
        List<Element> tbodyElements = new ArrayList<>();

        NodeList stretchList = XmlUtil.getElementsById("StretchTable", containerElement);

        int index = 0;

        for (int i=0, sLen = stretchList.getLength(); i < sLen; ++i){
            Element tbody = (Element) XmlUtil.getParent("tbody", stretchList.item(i));

            if (!tbodyElements.contains(tbody)) {
                index++;
                tbodyElements.add(tbody);

                //ajax container
                String tbodyWicketId = "process_" + index;
                tbody.setAttribute("wicket:id", tbodyWicketId);
                tbody.removeAttribute("id");

                int addRow = !tbody.getAttribute("addRow").isEmpty() ? Integer.parseInt(tbody.getAttribute("addRow")) : 1;
                NodeList stretchTables = XmlUtil.getElementsById("StretchTable", tbody);

                //repeater
                String repeaterWicketId = "repeater_" + index;

                Element wrapContainer = template.createElement("wicket:container");
                wrapContainer.setAttribute("wicket:id", repeaterWicketId);

                int rows = stretchTables.getLength();
                for (int j =  rows - addRow; j < rows; ++j){
                    Element stretchTableElement = (Element) stretchTables.item(j);
                    NodeList stretchInputList = stretchTableElement.getElementsByTagName("input");

                    //Input
                    for (int k = 0; k < stretchInputList.getLength(); k++){
                        System.out.println(stretchInputList.getLength());

                        Element stretchInputElement = (Element) stretchInputList.item(k);

                        String id = stretchInputElement.getAttribute("id");
                        stretchInputElement.setAttribute("wicket:id", id);
                        stretchInputElement.removeAttribute("id");

                        //Field Code
                        for (Field field : fields){
                            String sprName = field.getSprName();

                            if (FieldCode.IMPLEMENTED.contains(sprName) && id.equals(field.getName())){
                                template.renameNode(stretchInputElement, null, "span");

                                stretchInputElement.setAttribute("field", sprName);

                                k--; //stretchInputList update length after rename node

                                //dialog
                                if (!sprNameSet.contains(sprName)){
                                    Element sprDivElement = template.createElement("div");
                                    sprDivElement.setAttribute("wicket:id", "dialog_" + sprName);

                                    containerElement.insertBefore(sprDivElement, containerElement.getFirstChild());

                                    sprNameSet.add(sprName);
                                }

                                break;
                            }
                        }

                        //type
                        Element schemaElement = XmlUtil.getElementByName(id, schema);
                        String type = schemaElement.getAttribute("type");

                        stretchInputElement.setAttribute("type", "text");

                        stretchInputElement.setAttribute("schema", type);
                        stretchInputElement.setAttribute("mask", ((Element) XmlUtil.getParent("td", stretchInputElement)).getAttribute("mask"));
                    }

                    //Row number
                    NodeList spRownumList = XmlUtil.getElementsById("spRownum", stretchTableElement);

                    for (int k = 0, spLen = spRownumList.getLength(); k < spLen; ++k) {
                        Element spRownumElement = (Element) spRownumList.item(k);

                        if (spRownumElement != null){
                            spRownumElement.setAttribute("wicket:id", "spRownum_" + index + "_" + j + "_" + k);
                            spRownumElement.removeAttribute("id");
                        }
                    }

                    wrapContainer.appendChild(stretchTableElement);
                }
                tbody.appendChild(wrapContainer);

                //add delete link
                Element firstRow = (Element) stretchTables.item(rows - addRow);
                NodeList tdList = firstRow.getElementsByTagName("td");
                if (tdList.getLength() == 0){
                    tdList = firstRow.getElementsByTagName("TD");
                }
                Element firstColumn = (Element) tdList.item(0);

                Element wrapTableElement = template.createElement("table");
                Element trElement = template.createElement("tr");
                Element tdLeftElement = template.createElement("td");
                tdLeftElement.setAttribute("wicket:id", "add_row_panel");
                Element tdRightElement = template.createElement("td");

                trElement.appendChild(tdLeftElement);
                trElement.appendChild(tdRightElement);
                wrapTableElement.appendChild(trElement);

                NodeList childNodes = firstColumn.getChildNodes();
                while (childNodes.getLength() > 0){
                    tdRightElement.appendChild(childNodes.item(0));
                }

                firstColumn.appendChild(wrapTableElement);

                //style
                wrapTableElement.setAttribute("style", "width: 100%;");
                wrapTableElement.setAttribute("border", "0");
                wrapTableElement.setAttribute("cellpadding", "0");
                wrapTableElement.setAttribute("cellspacing", "0");
                tdLeftElement.setAttribute("style", "width: 40px;");
            }
        }

        return XmlUtil.getString(panel);
    }



}
