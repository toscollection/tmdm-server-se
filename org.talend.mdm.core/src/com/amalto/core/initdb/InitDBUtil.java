package com.amalto.core.initdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.objects.configurationinfo.localutil.ConfigurationHelper;

/**
 * Create system init data cluster / data model. etc
 * Current only support head universe
 */
public class InitDBUtil {

    private static final Logger logger = Logger.getLogger(InitDBUtil.class);

    private static final Map<String, List<String>> initDB = new HashMap<String, List<String>>();

    private static final Map<String, List<String>> initExtensionDB = new HashMap<String, List<String>>();

    private static final String INIT_DB_CONFIG = "/com/amalto/core/initdb/initdb.xml"; //$NON-NLS-1$

    private static final String INIT_DB_EXTENSION_CONFIG = "/com/amalto/core/initdb/initdb-extension.xml"; //$NON-NLS-1$

    private static boolean useExtension = false;

    public static void init() {
        InputStream dbIn = null;
        InputStream edbIn = null;
        try {
            dbIn = InitDBUtil.class.getResourceAsStream(INIT_DB_CONFIG);
            edbIn = InitDBUtil.class.getResourceAsStream(INIT_DB_EXTENSION_CONFIG);
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            parseInitMap(dbIn, builder, initDB);
            if (edbIn != null) {
                useExtension = true;
                logger.info("Use Extension..."); //$NON-NLS-1$
                parseInitMap(edbIn, builder, initExtensionDB);
            }
        } catch (Exception e) {
            logger.error(e.getCause());
        } finally {
            if (dbIn != null) try {
                dbIn.close();
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not close stream.", e);
                }
            }
            if (edbIn != null) try {
                edbIn.close();
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not close stream.", e);
                }
            }
        }
    }

    private static void parseInitMap(InputStream in, DocumentBuilder builder, Map<String, List<String>> initMap) throws Exception {
        Document doc = builder.parse(in);
        NodeList nodelist = doc.getElementsByTagName("item"); //$NON-NLS-1$
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            NodeList list = node.getChildNodes();
            String name = null;
            for (int j = 0; j < list.getLength(); j++) {
                Node n = list.item(j);
                if (n instanceof Element) {
                    if ("name".equals(n.getNodeName())) { //$NON-NLS-1$
                        name = n.getTextContent();
                        if (initMap.get(name) == null) {
                            initMap.put(name, new ArrayList<String>());
                        }
                    }
                    if ("list".equals(n.getNodeName())) { //$NON-NLS-1$
                        if (n.getTextContent() == null || n.getTextContent().trim().length() == 0) {
                            continue;
                        }
                        List<String> lists = initMap.get(name);
                        String[] arr = n.getTextContent().split(";"); //$NON-NLS-1$
                        lists.addAll(Arrays.asList(arr));
                    }
                }
            }
        }
    }

    /**
     * init db
     */
    public static void initDB() {
        updateDB("/com/amalto/core/initdb/data", initDB); //$NON-NLS-1$
        if (useExtension) {
            updateDB("/com/amalto/core/initdb/extensiondata", initExtensionDB); //$NON-NLS-1$
            //init db extension job
            InitDbExtJobRepository.getInstance().execute();
        }
    }

    private static void updateDB(String resourcePath, Map<String, List<String>> initdb) {
        for (Entry<String, List<String>> entry : initdb.entrySet()) {
            String dataCluster = entry.getKey();
            try {
                ConfigurationHelper.createCluster(null, dataCluster);//slow but more reliable
            } catch (Exception e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not create cluster.", e);
                }
            }
            List<String> list = entry.getValue();
            // create items
            Iterator<String> iterator = list.iterator();
            while(iterator.hasNext()) {
                String item = iterator.next();
                try {
                    InputStream in = InitDBUtil.class.getResourceAsStream(resourcePath + "/" + item); //$NON-NLS-1$
                    String xmlString = getString(in);
                    String uniqueID = item;
                    int pos = item.lastIndexOf('/');
                    if (pos != -1) {
                        uniqueID = item.substring(pos + 1);
                    }
                    uniqueID = uniqueID.replaceAll("\\+", " "); //$NON-NLS-1$ //$NON-NLS-2$
                    if (Boolean.valueOf((String) MDMConfiguration.getConfiguration().get("cluster_override"))) { //$NON-NLS-1$
                        ConfigurationHelper.deleteDocument(null, dataCluster, uniqueID);
                    }
                    ConfigurationHelper.putDocument(dataCluster, xmlString, uniqueID);
                } catch (Exception e) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not delete document.", e);
                    }
                } finally {
                    iterator.remove();
                }
            }
        }
    }

    public static String getString(InputStream in) {
        if (in == null) {
            return StringUtils.EMPTY;
        }
        StringBuilder result = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        try {
            String line = reader.readLine();
            while (line != null) {
                result.append(line).append("\n");
                line = reader.readLine();
            }
        } catch (Exception e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Could not read from stream.", e);
            }
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Could not close stream.", e);
                }
            }
        }
        return result.toString();
    }
}
