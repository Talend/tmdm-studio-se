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
package com.amalto.workbench.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDComplexTypeDefinition;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDModelGroup;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTerm;
import org.eclipse.xsd.XSDTypeDefinition;
import org.talend.mdm.webservice.TMDMService;
import org.talend.mdm.webservice.WSDataModel;
import org.talend.mdm.webservice.WSDataModelPK;
import org.talend.mdm.webservice.WSGetDataModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.widgets.xmlviewer.XMLConfiguration;
import com.amalto.workbench.widgets.xmlviewer.XMLSourceViewer;
import com.amalto.workbench.widgets.xmlviewer.XMLSourceViewerHelper;
import com.amalto.workbench.widgets.xmlviewer.contentassist.IKeyWordProvider;

public class DOMViewDialog extends Dialog implements IKeyWordProvider {

    private static Log log = LogFactory.getLog(DOMViewDialog.class);

    public final static int BUTTON_CLOSE = 10;

    public final static int BUTTON_SAVE = 15;

    public final static int BUTTON_CANCEL = 20;

    public final static int TREE_VIEWER = 0;

    public final static int SOURCE_VIEWER = 1;

    protected Node node;

    protected TreeViewer domViewer;

    protected XMLSourceViewer sourceViewer;

    protected Combo dataModelCombo;

    protected TabFolder tabFolder;

    protected int firstTab = TREE_VIEWER;

    protected Label mcLable = null;

    // protected boolean elementChanged = false;

    private int buttonPressed = 0;

    private boolean editable = false;

    private Collection<String> dataModelNames;

    private String selectedDataModel;

    private String desc;

    private Collection<Listener> listeners = new ArrayList<Listener>();

    private final TMDMService port;

    public DOMViewDialog(Shell parentShell, Node node) {
        this(parentShell, null, node, false, null, TREE_VIEWER, null);
    }

    public DOMViewDialog(Shell parentShell, Node node, boolean editable, Collection<String> dataModelNames, int firstTab,
            String selectedDataModel, String desc) {
        this(parentShell, null, node, editable, dataModelNames, firstTab, selectedDataModel);
        this.desc = desc;
    }

    public DOMViewDialog(Shell parentShell, TMDMService port, Node node, boolean editable, Collection<String> dataModelNames,
            int firstTab, String selectedDataModel) {
        super(parentShell);
        this.port = port;
        this.node = node;
        this.editable = editable;
        this.dataModelNames = new ArrayList<String>();
        if (dataModelNames != null) {
            this.dataModelNames.addAll(dataModelNames);
        }
        this.firstTab = firstTab;
        this.selectedDataModel = selectedDataModel;
        setShellStyle(getShellStyle() | SWT.RESIZE | SWT.MAX);
    }

    @Override
    protected Control createDialogArea(Composite parent) {

        try {
            // Should not really be here but well,....
            if (editable) {
                parent.getShell().setText(Messages.DOMViewDialog_EditorViewer);
            } else {
                parent.getShell().setText(Messages.DOMViewDialog_Viewer);
            }

            Composite composite = new Composite(parent, SWT.NULL);
            GridLayout layout = new GridLayout();
            layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
            layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
            layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
            layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
            composite.setLayout(layout);
            composite.setLayoutData(new GridData(GridData.FILL_BOTH));
            applyDialogFont(composite);

            if (desc != null && desc.length() > 0) {
                new Label(composite, SWT.NONE).setText(desc);
            }

            tabFolder = new TabFolder(composite, SWT.TOP | SWT.H_SCROLL | SWT.V_SCROLL);
            tabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            ((GridData) tabFolder.getLayoutData()).heightHint = 600;
            ((GridData) tabFolder.getLayoutData()).widthHint = 600;

            tabFolder.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {
                }

                public void widgetSelected(SelectionEvent e) {
                    if (tabFolder.getSelectionIndex() == 0) {
                        if (node == null) {
                            try {

                                if (sourceViewer == null || sourceViewer.getDocument() == null) {
                                    return;
                                }
                                node = Util.parse(sourceViewer.getText());

                            } catch (Exception ex) {

                                log.error(ex.getMessage(), ex);
                                tabFolder.setSelection(1);
                                MessageDialog.openError(DOMViewDialog.this.getShell(), Messages.DOMViewDialog_XMLInvalid,
                                        Messages.bind(Messages.DOMViewDialog_XMLInvalidInfo, ex.getLocalizedMessage()));
                                return;
                            }
                            domViewer.setInput(node);
                            domViewer.expandAll();
                        }
                    } else if (tabFolder.getSelectionIndex() == 1) {
                        try {
                            sourceViewer.setText(Util.nodeToString(node));
                            node = null; // this should be better implemented in a change listener on the text
                        } catch (Exception ex) {
                            MessageDialog.openError(DOMViewDialog.this.getShell(), Messages.DOMViewDialog_ErrorTitle,
                                    Messages.bind(Messages.DOMViewDialog_ErrorMsg, ex.getLocalizedMessage()));
                            sourceViewer.setText(""); //$NON-NLS-1$
                        }
                    }
                }// widget Selected
            });

