// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.util;

import java.util.Locale;
import java.util.ResourceBundle;

public class MessagesFactory {

    private static ResourceBundleLocator resourceBundleLocator;

    private MessagesFactory() {
    }

    public static Messages getMessages(String baseName, ClassLoader loader) {
        return new Messages(baseName, loader);
    }

    static synchronized ResourceBundleLocator getLocator() {
        if (resourceBundleLocator == null) {
            resourceBundleLocator = new DefaultResourceBundleLocator();
        }
        return resourceBundleLocator;
    }

    private static class DefaultResourceBundleLocator implements ResourceBundleLocator {

        public ResourceBundle getBundle(String baseName, Locale locale, ClassLoader loader) {
            if (locale == null)
                locale = getLocale();
            return ResourceBundle.getBundle(baseName, locale, loader);
        }

        private static Locale getLocale() {
            return LocaleUtil.getLocale();
        }
    }
}
