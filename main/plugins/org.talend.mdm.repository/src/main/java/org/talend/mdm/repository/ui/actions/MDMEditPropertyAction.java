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
package org.talend.mdm.repository.ui.actions;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.wizards.MdmPropertiesWizard;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * DOC jsxie class global comment. Detailled comment
 */
public class MDMEditPropertyAction extends AbstractRepositoryAction {

    protected IRepositoryViewObject viewObject;

    public MDMEditPropertyAction() {
        super(Messages.EditPropertiesAction_action_title);
        setImageDescriptor(ImageCache.getImage(EImage.EDIT_PROPERTY.getPath()));
    }

    @Override
    public String getGroupName() {
        return GROUP_COMMON;
    }

    @Override
    protected boolean needValidateLockedObject() {
        return true;
    }

    private boolean isCanceled = true;

    @Override
    protected void doRun() {
        Object obj = getSelectedObject().get(0);
        if (obj instanceof IRepositoryViewObject) {
            viewObject = (IRepositoryViewObject) obj;
            Wizard wizard = getEditWizard(viewObject);

            WizardDialog dlg = new WizardDialog(Display.getCurrent().getActiveShell(), wizard);
            if (dlg.open() == Window.OK) {
                isCanceled = false;
                commonViewer.refresh(obj);
            } else {
                isCanceled = true;
            }

        }

    }

    protected Wizard getEditWizard(IRepositoryViewObject viewObject) {
        return new MdmPropertiesWizard(viewObject, new Path(viewObject.getPath()), false);
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        if (getSelectedObject().size() == 1) {
            if (RepositoryResourceUtil.isSystemViewObject(viewObj)) {
                return false;
            }
            return true;
        }
        return false;
    }

    protected boolean isCanceled() {
        return this.isCanceled;
    }
}
