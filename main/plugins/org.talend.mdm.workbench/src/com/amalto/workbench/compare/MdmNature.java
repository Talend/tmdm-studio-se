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
package com.amalto.workbench.compare;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

import com.amalto.workbench.MDMWorbenchPlugin;

public class MdmNature implements IProjectNature {

    public static final String ID = MDMWorbenchPlugin.ID + ".mdmnature";//$NON-NLS-1$

    public void configure() throws CoreException {


    }

    public void deconfigure() throws CoreException {


    }

    public IProject getProject() {

        return null;
    }

    public void setProject(IProject project) {

    }

}
