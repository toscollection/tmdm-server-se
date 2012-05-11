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
package org.talend.mdm.webapp.base.server.i18n;

import org.talend.mdm.webapp.base.client.i18n.BaseMessages;

import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;

@SuppressWarnings("nls")
public final class BaseMessagesImpl implements BaseMessages {

    private static final Messages MESSAGES = MessagesFactory.getMessages("org.talend.mdm.webapp.base.client.i18n.BaseMessages",
            BaseMessagesImpl.class.getClassLoader());

    public String exception_parse_illegalChar(int beginIndex) {
        return MESSAGES.getMessage("exception_parse_illegalChar", beginIndex);
    }

    public String exception_parse_unknownOperator(String value) {
        return MESSAGES.getMessage("exception_parse_unknownOperator", value);
    }

    public String exception_parse_missEndBlock(char endBlock, int i) {
        return MESSAGES.getMessage("exception_parse_missEndBlock", endBlock, i);
    }

    public String exception_parse_tooManyEndBlock(char endBlock, int i) {
        return MESSAGES.getMessage("exception_parse_tooManyEndBlock", endBlock, i);
    }

    public String page_size_label() {
        return MESSAGES.getMessage("page_size_label");
    }

    public String page_size_notice() {
        return MESSAGES.getMessage("page_size_notice");
    }

    public String info_title() {
        return MESSAGES.getMessage("info_title");
    }

    public String error_title() {
        return MESSAGES.getMessage("error_title");
    }

    public String warning_title() {
        return MESSAGES.getMessage("warning_title");
    }

    public String confirm_title() {
        return MESSAGES.getMessage("confirm_title");
    }

    public String unknown_error() {
        return MESSAGES.getMessage("unknown_error");
    }

    public String session_timeout_error() {
        return MESSAGES.getMessage("session_timeout_error");
    }

    public String typemode_notfound_error(String typePath) {
        if (typePath == null)
            typePath = "";
        return MESSAGES.getMessage("typemode_notfound_error", typePath);
    }
}
