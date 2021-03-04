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
package org.talend.mdm.repository.ui.wizards.process;

import org.talend.mdm.repository.core.impl.transformerV2.ITransformerV2NodeConsDef;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public interface IProcessTypeComposite extends ITransformerV2NodeConsDef {

    public int getCurrentProcessType();

    public boolean needShowSelectEntityBun();

    public String getProcessPrefix();

    public String getConfigWizardPageId();

    public String getDesc();
}
