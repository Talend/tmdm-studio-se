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
package com.amalto.workbench.detailtabs.sections.model.element;

import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.detailtabs.sections.handlers.CommitHandler;
import com.amalto.workbench.detailtabs.sections.handlers.ElementWrapperCommitHandler;
import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.utils.Util;

public class ElementWrapper implements ISubmittable {

    private XSDParticle sourceElement;

    private String newName = "";//$NON-NLS-1$

    private String newReference = "";//$NON-NLS-1$

    private int newMinOcur = 1;

    private int newMaxOcur = 1;

    private boolean autoExpand;

    public ElementWrapper(XSDParticle sourceElement, String newName, String newReference, int newMinOcur, int newMaxOcur, boolean autoExpand) {

        this.sourceElement = sourceElement;
        this.newName = newName;
        this.newReference = newReference;
        this.newMinOcur = newMinOcur;
        this.newMaxOcur = newMaxOcur;
        this.autoExpand=autoExpand;
    }

    public XSDParticle getSourceElement() {
        return sourceElement;
    }

    public void setSourceElement(XSDParticle element) {
        this.sourceElement = element;
    }

    public String getNewName() {

        if (hasNewReference())
            return "";//$NON-NLS-1$

        return newName;
    }

    public void setNewName(String newName) {

        this.newName = newName;

        if (newName == null)
            this.newName = "";//$NON-NLS-1$

    }

    public String getNewReference() {
        return newReference;
    }

    public void setNewReference(String newReference) {

        this.newReference = newReference;

        if (newReference == null)
            this.newReference = "";//$NON-NLS-1$

    }
    public boolean isAutoExpand(){
    	return autoExpand;
    }
    public int getNewMinOcur() {
        return newMinOcur;
    }

    public void setNewMinOcur(int newMinOcur) {
        this.newMinOcur = newMinOcur;
    }

    public int getNewMaxOcur() {
        return newMaxOcur;
    }

    public void setNewMaxOcur(int newMaxOcur) {
        this.newMaxOcur = newMaxOcur;
    }

    public String getSourceName() {
        return Util.getParticleName(sourceElement);
    }

    public String getSourceReference() {
        return Util.getParticleReferenceName(sourceElement);
    }

    public XSDElementDeclaration getSourceXSDContent() {
        return (XSDElementDeclaration) sourceElement.getContent();
    }

    public int getSourceMinCardinality() {
        return sourceElement.getMinOccurs();
    }

    public int getSourceMaxCardinality() {
        return sourceElement.getMaxOccurs();
    }

    public CommitHandler<ElementWrapper> createCommitHandler() {
        return new ElementWrapperCommitHandler(this);
    }

    public boolean hasNewReference() {
        return !"".equals(getNewReference());//$NON-NLS-1$
    }

    public XSDSchema getSchema() {
        return sourceElement.getSchema();
    }
}
