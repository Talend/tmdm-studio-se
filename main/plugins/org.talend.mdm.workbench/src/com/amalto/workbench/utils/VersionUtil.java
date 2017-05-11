// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.workbench.utils;

import org.talend.mdm.webservice.WSDataClusterPK;
import org.talend.mdm.webservice.WSDataModelPK;
import org.talend.mdm.webservice.WSMenuPK;
import org.talend.mdm.webservice.WSRolePK;
import org.talend.mdm.webservice.WSRoutingRulePK;
import org.talend.mdm.webservice.WSStoredProcedurePK;
import org.talend.mdm.webservice.WSTransformerV2PK;
import org.talend.mdm.webservice.WSViewPK;

import com.amalto.workbench.models.TreeObject;

/**
 * 
 * @author hshu
 * 
 */
public class VersionUtil {

    public static String determineTypeByTreeObjectType(int xobjectType) {
        String type = null;
        switch (xobjectType) {
        case TreeObject._SERVER_:
            type = "Root";//$NON-NLS-1$
            break;
        /*
         * case TreeObject.INBOUND_ADAPTOR : type = "Inbound Adaptor"; break; case TreeObject.INBOUND_PLUGIN: type =
         * "Inbound Plugin"; break; case TreeObject.DESTINATION : type = "Destination"; break; case
         * TreeObject.OUTBOUND_ADAPTOR: type = "Outbound Adaptor"; break; case TreeObject.OUTBOUND_PLUGIN: type =
         * "Outbound Plugin"; break; case TreeObject.DOCUMENT: type = "Document"; break; case TreeObject.ITEM: type =
         * "Item";
         */
        case TreeObject.VIEW:
            type = "View";//$NON-NLS-1$
            break;
        case TreeObject.DATA_MODEL:
            type = "Data Model";//$NON-NLS-1$
            break;
        /*
         * case TreeObject.SOURCE: type = "Source"; break;
         */
        case TreeObject.DATA_CLUSTER:
            type = "Data Cluster";//$NON-NLS-1$
            break;
        case TreeObject.ROLE:
            type = "Role";//$NON-NLS-1$
            break;
        case TreeObject.STORED_PROCEDURE:
            type = "Stored Procedure";//$NON-NLS-1$
            break;
        case TreeObject.TRANSFORMER:
            type = "Transformer V2";//$NON-NLS-1$
            break;
        case TreeObject.MENU:
            type = "Menu";//$NON-NLS-1$
            break;
        case TreeObject.ROUTING_RULE:
            type = "Routing Rule";//$NON-NLS-1$
            break;
        }
        return type;
    }

    public static String determineInstanceByTreeObjectType(int xobjectType, TreeObject xobject) {
        String instance = null;
        switch (xobjectType) {
        /*
         * case TreeObject.INBOUND_ADAPTOR : instance = (xobject.isXObject() ?
         * ((WSInboundAdaptorPK)xobject.getWsKey()).getPk() : null); break; case TreeObject.INBOUND_PLUGIN: instance =
         * (xobject.isXObject() ? ((WSInboundPluginPK)xobject.getWsKey()).getPk() : null); break; case
         * TreeObject.DESTINATION : instance = (xobject.isXObject() ? ((WSDestinationPK)xobject.getWsKey()).getPk() :
         * null); break; case TreeObject.OUTBOUND_ADAPTOR: instance = (xobject.isXObject() ?
         * ((WSOutboundAdaptorPK)xobject.getWsKey()).getPk() : null); break; case TreeObject.OUTBOUND_PLUGIN: instance =
         * (xobject.isXObject() ? ((WSOutboundPluginPK)xobject.getWsKey()).getPk() : null); break; case
         * TreeObject.DOCUMENT: instance = (xobject.isXObject() ? ((WSDocumentPK)xobject.getWsKey()).getPk() : null);
         * break; case TreeObject.ITEM: instance = (xobject.isXObject() ? ((WSItemPK)xobject.getWsKey()).getPk() :
         * null);
         */
        case TreeObject.VIEW:
            instance = (xobject.isXObject() ? ((WSViewPK) xobject.getWsKey()).getPk() : null);
            break;
        case TreeObject.DATA_MODEL:
            instance = (xobject.isXObject() ? ((WSDataModelPK) xobject.getWsKey()).getPk() : null);
            break;
        /*
         * case TreeObject.SOURCE: instance = (xobject.isXObject() ? ((WSSourcePK)xobject.getWsKey()).getPk() : null);
         * break;
         */
        case TreeObject.DATA_CLUSTER:
            instance = (xobject.isXObject() ? ((WSDataClusterPK) xobject.getWsKey()).getPk() : null);
            break;
        case TreeObject.SUBSCRIPTION_ENGINE:
            instance = null;
            break;
        case TreeObject.ROLE:
            instance = (xobject.isXObject() ? ((WSRolePK) xobject.getWsKey()).getPk() : null);
            break;
        case TreeObject.STORED_PROCEDURE:
            instance = (xobject.isXObject() ? ((WSStoredProcedurePK) xobject.getWsKey()).getPk() : null);
            break;
        case TreeObject.TRANSFORMER:
            instance = (xobject.isXObject() ? ((WSTransformerV2PK) xobject.getWsKey()).getPk() : null);
            break;
        case TreeObject.MENU:
            instance = (xobject.isXObject() ? ((WSMenuPK) xobject.getWsKey()).getPk() : null);
            break;
        case TreeObject.ROUTING_RULE:
            instance = (xobject.isXObject() ? ((WSRoutingRulePK) xobject.getWsKey()).getPk() : null);
            break;
        }
        return instance;
    }
}
