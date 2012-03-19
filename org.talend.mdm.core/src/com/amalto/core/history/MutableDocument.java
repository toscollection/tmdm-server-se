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

/**
 * A {@link Document} that is able to change.
 */
public interface MutableDocument extends Document {

    /**
     * @param path The path for the accessor.
     * @return Create a {@link Accessor} for a field in this document.
     */
    Accessor createAccessor(String path);

    /**
     * @return Returns this document as a DOM tree. This method must be used with extra caution: it gives direct access
     * to the underlying representation of the {@link MutableDocument}.
     */
    org.w3c.dom.Document asDOM();

    /**
     * <p>
     * Change value of a field in the document.
     * </p>
     *
     * @param field    XPath to the field in the document.
     * @param newValue New value to be set.
     * @return A mutable document ready to be used (might be the same instance).
     */
    MutableDocument setField(String field, String newValue);

    /**
     * <p>
     * Delete a field in the document
     * </p>
     *
     * @param field XPath to the field in the document.
     * @return The document with the field deleted.
     */
    MutableDocument deleteField(String field);

    /**
     * <p>
     * Adds a field in the document.
     * </p>
     *
     * @param field XPath to the field in the document.
     * @param value Field value
     * @return The document with a new field added with the value passed as parameter.
     */
    MutableDocument addField(String field, String value);

    /**
     * @return Returns a document with created status.
     * @param content Content of the document at its creation time.
     */
    MutableDocument create(MutableDocument content);

    /**
     * Deletes the document. MDM supports two different kinds of deletes: LOGICAL and PHYSICAL.
     *
     * @param deleteType The type of delete to perform.
     * @return A {@link MutableDocument} after delete has been performed.
     * @see DeleteType
     */
    MutableDocument delete(DeleteType deleteType);

    /**
     * Recovers the document. MDM supports two different kinds of deletes: LOGICAL and PHYSICAL.
     *
     * @param deleteType Type of delete to recover.
     * @return A recovered document.
     * @throws IllegalStateException If the document wasn't deleted by the <code>deleteType</code> type.
     * @see DeleteType
     */
    MutableDocument recover(DeleteType deleteType);

    /**
     * @return Returns a unmodifiable document that might be result of action optimizations.
     */
    Document applyChanges();

}
