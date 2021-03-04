// ============================================================================
//
// Copyright (C) 2006-2021 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MDMServerDef {

    /**
     *
     */
    private static final String DEFAULT_PROTOCOL = "http://"; //$NON-NLS-1$

    public static final String DEFAULT_PATH = "/talendmdm/services/soap";//$NON-NLS-1$

    private String name = "";//$NON-NLS-1$

    private String host = "localhost";//$NON-NLS-1$

    private String port = "8180";//$NON-NLS-1$

    private String user = "";//$NON-NLS-1$

    private String passwd = "";//$NON-NLS-1$

    private String protocol = DEFAULT_PROTOCOL;

    /**
     * Getter for protocol.
     *
     * @return the protocol
     */
    public String getProtocol() {
        return this.protocol;
    }

    /**
     * Sets the protocol.
     *
     * @param protocol the protocol to set
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    private String path = DEFAULT_PATH;

    public MDMServerDef(String name, String protocol, String host, String port, String path, String user, String passwd) {
        this.name = name;
        this.host = host;
        this.port = port;
        this.path = path;
        this.user = user;
        this.passwd = passwd;
        if (protocol == null) {
            this.protocol = DEFAULT_PROTOCOL;
        } else {
            this.protocol = protocol;
        }

    }

    public MDMServerDef() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUrl() {
        StringBuilder sb = new StringBuilder(protocol);

        sb.append(host);
        sb.append(":");//$NON-NLS-1$
        sb.append(port);
        sb.append(path);

        return sb.toString();
    }

    public static MDMServerDef parse(String protocol, String url, String user, String passwd, String name) {
        String patternUrl = "^" + protocol + "(.+):(\\d+)(/.*)";//$NON-NLS-1$ //$NON-NLS-2$
        Matcher m = Pattern.compile(patternUrl).matcher(url);

        if (!m.find()) {
            return null;
        }

        String host = m.group(1);
        String port = m.group(2);
        String path = m.group(3);

        return new MDMServerDef(name, protocol, host, port, path, user, passwd);
    }

    public static MDMServerDef parse(String url, String user, String passwd, String name) {
        return parse(DEFAULT_PROTOCOL, url, user, passwd, name);
    }
}
