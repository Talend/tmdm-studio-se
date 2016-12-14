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
package org.talend.mdm.repository.ui.navigator;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;
import org.eclipse.ui.navigator.IDescriptionProvider;
import org.talend.core.GlobalServiceRegister;
import org.talend.core.context.Context;
import org.talend.core.context.RepositoryContext;
import org.talend.core.model.properties.Item;
import org.talend.core.model.properties.Property;
import org.talend.core.model.properties.User;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.LockInfo;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.core.runtime.CoreRuntimePlugin;
import org.talend.core.services.IGITProviderService;
import org.talend.mdm.repository.core.IRepositoryNodeConfiguration;
import org.talend.mdm.repository.core.IRepositoryNodeLabelProvider;
import org.talend.mdm.repository.core.service.IMDMSVNProviderService;
import org.talend.mdm.repository.extension.RepositoryNodeConfigurationManager;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;
import org.talend.repository.model.IProxyRepositoryFactory;

/**
 * DOC hbhong class global comment. Detailled comment <br/>
 * 
 */
public class MDMRepositoryLabelProvider extends ColumnLabelProvider implements ILabelProvider, IDescriptionProvider,
        IColorProvider, IFontProvider, ICommonLabelProvider, IStyledLabelProvider {

    private static Logger log = Logger.getLogger(MDMRepositoryLabelProvider.class);

    private IProxyRepositoryFactory factory = CoreRuntimePlugin.getInstance().getProxyRepositoryFactory();

    private IMDMSVNProviderService svnProviderService = null;

    private Boolean isInSvnMode = null;

    private IGITProviderService gitProviderService;

    private Boolean isInGitMode;

    @Override
    public void addListener(ILabelProviderListener listener) {
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    @Override
    public void removeListener(ILabelProviderListener listener) {
    }

    public String getDescription(Object element) {
        IRepositoryNodeLabelProvider provider = getLabelProvider(element);
        if (provider != null) {
            return provider.getDescription(element);
        }
        return null;
    }

    @Override
    public Image getImage(Object element) {
        IRepositoryNodeLabelProvider provider = getLabelProvider(element);
        if (provider != null) {
            return provider.getImage(element);
        }
        return null;
    }

    @Override
    public String getText(Object element) {
        IRepositoryNodeLabelProvider provider = getLabelProvider(element);
        if (provider != null) {
            return provider.getText(element);
        }
        return element.toString();
    }

    protected IRepositoryNodeLabelProvider getLabelProvider(Object element) {
        if (element instanceof IRepositoryViewObject) {
            IRepositoryViewObject viewObject = (IRepositoryViewObject) element;
            viewObject = RepositoryResourceUtil.assertViewObject(viewObject);
            Property property = viewObject.getProperty();
            if (property != null) {
                Item item = property.getItem();
                IRepositoryNodeConfiguration conf = RepositoryNodeConfigurationManager.getConfiguration(item);
                if (conf != null) {
                    return conf.getLabelProvider();
                }
            }
        }
        return null;
    }

    @Override
    public Color getForeground(Object element) {
        IRepositoryNodeLabelProvider provider = getLabelProvider(element);
        if (provider != null) {
            return provider.getForeground(element);
        }
        return null;
    }

    @Override
    public Font getFont(Object element) {
        IRepositoryNodeLabelProvider provider = getLabelProvider(element);
        if (provider != null) {
            return provider.getFont(element);
        }
        return null;
    }

    @Override
    public Color getBackground(Object element) {
        return null;
    }

    @Override
    public String getToolTipText(Object element) {
        if (isInSvnMode()) {
            IRepositoryViewObject viewObj = (IRepositoryViewObject) element;

            if (getSvnProviderService() != null) {
                String toolTip = getSvnProviderService().getLockInfo(viewObj);
                return toolTip;
            }
        }

        if (isInGitMode()) {
            return getLockInfo(element);
        }

        return null;
    }

    private String getLockInfo(Object element) {
        User currentLoginUser = ((RepositoryContext) CoreRuntimePlugin.getInstance().getContext()
                .getProperty(Context.REPOSITORY_CONTEXT_KEY)).getUser();
        String currentLogin = null;
        if (currentLoginUser != null) {
            currentLogin = currentLoginUser.getLogin();
        }
        IRepositoryViewObject viewObj = (IRepositoryViewObject) element;
        Item item = viewObj.getProperty().getItem();
        if (item != null) {
            LockInfo lockInfo = ProxyRepositoryFactory.getInstance().getLockInfo(item);
            if (!lockInfo.getUser().equals(currentLogin)) {
                String login = lockInfo.getUser();
                String application = lockInfo.getApplication();
                if (login != null && !"".equals(login)) {//$NON-NLS-1$
                    String content = Messages.bind(Messages.MDMRepositoryLabelProvider_lockinfo, login, application);
                    return content;
                }
            }
        }

        return null;
    }

    private boolean isInSvnMode() {
        if (isInSvnMode == null) {
            IMDMSVNProviderService service = getSvnProviderService();
            if (service != null && service.isProjectInSvnMode()) {
                isInSvnMode = Boolean.TRUE;
            }

            if (isInSvnMode == null) {
                isInSvnMode = Boolean.FALSE;
            }
        }

        return isInSvnMode.booleanValue();
    }

    private boolean isInGitMode() {
        if (isInGitMode == null) {
            IGITProviderService service = getGitProviderService();
            if (service != null && service.isProjectInGitMode()) {
                isInGitMode = Boolean.TRUE;
            }

            if (isInGitMode == null) {
                isInGitMode = Boolean.FALSE;
            }
        }

        return isInGitMode.booleanValue();
    }

    private IMDMSVNProviderService getSvnProviderService() {
        if (svnProviderService == null) {
            try {
                if (GlobalServiceRegister.getDefault().isServiceRegistered(IMDMSVNProviderService.class)) {
                    svnProviderService = (IMDMSVNProviderService) GlobalServiceRegister.getDefault()
                            .getService(IMDMSVNProviderService.class);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        return svnProviderService;
    }

    private IGITProviderService getGitProviderService() {
        if (gitProviderService == null) {
            try {
                if (GlobalServiceRegister.getDefault().isServiceRegistered(IGITProviderService.class)) {
                    gitProviderService = (IGITProviderService) GlobalServiceRegister.getDefault()
                            .getService(IGITProviderService.class);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }

        }

        return gitProviderService;
    }

    public void restoreState(IMemento aMemento) {
    }

    public void saveState(IMemento aMemento) {
    }

    public void init(ICommonContentExtensionSite aConfig) {
    }

    public StyledString getStyledText(Object element) {
        return new StyledString(getText(element));
    }

}
