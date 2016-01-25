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
package com.amalto.workbench.editors;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.actions.EditXObjectAction;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectBrowserInput;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSUniverseXtentisObjectsRevisionIDs;
import com.amalto.workbench.webservices.XtentisPort;

public class BrowseRevisionMainPage extends AMainPageV2 {// implements Observer

    private static Log log = LogFactory.getLog(BrowseRevisionMainPage.class); // {

    protected org.eclipse.swt.widgets.List revisionIDList;

    protected org.eclipse.swt.widgets.List universeList;

    protected TreeParent parent;

    protected TreeObject object;

    protected List<String> deleteRevisionIDs = new ArrayList<String>();

    protected List<WSUniverse> universes;

    public BrowseRevisionMainPage(FormEditor editor) {
        super(editor, BrowseRevisionMainPage.class.getName(), Messages.BrowseRevisionMainPage_RevisionBrowser
                + ((XObjectBrowserInput) editor.getEditorInput()).getName().replaceAll("\\[.*\\]", "").trim());//$NON-NLS-1$//$NON-NLS-2$

    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite mainComposite) {

        // revisionID

        Composite revisionIDComposite = toolkit.createComposite(mainComposite);
        revisionIDComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        revisionIDComposite.setLayout(new GridLayout(1, true));

        Label revisionLabel = toolkit.createLabel(revisionIDComposite, Messages.BrowseRevisionMainPage_RevisionID);
        revisionLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, true, 1, 1));
        revisionIDList = new org.eclipse.swt.widgets.List(revisionIDComposite, SWT.V_SCROLL | SWT.BORDER | SWT.LINE_SOLID);
        revisionIDList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));
        ((GridData) revisionIDList.getLayoutData()).heightHint = 100;

        // universe
        Composite universeComposite = toolkit.createComposite(revisionIDComposite);
        universeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        universeComposite.setLayout(new GridLayout(1, true));

        Label universeLabel = toolkit.createLabel(universeComposite, Messages.BrowseRevisionMainPage_Version);
        universeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, true, 1, 1));
        universeList = new org.eclipse.swt.widgets.List(universeComposite, SWT.V_SCROLL | SWT.BORDER | SWT.LINE_SOLID);
        universeList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
        ((GridData) universeList.getLayoutData()).heightHint = 150;

        try {
            final XtentisPort port = Util.getPort(getXObject());

            revisionIDList.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                }

                public void widgetSelected(SelectionEvent e) {

                    if (revisionIDList.getSelection()[0].equals(IConstants.HEAD)) {
                        universeList.removeAll();
                        universeList.add(IConstants.HEAD, 0);
                        return;
                    } else {
                        universeList.removeAll();
                        if (!"".equals(revisionIDList.getSelection()[0])) {//$NON-NLS-1$
                            int index = BrowseRevisionMainPage.this.getXObject().getDisplayName().indexOf("[");//$NON-NLS-1$
                            String objectName = "";//$NON-NLS-1$
                            if (index > 0)
                                objectName = BrowseRevisionMainPage.this.getXObject().getDisplayName().substring(0, index - 1);
                            List<String> universes = Util.getUniverseBYRevisionID(port, revisionIDList.getSelection()[0],
                                    objectName);
                            universeList.setItems(universes.toArray(new String[universes.size()]));
                            // universeList.add(IConstants.HEAD, 0);
                        }

                    }
                }
            });
            revisionIDList.addKeyListener(new KeyListener() {

                public void keyPressed(KeyEvent e) {
                }

                public void keyReleased(KeyEvent e) {
                    if (e.keyCode == SWT.DEL) {
                        if (!revisionIDList.getSelection()[0].equals("HEAD")) {//$NON-NLS-1$
                            BrowseRevisionMainPage.this.comitting = true;
                            deleteRevisionIDs.add(revisionIDList.getSelection()[0]);
                            revisionIDList.remove(revisionIDList.getSelection()[0]);
                            BrowseRevisionMainPage.this.comitting = false;
                            markDirty();
                        }
                    }
                }
            });
            universeList.addMouseListener(new MouseListener() {

                public void mouseDoubleClick(MouseEvent e) {
                    EditXObjectAction action;
                    parent = BrowseRevisionMainPage.this.getXObject().getServerRoot();
                    TreeObject[] objects = parent.getChildren();
                    TreeObject[] subObjects;
                    for (int i = 0; i < objects.length; i++) {
                        if (objects[i].getDisplayName().equals("Version")) {//$NON-NLS-1$
                            subObjects = ((TreeParent) objects[i]).getChildren();
                            for (int j = 0; j < subObjects.length; j++) {
                                if (subObjects[j].getDisplayName().endsWith(universeList.getSelection()[0])) {
                                    action = new EditXObjectAction(subObjects[j], BrowseRevisionMainPage.this.getSite().getPage());
                                    action.run();
                                    break;
                                }

                            }
                        }

                    }

                }

                public void mouseDown(MouseEvent e) {

                }

                public void mouseUp(MouseEvent e) {

                }

            });

            revisionIDList.setSelection(0);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    @Override
    protected void commit() {

        try {
            if (this.refreshing)
                return;
            this.comitting = true;

            XtentisPort port;
            port = Util.getPort(new URL(getXObject().getEndpointAddress()), getXObject().getUniverse(), getXObject()
                    .getUsername(), getXObject().getPassword());

            for (int i = 0; i < deleteRevisionIDs.size(); i++) {

                String revisionID = deleteRevisionIDs.get(i);
                if (!"".equals(revisionID)) {//$NON-NLS-1$
                    int index = BrowseRevisionMainPage.this.getXObject().getDisplayName().indexOf("[");//$NON-NLS-1$
                    String objectName = "";//$NON-NLS-1$
                    if (index > 0)
                        objectName = BrowseRevisionMainPage.this.getXObject().getDisplayName().substring(0, index - 1);

                    universes = Util.getWSUniverseBYRevisionID(port, revisionID, objectName);
                    for (WSUniverse universe : universes) {
                        WSUniverseXtentisObjectsRevisionIDs[] rids = universe.getXtentisObjectsRevisionIDs();
                        for (int j = 0; j < rids.length; j++) {
                            if (rids[j].getXtentisObjectName().equals(objectName))
                                rids[j].setRevisionID("");//$NON-NLS-1$
                        }
                    }
                }
            }
            this.comitting = false;
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
        }

    }

    public List<WSUniverse> getUniverses() {
        return universes;
    }

    @Override
    protected void refreshData() {
        if (comitting)
            return;

        BrowseRevisionMainPage.this.refreshing = true;
        try {
            final XtentisPort port = Util.getPort(getXObject());
            Map<String, List<String>> map = Util.getUniverseMap(port);
            String displayname = getXObject().getDisplayName().replaceAll("\\[.*\\]", "").trim();//$NON-NLS-1$//$NON-NLS-2$
            List<String> revisions;
            String name = EXtentisObjects.getXtentisObjectName(displayname);
            revisions = map.get(name.trim());
            if (revisions != null && revisions.contains(""))//$NON-NLS-1$
                revisions.remove("");//$NON-NLS-1$
            if (revisions != null) {
                revisionIDList.setItems(revisions.toArray(new String[revisions.size()]));
            }
            revisionIDList.add(IConstants.HEAD, 0);
            universeList.add(IConstants.HEAD, 0);
            revisionIDList.setSelection(0);

            refreshing = false;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if(!Util.handleConnectionException(this.getSite().getShell(), e, Messages.BrowseRevisionMainPage_ErrorRefreshingPage)){
            	MessageDialog.openError(this.getSite().getShell(), Messages.BrowseRevisionMainPage_ErrorRefreshingPage,
                        Messages.bind(Messages.BrowseRevisionMainPage_ErrorRefreshingPageX, e.getLocalizedMessage()));
            }
        }

    }

    @Override
    protected void createActions() {
        

    }

}
