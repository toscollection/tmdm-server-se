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

package com.amalto.core.storage.hibernate;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.ObjectDataRecordReader;
import org.hibernate.Session;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

class IdQueryHandler extends AbstractQueryHandler {

    private Object object;

    public IdQueryHandler(Storage storage,
                          MappingRepository mappingMetadataRepository,
                          StorageClassLoader storageClassLoader,
                          Session session,
                          Select select,
                          List<TypedExpression> selectedFields,
                          Set<EndOfResultsCallback> callbacks) {
        super(storage, mappingMetadataRepository, storageClassLoader, session, select, selectedFields, callbacks);
    }

    @Override
    public StorageResults visit(Select select) {
        if (select.getCondition() == null) {
            throw new IllegalArgumentException("Select clause is expecting a condition.");
        }

        select.getCondition().accept(this);

        ComplexTypeMetadata mainType = select.getTypes().get(0);
        String mainTypeName = mainType.getName();
        String className = ClassCreator.PACKAGE_PREFIX + mainTypeName;

        Wrapper loadedObject = (Wrapper) session.get(className, (Serializable) object);

        if (loadedObject == null) {
            CloseableIterator<DataRecord> iterator = new CloseableIterator<DataRecord>() {
                public boolean hasNext() {
                    return false;
                }

                public DataRecord next() {
                    throw new UnsupportedOperationException();
                }

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public void close() throws IOException {
                    // Nothing to do.
                }
            };
            return new HibernateStorageResults(storage, select, iterator) {
                @Override
                public int getCount() {
                    return 0;
                }
            };
        } else {
            Iterator objectIterator = Collections.singleton(loadedObject).iterator();
            CloseableIterator<DataRecord> iterator;
            if (!select.isProjection()) {
                iterator = new ListIterator(mappingMetadataRepository, storageClassLoader, objectIterator, callbacks);
            } else {
                iterator = new ListIterator(mappingMetadataRepository, storageClassLoader, objectIterator, callbacks) {
                    @Override
                    public DataRecord next() {
                        DataRecord next = super.next();
                        DataRecord nextRecord = new DataRecord(next.getType(), next.getRecordMetadata());
                        for (TypedExpression selectedField : selectedFields) {
                            FieldMetadata field = selectedField.accept(new VisitorAdapter<FieldMetadata>() {
                                @Override
                                public FieldMetadata visit(Field field) {
                                    return field.getFieldMetadata();
                                }

                                @Override
                                public FieldMetadata visit(Alias alias) {
                                    return alias.getTypedExpression().accept(this);
                                }
                            });
                            if (field != null) {
                                nextRecord.set(field, next.get(field));
                            }
                        }
                        return nextRecord;
                    }
                };
            }
            return new HibernateStorageResults(storage, select, iterator) {
                @Override
                public int getCount() {
                    return 1;
                }
            };
        }
    }


    @Override
    public StorageResults visit(Compare condition) {
        object = condition.getRight().accept(VALUE_ADAPTER);
        return null;
    }

    private class DataRecordIterator extends CloseableIterator<DataRecord> {

        private final TypeMapping mainType;

        private final Wrapper loadedObject;

        private boolean hasRead;

        public DataRecordIterator(TypeMapping mainType, Wrapper loadedObject) {
            this.mainType = mainType;
            this.loadedObject = loadedObject;
        }

        public boolean hasNext() {
            return !hasRead;
        }

        public DataRecord next() {
            try {
                ObjectDataRecordReader reader = new ObjectDataRecordReader();
                return reader.read(mainType, loadedObject);
            } finally {
                hasRead = true;
            }
        }

        public void remove() {
        }

        public void close() throws IOException {
            hasRead = true;
        }
    }
}