            TabItem tiDOM = new TabItem(tabFolder, SWT.NULL);
            tiDOM.setText(Messages.DOMViewDialog_Tree);
            tiDOM.setToolTipText(Messages.DOMViewDialog_DisplayAsDomTree);

            domViewer = new TreeViewer(tabFolder, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
            domViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            // ((GridData)domViewer.getControl().getLayoutData()).heightHint=300;
            // ((GridData)domViewer.getControl().getLayoutData()).widthHint=300;
            domViewer.setSorter(null);
            domViewer.setLabelProvider(new DOMTreeLabelProvider());
            domViewer.setContentProvider(new DOMTreeContentProvider());
            domViewer.setInput(node);
            domViewer.expandAll();
            domViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

            tiDOM.setControl(domViewer.getControl());

            TabItem tiSource = new TabItem(tabFolder, SWT.NULL);
            tiSource.setText(Messages.DOMViewDialog_TiSourceText);
            tiSource.setToolTipText(Messages.DOMViewDialog_TiSourceTip);

            XMLSourceViewerHelper sourceViewerHelper = XMLSourceViewerHelper.getInstance();
            sourceViewer = new XMLSourceViewer(tabFolder, sourceViewerHelper.createVerticalRuler(),
                    sourceViewerHelper.createOverviewRuler(), true, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
            XMLConfiguration sourceViewerConfiguration = new XMLConfiguration(this);
            sourceViewer.configure(sourceViewerConfiguration);
            sourceViewer.initilize();
            sourceViewer.getControl().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            sourceViewer.setText(Util.nodeToString(node));
            sourceViewer.setEditable(this.editable);
            /*
             * sourceViewer.addTextListener( new ITextListener() { public void
             * textChanged(org.eclipse.jface.text.TextEvent event) { if ((event.getText()==null) ||
             * ("".equals(event.getText()))) return; node = null; elementChanged = true; } } );
             */
            tiSource.setControl(sourceViewer.getControl());

            tabFolder.setSelection(firstTab);
            if (firstTab == SOURCE_VIEWER) {
                node = null; // force refresh of tree
            }

            return composite;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getShell(), Messages._Error,
                    Messages.bind(Messages.DOMViewDialog_ErrorMsg1, e.getLocalizedMessage()));
            return null;
        }

    }

    public int getButtonPressed() {
        return buttonPressed;
    }

    public String getDataModelName() {
        return dataModelCombo.getText();
    }

    public Node getNode() {
        return this.node;
    }

    public String getXML() {
        return sourceViewer.getText();
    }

    private Map<String, String[]> keyWordMap = new HashMap<String, String[]>();

    private void updateKeyMap(String dataModelName) {
        if (dataModelName == null || port == null) {
            return;
        }
        if (keyWordMap.get(dataModelName) == null) {
            WSGetDataModel wsGetModel = new WSGetDataModel(new WSDataModelPK(dataModelName));
            try {
                WSDataModel dataModel = port.getDataModel(wsGetModel);
                String xsdSchemaStr = dataModel.getXsdSchema();
                if (xsdSchemaStr != null) {
                    XSDSchema schema = Util.getXSDSchema(xsdSchemaStr);
                    Set<String> allKeyWords = getAllKeyWords(schema);
                    if (!allKeyWords.isEmpty()) {
                        String[] keys = allKeyWords.toArray(new String[0]);
                        Arrays.sort(keys);
                        keyWordMap.put(dataModelName, keys);
                    }
                }
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    public String[] getCurrentKeyWords() {
        String dataModelName = dataModelCombo.getText();
        if (dataModelName != null) {
            return keyWordMap.get(dataModelName);
        }
        return null;
    }

    private Set<String> getAllKeyWords(XSDSchema schema) {
        Set<String> keys = new HashSet<String>();
        for (XSDElementDeclaration elementDecl : schema.getElementDeclarations()) {
            collectKeyWords(elementDecl, keys);
        }
        return keys;
    }

    private void collectKeyWords(XSDElementDeclaration elementDeclaration, Set<String> keys) {
        String elementName = elementDeclaration.getName();
        keys.add(elementName);
        XSDTypeDefinition typeDefinition = elementDeclaration.getType();
        if (typeDefinition instanceof XSDComplexTypeDefinition) {
            XSDParticle particle = (XSDParticle) ((XSDComplexTypeDefinition) typeDefinition).getContent();
            XSDTerm term = particle.getTerm();
            if (term instanceof XSDModelGroup) {
                EList<XSDParticle> particles = ((XSDModelGroup) term).getContents();
                for (XSDParticle p : particles) {
                    XSDTerm childTerm = p.getTerm();
                    if (childTerm instanceof XSDElementDeclaration) {
                        collectKeyWords((XSDElementDeclaration) childTerm, keys);
                    }
                }
            }
        }
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {

        if (!editable) {
            createButton(parent, BUTTON_CLOSE, Messages.Close, true);
        } else {
            mcLable = new Label(parent, SWT.RIGHT);
            mcLable.setText(Messages.DOMViewDialog_DataModel);
            dataModelCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
            String[] dms = dataModelNames.toArray(new String[dataModelNames.size()]);
            Arrays.sort(dms);
            dataModelCombo.setItems(dms);
            dataModelCombo.select(-1);
            dataModelCombo.addSelectionListener(new SelectionAdapter() {

                @Override
                public void widgetSelected(SelectionEvent e) {
                    String dataModelName = dataModelCombo.getText();
                    updateKeyMap(dataModelName);
                }
            });

            if (selectedDataModel != null) {
                for (int i = 0; i < dms.length; i++) {
                    String dm = dms[i];
                    if (dm.equals(selectedDataModel)) {
                        dataModelCombo.select(i);
                        updateKeyMap(dm);
                        break;
                    }
                }
            }

            createButton(parent, BUTTON_SAVE, Messages.Save, false);
            createButton(parent, BUTTON_CANCEL, Messages.Cancel, true);
        }
    }

    @Override
    protected void buttonPressed(int buttonId) {
        this.buttonPressed = buttonId;

        if (buttonId == BUTTON_SAVE) {
            // check that Data Model is not nul
            if (dataModelCombo.getSelectionIndex() == -1) {
                MessageDialog.openError(DOMViewDialog.this.getShell(), Messages.DOMViewDialog_ErrorTitle2,
                        Messages.DOMViewDialog_ErrorMsg2);
                return;
            }
            // if save and on DOM viewer get the XML String
            if (tabFolder.getSelectionIndex() == 0) {
                try {
                    sourceViewer.setText(Util.nodeToString(node));
                } catch (Exception ex) {
                    tabFolder.setSelection(1);
                    MessageDialog.openError(DOMViewDialog.this.getShell(), Messages.DOMViewDialog_ErrorTitle3,
                            Messages.bind(Messages.DOMViewDialog_ErrorMsg3, ex.getLocalizedMessage()));
                    return;
                }
            }
        }

        Event e = new Event();
        e.button = buttonId;
        notifyListeners(e);
    }

    public void addListener(Listener listener) {
        listeners.add(listener);
    }

    public void notifyListeners(Event e) {
        for (Listener listener : listeners) {
            listener.handleEvent(e);
        }
    }

    /**
     * DOM Tree Content Provider
     * 
     * @author bgrieder
     * 
     */
    class DOMTreeContentProvider implements IStructuredContentProvider, ITreeContentProvider {

        public DOMTreeContentProvider() {
        }

        public void inputChanged(Viewer v, Object oldInput, Object newInput) {
        }

        public void dispose() {
        }

        public Object[] getElements(Object parent) {
            return getChildren(parent);
        }

        public Object getParent(Object child) {
            // if (child instanceof Element) {
            return ((Element) child).getParentNode();
            // }
            // return null;
        }

        public Object[] getChildren(Object parent) {
            if (parent instanceof Document) {
                return new Object[] { ((Document) parent).getDocumentElement() };
            }

            if (parent instanceof Element) {
                Element e = (Element) parent;
                ArrayList<Node> list = new ArrayList<Node>();
                // Attributes
                NamedNodeMap map = e.getAttributes();
                for (int i = 0; i < map.getLength(); i++) {
                    list.add(map.item(i));
                }
                // Sub-Elements
                NodeList nl = ((Element) parent).getChildNodes();
                for (int i = 0; i < nl.getLength(); i++) {
                    if (nl.item(i) instanceof Element) {
                        list.add(nl.item(i));
                    }
                }
                if (list.size() == 0) {
                    return null;
                } else {
                    return list.toArray(new Node[list.size()]);
                }
            }

            return null;
        }

        public boolean hasChildren(Object parent) {
            if (parent instanceof Document) {
                return true;
            }
            if (parent instanceof Element) {
                return (((Element) parent).getChildNodes().getLength() + ((Element) parent).getAttributes().getLength()) > 1;
            }
            return false;
        }

    }

    /**
     * DOM Tree Label Provider
     * 
     * @author bgrieder
     * 
     */
    class DOMTreeLabelProvider extends LabelProvider {

        @Override
        public String getText(Object obj) {
            if (obj instanceof Node) {
                Node e = (Node) obj;
                if (e.getChildNodes().getLength() > 1) {
                    return e.getLocalName();
                } else {
                    return e.getLocalName() + ": " + e.getTextContent();//$NON-NLS-1$
                }
            }
            return "?? " + obj.getClass().getName() + " : " + obj.toString();//$NON-NLS-1$//$NON-NLS-2$

        }

        @Override
        public Image getImage(Object obj) {
            if (obj instanceof Element) {
                if (((Element) obj).getChildNodes().getLength() > 1) {
                    return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
                } else {
                    return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
                }
            } else if (obj instanceof Node) {
                return ImageCache.getImage("icons/attribute.gif").createImage();//$NON-NLS-1$
            }
            return ImageCache.getImage("icons/small_warn.gif").createImage();//$NON-NLS-1$
        }

    }// Class DOM Tree Label Provider

}
