// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget.filter;

import org.talend.mdm.webapp.browserecords.shared.Constants;

import com.extjs.gxt.ui.client.data.ModelData;

/**
 * created by yjli on 2014-2-8 Detailled comment
 * 
 */
public class BooleanFilter extends com.extjs.gxt.ui.client.widget.grid.filters.BooleanFilter {

    public BooleanFilter(String dataIndex) {
        super(dataIndex);
    }

    @Override
    public void setMessages(FilterMessages messages) {
        BooleanFilterMessages booleanFilterMessages = (BooleanFilterMessages) messages;
        booleanFilterMessages.setYesText(Constants.BOOLEAN_TRUE_DISPLAY_VALUE);
        booleanFilterMessages.setNoText(Constants.BOOLEAN_FALSE_DISPLAY_VALUE);
        super.setMessages(messages);
    }

    @Override
    public boolean validateModel(ModelData model) {
        Boolean val = Boolean.valueOf((String) getModelValue(model));
        return getValue().equals(val == null ? Boolean.FALSE : val);
    }
}
