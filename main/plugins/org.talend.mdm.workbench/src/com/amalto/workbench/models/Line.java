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

import java.util.ArrayList;
import java.util.List;

import com.amalto.workbench.widgets.ComplexTableViewerColumn;

/**
 * The instance of complexTableViewer
 *
 * @author aiming
 *
 */
public class Line implements Cloneable {

    /**
     * key is the TableColumn name, value is the column's value
     */
    public List<KeyValue> keyValues = new ArrayList<KeyValue>();

    private List<String> keys;

    public Line(ComplexTableViewerColumn[] keys, String[] values) {
        for (int i = 0; i < keys.length && i < values.length; i++) {
            keyValues.add(new KeyValue(keys[i].getName(), values[i]));
        }
    }

    public Line(List<KeyValue> keyValues) {
        this.keyValues = keyValues;
    }

    public boolean containsKey(String key) {
        initKeys();

        return keys.contains(key);
    }

    public int indexOf(String key) {
        initKeys();

        return keys.indexOf(key);
    }

    public String getValue(String key) {
        int index = indexOf(key);
        if (index != -1) {
            return keyValues.get(index).value;
        }

        return null;
    }

    private void initKeys() {
        if (keys == null) {
            keys = new ArrayList<String>();
            for (KeyValue keyvalue : keyValues) {
                keys.add(keyvalue.key);
            }
        }
    }

    @Override
    public Line clone() {
        List<KeyValue> copyKeyValues = new ArrayList<KeyValue>();
        for (KeyValue kv : keyValues) {
            KeyValue kkvv = new KeyValue(kv.key, kv.value);
            copyKeyValues.add(kkvv);
        }
        return new Line(copyKeyValues);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Line)) {
            return false;
        }
        Line other = (Line) obj;
        if (keyValues.size() != other.keyValues.size()) {
            return false;
        }
        for (int i = 0; i < keyValues.size(); i++) {
            if (!keyValues.get(i).equals(other.keyValues.get(i))) {
                return false;
            }
        }
        return true;
    }
}
