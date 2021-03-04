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

import org.talend.mdm.repository.ui.wizards.process.IProcessTypeComposite;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class SmartviewProcessTypeComposite implements IProcessTypeComposite {

    public int getCurrentProcessType() {

        return TYPE_SMARTVIEW;
    }

    public boolean needShowSelectEntityBun() {
        return true;
    }

    public String getProcessPrefix() {
        return "Smart_view_"; //$NON-NLS-1$
    }

    public String getConfigWizardPageId() {

        return null;
    }

    public String getDesc() {
        return null;
    }
}
