// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.detailtabs.sections;

import java.util.Set;

import org.eclipse.swt.widgets.Composite;

import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;

public abstract class XpathSection extends XSDComponentSection {

	public String getDataModelName(){
		return super.getDataModelName();
	}
	
	public abstract Set<String> getEntities();
	
	public abstract IAllDataModelHolder getDataHolder();
	
	protected abstract ISubmittable getSubmittedObj();

	@Override
	protected void createControlsInSection(Composite compSectionClient) {
		// TODO Auto-generated method stub

	}

}
