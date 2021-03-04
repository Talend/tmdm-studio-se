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
package com.amalto.workbench.editors;

import java.net.URL;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;

import com.amalto.workbench.actions.EditXObjectAction;
import com.amalto.workbench.dialogs.DOMViewDialog;
import com.amalto.workbench.dialogs.QueryParametersDialog;
import com.amalto.workbench.dialogs.TextViewDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeObjectTransfer;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.service.MissingJarService;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.WidgetUtils;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSExecuteStoredProcedure;
import com.amalto.workbench.webservices.WSPutStoredProcedure;
import com.amalto.workbench.webservices.WSStoredProcedure;
import com.amalto.workbench.webservices.WSStoredProcedurePK;
import com.amalto.workbench.webservices.WSStringArray;

public class StoredProcedureMainPage extends AMainPage implements ITextListener {

    private static Log log = LogFactory.getLog(StoredProcedureMainPage.class);

    protected Text descriptionText;

    protected SourceViewer procedureViewer;

    protected TableViewer resultsViewer;

    protected Combo dataClusterCombo;

    protected Label resultsLabel;

    protected LinkedList<String> currentParameters = new LinkedList<String>();

    protected boolean refreshing = false;

    private boolean comitting = false;

    private Button refreshCacheBtn;

    private final static String[] KEYWORDS = new String[] { Messages.StoredProcedureMainPage_0,
            Messages.StoredProcedureMainPage_1, Messages.StoredProcedureMainPage_2 };

