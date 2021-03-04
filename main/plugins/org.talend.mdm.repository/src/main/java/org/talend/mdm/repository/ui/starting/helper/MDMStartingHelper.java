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
package org.talend.mdm.repository.ui.starting.helper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.ui.internal.intro.impl.model.IntroContentProvider;
import org.eclipse.ui.internal.intro.impl.model.loader.ContentProviderManager;
import org.eclipse.ui.internal.intro.impl.model.loader.IntroContentParser;
import org.eclipse.ui.internal.intro.impl.model.util.ModelUtil;
import org.eclipse.ui.intro.config.IIntroContentProviderSite;
import org.eclipse.ui.intro.config.IIntroXHTMLContentProvider;
import org.osgi.framework.Bundle;
import org.talend.mdm.repository.i18n.MessagesE;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.ui.starting.MDMStartingConstants;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class MDMStartingHelper {

    private static Logger log = Logger.getLogger(MDMStartingHelper.class);

    private static MDMStartingHelper helper = null;

    private Bundle bundle;

    private MDMStartingHelper() {
    }

    public static MDMStartingHelper getHelper() {
        if (helper == null) {
            helper = new MDMStartingHelper();
        }

        return helper;
    }

    public String getHtmlContent() throws IOException {
        String externalFileName = getExternalFileName();

        String htmlContent = resolveHtmlContent(externalFileName);

        return htmlContent;
    }

    private String getExternalFileName() throws IOException {
        String htmlResource = "resources/welcomepage/mdm-welcomepage.html";//$NON-NLS-1$

        bundle = getRepositoryBundle();
        URL entry = bundle.getEntry(htmlResource);
        if (entry != null) {
            URL fileURL = FileLocator.toFileURL(entry);
            String externalFormFileUrl = fileURL.toExternalForm();

            String prefix = "file:/";//$NON-NLS-1$
            String fileProtocol = "file:///";//$NON-NLS-1$
            if (externalFormFileUrl.startsWith(prefix)) {
                if (!externalFormFileUrl.startsWith(fileProtocol)) {
                    externalFormFileUrl = fileProtocol + externalFormFileUrl.substring(prefix.length());
                }
            }

            return externalFormFileUrl;
        }

        return null;
    }

    private Bundle getRepositoryBundle() {
        if (bundle == null) {
            bundle = RepositoryPlugin.getDefault().getBundle();
        }

        return bundle;
    }

    @SuppressWarnings("restriction")
    private String resolveHtmlContent(String externalFileName) {
        if (externalFileName == null) {
            return "";//$NON-NLS-1$
        }

        String content = "";//$NON-NLS-1$
        IntroContentParser parser = new IntroContentParser(externalFileName);
        Document dom = parser.getDocument();
        if (dom != null) {
            resolveInternationalization(dom);
            resolveImagePath(dom);
            resolveDynamicContent(dom, null);
            content = IntroContentParser.convertToString(dom);
        }

        return content;
    }

    private void resolveInternationalization(Document dom) {
        NodeList internationals = dom.getElementsByTagNameNS("*", //$NON-NLS-1$
                MDMStartingConstants.KEY_INTERNATIONAL);

        Node[] nodes = getArray(internationals);
        for (int i = 0; i < nodes.length; i++) {
            Element internationalElement = (Element) nodes[i];
            String nodeName = MessagesE.getString(internationalElement.getAttribute(MDMStartingConstants.ATT_ID));
            internationalElement.getParentNode().replaceChild(dom.createTextNode(nodeName), internationalElement);
        }
    }

    private void resolveImagePath(Document dom) {
        String imagePath = getImagePath();
        NodeList elements = dom.getElementsByTagNameNS("*", "img");//$NON-NLS-1$//$NON-NLS-2$
        Node[] nodes = getArray(elements);
        for (int i = 0; i < nodes.length; i++) {
            Element element = (Element) nodes[i];
            if (element != null) {
                element.setAttribute("src", "file:///" + imagePath);//$NON-NLS-1$//$NON-NLS-2$
                break;
            }
        }
    }

    private String getImagePath() {
        File file = null;
        try {
            File bundleFile = FileLocator.getBundleFile(getRepositoryBundle());
            String resourceFolder = "icons/server_export.png";//$NON-NLS-1$
            file = new File(bundleFile, resourceFolder);
        } catch (IOException e) {
            log.error("resolve bundle file error.", e);//$NON-NLS-1$
        }

        return file.getAbsolutePath();
    }

    public static Node[] getArray(NodeList nodeList) {
        Node nodes[] = new Node[nodeList.getLength()];
        for (int i = 0; i < nodeList.getLength(); i++) {
            nodes[i] = nodeList.item(i);
        }

        return nodes;
    }

    @SuppressWarnings("restriction")
    private Document resolveDynamicContent(Document dom, IIntroContentProviderSite site) {

        // get all content provider elements in DOM.
        NodeList contentProviders = dom.getElementsByTagNameNS("*", //$NON-NLS-1$
                IntroContentProvider.TAG_CONTENT_PROVIDER);

        // get the array version of the nodelist to work around DOM api design.
        Node[] nodes = getArray(contentProviders);
        for (int i = 0; i < nodes.length; i++) {
            Element contentProviderElement = (Element) nodes[i];
            IntroContentProvider provider = new IntroContentProvider(contentProviderElement, getRepositoryBundle());
            // If we've already loaded the content provider for this element,
            // retrieve it, otherwise load the class.
            IIntroXHTMLContentProvider providerClass = (IIntroXHTMLContentProvider) ContentProviderManager.getInst()
                    .getContentProvider(provider);
            if (providerClass == null) {
                // content provider never created before, create it.
                providerClass = (IIntroXHTMLContentProvider) ContentProviderManager.getInst().createContentProvider(provider,
                        site);
            }

            if (providerClass != null) {
                // create a div with the same id as the contentProvider, pass it
                // as the parent to create the specialized content, and then
                // replace the contentProvider element with this div.
                Properties att = new Properties();
                att.setProperty("id", provider.getId()); //$NON-NLS-1$
                Element contentDiv = ModelUtil.createElement(dom, ModelUtil.TAG_DIV, att);
                providerClass.createContent(provider.getId(), contentDiv);

                if (contentDiv.getChildNodes().getLength() != 0) {
                    if (MDMStartingConstants.OPTION_NAME.equals(provider.getId())
                            || MDMStartingConstants.PRODUCT_NAME.equals(provider.getId())) {
                        contentProviderElement.getParentNode().replaceChild(contentDiv.getFirstChild(), contentProviderElement);
                    } else {
                        contentProviderElement.getParentNode().replaceChild(contentDiv, contentProviderElement);
                    }
                }
            } else {
                // we couldn't load the content provider, so add any alternate
                // text content if there is any.
                // INTRO: do it. 3.0 intro content style uses text element as
                // alt text. We can load XHTML content here.
            }
        }

        return dom;
    }
}
