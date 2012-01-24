/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.history;

import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.accessor.DOMAccessorFactory;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
*
*/
public class EmptyDocument implements MutableDocument {
    public static final org.w3c.dom.Document EMPTY_DOCUMENT;

    static {
        try {
            EMPTY_DOCUMENT = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public String exportToString() {
        return StringUtils.EMPTY;
    }

    public Accessor createAccessor(String path) {
        return DOMAccessorFactory.createAccessor(path, this);
    }

    public org.w3c.dom.Document asDOM() {
        return EMPTY_DOCUMENT;
    }

    public Document transform(DocumentTransformer transformer) {
        if (transformer == null) {
            throw new IllegalArgumentException("Transformer argument cannot be null");
        }
        return transformer.transform(this);
    }

    public boolean isCreated() {
        return false;
    }

    public boolean isDeleted() {
        return false;
    }

    public void restore() {
    }

    public MutableDocument setField(String field, String newValue) {
        return this;
    }

    public MutableDocument deleteField(String field) {
        return this;
    }

    public MutableDocument addField(String field, String value) {
        return this;
    }

    public MutableDocument create() {
        return this;
    }

    public MutableDocument delete(DeleteType deleteType) {
        return this;
    }

    public MutableDocument recover(DeleteType deleteType) {
        return this;
    }

    public Document applyChanges() {
        return this;
    }
}
