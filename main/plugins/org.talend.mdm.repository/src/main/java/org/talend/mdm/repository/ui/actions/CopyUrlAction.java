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

import java.util.List;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.AbstractRepositoryAction;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.WSResourceItem;
import org.talend.mdm.repository.model.mdmserverobject.WSResourceE;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;

public class CopyUrlAction extends AbstractRepositoryAction {

    public CopyUrlAction() {
        super(Messages.CopyUrlAction_CopyUrl);
        setImageDescriptor(ImageCache.getImage(EImage.COPY.getPath()));
    }

    @Override
    public String getGroupName() {
        return GROUP_SVN;
    }

    @Override
    protected void doRun() {
        multiCopy();
    }

    private void multiCopy() {
        StringBuilder result = new StringBuilder();

        List<Object> selectedObject = getSelectedObject();

        for (Object obj : selectedObject) {
            IRepositoryViewObject viewObject = (IRepositoryViewObject) obj;

            // picture file info
            Item item = viewObject.getProperty().getItem();
            WSResourceE wsItem = null;
            if (!(item instanceof WSResourceItem)) {
                continue;
            }
            wsItem = ((WSResourceItem) item).getResource();
            String catalog = wsItem.getImageCatalog();
            if (catalog == null) {
                continue;
            }
            String fileName = viewObject.getLabel() + '.' + wsItem.getFileExtension();
            // MDMServerDef thing
            MDMServerDef serverDef = RepositoryResourceUtil.getLastServerDef(viewObject);
            if (serverDef == null) {
                continue;
            }

            // all picture url string
            String contextPath = Util.getContextPath(serverDef.getPath());
            String uripre = serverDef.getProtocol() + serverDef.getHost() + ':' + serverDef.getPort() + contextPath;

            result.append(uripre);
            result.append("/imageserver/upload/" + catalog + '/' + fileName); //$NON-NLS-1$
            result.append('\n');
        }

        // copy url to clipboard
        if (result.length() != 0) {
            Clipboard cb = new Clipboard(Display.getCurrent());
            String textData = result.toString();
            TextTransfer transfer = TextTransfer.getInstance();
            cb.setContents(new Object[] { textData }, new Transfer[] { transfer });
            cb.dispose();
        }
    }
}
