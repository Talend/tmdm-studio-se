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
package org.talend.mdm.repository.core.dnd;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.navigator.CommonDragAdapterAssistant;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class RepositoryDragAssistant extends CommonDragAdapterAssistant {

    Transfer[] transfers = new Transfer[] { LocalSelectionTransfer.getTransfer() };

    @Override
    public Transfer[] getSupportedTransferTypes() {
        return transfers;
    }


    public boolean setDragData(DragSourceEvent anEvent, IStructuredSelection aSelection) {

        return false;
    }

}
