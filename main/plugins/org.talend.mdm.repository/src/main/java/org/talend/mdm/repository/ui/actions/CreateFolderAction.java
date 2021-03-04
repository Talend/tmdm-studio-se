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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.models.FolderRepositoryObject;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class CreateFolderAction extends AbstractRepositoryAction {

    /**
     * DOC hbhong CreateFolderAction constructor comment.
     *
     * @param text
     */
    public CreateFolderAction() {
        super(Messages.CreateFolderAction_createCategory);
        setImageDescriptor(ImageCache.getImage(EImage.COMPRESSED_FOLDER_OBJ.getPath()));
    }

    @Override
    public String getGroupName() {
        return GROUP_EDIT;
    }

    @Override
    protected void doRun() {
        Object object = getSelectedObject().get(0);
        if (object != null && object instanceof FolderRepositoryObject) {
            final FolderRepositoryObject containerViewObject = (FolderRepositoryObject) object;
            InputDialog dlg = new InputDialog(getShell(), Messages.CreateFolderAction_newCategory,
                    Messages.CreateFolderAction_inputCategoryName, null, new IInputValidator() {

                        @Override
                        public String isValid(String newText) {
                            if ((newText == null) || "".equals(newText)) { //$NON-NLS-1$
                                return Messages.Common_nameCanNotBeEmpty;
                            }
                            if(newText.equalsIgnoreCase("System")) { //$NON-NLS-1$
                                return Messages.cannotCreateSystemCategory;
                            }
                            Pattern p = Pattern.compile("([\\s*|\\W*]+)");//$NON-NLS-1$
                            Matcher m = p.matcher(newText);
                            if (m.find()) {
                                m.group(1);
                                return Messages.Common_nameInvalid;
                            }
                            if (RepositoryResourceUtil.isDeletedFolder(containerViewObject.getRepositoryObjectType(),
                                    containerViewObject, newText)) {
                                return Messages.Common_nameIsUsed;
                            }
                            for (IRepositoryViewObject viewObj : containerViewObject.getChildren()) {
                                Item item = viewObj.getProperty().getItem();
                                if (item instanceof ContainerItem) {
                                    if (((ContainerItem) item).getLabel().equals(newText)) {
                                        return Messages.CreateFolderAction_nameIsUsed;
                                    }
                                }
                            }
                            return null;
                        };
                    });
            dlg.setBlockOnOpen(true);
            if (dlg.open() == Window.CANCEL) {
                return;
            }
            String categoryName = dlg.getValue();
            //
            if (categoryName != null) {
                IRepositoryViewObject folderViewObject = RepositoryResourceUtil.createFolderViewObject(
                        containerViewObject.getRepositoryObjectType(), categoryName, containerViewObject.getProperty().getItem(),
                        false);
                containerViewObject.getChildren().add(folderViewObject);
                commonViewer.refresh(containerViewObject);
                commonViewer.expandToLevel(containerViewObject, 1);

            }
        }
    }

    @Override
    public boolean isVisible(IRepositoryViewObject viewObj) {
        if (getSelectedObject().size() > 1) {
            return false;
        }

        return super.isVisible(viewObj);
    }
}
