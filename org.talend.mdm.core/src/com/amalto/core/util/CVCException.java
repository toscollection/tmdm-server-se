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
package com.amalto.core.util;


public class CVCException extends Exception {

    public CVCException() {
        super();
    }

    public CVCException(String message) {
        super(message);
    }

    public CVCException(String message, Throwable cause) {
        super(message, cause);
    }

    public CVCException(Throwable cause) {
        super(cause);
    }
}
