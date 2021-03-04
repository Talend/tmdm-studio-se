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
package org.talend.mdm.repository.ui.wizards.process.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.wizards.process.IProcessTypeComposite;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public abstract class AbstractProcessTypeComposite extends Group implements IProcessTypeComposite {

    /**
     * Create the composite.
     *
     * @param parent
     * @param style
     */
    public AbstractProcessTypeComposite(Composite parent, SelectionListener selectionListener) {
        super(parent, SWT.NONE);
        setText(Messages.AbstractProcessTypeComposite_selectProcessType);

    }

    @Override
    protected void checkSubclass() {
        // Disable the check that prevents subclassing of SWT components
    }

}
