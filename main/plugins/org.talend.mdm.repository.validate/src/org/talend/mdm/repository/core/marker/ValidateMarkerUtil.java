// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.core.marker;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.ui.views.MDMProblemView;
import org.talend.mdm.repository.utils.UIUtil;

/**
 * created by HHB on 2013-2-28 Detailled comment
 * 
 */
public class ValidateMarkerUtil implements IValidationMarker {

    static Logger log = Logger.getLogger(ValidateMarkerUtil.class);

    private static Map<ERepositoryObjectType, String[]> viewTypeMarkerMap = new HashMap<ERepositoryObjectType, String[]>();
    static {
        initMarkerMap();
    }

    private static void initMarkerMap() {
        // DATA Model
        viewTypeMarkerMap.put(IServerObjectRepositoryType.TYPE_DATAMODEL, new String[] { MARKER_DATA_MODEL, MARKER_XSD_ERR });
        viewTypeMarkerMap.put(IServerObjectRepositoryType.TYPE_VIEW, new String[]{MARKER_VIEW});
    }

    private static String[] allMarkers = null;

    public static String[] getMarkerTypeByViewType(ERepositoryObjectType type) {
        return viewTypeMarkerMap.get(type);
    }

    public static String[] getAllToRemoveMarkers() {
        return new String[] { MARKER_XSD_ERR, MARKER_MDM };
    }

    public static String[] getAllMarkers() {
        if (allMarkers == null) {
            List<String> markers = new LinkedList<String>();
            for (String[] values : viewTypeMarkerMap.values()) {
                for (String marker : values) {
                    markers.add(marker);
                }
            }
            allMarkers = markers.toArray(new String[0]);
        }
        return allMarkers;
    }

    public static void updateProbleViewTitle() {
        if (UIUtil.isWorkInUI()) {

            Display.getDefault().asyncExec(new Runnable() {

                @Override
                public void run() {
                    IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
                    if (page != null && UIUtil.isMDMPerspective(page)) {
                        IViewPart view = null;
                        try {
                            view = page.findView(MDMProblemView.VIEW_ID);
                            if (view == null) {
                                view = page.showView(MDMProblemView.VIEW_ID, null, IWorkbenchPage.VIEW_CREATE);
                            }
                            if (view != null) {
                                ((MDMProblemView) view).updateViewTitle();
                            }
                        } catch (PartInitException e) {
                            log.error(e.getMessage(), e);
                        }
                    }

                }
            });

        }
    }
}
