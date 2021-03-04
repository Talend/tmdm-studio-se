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
package org.talend.mdm.repository.ui.starting.actions;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.navigator.CommonViewer;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;
import org.talend.mdm.repository.ui.actions.datamodel.NewDataModelAction;
import org.talend.mdm.repository.ui.navigator.MDMRepositoryView;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

public class AddDataModelLinkAction extends AbstractShowViewAction {

    private static Logger log = Logger.getLogger(AddDataModelLinkAction.class);

    protected void doRun(IIntroSite site, Properties params) {
        try {
            MDMRepositoryView findView = showRepositoryView();

            CommonViewer commonViewer = findView.getCommonViewer();

            IRepositoryViewObject[] categoryViewObjects = RepositoryResourceUtil
                    .getCategoryViewObjects(IServerObjectRepositoryType.TYPE_DATAMODEL);
            if (categoryViewObjects.length > 0) {
                NewDataModelAction addAction = new NewDataModelAction();
                addAction.initCommonViewer(commonViewer);
                addAction.setParentItem((ContainerItem) categoryViewObjects[0].getProperty().getItem());
                addAction.run();
            }
        } catch (PartInitException e) {
            log.error(e.getMessage(), e);
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.ui.starting.actions.AbstractShowViewAction#getReadOnlyMessage()
     */
    @Override
    protected String getReadOnlyMessage() {
        return Messages.AddDataModelLinkAction_authorityMessage;
    }

}
