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

package com.amalto.core.storage.datasource;

import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class DataSourceFactory {

    public static final String DB_DATASOURCES = "db.datasources"; //$NON-NLS-1$

    private static final DataSourceFactory INSTANCE = new DataSourceFactory();

    private static final Logger LOGGER = Logger.getLogger(DataSourceFactory.class);

    private static DocumentBuilderFactory factory;

    private static final XPath xPath = XPathFactory.newInstance().newXPath();

    private DataSourceFactory() {
        factory = DocumentBuilderFactory.newInstance();
    }

    public static DataSourceFactory getInstance() {
        return INSTANCE;
    }

    public boolean hasDataSource(String dataSourceName) {
        return hasDataSource(readDataSourcesConfiguration(), dataSourceName);
    }

    public boolean hasDataSource(InputStream configurationStream, String dataSourceName) {
        if (dataSourceName == null) {
            throw new IllegalArgumentException("Data source name can not be null.");
        }
        Map<String, DataSourceDefinition> dataSourceMap = readDocument(configurationStream);
        return dataSourceMap.get(dataSourceName) != null;
    }

    public DataSourceDefinition getDataSource(String dataSourceName, String container) {
        return getDataSource(readDataSourcesConfiguration(), dataSourceName, container);
    }

    public DataSourceDefinition getDataSource(InputStream configurationStream, String dataSourceName, String container) {
        if (dataSourceName == null) {
            throw new IllegalArgumentException("Data source name can not be null.");
        }
        if (container == null) {
            throw new IllegalArgumentException("Container name can not be null.");
        }
        Map<String, DataSourceDefinition> dataSourceMap = readDocument(configurationStream);
        DataSourceDefinition dataSource = dataSourceMap.get(dataSourceName);
        if (dataSource == null) {
            throw new IllegalArgumentException("Data source '" + dataSourceName + "' can not be found in configuration.");
        }
        // Additional post parsing (replace potential ${container} with container parameter value).
        replaceContainerName(container, dataSource.getMaster());
        if (dataSource.hasStaging()) {
            replaceContainerName(container, dataSource.getStaging());
        }
        return dataSource;
    }

    private static void replaceContainerName(String container, DataSource dataSource) {
        if (dataSource instanceof RDBMSDataSource) {
            RDBMSDataSource rdbmsDataSource = (RDBMSDataSource) dataSource;
            String connectionURL = rdbmsDataSource.getConnectionURL();
            String processedConnectionURL;
            if (((RDBMSDataSource) dataSource).getDialectName() == RDBMSDataSource.DataSourceDialect.POSTGRES) {
                // Postgres always creates lower case database name
                processedConnectionURL = connectionURL.replace("${container}", container.toLowerCase()); //$NON-NLS-1$
            } else {
                processedConnectionURL = connectionURL.replace("${container}", container); //$NON-NLS-1$
            }
            rdbmsDataSource.setConnectionURL(processedConnectionURL);
            String databaseName = rdbmsDataSource.getDatabaseName();
            String processedDatabaseName = databaseName.replace("${container}", container); //$NON-NLS-1$
            if (((RDBMSDataSource) dataSource).getDialectName() == RDBMSDataSource.DataSourceDialect.POSTGRES) {
                // Postgres always creates lower case database name
                processedDatabaseName = databaseName.toLowerCase();
            }
            rdbmsDataSource.setDatabaseName(processedDatabaseName);
        }
    }

    private static synchronized InputStream readDataSourcesConfiguration() {
        Properties configuration = MDMConfiguration.getConfiguration();
        String dataSourcesFileName = (String) configuration.get(DB_DATASOURCES);
        // DB_DATASOURCES property is mandatory to continue.
        if (dataSourcesFileName == null) {
            throw new IllegalStateException(DB_DATASOURCES + " is not defined in MDM configuration.");
        }

        InputStream configurationAsStream = null;

        // 1- Try from file (direct lookup)
        File file = new File(dataSourcesFileName);
        if (file.exists()) {
            LOGGER.info("Reading from datasource file at '" + file.getAbsolutePath() + "'.");
            try {
                configurationAsStream = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new IllegalStateException("Unexpected state (file exists but can't create a stream from it).", e);
            }
        }

        // 1- Try from file (lookup from user.dir)
        if (configurationAsStream == null) {
            file = new File(System.getProperty("user.dir") + "/bin/" + dataSourcesFileName); //$NON-NLS-1$ //$NON-NLS-2$
            LOGGER.info("Reading from datasource file at '" + file.getAbsolutePath() + "'.");
            if (file.exists()) {
                try {
                    configurationAsStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    throw new IllegalStateException("Unexpected state (file exists but can't create a stream from it).", e);
                }
            }
        }

        // 2- From class path
        if (configurationAsStream == null) {
            List<String> filePaths = Arrays.asList(dataSourcesFileName);
            Iterator<String> iterator = filePaths.iterator();

            String currentFilePath = StringUtils.EMPTY;
            while (configurationAsStream == null && iterator.hasNext()) {
                currentFilePath = iterator.next();
                configurationAsStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(currentFilePath);
            }
            if (configurationAsStream != null) {
                LOGGER.info("Reading from datasource file at '" + currentFilePath + "'.");
            }
        }

        // 3- error: configuration was not found
        if (configurationAsStream == null) {
            throw new IllegalStateException("Could not find datasources configuration file '" + dataSourcesFileName + "'.");
        }

        return configurationAsStream;

    }

    private static Map<String, DataSourceDefinition> readDocument(InputStream configurationAsStream) {
        Document document;
        try {
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            document = documentBuilder.parse(configurationAsStream);
        } catch (Exception e) {
            throw new RuntimeException("Exception occurred during data sources XML configuration parsing", e);
        }

        try {
            NodeList datasources = (NodeList) evaluate(document, xPath, "/datasources/datasource", XPathConstants.NODESET);
            Map<String, DataSourceDefinition> nameToDataSources = new HashMap<String, DataSourceDefinition>();
            for (int i = 0; i < datasources.getLength(); i++) {
                Node currentDataSourceElement = datasources.item(i);
                String name = (String) evaluate(currentDataSourceElement, xPath, "@name", XPathConstants.STRING); //$NON-NLS-1$
                DataSource master = getDataSourceConfiguration(currentDataSourceElement, name, "master");
                if (master == null) {
                    throw new IllegalArgumentException("Data source '" + name + "'does not declare a master data section");
                }
                DataSource staging = getDataSourceConfiguration(currentDataSourceElement, name, "staging");
                nameToDataSources.put(name, new DataSourceDefinition(name, master, staging));
            }
            return nameToDataSources;
        } catch (XPathExpressionException e) {
            throw new RuntimeException("Invalid data sources configuration.", e);
        }
    }

    private static DataSource getDataSourceConfiguration(Node document, String name, String path) throws XPathExpressionException {
        Node dataSource = (Node) evaluate(document, xPath, path, XPathConstants.NODE); //$NON-NLS-1$
        if (dataSource == null) {
            return null;
        }
        String type = (String) evaluate(dataSource, xPath, "type", XPathConstants.STRING); //$NON-NLS-1$
        if ("RDBMS".equals(type)) { //$NON-NLS-1$
            String dialectName = (String) evaluate(dataSource, xPath, "rdbms-configuration/dialect", XPathConstants.STRING); //$NON-NLS-1$
            String driverClassName = (String) evaluate(dataSource, xPath, "rdbms-configuration/connection-driver-class", XPathConstants.STRING); //$NON-NLS-1$
            String connectionURL = (String) evaluate(dataSource, xPath, "rdbms-configuration/connection-url", XPathConstants.STRING); //$NON-NLS-1$
            String userName = (String) evaluate(dataSource, xPath, "rdbms-configuration/connection-username", XPathConstants.STRING); //$NON-NLS-1$
            String password = (String) evaluate(dataSource, xPath, "rdbms-configuration/connection-password", XPathConstants.STRING); //$NON-NLS-1$
            String indexDirectory = (String) evaluate(dataSource, xPath, "rdbms-configuration/fulltext-index-directory", XPathConstants.STRING); //$NON-NLS-1$
            String cacheDirectory = (String) evaluate(dataSource, xPath, "rdbms-configuration/cache-directory", XPathConstants.STRING); //$NON-NLS-1$
            String initConnectionURL = (String) evaluate(dataSource, xPath, "rdbms-configuration/init/connection-url", XPathConstants.STRING); //$NON-NLS-1$
            String initUserName = (String) evaluate(dataSource, xPath, "rdbms-configuration/init/connection-username", XPathConstants.STRING); //$NON-NLS-1$
            String initPassword = (String) evaluate(dataSource, xPath, "rdbms-configuration/init/connection-password", XPathConstants.STRING); //$NON-NLS-1$
            String databaseName = (String) evaluate(dataSource, xPath, "rdbms-configuration/init/database-name", XPathConstants.STRING); //$NON-NLS-1$

            return new RDBMSDataSource(name,
                    dialectName,
                    driverClassName,
                    userName,
                    password,
                    indexDirectory,
                    cacheDirectory,
                    connectionURL,
                    databaseName,
                    initPassword,
                    initUserName,
                    initConnectionURL);
        } else {
            throw new NotImplementedException("No support for type '" + type + "'.");
        }
    }

    private static Object evaluate(Node node, XPath xPathParser, String expression, QName returnType) throws XPathExpressionException {
        XPathExpression result = xPathParser.compile(expression);
        return result.evaluate(node, returnType);
    }

}
