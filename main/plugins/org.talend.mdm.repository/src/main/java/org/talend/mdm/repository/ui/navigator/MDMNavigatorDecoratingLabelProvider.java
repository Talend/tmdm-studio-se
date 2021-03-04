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
package org.talend.mdm.repository.ui.navigator;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.ui.internal.navigator.NavigatorContentService;
import org.eclipse.ui.internal.navigator.NavigatorDecoratingLabelProvider;
import org.eclipse.ui.internal.navigator.extensions.NavigatorContentExtension;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.eclipse.ui.navigator.INavigatorContentService;


/**
 * created by liusognbo on 2013-9-2
 *
 */
@SuppressWarnings("restriction")
public class MDMNavigatorDecoratingLabelProvider extends NavigatorDecoratingLabelProvider {

    public MDMNavigatorDecoratingLabelProvider(ILabelProvider commonLabelProvider) {
        super(commonLabelProvider);
    }

    @Override
    public String getToolTipText(Object element) {
        if (getViewer() instanceof CommonViewer) {
            CommonViewer viewer = (CommonViewer) getViewer();
            INavigatorContentService contentService = viewer.getNavigatorContentService();
            if (contentService != null && contentService instanceof NavigatorContentService) {
                Collection<?> contentExtensions = ((NavigatorContentService) contentService).findPossibleLabelExtensions(element);
                for (Iterator<?> itr = contentExtensions.iterator(); itr.hasNext();) {
                    Object extension = itr.next();
                    if (extension instanceof NavigatorContentExtension) {
                        ICommonLabelProvider provider = ((NavigatorContentExtension) extension).getLabelProvider();
                        if (provider instanceof CellLabelProvider) {
                            String toolTipText = ((CellLabelProvider) provider).getToolTipText(element);
                            if (toolTipText != null) {
                                return toolTipText;
                            }
                        }
                    }
                }
            }
        }

        return super.getToolTipText(element);
    }

}
