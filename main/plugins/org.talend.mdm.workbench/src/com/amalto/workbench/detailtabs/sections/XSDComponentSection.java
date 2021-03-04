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
package com.amalto.workbench.detailtabs.sections;

import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.utils.XSDUtil;

public abstract class XSDComponentSection extends CommitBarListenerSection<XSDComponent> {

    protected XSDComponent curXSDComponent;

    @Override
    public XSDComponent getEditedObj() {
        return curXSDComponent;
    }

    @Override
    protected void initUIContents(XSDComponent editedObj) {
        curXSDComponent = editedObj;
    }

    protected String getEntityName(){
    	String entity= getEntity(curXSDComponent);
    	return entity;
    }

    private String getEntity(XSDConcreteComponent com){
    	if(com==null) return null;
    	if(com.getContainer() instanceof XSDSchema && com instanceof XSDElementDeclaration){
    		return ((XSDElementDeclaration)com).getName();
    	}else{
    		return getEntity(com.getContainer());
    	}
    }

    protected boolean isComplexTypeElement(XSDComponent component) {
        if (component instanceof XSDParticle) {
            return !XSDUtil.isSimpleTypeElement((XSDParticle) component);
        }

        return false;
    }
}
