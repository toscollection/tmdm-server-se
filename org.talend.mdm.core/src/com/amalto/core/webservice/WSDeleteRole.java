/*
 * Copyright (C) 2006-2017 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */
package com.amalto.core.webservice;

import javax.xml.bind.annotation.XmlType;

@XmlType(name="WSDeleteRole")
public class WSDeleteRole {
    protected com.amalto.core.webservice.WSRolePK wsRolePK;
    
    public WSDeleteRole() {
    }
    
    public WSDeleteRole(com.amalto.core.webservice.WSRolePK wsRolePK) {
        this.wsRolePK = wsRolePK;
    }
    
    public com.amalto.core.webservice.WSRolePK getWsRolePK() {
        return wsRolePK;
    }
    
    public void setWsRolePK(com.amalto.core.webservice.WSRolePK wsRolePK) {
        this.wsRolePK = wsRolePK;
    }
}
