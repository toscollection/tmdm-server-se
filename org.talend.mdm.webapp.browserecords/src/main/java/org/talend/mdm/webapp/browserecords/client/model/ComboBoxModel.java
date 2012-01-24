// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.model;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class ComboBoxModel extends BaseModelData {

    private static final long serialVersionUID = 1L;

    public ComboBoxModel() {
    }

    public ComboBoxModel(String text, String value) {
        setText(text);
        setValue(value);
    }

    public String getText() {
        return get("text"); //$NON-NLS-1$
    }

    public void setText(String text) {
        set("text", text); //$NON-NLS-1$
    }

    public String getValue() {
        return get("value"); //$NON-NLS-1$
    }

    public void setValue(String value) {
        set("value", value); //$NON-NLS-1$
    }
}
