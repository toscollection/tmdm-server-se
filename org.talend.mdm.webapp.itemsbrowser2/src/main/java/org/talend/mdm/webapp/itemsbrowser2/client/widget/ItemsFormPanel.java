/*
 * Ext GWT - Ext for GWT Copyright(c) 2007-2009, Ext JS, LLC. licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.util.Locale;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.creator.FieldSetCreator;
import org.talend.mdm.webapp.itemsbrowser2.shared.ComplexTypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

public class ItemsFormPanel extends Composite {

    FormPanel content = new FormPanel();

    RecordToolBar toolbar = new RecordToolBar();

    private FormBinding formBindings;

    public ItemsFormPanel() {
        content.setHeaderVisible(false);
        content.setScrollMode(Scroll.AUTO);
        content.setTopComponent(toolbar);
        this.initComponent(content);
    }


    public void paint(EntityModel entityModel) {
        content.removeAll();
        formBindings = new FormBinding(content);
        Map<String, TypeModel> dataTypes = entityModel.getMetaDataTypes();
        String concept = entityModel.getConceptName();
        TypeModel typeModel = dataTypes.get(concept);
        toolbar.updateToolBar();
        
        FieldSet fildSet = FieldSetCreator.createFieldGroup((ComplexTypeModel) typeModel, formBindings, true, Locale.getLanguage(Itemsbrowser2.getSession().getAppHeader()));
        if (fildSet != null) {
            content.add(fildSet);
        }

        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        if (itemsSearchContainer.getItemsListPanel().getGrid() != null) {
            ListStore<ItemBean> store = itemsSearchContainer.getItemsListPanel().getGrid().getStore();
            formBindings.setStore(store);
        }
        content.layout(true);
    }

    public void refreshGrid() {
        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        if (itemsSearchContainer.getItemsListPanel().getGrid() != null) {
            itemsSearchContainer.getItemsListPanel().getStore().getLoader().load();
        }
    }
    
    public void commitItemBean(){
        formBindings.getModel();
        Store store = formBindings.getStore();
        if (store != null) {
            Record record = store.getRecord(formBindings.getModel());
            if (record != null){
                record.commit(false);
            }
        } 
    }

    public ItemBean getNewItemBean() {
        ItemBean item = (ItemBean) formBindings.getModel();
        String concept = item.getConcept();

        Document doc = XMLParser.createDocument();
        Map<String, Element> elementSet = new HashMap<String, Element>();
        for (FieldBinding fb : formBindings.getBindings()) {
            Field field = fb.getField();
            Object value = null;
            if (field instanceof SimpleComboBox) {
                value = ((SimpleComboBox) field).getValue().get("value"); //$NON-NLS-1$
            }
            if (field instanceof DateField) {
                value = DateTimeFormat.getFormat("yyyy-MM-dd").format((Date) field.getValue()); //$NON-NLS-1$
            } else {
                value = field.getValue();
            }

            if (value instanceof List){
                String key = field.getName();
                String parentPath = key.substring(0, key.lastIndexOf('/'));
                String elName = key.substring(key.lastIndexOf('/') + 1);
                createElements(parentPath, elName, (List)value, elementSet, doc);
            } else {
                createElements(field.getName(), value == null ? "" : value.toString(), elementSet, doc);
            }
        }
        Element el = elementSet.get(concept);
        doc.appendChild(el);
        item.setItemXml(doc.toString());
        Window.alert(item.getItemXml());
        return item;
    }


    private String createElementString(String elName, List values, Document doc){
        StringBuffer buffer = new StringBuffer();
        for (Object o : values){
            Element el = doc.createElement(elName);
            el.appendChild(doc.createTextNode(o.toString()));
            buffer.append(el.toString());
        }
        return buffer.toString();
    }

    private void createElements(String xpath, String value, Map<String, Element> elementSet, Document doc){
        Element parent = null;
        String[] xps = xpath.split("/"); //$NON-NLS-1$
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        for (String xp : xps) {
            if (isFirst) {
                sb.append(xp);
                isFirst = false;
            } else {
                sb.append("/" + xp); //$NON-NLS-1$
            }
            Element tempEl = elementSet.get(sb.toString());
            if (tempEl == null) {
                tempEl = (Element) doc.createElement(xp);
                elementSet.put(sb.toString(), tempEl);
            }
            if (parent != null) {
                parent.appendChild(tempEl);
            }
            parent = tempEl;
        }
        parent.appendChild(doc.createTextNode(value));
    }
    
    public void createElements(String xpath, String elName, List value, Map<String, Element> elementSet, Document doc){
        Element parent = null;
        String[] xps = xpath.split("/");
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        for (String xp : xps){
            if (isFirst){
                sb.append(xp);
                isFirst = false;
            } else {
                sb.append("/" + xp);
            }
            Element tempEl = elementSet.get(sb.toString());
            if (tempEl == null){
                tempEl = (Element) doc.createElement(xp);
                elementSet.put(sb.toString(), tempEl);
            }
            if (parent != null){
                parent.appendChild(tempEl);
            }
            parent = tempEl;
        }
        for (Object o : value){
            Element el = doc.createElement(elName);
            el.appendChild(doc.createTextNode(o.toString()));
            parent.appendChild(el); 
        }
    }

    public ItemBean getItemBean() {
        return (ItemBean) formBindings.getModel();
    }

    public void bind(ModelData modelData) {
        if (modelData != null) {
            formBindings.bind(modelData);
        } else {
            formBindings.unbind();
        }
    }

    public void unbind() {
        formBindings.unbind();
    }

    public void setReadOnly(ModelData modelData, String[] keys) {
        // primary key is readonly when updating
        if (!((ItemBean) modelData).getIds().equals("")) //$NON-NLS-1$
            for (Field<?> field : content.getFields()) {
                for (String key : keys) {
                    if (field.getName().equals(key)) {
                        field.setEnabled(false);
                        continue;
                    }
                }
            }
    }
}
