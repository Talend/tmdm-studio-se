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
package org.talend.mdm.repository.ui.actions.view;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.xsd.XSDElementDeclaration;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.ItemState;
import org.talend.core.model.properties.PropertiesFactory;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.impl.view.IViewNodeConstDef;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.model.mdmproperties.MdmpropertiesFactory;
import org.talend.mdm.repository.model.mdmproperties.WSViewItem;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSBooleanE;
import org.talend.mdm.repository.model.mdmserverobject.WSViewE;
import org.talend.mdm.repository.ui.actions.AbstractSimpleAddAction;
import org.talend.mdm.repository.ui.dialogs.ViewInputDialog2;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.mdm.repository.utils.RepositoryTransformUtil;
import org.talend.mdm.repository.utils.ValidateUtil;

/**
 * DOC class global comment. Detailled comment <br/>
 *
 */
public class NewViewAction extends AbstractSimpleAddAction implements IViewNodeConstDef {

    private XSDElementDeclaration decl;

    public XSDElementDeclaration getXSDElementDeclaration() {
        return this.decl;
    }

    public NewViewAction() {
        super();
    }

    @Override
    protected String getDialogTitle() {
        return Messages.NewViewAction_newView;
    }

    @Override
    protected void doRun() {
        updateParentItem();

        int type = getType();

        IWorkbenchPartSite site = commonViewer.getCommonNavigator().getSite();
        ViewInputDialog2 vid = new ViewInputDialog2(site, getShell(), getDialogTitle(), Messages.Common_inputName,
                getInputValidator(), false, type);
        vid.setBtnShow(true);
        vid.create();
        vid.getShell().setSize(new Point(500, 320));
        vid.setBlockOnOpen(true);
        if (vid.open() == Window.CANCEL) {
            return;
        }
        String key = vid.getEntityName();
        String filterName = vid.getFilterName();

        Item item = null;
        if (filterName == null) {
            item = createServerObject(key);
        } else {
            String filterPart = ""; //$NON-NLS-1$
            if (!filterName.isEmpty()) {
                filterPart = "#" + filterName; //$NON-NLS-1$
            }

            item = createServerObject(PREFIX_VIEW_UPPER + key + filterPart);
        }
        commonViewer.refresh(selectObj);
        commonViewer.expandToLevel(selectObj, 1);

        openEditor(item);
    }

    private int getType() {

        int type = TYPE_VIEW;

        IRepositoryViewObject repositoryViewObject = (IRepositoryViewObject) selectObj;

        ContainerItem containerItem = (ContainerItem) repositoryViewObject.getProperty().getItem();
        String path = containerItem.getState().getPath();
        if (path.isEmpty()) {
            type = TYPE_VIEW;
        } else if (path.startsWith(IPath.SEPARATOR + PATH_WEBFILTER)) {
            type = TYPE_WEBFILTER;
        } else if (path.startsWith(IPath.SEPARATOR + PATH_SEARCHFILTER)) {
            type = TYPE_SEARCHFILTER;
        }

        return type;
    }

    @Override
    protected Item createServerObject(String key) {

        WSViewItem item = MdmpropertiesFactory.eINSTANCE.createWSViewItem();

        ItemState itemState = PropertiesFactory.eINSTANCE.createItemState();
        item.setState(itemState);
        //
        WSViewE view = newView(key);
        item.setWsView(view);
        int viewType = RepositoryTransformUtil.getInstance().getViewType(key);
        if (parentItem != null) {
            String path = parentItem.getState().getPath();
            if (path.isEmpty()) {
                if (viewType == TYPE_WEBFILTER) {
                    path = IPath.SEPARATOR + PATH_WEBFILTER;
                } else {
                    path = IPath.SEPARATOR + PATH_SEARCHFILTER;
                }
            }
            item.getState().setPath(path);
        } else {
            if (viewType == TYPE_WEBFILTER) {
                item.getState().setPath(IPath.SEPARATOR + PATH_WEBFILTER);
            } else {
                item.getState().setPath(IPath.SEPARATOR + PATH_SEARCHFILTER);
            }
        }
        createItemAndSave(item, key);
        return item;
    }

    protected void createItemAndSave(WSViewItem item, String key) {
        RepositoryResourceUtil.createItem(item, key);
    }

    protected WSViewE newView(String key) {

        WSBooleanE wsBool = MdmserverobjectFactory.eINSTANCE.createWSBooleanE();
        wsBool.set_true(false);

        WSViewE view = MdmserverobjectFactory.eINSTANCE.createWSViewE();
        view.setName(key);
        view.setDescription(""); //$NON-NLS-1$
        view.setTransformerPK(null);
        view.setIsTransformerActive(wsBool);

        return view;
    }

    public void setXSDElementDeclaration(XSDElementDeclaration decl) {
        this.decl = decl;

    }

    private IInputValidator getInputValidator() {
        return new IInputValidator() {

            public String isValid(String newText) {
                if (newText == null || newText.trim().length() == 0) {
                    return Messages.Common_nameCanNotBeEmpty;
                }
                if (!ValidateUtil.matchViewProcessRegex(newText)) {
                    return Messages.Common_nameInvalid;
                }
                if (RepositoryResourceUtil.isExistByName(parentItem.getRepObjType(), newText.trim())) {
                    return Messages.Common_nameIsUsed;
                }
                return null;
            };
        };
    }

    public void createNewView(String viewName) {
        RepositoryResourceUtil.removeViewObjectPhysically(IServerObjectRepositoryType.TYPE_VIEW, viewName, null, null);
        createServerObject(viewName);
        refreshRepositoryRoot(IServerObjectRepositoryType.TYPE_VIEW);
        refreshRepositoryRoot(IServerObjectRepositoryType.TYPE_ROLE);
    }
}
