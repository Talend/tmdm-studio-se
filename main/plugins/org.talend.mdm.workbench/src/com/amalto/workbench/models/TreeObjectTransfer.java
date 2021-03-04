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

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class TreeObjectTransfer extends ByteArrayTransfer {

    private static Log log = LogFactory.getLog(TreeObjectTransfer.class);

    private static final String TYPENAME = "treeobjectdata";//$NON-NLS-1$

    private static final int TYPEID = registerType(TYPENAME);

    private static TreeObjectTransfer _instance = new TreeObjectTransfer();

    public static TreeObjectTransfer getInstance() {
        return _instance;
    }

    private TreeObject[] treeObjs;

    public void javaToNative(Object object, TransferData transferData) {
        if (!checkType(object) || !isSupportedType(transferData)) {
            DND.error(DND.ERROR_INVALID_DATA);
        }

        treeObjs = (TreeObject[]) object;
        ByteArrayOutputStream byteS = new ByteArrayOutputStream();
        DataOutputStream writeOut = new DataOutputStream(byteS);

        try {
            for (TreeObject treeData : treeObjs) {
                writeOut.writeInt(treeData.getType());
            }
            byte[] buffer = byteS.toByteArray();
            super.javaToNative(buffer, transferData);
            writeOut.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

    }

    public Object nativeToJava(TransferData transferData) {
        if (!isSupportedType(transferData))
            return null;

        byte[] buffer = (byte[]) super.nativeToJava(transferData);
        if (buffer == null)
            return null;
        if (treeObjs != null)
            return treeObjs;
        return transferData;
    }

    protected String[] getTypeNames() {
        return new String[] { TYPENAME };
    }

    protected int[] getTypeIds() {
        return new int[] { TYPEID };
    }

    boolean checkType(Object object) {
        return (object != null && object instanceof TreeObject[]);
    }

    protected boolean validate(Object object) {
        return checkType(object);
    }
}
