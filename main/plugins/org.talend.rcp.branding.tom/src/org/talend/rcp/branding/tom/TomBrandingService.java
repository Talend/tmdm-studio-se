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
package org.talend.rcp.branding.tom;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.osgi.framework.Bundle;
import org.talend.rcp.branding.tom.i18n.Messages;

import com.amalto.workbench.service.branding.DefaultBrandingConfiguraton;
import com.amalto.workbench.service.branding.IBrandingConfiguration;
import com.amalto.workbench.service.branding.IBrandingService;

/**
 * DOC smallet class global comment. Detailled comment <br/>
 * 
 */
public class TomBrandingService implements IBrandingService {

    public String getFullProductName() {
        return Messages.getString("productfullname"); //$NON-NLS-1$
    }

    public String getShortProductName() {
        return Messages.getString("productshortname"); //$NON-NLS-1$
    }

    public String getCorporationName() {
        return Messages.getString("corporationname"); //$NON-NLS-1$
    }

    public ImageDescriptor getLoginVImage() {
        return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, Messages.getString("loginimageleft")); //$NON-NLS-1$
    }

    public ImageDescriptor getLoginHImage() {
        return Activator.imageDescriptorFromPlugin(Activator.PLUGIN_ID, Messages.getString("loginimagehigh")); //$NON-NLS-1$
    }

    public URL getLicenseFile() throws IOException {
        final Bundle b = Platform.getBundle(Activator.PLUGIN_ID);
        final URL url = FileLocator.toFileURL(FileLocator.find(b, new Path("resources/license.txt"), null)); //$NON-NLS-1$
        return url;
    }

    public IBrandingConfiguration getBrandingConfiguration() {
        return new DefaultBrandingConfiguraton();
    }

    public String getAcronym() {
        return "tmc";
    }

}
