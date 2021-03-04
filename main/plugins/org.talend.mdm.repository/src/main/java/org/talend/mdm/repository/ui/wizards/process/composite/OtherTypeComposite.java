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

import com.amalto.workbench.utils.Util;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class OtherTypeComposite implements IProcessTypeComposite {

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.ui.wizards.process.IProcessTypeComposite#getCurrentProcessType()
     */
    public int getCurrentProcessType() {

        return TYPE_OTHER;
    }

    public boolean needShowSelectEntityBun() {
        return false;
    }

    public String getProcessPrefix() {
        return ""; //$NON-NLS-1$
    }

    public String getConfigWizardPageId() {
        if (Util.IsEnterPrise()) {
            // template job create page
            return "org.talend.mdm.repository.enterprise.ui.wizards.jobtemplate.CreateJobTemplateWizardPage"; //$NON-NLS-1$
        }
        return null;
    }

    public String getDesc() {
        return null;
    }
}
