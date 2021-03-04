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
package com.amalto.workbench.widgets.xmleditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.widgets.xmleditor.infoholder.ExternalInfoHolder;

public class ExtensibleContentEditor extends Composite {

    public static final String EXTENSIONID = "org.talend.mdm.workbench.extensibleXMLEditorPage";//$NON-NLS-1$

    protected TabFolder tabFolderEditors;

    private int lastSelectedTabIndex;

    private String id = "";//$NON-NLS-1$

    public ExtensibleContentEditor(Composite parent, int style, String id) {
        super(parent, style);

        if (id != null)
            this.id = id;

        setLayout(new FillLayout());
        tabFolderEditors = new TabFolder(this, SWT.BOTTOM);

        tabFolderEditors.addSelectionListener(new ExtensibleXMLEditorSelectionListener());

    }

    public String getId() {
        return id;
    }

    public void setPageGroup(String group) {

        clearTabFolder();

        if (group == null)
            return;

        List<ExtensibleContentEditorPageDescription> creatorDescriptions = sortCreatorDescriptions(getPageCeatorDescriptions(group));

        createPages(creatorDescriptions, group);

        lastSelectedTabIndex = getDefaultPageIndex(creatorDescriptions);
        tabFolderEditors.setSelection(lastSelectedTabIndex);

    }

    public void setContent(String content) {
        setContent(new ExtensibleEditorContent(content));
    }

    public void setContent(ExtensibleEditorContent content) {

        if (content == null)
            return;

        for (TabItem eachItem : tabFolderEditors.getItems()) {

            if (!(eachItem.getControl() instanceof ExtensibleContentEditorPage))
                continue;

            ((ExtensibleContentEditorPage) eachItem.getControl()).setContent(content);
        }

    }

    public ExtensibleEditorContent getContent() {

        int index = tabFolderEditors.getSelectionIndex();
        if (index == -1)
            return new ExtensibleEditorContent("");//$NON-NLS-1$

        Control control = tabFolderEditors.getItem(index).getControl();
        if (!(control instanceof ExtensibleContentEditorPage))
            return new ExtensibleEditorContent("");//$NON-NLS-1$

        return ((ExtensibleContentEditorPage) control).getContent();
    }

    protected void createPages(List<ExtensibleContentEditorPageDescription> creatorDescriptions, String plugin) {

        for (ExtensibleContentEditorPageDescription eachCreatorDes : creatorDescriptions) {
            addPage(eachCreatorDes, plugin);
        }
    }

    public void addPage(ExtensibleContentEditorPageDescription creatorDescription, String plugin) {

        TabItem item = new TabItem(tabFolderEditors, SWT.NONE);
        item.setText(creatorDescription.getLabel());
        item.setControl(creatorDescription.getCreator().createXMLEditorPage(tabFolderEditors, SWT.NONE, plugin));

        item.getControl().setBackground(getDisplay().getSystemColor(SWT.COLOR_WHITE));

    }

    protected int getDefaultPageIndex(List<ExtensibleContentEditorPageDescription> creatorDescriptions) {

        for (int i = 0; i < creatorDescriptions.size(); i++) {

            if (creatorDescriptions.get(i).isDefault())
                return i;
        }

        return 0;
    }

    protected List<ExtensibleContentEditorPageDescription> sortCreatorDescriptions(
            Map<Integer, List<ExtensibleContentEditorPageDescription>> index2CreatorDes) {

        List<Integer> indexes = Arrays.asList(index2CreatorDes.keySet().toArray(new Integer[0]));
        Collections.sort(indexes);

        List<ExtensibleContentEditorPageDescription> results = new ArrayList<ExtensibleContentEditorPageDescription>();

        for (Integer eachSortedIndex : indexes)
            results.addAll(index2CreatorDes.get(eachSortedIndex));

        return results;
    }

    protected Map<Integer, List<ExtensibleContentEditorPageDescription>> getPageCeatorDescriptions(String group) {
        Map<Integer, List<ExtensibleContentEditorPageDescription>> index2CreatorDes = new HashMap<Integer, List<ExtensibleContentEditorPageDescription>>();

        for (IConfigurationElement eachElement : Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSIONID)) {

            if (!id.equals(eachElement.getAttribute("editorId")) || !group.equals(eachElement.getAttribute("group")))//$NON-NLS-1$//$NON-NLS-2$
                continue;

            ExtensibleContentEditorPageCreator creator = getPageCreator(eachElement);

            if (creator == null)
                continue;

            int index = getPageIndex(eachElement);

            List<ExtensibleContentEditorPageDescription> creatorDeses = index2CreatorDes.get(index);
            if (creatorDeses == null) {
                creatorDeses = new ArrayList<ExtensibleContentEditorPageDescription>();
                index2CreatorDes.put(index, creatorDeses);
            }

            creatorDeses.add(new ExtensibleContentEditorPageDescription(getPageLabel(eachElement), index, creator,
                    isDefaultPage(eachElement)));

        }

