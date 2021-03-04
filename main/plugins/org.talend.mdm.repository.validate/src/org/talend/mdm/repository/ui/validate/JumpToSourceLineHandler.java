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
package org.talend.mdm.repository.ui.validate;

import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.e4.core.commands.ExpressionContext;
import org.eclipse.ui.views.markers.MarkerItem;
import org.eclipse.ui.views.markers.MarkerViewHandler;
import org.talend.mdm.repository.core.marker.IValidationMarker;
import org.talend.mdm.repository.ui.views.MDMProblemView;

/**
 * created by HHB on 2013-4-8 only work for data model
 *
 */
public class JumpToSourceLineHandler extends MarkerViewHandler {

    static Logger log = Logger.getLogger(JumpToSourceLineHandler.class);

    private List selectedObjs;

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
     */
    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        IMarker[] selectedMarkers = getSelectedMarkers(event);
        MDMProblemView view = (MDMProblemView) getView(event);
        view.openSelectedMarkers(true);
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        if (selectedObjs != null) {
            int size = selectedObjs.size();
            if (size == 0 || size > 1) {
                return false;
            }
            Object object = selectedObjs.get(0);
            if (object instanceof MarkerItem) {
                IMarker marker = ((MarkerItem) object).getMarker();
                try {
                    String type = marker.getType();
                    if (type != null) {
                        return IValidationMarker.MARKER_DATA_MODEL.equals(type) || IValidationMarker.MARKER_XSD_ERR.equals(type);
                    }
                } catch (CoreException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.eclipse.core.commands.AbstractHandler#setEnabled(java.lang.Object)
     */
    @Override
    public void setEnabled(Object evaluationContext) {
        if (evaluationContext != null) {
            Object defaultVariable = null;
            if (evaluationContext instanceof ExpressionContext) {
                ExpressionContext exc = (ExpressionContext) evaluationContext;
                defaultVariable = exc.getDefaultVariable();
            } else if (evaluationContext instanceof EvaluationContext) {
                EvaluationContext context = (EvaluationContext) evaluationContext;
                defaultVariable = context.getDefaultVariable();
            }

            if (defaultVariable instanceof List) {
                selectedObjs = (List) defaultVariable;
            } else {
                selectedObjs = null;
            }
        }

    }

}
