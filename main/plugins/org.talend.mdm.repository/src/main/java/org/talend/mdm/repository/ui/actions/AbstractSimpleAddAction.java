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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.core.service.ContainerCacheService;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.ValidateUtil;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public abstract class AbstractSimpleAddAction extends AbstractRepositoryAction {

    protected static Logger log = Logger.getLogger(AbstractSimpleAddAction.class);

    /**
     * DOC hbhong AddMenu constructor comment.
     *
     * @param text
     */
    public AbstractSimpleAddAction() {
        super("New"); //$NON-NLS-1$
        setImageDescriptor(ImageCache.getImage(EImage.ADD_OBJ.getPath()));
    }

    protected ContainerItem parentItem;

    protected Object selectObj;

    protected abstract String getDialogTitle();

    @Override
    protected void doRun() {
        updateParentItem();

        String key = getInputName();
        if (key != null) {
            Item item = createServerObject(key);
            getCommonViewer().refresh(selectObj);
            getCommonViewer().expandToLevel(selectObj, 1);
            if (runOpenActionAfterCreation(item)) {
                openEditor(item);
            }
        }

    }

    protected String getInputName() {
        InputDialog dlg = new InputDialog(getShell(), getDialogTitle(), Messages.Common_inputName, null, new IInputValidator() {

            public String isValid(String newText) {
                if (newText == null || newText.trim().length() == 0) {
                    return Messages.Common_nameCanNotBeEmpty;
                }
                if (!ValidateUtil.matchCommonRegex(newText)) {
                    return Messages.Common_nameInvalid;
                }
                if (RepositoryResourceUtil.isExistByName(parentItem.getRepObjType(), newText.trim())) {
                    return Messages.Common_nameIsUsed;
                }
                return null;
            };
        }) {

            @Override
            protected Control createDialogArea(Composite parent) {

                Control area = super.createDialogArea(parent);
                extendDialogArea(area);
                return area;

            }

        };
        dlg.setBlockOnOpen(true);
        if (dlg.open() == Window.CANCEL) {
            return null;
        }

        String key = dlg.getValue();
        return key;
    }

    protected void extendDialogArea(Control area) {

    }

    protected void updateParentItem() {
        parentItem = null;
        if (!getSelectedObject().isEmpty()) {
            selectObj = getSelectedObject().get(0);
            if (selectObj instanceof IRepositoryViewObject) {
                Item pItem = ((IRepositoryViewObject) selectObj).getProperty().getItem();
                if (pItem instanceof ContainerItem) {
                    parentItem = (ContainerItem) pItem;
                }
            }
        }
    }

    public void setParentItem(ContainerItem parentItem) {
        this.parentItem = parentItem;
    }

    protected boolean runOpenActionAfterCreation(Item item) {
        return true;
    }

    protected void openEditor(Item item) {
        if (item == null) {
            return;
        }
        IRepositoryViewObject viewObject = ContainerCacheService.get(item.getProperty());
        OpenObjectAction action = new OpenObjectAction();
        List<Object> selObjects = new ArrayList<Object>();
        selObjects.add(viewObject);
        action.setSelObjects(selObjects);
        action.run();
    }

    protected abstract Item createServerObject(String key);

    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        if (getSelectedObject().size() > 1) {
            return false;
        }

        return super.isVisible(viewObj);
    }
}
