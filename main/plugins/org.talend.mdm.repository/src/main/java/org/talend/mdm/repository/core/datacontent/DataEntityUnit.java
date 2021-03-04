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
package org.talend.mdm.repository.core.datacontent;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * created by HHB on 2012-10-8 Detailled comment
 *
 */
public class DataEntityUnit implements PropertyChangeListener {

    /**
     *
     */
    private static final String PROP_SELECTED = "selected"; //$NON-NLS-1$

    String entityName;

    long count;

    boolean selected;

    String dataModelName;

    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    public DataEntityUnit() {
        setSelected(true);
    };

    public DataEntityUnit(String entityName) {
        this();
        this.entityName = entityName;
    };

    /**
     * Getter for dataModelName.
     *
     * @return the dataModelName
     */
    public String getDataModelName() {
        return this.dataModelName;
    }

    /**
     * Sets the dataModelName.
     *
     * @param dataModelName the dataModelName to set
     */
    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName;
    }

    /**
     * Getter for selected.
     *
     * @return the selected
     */
    public boolean isSelected() {
        return this.selected;
    }

    /**
     * Sets the selected.
     *
     * @param selected the selected to set
     */
    public void setSelected(boolean selected) {
        propertyChangeSupport.firePropertyChange(PROP_SELECTED, this.isSelected(), this.selected = selected);
    }

    /**
     * Getter for entityName.
     *
     * @return the entityName
     */
    public String getEntityName() {
        return this.entityName;
    }

    /**
     * Sets the entityName.
     *
     * @param entityName the entityName to set
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Getter for count.
     *
     * @return the count
     */
    public long getCount() {
        return this.count;
    }

    /**
     * Sets the count.
     *
     * @param count the count to set
     */
    public void setCount(long count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return " Entity:" + entityName + " Selected:" + selected; //$NON-NLS-1$ //$NON-NLS-2$
    }

    /*
     * (non-Javadoc)
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent evt) {
        propertyChangeSupport.firePropertyChange(PROP_SELECTED, evt.getOldValue(), evt.getNewValue());

    }
}
