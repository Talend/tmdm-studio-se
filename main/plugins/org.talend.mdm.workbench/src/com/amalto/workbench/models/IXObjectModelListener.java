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
package com.amalto.workbench.models;

public interface IXObjectModelListener {

    public final static int ADD = 0;

    public final static int DELETE = 1;

    public final static int UPDATE = 2;

    public final static int SAVE = 3; // an exiting model object was changed

    public final static int NEED_REFRESH = 4; // the model need to resync with the server

    public void handleEvent(int type, TreeObject parent, TreeObject child);

}
