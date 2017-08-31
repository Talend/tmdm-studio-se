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
package org.talend.mdm.repository.core.service;

import java.util.Map;

import org.talend.core.IService;
import org.talend.mdm.repository.core.service.ws.AbstractGetDocument;
import org.talend.mdm.repository.core.service.ws.AbstractPluginDetail;

import com.amalto.workbench.widgets.xmleditor.infoholder.ExternalInfoHolder;

/**
 * @author sbliu
 *
 */
public interface ITriggerProcessService extends IService {

    void addExtraGetDoc(Map<String, AbstractGetDocument> documentServiceMap, String twoLettersLanguageCode);

    void addDetail(Map<String, AbstractPluginDetail> transformerPluginMap, String twoLettersLanguageCode);

    Map<String, ExternalInfoHolder<?>[]> getTriggerExtraExternalInfoHolderForType();

    Map<String, ExternalInfoHolder<?>[]> getProcessExtraExternalInfoHolderForType();
}