        return index2CreatorDes;
    }

    protected ExtensibleContentEditorPageCreator getPageCreator(IConfigurationElement eachElement) {

        try {
            Object creatorObj = eachElement.createExecutableExtension("class");//$NON-NLS-1$

            if (creatorObj instanceof ExtensibleContentEditorPageCreator)
                return (ExtensibleContentEditorPageCreator) creatorObj;

        } catch (CoreException e) {
            return null;
        }

        return null;
    }

    protected int getPageIndex(IConfigurationElement eachElement) {

        try {
            return Integer.parseInt(eachElement.getAttribute("index"));//$NON-NLS-1$
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    protected String getPageLabel(IConfigurationElement eachElement) {
        return eachElement.getAttribute("label");//$NON-NLS-1$
    }

    protected boolean isDefaultPage(IConfigurationElement eachElement) {
        try {
            return Boolean.parseBoolean(eachElement.getAttribute("isDefault"));//$NON-NLS-1$
        } catch (Exception e) {
            return false;
        }
    }

    protected void clearTabFolder() {

        for (TabItem eachTabItem : tabFolderEditors.getItems()) {
            eachTabItem.getControl().dispose();
            eachTabItem.dispose();
        }

    }

    protected ExtensibleContentEditorPage getXMLEditorPage(int index) {

        try {
            TabItem item = tabFolderEditors.getItem(index);

            if (!(item.getControl() instanceof ExtensibleContentEditorPage))
                return null;

            return (ExtensibleContentEditorPage) item.getControl();

        } catch (Exception e) {
            return null;
        }

    }

    public void addExtensibleXMLEditorPageListener(ExtensibleContentEditorPageListener listener) {

        for (TabItem eachTabItem : tabFolderEditors.getItems()) {

            if (!(eachTabItem.getControl() instanceof ExtensibleContentEditorPage))
                continue;

            ((ExtensibleContentEditorPage) eachTabItem.getControl()).addExtensibleXMLEditorPageListener(listener);
        }
    }

    public void removeExtensibleXMLEditorPageListener(ExtensibleContentEditorPageListener listener) {

        for (TabItem eachTabItem : tabFolderEditors.getItems()) {

            if (!(eachTabItem.getControl() instanceof ExtensibleContentEditorPage))
                continue;

            ((ExtensibleContentEditorPage) eachTabItem.getControl()).removeExtensibleXMLEditorPageListener(listener);
        }

    }

    public void clearExternalResources() {

        for (TabItem eachTabItem : tabFolderEditors.getItems()) {

            if (!(eachTabItem.getControl() instanceof ExtensibleContentEditorPage))
                continue;

            ((ExtensibleContentEditorPage) eachTabItem.getControl()).clearExternalResources();
        }

    }

    public void setExternalInfoHolder(ExternalInfoHolder<?> externalInfoHolder) {

        for (TabItem eachTabItem : tabFolderEditors.getItems()) {

            if (!(eachTabItem.getControl() instanceof ExtensibleContentEditorPage))
                continue;

            ((ExtensibleContentEditorPage) eachTabItem.getControl()).setExternalInfoHolder(externalInfoHolder);
        }

    }

    public void reloadExternalInfo() {

        for (TabItem eachTabItem : tabFolderEditors.getItems()) {

            if (!(eachTabItem.getControl() instanceof ExtensibleContentEditorPage))
                continue;

            ((ExtensibleContentEditorPage) eachTabItem.getControl()).reloadExternalInfo();
        }

    }

    public class ExtensibleXMLEditorSelectionListener extends SelectionAdapter {

        @Override
        public void widgetSelected(SelectionEvent e) {

            if (tabFolderEditors.getSelectionIndex() == -1 || tabFolderEditors.getSelectionIndex() == lastSelectedTabIndex)
                return;

            ExtensibleContentEditorPage oldPage = getXMLEditorPage(lastSelectedTabIndex);
            ExtensibleContentEditorPage newPage = getXMLEditorPage(tabFolderEditors.getSelectionIndex());

            if (oldPage != null) {

                try {
                    oldPage.saveContentFromUI();
                } catch (ExtensibleContentEditorPageSaveException e1) {
                    e.doit = false;
                    MessageDialog.openError(getShell(), Messages._Error,
                            Messages.ExtensibleContentEditor_ErrorMsg);
                    return;
                }
            }

            if (newPage != null)
                newPage.refresh();

            lastSelectedTabIndex = tabFolderEditors.getSelectionIndex();
        }
    }

}
