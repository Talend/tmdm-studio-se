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
package com.amalto.workbench.widgets.xmlviewer.action;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextViewer;

public class TextViewerOperationHandler extends AbstractHandler {
	private int operationCode;
	private ITextViewer viewer;

	public TextViewerOperationHandler(ITextViewer viewer, int operationCode) {
		this.viewer = viewer;
		this.operationCode = operationCode;
	}

	public Object execute(ExecutionEvent arg0) throws ExecutionException {
		if (viewer != null && operationCode != -1)
			viewer.getTextOperationTarget().doOperation(operationCode);
		return null;
	}
}
