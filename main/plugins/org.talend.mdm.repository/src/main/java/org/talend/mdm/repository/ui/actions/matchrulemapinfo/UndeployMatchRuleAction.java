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
package org.talend.mdm.repository.ui.actions.matchrulemapinfo;

import java.util.ArrayList;
import java.util.List;

import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.ui.actions.UndeployAction;

/**
 * created by HHB on 2013-9-13 Detailled comment
 *
 */
public class UndeployMatchRuleAction extends UndeployAction {

    public UndeployMatchRuleAction() {
        super(Messages.UndeployMatchRuleAction_label);
    }

    protected List<IRepositoryViewObject> getSelectedRepositoryViewObject() {
        new ArrayList<IRepositoryViewObject>();
        List<Object> selectedObjects = getSelectedObject();
        List<IRepositoryViewObject> viewObjs = MatchRuleMapInfoActionHelper.convertToMapInfoObject(selectedObjects);
        return viewObjs;
    }
}
