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

package com.amalto.core.history.action;

import com.amalto.core.history.DeleteType;
import com.amalto.core.history.MutableDocument;

import java.util.Date;

/**
 *
 */
public class LogicalDeleteAction extends AbstractAction {
    public LogicalDeleteAction(Date date, String source, String userName) {
        super(date, source, userName);
    }

    public MutableDocument perform(MutableDocument document) {
        return document.delete(DeleteType.LOGICAL);
    }

    public MutableDocument undo(MutableDocument document) {
        return document.recover(DeleteType.LOGICAL);
    }

    public MutableDocument addModificationMark(MutableDocument document) {
        return document;
    }

    public MutableDocument removeModificationMark(MutableDocument document) {
        return document;
    }
}
