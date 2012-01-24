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

import java.io.Serializable;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class BreadCrumbModel implements Serializable {

    private String ids;

    private String concept;

    private String label;

    private String pkInfo;

    private boolean ifLink;

    public BreadCrumbModel() {

    }

    public BreadCrumbModel(String concept, String label, String ids, String pkInfo, boolean ifLink) {
        this.concept = concept;
        this.label = label;
        this.ids = ids;
        this.pkInfo = pkInfo;
        this.ifLink = ifLink;
    }

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getConcept() {
        return concept;
    }

    public void setConcept(String concept) {
        this.concept = concept;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPkInfo() {
        return pkInfo;
    }

    public void setPkInfo(String pkInfo) {
        this.pkInfo = pkInfo;
    }

    public boolean isIfLink() {
        return ifLink;
    }

    public void setIfLink(boolean ifLink) {
        this.ifLink = ifLink;
    }

}
