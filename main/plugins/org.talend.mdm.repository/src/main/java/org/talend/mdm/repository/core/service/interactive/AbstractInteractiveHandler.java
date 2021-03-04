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
package org.talend.mdm.repository.core.service.interactive;

import java.util.List;

import javax.xml.ws.WebServiceException;

import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.command.deploy.AbstractDeployCommand;
import org.talend.mdm.repository.core.service.IInteractiveHandler;
import org.talend.mdm.repository.core.service.RepositoryWebServiceAdapter;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerObjectItem;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.utils.Bean2EObjUtil;
import org.talend.mdm.repository.utils.UIUtil;

import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public abstract class AbstractInteractiveHandler implements IInteractiveHandler {

    protected TMDMService getService(MDMServerDef serverDef) throws XtentisException {
        boolean workInUI = UIUtil.isWorkInUI();
        return RepositoryWebServiceAdapter.getMDMService(serverDef, workInUI);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.talend.mdm.repository.core.service.IInteractiveHandler#convert(org.talend.core.model.properties.Item,
     * org.talend.mdm.repository.model.mdmserverobject.MDMServerObject)
     */
    @Override
    public Object convert(Item item, MDMServerObject serverObj) {
        Object wsObj = Bean2EObjUtil.getInstance().convertFromEObj2Bean(serverObj);
        return wsObj;
    }

    @Override
    public boolean doDeployWSObject(TMDMService service, Object wsObj) {
        return false;
        // do nothing
    }

    public boolean doRemove(TMDMService service, AbstractDeployCommand cmd) throws WebServiceException, XtentisException {
        return true;
        // do nothing
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.mdm.repository.core.service.IInteractiveHandler#deploy(com.amalto.workbench.webservices.XtentisPort,
     * org.talend.core.model.properties.Item, org.talend.mdm.repository.model.mdmserverobject.MDMServerObject)
     */
    @Override
    public boolean deploy(AbstractDeployCommand cmd) throws XtentisException {
        IRepositoryViewObject viewObj = cmd.getViewObject();
        Item item = viewObj.getProperty().getItem();
        MDMServerObject serverObject = ((MDMServerObjectItem) item).getMDMServerObject();
        Object wsObj = convert(item, serverObject);
        TMDMService service = getService(cmd.getServerDef());
        return doDeployWSObject(service, wsObj);
    }

    @Override
    public boolean remove(AbstractDeployCommand cmd) throws XtentisException {
        TMDMService service = getService(cmd.getServerDef());
        return doRemove(service, cmd);
    }

    @Override
    public void assertPropertyIsInited(Item item) {
    }

    @Override
    public List<IRepositoryViewObject> getAssociatedObjects(IRepositoryViewObject obj) {
        return null;
    }

    @Override
    public boolean isShownInResultDialog(IRepositoryViewObject viewObj) {
        return true;
    }

}
