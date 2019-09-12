// ============================================================================
//
// Copyright (C) 2006-2019 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDIdentityConstraintDefinition;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDXPathDefinition;

import com.amalto.workbench.editors.DataModelMainPage;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;
import com.amalto.workbench.utils.XSDUtil;
import com.amalto.workbench.utils.XtentisException;

public class XSDDeleteParticleAction extends UndoAction {

    private static Log log = LogFactory.getLog(XSDDeleteParticleAction.class);

    private XSDParticle xsdPartle = null;

    public XSDDeleteParticleAction(DataModelMainPage page) {
        super(page);
        setImageDescriptor(ImageCache.getImage(EImage.DELETE_OBJ.getPath()));
        setText(Messages.XSDDeleteParticleAction_DelElement);
        setToolTipText(Messages.XSDDeleteParticleAction_DelBusinessElement);
    }

    public void run(Object toDel) {
        if (!(toDel instanceof XSDParticle)) {
            return;
        }
        xsdPartle = (XSDParticle) toDel;
        run();
    }

    public IStatus doAction() {
        try {

            // xsdPartle is to support the multiple delete action on key press,
            // which each delete action on particle must be explicit passed a xsd particle to
            // delete
            XSDParticle particle = (XSDParticle) xsdPartle;
            if (particle == null) {
                ISelection selection = page.getTreeViewer().getSelection();
                particle = (XSDParticle) ((IStructuredSelection) selection).getFirstElement();
            }
            XSDTerm term = particle.getTerm();

            XSDElementDeclaration decl = null;
            if (term instanceof XSDElementDeclaration) {
                decl = (XSDElementDeclaration) particle.getTerm();
            }

            if (particle.getContainer() == null) {
                return Status.CANCEL_STATUS;
            }

            XSDIdentityConstraintDefinition identify = null;
            XSDXPathDefinition keyPath = null;
            List<Object> keyInfo = Util.getKeyInfo(decl);
            if (keyInfo != null && keyInfo.size() > 0) {
                identify = (XSDIdentityConstraintDefinition) keyInfo.get(0);
                keyPath = (XSDXPathDefinition) keyInfo.get(1);
                identify.getFields().remove(keyPath);
                if (identify.getFields().size() == 0) {
                    XSDElementDeclaration top = (XSDElementDeclaration) Util.getParent(decl);
                    top.getIdentityConstraintDefinitions().remove(identify);
                }
            }
            if (!(particle.getContainer() instanceof XSDModelGroup))
                throw new XtentisException(Messages.bind(Messages.XSDDeleteParticleAction_ExceptionInfo, particle.getContainer().getClass().getName()));

            XSDModelGroup group = (XSDModelGroup) particle.getContainer();
            group.getContents().remove(particle);

            String fieldName = decl.getName();
            XSDComplexTypeDefinition typedef = XSDUtil.getContainerTypeOfField(particle);

            XSDSchema schema = particle.getSchema();
            List<XSDElementDeclaration> concepts = schema.getElementDeclarations();
            for (XSDElementDeclaration concept : concepts) {
                if (hasBoundToConcept(typedef, concept)) {
                    XSDAnnotationsStructure annoStructure = new XSDAnnotationsStructure(concept);
                    Map<String, String> fieldCategoryMap = annoStructure.getFieldCategoryMap();
                    fieldCategoryMap.remove(fieldName);
                    annoStructure.setCategoryFields(fieldCategoryMap);
                    concept.updateElement();
                }
            }

            // if (term instanceof XSDElementDeclaration) {
            // //remove type definition is no more used and type is not built in
            // XSDTypeDefinition typeDef = decl.getTypeDefinition();
            // if ( (typeDef.getName()!=null) && //anonymous type
            // (!typeDef.getSchema().getSchemaForSchemaNamespace().equals(typeDef.getTargetNamespace()))
            // ){
            // if (Util.findElementsUsingType(group.getSchema(),typeDef.getTargetNamespace(),
            // typeDef.getName()).size()==0)
            // group.getSchema().getContents().remove(typeDef);
            // }
            // }

            xsdPartle = null;
            page.refresh();
            page.markDirty();

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(page.getSite().getShell(), Messages._Error,
                    Messages.bind(Messages.XSDDeleteParticleAction_ErrorMsg, e.getLocalizedMessage()));
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }

    /////////////

    private boolean hasBoundToConcept(XSDComplexTypeDefinition ctypeDef, XSDElementDeclaration concept) {
        XSDComplexTypeDefinition typeDefinition = (XSDComplexTypeDefinition) concept.getTypeDefinition();
        if (typeDefinition == ctypeDef) {
            return true;
        }

        if (!XSDUtil.isAnonymousType(ctypeDef) && !XSDUtil.isAnonymousType(typeDefinition)) {
            List<XSDComplexTypeDefinition> superComplexTypes = Util.getAllSuperComplexTypes(typeDefinition);
            if (superComplexTypes.contains(ctypeDef)) {
                return true;
            }
        }

        return false;
    }
    ////////////

    public void runWithEvent(Event event) {
        super.runWithEvent(event);
    }

    public void setXSDTODel(XSDParticle elem) {
        xsdPartle = elem;
    }
}
