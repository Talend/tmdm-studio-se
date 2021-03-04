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

public class KeyValue {

    public String key;

    public String value;

    public KeyValue(String key, String value) {
        this.key = key;
        this.value = value;
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof KeyValue)) {
            return false;
        }
        KeyValue other=(KeyValue) obj;
        if (key != null && value != null) {
            return key.equals(other.key) && value.equals(other.value);
        }
        if (key != null && value == null) {
            if (other.value != null) {
                return false;
            }
            return key.equals(other.key);
        }

        if (key == null && value != null) {
            if (other.key != null) {
                return false;
            }
            return value.equals(other.value);
        }

        return other.key == null && other.value == null;
    }

    @Override
    public String toString() {
      return "key["+key+"] value["+value+"]";   //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
    }
}