    public StoredProcedureMainPage(FormEditor editor) {
        super(editor, StoredProcedureMainPage.class.getName(), Messages.StoredProcedureMainPage_3
                + ((XObjectEditorInput) editor.getEditorInput()).getName()
                + Util.getRevision((TreeObject) ((XObjectEditorInput) editor.getEditorInput()).getModel()));
    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {

        try {

            WSStoredProcedure wsStoredProcedure = (WSStoredProcedure) (getXObject().getWsObject());

            // description
            Label descriptionLabel = toolkit.createLabel(charComposite, Messages.StoredProcedureMainPage_4, SWT.NULL);
            descriptionLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 1, 1));

            descriptionText = toolkit.createText(charComposite, "", SWT.BORDER);//$NON-NLS-1$
            descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            descriptionText.setText(wsStoredProcedure.getDescription() == null ? "" : wsStoredProcedure.getDescription());//$NON-NLS-1$
            descriptionText.addModifyListener(this);
            // Procedure
            Group storedProcedureGroup = new Group(charComposite, SWT.SHADOW_NONE);
            storedProcedureGroup.setText(Messages.StoredProcedureMainPage_5);
            storedProcedureGroup.setLayout(new GridLayout(1, true));
            storedProcedureGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

            ((GridData) storedProcedureGroup.getLayoutData()).minimumHeight = 100;
            procedureViewer = new SourceViewer(storedProcedureGroup, new VerticalRuler(10), SWT.V_SCROLL);
            procedureViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            procedureViewer.addTextListener(this);
            WidgetUtils.initRedoUndo(procedureViewer);
            refreshCacheBtn = toolkit.createButton(charComposite, Messages.StoredProcedureMainPage_6, SWT.CHECK);
            refreshCacheBtn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
            refreshCacheBtn.addSelectionListener(new SelectionListener() {

                public void widgetSelected(SelectionEvent e) {
                    markDirty();

                }

                public void widgetDefaultSelected(SelectionEvent e) {
                    markDirty();

                }
            });
            /************************************************************
             * Execute Stored Procedure
             ************************************************************/

            createCompDropTarget();
            Composite resultsGroup = this.getNewSectionComposite(Messages.StoredProcedureMainPage_7);
            resultsGroup.setLayout(new GridLayout(4, false));

            // data cluster
            Hyperlink dataClusterLink = toolkit.createHyperlink(resultsGroup, Messages.StoredProcedureMainPage_8, SWT.NULL);
            dataClusterLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));
            dataClusterLink.addHyperlinkListener(new IHyperlinkListener() {

                public void linkEntered(org.eclipse.ui.forms.events.HyperlinkEvent e) {
                }

                public void linkExited(org.eclipse.ui.forms.events.HyperlinkEvent e) {
                }

                public void linkActivated(org.eclipse.ui.forms.events.HyperlinkEvent e) {
                    if (!isLocalInput()) {
                        TreeParent serverRoot = StoredProcedureMainPage.this.getXObject().getServerRoot();
                        TreeObject iaObject = new TreeObject(StoredProcedureMainPage.this.dataClusterCombo.getText(), serverRoot,
                                TreeObject.DATA_CLUSTER, new WSDataClusterPK(StoredProcedureMainPage.this.dataClusterCombo
                                        .getText()), null);
                        (new EditXObjectAction(iaObject, StoredProcedureMainPage.this.getSite().getPage())).run();
                    }
                };
            });
            dataClusterCombo = new Combo(resultsGroup, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.SINGLE);
            dataClusterCombo.setLayoutData(new GridData(SWT.BEGINNING, SWT.NONE, false, false, 1, 1));

            Button executeButton = new Button(resultsGroup, SWT.PUSH);
            executeButton.setText(Messages.StoredProcedureMainPage_9);
            executeButton.addMouseListener(new MouseListener() {

                public void mouseUp(MouseEvent e) {
                    executeProcedure();
                }

                public void mouseDoubleClick(MouseEvent e) {
                }

                public void mouseDown(MouseEvent e) {
                }
            });

            resultsLabel = toolkit
                    .createLabel(
                            resultsGroup,
                            "                                                                                                           ",//$NON-NLS-1$
                            SWT.NULL);
            resultsLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, true, false, 1, 1));

            resultsViewer = new TableViewer(resultsGroup);
            resultsViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
            ((GridData) resultsViewer.getControl().getLayoutData()).heightHint = 300;
            resultsViewer.setContentProvider(new ArrayContentProvider());
            resultsViewer.setLabelProvider(new XMLTableLabelProvider());
            resultsViewer.addDoubleClickListener(new IDoubleClickListener() {

                public void doubleClick(DoubleClickEvent event) {
                    resultsViewer.setSelection(event.getSelection());
                    new ResultsViewAction(StoredProcedureMainPage.this.getSite().getShell(), resultsViewer).run();
                }
            });

            hookContextMenu();

            refreshData();

            dataClusterCombo.select(0);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }// createCharacteristicsContent

    @Override
    protected void refreshData() {
        if (this.comitting) {
            return;
        }

        this.refreshing = true;

        WSStoredProcedure wsStoredProcedure = (WSStoredProcedure) (getXObject().getWsObject());
        String s;

        s = wsStoredProcedure.getDescription() == null ? "" : wsStoredProcedure.getDescription();//$NON-NLS-1$
        if (!s.equals(descriptionText.getText())) {
            descriptionText.setText(s);
        }

        Document doc = new Document(wsStoredProcedure.getProcedure());
        procedureViewer.setDocument(doc);
        refreshCacheBtn.setSelection(wsStoredProcedure.isRefreshCache() != null && wsStoredProcedure.isRefreshCache() ? true
                : false);

        initDataClusterCombo();
        this.refreshing = false;
    }

    protected void initDataClusterCombo() {
        dataClusterCombo.removeAll();
        List<WSDataClusterPK> dataClusterPKs;
        try {
            dataClusterPKs = Util.getAllDataClusterPKs(new URL(getXObject().getEndpointAddress()), getXObject().getUsername(), getXObject().getPassword());
        } catch (Exception ex) {
            MessageDialog.openError(
                    StoredProcedureMainPage.this.getSite().getShell(),
                    Messages._Error,
                    Messages.StoredProcedureMainPage_11 + ex.getClass().getName() + Messages.StoredProcedureMainPage_12
                            + ex.getLocalizedMessage());
            this.refreshing = false;
            return;
        }
        if ((dataClusterPKs == null) || (dataClusterPKs.size() == 0)
                || ((dataClusterPKs.size() == 1) && ("CACHE".equals(dataClusterPKs.get(0).getPk())))) {//$NON-NLS-1$
            MessageDialog.openError(this.getSite().getShell(), Messages._Error, Messages.StoredProcedureMainPage_14);
            return;
        }
        dataClusterCombo.add("[ALL]");//$NON-NLS-1$
        for (int i = 0; i < dataClusterPKs.size(); i++) {
            if (!"CACHE".equals(dataClusterPKs.get(i).getPk())) {
                dataClusterCombo.add(dataClusterPKs.get(i).getPk());
            }
        }
    }

    @Override
    protected void commit() {
        if (this.refreshing) {
            return;
        }

        this.comitting = true;

        WSStoredProcedure wsStoredProcedure = (WSStoredProcedure) (getXObject().getWsObject());
        wsStoredProcedure.setDescription(descriptionText.getText());
        wsStoredProcedure.setProcedure(procedureViewer.getDocument().get());
        wsStoredProcedure.setRefreshCache(refreshCacheBtn.getSelection());
        this.comitting = false;
    }

    // no specific actions here
    @Override
    public void createActions() {
        return;
    }

    public void textChanged(TextEvent event) {
        if (this.refreshing) {
            return;
        }
        markDirty();
    }

    private void hookContextMenu() {
        MenuManager menuMgr = new MenuManager();
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {

            public void menuAboutToShow(IMenuManager manager) {
                manager.add(new ResultsViewAction(StoredProcedureMainPage.this.getSite().getShell(),
                        StoredProcedureMainPage.this.resultsViewer));
            }
        });
        Menu menu = menuMgr.createContextMenu(resultsViewer.getControl());
        resultsViewer.getControl().setMenu(menu);
        getSite().registerContextMenu(menuMgr, resultsViewer);
    }

    protected void executeProcedure() {
        boolean checkMissingJar = MissingJarService.getInstance().checkMissingJar(true);
        if (!checkMissingJar) {
            return;
        }
        BusyIndicator.showWhile(this.getPartControl().getDisplay(), new Runnable() {

            public void run() {
                WSDataClusterPK dcpk = null;
                if (!"[ALL]".equals(dataClusterCombo.getText())) {
                    dcpk = new WSDataClusterPK(dataClusterCombo.getText());
                }
                try {
                    String proc = procedureViewer.getDocument().get();
                    // read parameters
                    int number = 0;

                    while (true) {
                        Pattern p = Pattern.compile(".*[^\\\\]%" + number + "[^\\d]*.*", Pattern.DOTALL);//$NON-NLS-1$//$NON-NLS-2$
                        Matcher m = p.matcher(proc);
                        if (!m.matches()) {
                            break;
                        } else {
                            ++number;
                        }
                    }
                    String[] ps = null;
                    if (number > 0) {
                        // transfer current parameters to new array
                        ps = new String[number];
                        for (int i = 0; i < number; i++) {
                            if (i < currentParameters.size()) {
                                ps[i] = currentParameters.get(i);
                            } else {
                                ps[i] = "";//$NON-NLS-1$
                            }
                        }
                        // call parameters window
                        QueryParametersDialog dialog = new QueryParametersDialog(StoredProcedureMainPage.this.getSite()
                                .getShell(), ps);
                        dialog.setBlockOnOpen(true);
                        dialog.open();
                        if (dialog.getButtonPressed() == QueryParametersDialog.BUTTON_CANCEL) {
                            return;
                        }
                        ps = dialog.getParameters();
                        // Apply parameters
                        for (int i = 0; i < ps.length; i++) {
                            // transfer parameters back into current parameters
                            if (i < currentParameters.size()) {
                                currentParameters.set(i, ps[i]);
                            } else {
                                currentParameters.add(ps[i]);
                            }
                        }
                    }
                    // perform call
                    TMDMService service = getMDMService();
                    if (service != null) {
                        WSStoredProcedure wsStoredProcedure = (WSStoredProcedure) (getXObject().getWsObject());
                        service.putStoredProcedure(new WSPutStoredProcedure(wsStoredProcedure));
                        WSStringArray array = service.executeStoredProcedure(new WSExecuteStoredProcedure(currentParameters,
                                dcpk, new WSStoredProcedurePK(wsStoredProcedure.getName())));
                        List<String> results = array.getStrings();
                        resultsLabel.setText(Messages.StoredProcedureMainPage_15 + results.size()
                                + Messages.StoredProcedureMainPage_16);
                        resultsViewer.setInput(results);
                    }
                } catch (Exception ex) {
                    if (!Util.handleConnectionException(StoredProcedureMainPage.this.getSite().getShell(), ex, null)) {
                        String message = ex.getMessage();
                        Set<String> messages = getMessages(message);
                        StringBuilder builder = new StringBuilder();
                        for (String currentMessage : messages) {
                            builder.append(currentMessage + '\n');
                        }
                        MessageDialog.openError(StoredProcedureMainPage.this.getSite().getShell(), Messages._Error,
                                builder.toString());
                    }
                }
            }
        });
    }

    private static Set<String> getMessages(String msg) {
        StringTokenizer tokenizer = new StringTokenizer(msg, Messages.StoredProcedureMainPage_18);
        StringBuilder currentMessage = new StringBuilder();
        Set<String> messages = new HashSet<String>();

        int currentKeywordIndex = 0;

        while (tokenizer.hasMoreElements()) {
            String element = tokenizer.nextToken().trim();
            if (!element.matches(".*Exception:")) { //$NON-NLS-1$
                if (element.equals(KEYWORDS[currentKeywordIndex])) {
                    currentKeywordIndex++;
                    if (currentKeywordIndex == KEYWORDS.length - 1) {
                        String newMessage = currentMessage.toString();
                        messages.add(newMessage);
                        currentMessage = new StringBuilder();
                        currentKeywordIndex = 0;
                    }
                } else {
                    if (!element.equals("is:") && !element.equals("\n;")) { //$NON-NLS-1$ //$NON-NLS-2$
                        currentMessage.append(element.replace("\n;", "")).append(" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }
                }
            }
        }
        String newMessage = currentMessage.toString();
        messages.add(newMessage);

        return messages;
    }

    class ResultsViewAction extends Action {

        private Shell shell = null;

        private Viewer viewer;

        public ResultsViewAction(Shell shell, Viewer viewer) {
            super();
            this.shell = shell;
            this.viewer = viewer;
            setImageDescriptor(ImageCache.getImage("icons/add_obj.gif"));//$NON-NLS-1$
            setText(Messages.StoredProcedureMainPage_19);
            setToolTipText(Messages.StoredProcedureMainPage_20);
        }

        @Override
        public void run() {
            try {
                super.run();

                IStructuredSelection selection = ((IStructuredSelection) viewer.getSelection());
                String result = (String) selection.getFirstElement();
                // clean up highlights
                result = result.replaceAll("\\s*__h", "").replaceAll("h__\\s*", "");//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$

                // try to parse it
                try {
                    final DOMViewDialog d = new DOMViewDialog(shell, Util.parse(result));
                    d.setBlockOnOpen(true);
                    d.addListener(new Listener() {

                        public void handleEvent(Event event) {
                            if (event.button == DOMViewDialog.BUTTON_CLOSE) {
                                d.close();
                            }
                        }
                    });
                    d.open();
                } catch (Exception e) {
                    // not an XML
                    Dialog d = new TextViewDialog(shell, result);
                    d.setBlockOnOpen(true);
                    d.open();
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                MessageDialog.openError(shell, Messages._Error, Messages.StoredProcedureMainPage_22 + e.getLocalizedMessage());
            }
        }

        @Override
        public void runWithEvent(Event event) {
            super.runWithEvent(event);
        }

    }

    protected static Pattern highlightLeft = Pattern.compile("\\s*__h");//$NON-NLS-1$

    protected static Pattern highlightRight = Pattern.compile("h__\\s*");//$NON-NLS-1$

    protected static Pattern emptyTags = Pattern.compile("\\s*<(.*?)\\/>\\s*");//$NON-NLS-1$

    protected static Pattern openingTags = Pattern.compile("\\s*<([^\\/].*?[^\\/])>\\s*");//$NON-NLS-1$

    protected static Pattern closingTags = Pattern.compile("\\s*</(.*?)>\\s*");//$NON-NLS-1$

    class XMLTableLabelProvider implements ITableLabelProvider {

        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        public String getColumnText(Object element, int columnIndex) {
            String xml = (String) element;
            xml = highlightLeft.matcher(xml).replaceAll("");//$NON-NLS-1$
            xml = highlightRight.matcher(xml).replaceAll("");//$NON-NLS-1$
            xml = emptyTags.matcher(xml).replaceAll("[$1]");//$NON-NLS-1$
            xml = openingTags.matcher(xml).replaceAll("[$1: ");//$NON-NLS-1$
            xml = closingTags.matcher(xml).replaceAll("]");//$NON-NLS-1$
            if (xml.length() >= 150) {
                return xml.substring(0, 150) + "...";//$NON-NLS-1$
            }
            return xml;
        }

        public void addListener(ILabelProviderListener listener) {
        }

        public void dispose() {
        }

        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        public void removeListener(ILabelProviderListener listener) {
        }

    }

    private void createCompDropTarget() {
        DropTarget dropTarget = new DropTarget(procedureViewer.getTextWidget(), DND.DROP_MOVE | DND.DROP_LINK);
        dropTarget.setTransfer(new TreeObjectTransfer[] { TreeObjectTransfer.getInstance() });
        dropTarget.addDropListener(new DropTargetAdapter() {

            @Override
            public void dragEnter(DropTargetEvent event) {
            }

            @Override
            public void dragLeave(DropTargetEvent event) {
            }

            @Override
            public void dragOver(DropTargetEvent event) {
                event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
            }

            @Override
            public void drop(DropTargetEvent event) {
                if (event.data instanceof TreeObject[]) {
                    procedureViewer.getTextWidget().setText(
                            procedureViewer.getTextWidget().getText() + ((TreeObject[]) event.data)[0].getDisplayName());
                }
            }
        });

    }

}
