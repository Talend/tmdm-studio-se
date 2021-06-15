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

import java.awt.event.TextEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.amalto.workbench.dialogs.PluginDetailsDialog;
import com.amalto.workbench.dialogs.XpathSelectDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.Line;
import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.proposal.ContentProposalAdapterExtended;
import com.amalto.workbench.providers.XObjectEditorInput;
import com.amalto.workbench.utils.EInputTemplate;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.WidgetUtils;
import com.amalto.workbench.utils.XmlUtil;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSGetServicesList;
import com.amalto.workbench.webservices.WSRoutingRule;
import com.amalto.workbench.webservices.WSRoutingRuleExpression;
import com.amalto.workbench.webservices.WSServiceGetDocument;
import com.amalto.workbench.webservices.WSServicesList;
import com.amalto.workbench.webservices.WSServicesListItem;
import com.amalto.workbench.webservices.WSString;
import com.amalto.workbench.widgets.ComplexTableViewerColumn;
import com.amalto.workbench.widgets.ConditionWidget;
import com.amalto.workbench.widgets.TisTableViewer;
import com.amalto.workbench.widgets.xmleditor.ExtensibleContentEditor;
import com.amalto.workbench.widgets.xmleditor.ExtensibleContentEditorPage;
import com.amalto.workbench.widgets.xmleditor.ExtensibleContentEditorPageDescription;
import com.amalto.workbench.widgets.xmleditor.ExtensibleContentEditorPageListener;
import com.amalto.workbench.widgets.xmleditor.ExtensibleEditorContent;
import com.amalto.workbench.widgets.xmleditor.ExtensibleTextContentEditorPageCreator;
import com.amalto.workbench.widgets.xmleditor.TriggerCallProcessSourcePageCreator;
import com.amalto.workbench.widgets.xmleditor.infoholder.ExternalInfoHolder;

public class RoutingRuleMainPage extends AMainPageV2 {

    private static Log log = LogFactory.getLog(RoutingRuleMainPage.class);

    protected Text descriptionText;

    protected Text objectTypeText; // Concept

    protected Button isSynchronousButton;

    protected Combo serviceNameCombo;

    // protected Text serviceParametersText;
    protected ExtensibleContentEditor serviceParametersEditor;

    private ParameterEditorListener parameterEditorListener;

    private Map<String, ArrayList<ExternalInfoHolder<?>>> externalInfoName2Holder = new HashMap<String, ArrayList<ExternalInfoHolder<?>>>();

    // protected ListViewer routingExpressionsViewer;

    protected Button xpathButton;

    protected Combo operatorCombo;

    protected Text rightValueText;

    protected Button defultParameterBtn;

    protected DropTarget windowTarget;

    protected boolean refreshing = false;

    protected boolean comitting = false;

    protected TreeParent treeParent;

    private static String ROUTE_SERVICE = "amalto/local/service/";//$NON-NLS-1$

    protected String dataModelName;

    private Text conditionText;

    private final static String EXCONTENTEDITOR_ID = "trigger";//$NON-NLS-1$

    protected ComplexTableViewerColumn[] conditionsColumns = new ComplexTableViewerColumn[] {
            new ComplexTableViewerColumn("XPath", false, "newXPath", "newXPath", "", ComplexTableViewerColumn.XPATH_STYLE,//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                    new String[] {}, 0),
            new ComplexTableViewerColumn("Operator", false, "", "", "", ComplexTableViewerColumn.COMBO_STYLE,//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$
                    IConstants.ROUTE_CONDITION_OPERATORS, 0), new ComplexTableViewerColumn("Value", false, "", ""),//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
            new ComplexTableViewerColumn("Condition Id", false, "", "", true) };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

    private TisTableViewer conditionViewer;

    private Button deactiveButton;

    ContentProposalAdapterExtended adapter;

    private Text orderText;

    private Label orderLabel;

    public String getDataModelName() {
        return dataModelName;
    }

    public void setDataModelName(String dataModelName) {
        this.dataModelName = dataModelName;
    }

    public RoutingRuleMainPage(FormEditor editor) {
        super(editor, RoutingRuleMainPage.class.getName(), Messages.bind(
                Messages.triggerLabel,
                ((XObjectEditorInput) editor.getEditorInput()).getName()
                        + Util.getRevision((TreeObject) ((XObjectEditorInput) editor.getEditorInput()).getModel())));
        // get Version information
        try {

            treeParent = this.getXObject().getParent();
            if (treeParent == null && !isLocalInput()) {// if it is a new page,treeParent should be ROUTING_RULE
                treeParent = this.getXObject().getServerRoot().findServerFolder(TreeObject.ROUTING_RULE);
            }

            initExternalInfoHolder();

        } catch (Exception e) {/* no versioning support on old cores */
        }
    }

    protected void initExternalInfoHolder() {

        try {
            ExternalInfoHolder<?> allProcessNamesHolder = ExternalInfoHolder.getAllProcessesNamesHolder(Util
                    .getMDMService(getXObject()));
            ExternalInfoHolder<?> allJobInfosHolder = ExternalInfoHolder.getAllJobInfosHolder(Util.getMDMService(getXObject()));
            ExternalInfoHolder<?> allVarCandidatesHolder = ExternalInfoHolder.getTriggerAllCallJobVarsCandidatesHolder();
            ExternalInfoHolder<?> mdmServerInfoHolder = ExternalInfoHolder.getAllMDMServerInfoHolder(Util
                    .getMDMService(getXObject()));

            initExternalInfoHolderForEachType("callprocess", new ExternalInfoHolder<?>[] { allProcessNamesHolder });//$NON-NLS-1$
            initExternalInfoHolderForEachType("callJob", new ExternalInfoHolder<?>[] { allJobInfosHolder, mdmServerInfoHolder,//$NON-NLS-1$
                    allVarCandidatesHolder });

        } catch (XtentisException e) {
            log.error(e.getMessage(), e);
        }

    }

    protected void initExternalInfoHolderForEachType(String operaType, ExternalInfoHolder<?>[] infoHolders) {

        ArrayList<ExternalInfoHolder<?>> externalInfoHolders = new ArrayList<ExternalInfoHolder<?>>();
        externalInfoHolders.addAll(Arrays.asList(infoHolders));
        externalInfoName2Holder.put(operaType, externalInfoHolders);

    }

    @Override
    protected void createCharacteristicsContent(FormToolkit toolkit, Composite charComposite) {

        try {

            // description
            Label descriptionLabel = toolkit.createLabel(charComposite, Messages.descriptionLabel, SWT.NULL);
            descriptionLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
            descriptionText = toolkit.createText(charComposite, "", SWT.BORDER);//$NON-NLS-1$
            descriptionText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            descriptionText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    if (refreshing) {
                        return;
                    }
                    markDirtyWithoutCommit();
                }
            });
            // Util.createCompDropTarget(descriptionText);
            // objectType

            Label objectTypeLabel = toolkit.createLabel(charComposite, Messages.entityLabel, SWT.NULL);
            objectTypeLabel.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, true, 1, 1));
            Composite typeComposite = toolkit.createComposite(charComposite);
            typeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
            GridLayout layout = new GridLayout(2, false);
            layout.marginLeft = 0;
            layout.marginWidth = 0;
            typeComposite.setLayout(layout);

            objectTypeText = toolkit.createText(typeComposite, "", SWT.BORDER);//$NON-NLS-1$
            objectTypeText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING, SWT.CENTER, false, false, 1, 1));
            ((GridData) objectTypeText.getLayoutData()).widthHint = 300;
            // objectTypeText.setLocation(descriptionText.getLocation().x,descriptionText.getLocation().y+10);
            objectTypeText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    if (refreshing) {
                        return;
                    }
                    markDirtyWithoutCommit();
                }
            });
            xpathButton = toolkit.createButton(typeComposite, "", SWT.PUSH);//$NON-NLS-1$
            xpathButton.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
            xpathButton.setToolTipText(Messages.entitySelectLabel);
            xpathButton.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(SelectionEvent e) {

                }

                public void widgetSelected(SelectionEvent e) {

                    XpathSelectDialog xpathDialog;
                    xpathDialog = getNewXpathDlg();
                    xpathDialog.setBlockOnOpen(true);
                    xpathDialog.open();
                    if (xpathDialog.getReturnCode() == Window.OK) {
                        String xpath = xpathDialog.getXpath();
                        int index = xpathDialog.getXpath().indexOf("/");//$NON-NLS-1$
                        if (index > 0) {
                            xpath = xpathDialog.getXpath().substring(0, index);
                        }
                        objectTypeText.setText(xpath);
                    }
                }

            });
            // xpathWidget1 = new XpathWidget("...",treeParent, toolkit,
            // charComposite,(AMainPageV2)RoutingRuleMainPage.this,false,false,dataModelName);

            Composite paramComposite = toolkit.createComposite(charComposite);
            GridLayout pcLayout = new GridLayout(4, false);
            pcLayout.marginLeft = 0;
            pcLayout.horizontalSpacing = 0;
            paramComposite.setLayout(pcLayout);
            GridData pcLayoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
            pcLayoutData.horizontalSpan = 2;
            pcLayoutData.horizontalIndent = 0;
            paramComposite.setLayoutData(pcLayoutData);

            // issynchronous Button
            isSynchronousButton = toolkit.createButton(paramComposite, Messages.executesynLabel, SWT.CHECK);
            GridData synBtnLayoutData = new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1);
            synBtnLayoutData.horizontalIndent = 0;
            isSynchronousButton.setLayoutData(synBtnLayoutData);
            isSynchronousButton.addMouseListener(new MouseListener() {

                public void mouseUp(MouseEvent e) {
                    orderLabel.setEnabled(isSynchronousButton.getSelection());
                    orderText.setEnabled(isSynchronousButton.getSelection());
                    if (isSynchronousButton.getSelection()) {
                        orderText.setText("0"); //$NON-NLS-1$
                    } else {
                        orderText.setText(""); //$NON-NLS-1$
                    }
                    // mark for need to save
                    markDirtyWithoutCommit();
                }

                public void mouseDoubleClick(MouseEvent e) {
                }

                public void mouseDown(MouseEvent e) {
                }
            });

            orderLabel = toolkit.createLabel(paramComposite, Messages.RoutingRuleMainPage_executeOrder);
            GridData olLayoutData = new GridData();
            olLayoutData.horizontalIndent = 10;
            orderLabel.setLayoutData(olLayoutData);
            orderLabel.setEnabled(false);

            orderText = toolkit.createText(paramComposite, "", SWT.BORDER | SWT.SINGLE); //$NON-NLS-1$
            GridData otLayoutData = new GridData(SWT.FILL, SWT.CENTER, false, false);
            otLayoutData.widthHint = 50;
            otLayoutData.horizontalIndent = 10;
            orderText.setLayoutData(otLayoutData);
            orderText.setEnabled(false);
            orderText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    markDirtyWithoutCommit();
                }
            });
            deactiveButton = toolkit.createButton(paramComposite, Messages.deactivateLabel, SWT.CHECK);
            GridData dbLayoutData = new GridData(SWT.BEGINNING, SWT.FILL, false, true, 1, 1);
            dbLayoutData.horizontalIndent = 25;
            deactiveButton.setLayoutData(dbLayoutData);
            deactiveButton.addMouseListener(new MouseListener() {

                public void mouseUp(MouseEvent e) {
                    // mark for need to save
                    markDirtyWithoutCommit();
                }

                public void mouseDoubleClick(MouseEvent e) {
                }

                public void mouseDown(MouseEvent e) {
                }
            });

            // Routing Expressions
            Composite serviceGroup = this.getNewSectionComposite(Messages.serviceLabel);
            serviceGroup.setLayout(new GridLayout(2, false));
            // Service Name
            Label serviceNameLabel = toolkit.createLabel(serviceGroup, Messages.serviceJndiLabel, SWT.NULL);
            serviceNameLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 1, 1));

            Composite subPanel = toolkit.createComposite(serviceGroup);
            subPanel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
            subPanel.setLayout(new GridLayout(2, false));
            serviceNameCombo = new Combo(subPanel, SWT.DROP_DOWN | SWT.SINGLE | SWT.READ_ONLY);
            serviceNameCombo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, true, 1, 1));
            ((GridData) serviceNameCombo.getLayoutData()).widthHint = 300;
            serviceNameCombo.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {
                    if (refreshing) {
                        return;
                    }
                    String serviceName = serviceNameCombo.getText();
                    String helpPara = ""; //$NON-NLS-1$
                    if (!"".equals(serviceName) && !serviceName.equals(null)) { //$NON-NLS-1$
                        if (EInputTemplate.getXtentisObjexts().get(serviceName) != null) {
                            helpPara = EInputTemplate.getXtentisObjexts().get(serviceName).getContent();
                        } else {
                            helpPara = ""; //$NON-NLS-1$
                        }
                    }
                    // serviceParametersText.setText(XmlUtil.formatXmlSource(helpPara));
                    refreshParameterEditor(serviceName);

                    serviceParametersEditor.setContent(XmlUtil.formatXmlSource(helpPara));
                    markDirtyWithoutCommit();
                    initParamterProposal(serviceNameCombo.getText());
                }

            });

            initServiceNameCombo();
            // default parameters button
            defultParameterBtn = toolkit.createButton(subPanel, "", SWT.PUSH);//$NON-NLS-1$
            defultParameterBtn.setImage(ImageCache.getCreatedImage(EImage.HELP_CONTENTS.getPath()));
            defultParameterBtn.setToolTipText(Messages.helpLabel);
            defultParameterBtn.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
            defultParameterBtn.addSelectionListener(new SelectionListener() {

                public void widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent e) {
                };

                public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                    if (serviceNameCombo.getText().trim().length() == 0) {
                        return;
                    }
                    String doc = "";//$NON-NLS-1$
                    String desc = "";//$NON-NLS-1$
                    // WSRoutingRule wsObject = (WSRoutingRule) (getXObject().getWSObject());
                    try {

                        WSServiceGetDocument document = getServiceDocument(serviceNameCombo.getText().trim());

                        doc = document.getDocument();
                        desc = document.getDescription();
                    } catch (Exception e1) {
                        doc = "N/A";//$NON-NLS-1$
                    } finally {
                        showUpDialog(desc, doc);
                    }
                };

                private void showUpDialog(String desc, String doc) {
                    final PluginDetailsDialog dialog = new PluginDetailsDialog(getSite().getShell(), desc, doc, null,
                            Messages.documentionLabel);
                    dialog.addListener(new Listener() {

                        public void handleEvent(Event event) {
                            dialog.close();
                        }
                    });
                    dialog.create();
                    dialog.getShell().setText(serviceNameCombo.getText() + Messages.serviceDetailLabel);
                    dialog.setBlockOnOpen(true);
                    dialog.open();
                }
            });

            // Service Parameters
            Label serviceParametersLabel = toolkit.createLabel(serviceGroup, Messages.serviceParamLabel, SWT.NULL);
            serviceParametersLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, true, 2, 1));

            serviceParametersEditor = new ExtensibleContentEditor(serviceGroup, SWT.MULTI | SWT.WRAP, EXCONTENTEDITOR_ID);
            GridData gdServiceParameter = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1);
            // gdServiceParameter.widthHint = 200;
            gdServiceParameter.heightHint = 200;
            serviceParametersEditor.setLayoutData(gdServiceParameter);
            parameterEditorListener = new ParameterEditorListener();

            // serviceParametersText = toolkit.createText(serviceGroup, "",SWT.BORDER|SWT.MULTI|SWT.V_SCROLL|SWT.WRAP);
            // serviceParametersText.setLayoutData(
            // new GridData(SWT.FILL,SWT.FILL,true,false,2,1)
            // );
            // ((GridData) serviceParametersText.getLayoutData()).widthHint = 200;
            // ((GridData) serviceParametersText.getLayoutData()).heightHint = 120;

            // serviceParametersText.addModifyListener(new ModifyListener() {
            //
            // public void modifyText(ModifyEvent e) {
            // if (refreshing)
            // return;
            // markDirtyWithoutCommit();
            // }
            // });
            // // add by ymli.fix the bug:0011830. We can reuse the same ctrl+space in trigger configs.
            // serviceParametersText.addKeyListener(new KeyListener() {
            //
            // public void keyReleased(KeyEvent event) {
            //
            // // System.out.println("SWT.CTRL:"+SWT.CTRL);
            // int start = serviceParametersText.getSelection().x;
            // int end = serviceParametersText.getSelection().y;
            // if (event.stateMask == SWT.CTRL && event.keyCode == 17) {
            // // if(event.keyCode == SWT.F2){
            // ResourceSelectDialog dialog = new ResourceSelectDialog(getSite().getShell(), treeParent.getParent(),
            // "Select a resource node", ServerView.show().getSite());
            // dialog.setBlockOnOpen(true);
            // dialog.open();
            // if (dialog.getReturnCode() == Window.OK) {
            // String xpath = dialog.getXpath();
            // String textHead = serviceParametersText.getText(0, start - 1);
            // String textEnd = serviceParametersText.getText(end, serviceParametersText.getText().length());
            // serviceParametersText.setText(textHead + xpath + textEnd);
            // serviceParametersText.setSelection(start, start + xpath.length());
            // markDirtyWithoutCommit();
            // }
            // }
            // }
            //
            // public void keyPressed(KeyEvent e) {
            // }
            // });
            // Routing Expressions
            Composite routingExpressionsGroup = this.getNewSectionComposite(Messages.triggerExpressionLabel);
            routingExpressionsGroup.setLayout(new GridLayout(1, true));

            conditionsColumns[0].setColumnWidth(250);
            conditionsColumns[1].setColumnWidth(150);
            conditionsColumns[2].setColumnWidth(250);
            conditionsColumns[3].setColumnWidth(120);
            conditionViewer = getNewTisTableViewer(toolkit, routingExpressionsGroup);
            TreeParent treeParent = (TreeParent) getAdapter(TreeParent.class);
            conditionViewer.setTreeParent(treeParent);
            conditionViewer.setDatamodelName("UpdateReport"); //$NON-NLS-1$
            conditionViewer.setXpath(true);
            conditionViewer.setMainPage(this);
            conditionViewer.setAddMulti(true);
            conditionViewer.create();
            conditionViewer.setHeight(110);

            // and or not condition
            ConditionWidget conditionWidget = new ConditionWidget(routingExpressionsGroup, toolkit, this);
            conditionText = conditionWidget.getConditionText();
            conditionText.addModifyListener(new ModifyListener() {

                public void modifyText(ModifyEvent e) {

                    if (!refreshing) {
                        markDirtyWithoutCommit();
                    }
                }

            });

            wrap.Wrap(this, conditionViewer);

            // make the Page window a DropTarget - we need to dispose it
            windowTarget = new DropTarget(this.getPartControl(), DND.DROP_MOVE);
            windowTarget.setTransfer(new Transfer[] { TextTransfer.getInstance() });
            windowTarget.addDropListener(new DCDropTargetListener());
            refreshData();
            conditionText.addFocusListener(new FocusListener() {

                public void focusLost(FocusEvent e) {
                    // adapter.resetPosition();
                }

                public void focusGained(FocusEvent e) {
                    initConditionProposal();
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }// createCharacteristicsContent

    /**
     * DOC hbhong Comment method "getNewXpathDlg".
     *
     * @return
     */
    protected XpathSelectDialog getNewXpathDlg() {
        return new XpathSelectDialog(getSite().getShell(), treeParent, Messages.selectEntityLabel, getSite(), false,
                dataModelName);
    }

    protected WSServiceGetDocument getServiceDocument(String jndiName) {
        TMDMService service = getService();
        if (service != null) {
            return service.getServiceDocument(new WSString(jndiName));
        }
        return null;
    }

    protected TisTableViewer getNewTisTableViewer(FormToolkit toolkit, Composite routingExpressionsGroup) {
        return new TisTableViewer(Arrays.asList(conditionsColumns), toolkit, routingExpressionsGroup);
    }

    /**
     * DOC hbhong Comment method "initServiceNameCombo".
     *
     * @throws XtentisException
     */
    protected void initServiceNameCombo() throws XtentisException {
        WSServicesList list = Util.getMDMService(getXObject()).getServicesList(new WSGetServicesList(""));//$NON-NLS-1$
        List<WSServicesListItem> items = list.getItem();
        if (items != null) {
            String[] sortedList = new String[items.size()];
            for (int i = 0; i < items.size(); i++) {
                sortedList[i] = items.get(i).getJndiName();
            }
            Arrays.sort(sortedList);
            for (String element : sortedList) {
                serviceNameCombo.add(element);
            }
            // serviceNameCombo.add("");
        }
    }

    private void addSourceServiceParameterEditorPage(String serviceName) {

        if ("callprocess".equals(serviceName)) { //$NON-NLS-1$
            serviceParametersEditor.addPage(new ExtensibleContentEditorPageDescription("Source", Integer.MAX_VALUE,//$NON-NLS-1$
                    new TriggerCallProcessSourcePageCreator(), false), serviceName);
        } else {
            serviceParametersEditor.addPage(new ExtensibleContentEditorPageDescription("Source", Integer.MAX_VALUE,//$NON-NLS-1$
                    new ExtensibleTextContentEditorPageCreator(), false), serviceName);
        }
    }

    private void initConditionProposal() {
        // add content proposal to conditions
        java.util.List<Line> lines = (java.util.List<Line>) conditionViewer.getViewer().getInput();
        java.util.List<String> proposals = new ArrayList<String>();
        for (Line line : lines) {
            String value = line.keyValues.get(3).value;
            if (value != null && value.trim().length() > 0) {
                proposals.add(value);
            }
        }
        adapter = WidgetUtils.addContentProposal(conditionText, proposals.toArray(new String[proposals.size()]), new char[] {
                ' ', '(' });
        adapter.setPopupSize(new Point(120, 100));

    }

    private void initParamterProposal(String jndi) {
        if (externalInfoName2Holder.containsKey(jndi)) {
            // if ("callprocess".equals(jndi)) {
            // // add content proposal to paramter
            // WSTransformerV2PK[] transformerPKs = null;
            // try {
            // transformerPKs = Util.getPort(getXObject()).getTransformerV2PKs(new WSGetTransformerV2PKs(""))
            // .getWSTransformerV2PK();
            // } catch (Exception e) {
            // System.out.println("No Transformers");
            // }
            // java.util.List<String> proposals = new ArrayList<String>();
            // if (transformerPKs != null)
            // for (WSTransformerV2PK pk : transformerPKs) {
            // if (pk.getPk() != null && pk.getPk().length() > 0)
            // proposals.add(pk.getPk());
            // }
            // // ContentProposalAdapterExtended adapter = WidgetUtils.addContentProposal(serviceParametersText,
            // // (String[]) proposals.toArray(new String[proposals.size()]), new char[] { ' ', '=' });
            // // adapter.setPopupSize(new Point(300, 250));

            serviceParametersEditor.reloadExternalInfo();
        }
    }

    @Override
    protected void refreshData() {
        try {

            if (this.comitting) {
                return;
            }

            this.refreshing = true;

            WSRoutingRule wsRoutingRule = (WSRoutingRule) (getXObject().getWsObject());
            descriptionText.setText(wsRoutingRule.getDescription());
            isSynchronousButton.setSelection(wsRoutingRule.isSynchronous());
            if (wsRoutingRule.isDeactive() != null) {
                deactiveButton.setSelection(wsRoutingRule.isDeactive());
            }

            if (wsRoutingRule.isSynchronous()) {
                orderLabel.setEnabled(true);
                orderText.setEnabled(true);
                orderText.setText(String.valueOf(wsRoutingRule.getExecuteOrder()));
            }
            // serviceNameText.setText(wsRoutingRule.getServiceJNDI().replaceFirst("amalto/local/service/", ""));
            serviceNameCombo.setText(wsRoutingRule.getServiceJNDI().replaceFirst("amalto/local/service/", ""));//$NON-NLS-1$//$NON-NLS-2$
            // serviceParametersText.setText(wsRoutingRule.getParameters() == null ? "" :
            // XmlUtil.formatXmlSource(wsRoutingRule
            // .getParameters()));
            refreshParameterEditor(serviceNameCombo.getText().trim());
            serviceParametersEditor.setContent(wsRoutingRule.getParameters() == null ? "" : XmlUtil.formatXmlSource(wsRoutingRule//$NON-NLS-1$
                    .getParameters()));

            objectTypeText.setText(wsRoutingRule.getConcept());
            // xpathWidget1.setText(wsRoutingRule.getConcept());

            java.util.List<Line> lines = new ArrayList<Line>();

            for (WSRoutingRuleExpression wc : wsRoutingRule.getWsRoutingRuleExpressions()) {
                Line line = new Line(conditionsColumns, Util.convertRouteCondition(wc));
                lines.add(line);
            }
            conditionViewer.getViewer().setInput(lines);

            if (wsRoutingRule.getCondition() != null) {
                conditionText.setText(wsRoutingRule.getCondition());
            }
            this.refreshing = false;
            if (objectTypeText.getText().length() > 0 && !objectTypeText.getText().equals("*")) {//$NON-NLS-1$
                conditionViewer.setConceptName(objectTypeText.getText());
            }
            initParamterProposal(serviceNameCombo.getText());
            // initConditionProposal();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.errorMsgLabel,
                    Messages.bind(Messages.errorMsgLabelX, e.getLocalizedMessage()));
        }
    }

    @Override
    protected void commit() {
        try {

            if (this.refreshing) {
                return;
            }
            this.comitting = true;

            WSRoutingRule ws = (WSRoutingRule) (getXObject().getWsObject());
            ws.setDescription(descriptionText.getText());
            ws.setConcept(objectTypeText.getText());

            ws.setServiceJNDI(serviceNameCombo.getText().contains("/") ? serviceNameCombo.getText() : "amalto/local/service/"//$NON-NLS-1$//$NON-NLS-2$
                    + serviceNameCombo.getText());
            // ws.setParameters("".equals(serviceParametersText.getText()) ? null : serviceParametersText.getText());

            String curServiceParameter = serviceParametersEditor.getContent().getContent();
            ws.setParameters("".equals(curServiceParameter) ? null : curServiceParameter);//$NON-NLS-1$
            serviceParametersEditor.clearExternalResources();

            ws.setSynchronous(isSynchronousButton.getSelection());
            ws.setDeactive(deactiveButton.getSelection());

            if (isSynchronousButton.getSelection()) {
                String orderStr = orderText.getText().trim();
                if (orderStr.isEmpty()) {
                    orderStr = "0"; //$NON-NLS-1$
                }
                try {
                    int order = Integer.parseInt(orderStr);
                    ws.setExecuteOrder(order);
                } catch (Exception e) {
                }
            } else {
                ws.setExecuteOrder(0);
            }

            java.util.List<Line> lines = (java.util.List<Line>) conditionViewer.getViewer().getInput();
            java.util.List<WSRoutingRuleExpression> wclist = new ArrayList<WSRoutingRuleExpression>();
            for (Line item : lines) {
                String[] values = new String[] { item.keyValues.get(0).value, item.keyValues.get(1).value,
                        item.keyValues.get(2).value, item.keyValues.get(3).value };
                WSRoutingRuleExpression wc = Util.convertLineRoute(values);
                wclist.add(wc);
            }
            ws.getWsRoutingRuleExpressions().clear();
            ws.getWsRoutingRuleExpressions().addAll(wclist);

            // WSRoutingRuleExpressions refreshed by viewer
            ws.setCondition(conditionText.getText());
            this.comitting = false;

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageDialog.openError(this.getSite().getShell(), Messages.errorCommitLabel,
                    Messages.bind(Messages.errorCommitLabelX, e.getLocalizedMessage()));
        }
    }

    public void textChanged(TextEvent event) {
        markDirtyWithoutCommit();
    }

    private boolean isValidDigit(String text) {
        if (text.trim().isEmpty()) {
            return true;
        }

        Pattern pattern = Pattern.compile("[0-9]*");//$NON-NLS-1$
        Matcher matcher = pattern.matcher(text.trim());
        if (!matcher.matches()) {
            return false;
        }

        if (text.trim().startsWith("0") && text.trim().length() > 1) { //$NON-NLS-1$
            return false;
        }

        return true;
    }

    @Override
    public void dispose() {
        super.dispose();

        externalInfoName2Holder.clear();
        externalInfoName2Holder = null;

        serviceParametersEditor.dispose();

        windowTarget.dispose();
    }

    /****************************************************************************
     * DND
     ****************************************************************************/

    class DCDragSourceListener implements DragSourceListener {

        private int selected;

        public void dragFinished(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof List) && ((event.detail & DND.DROP_MOVE) == DND.DROP_MOVE)) {
                ((List) control).remove(selected);
                RoutingRuleMainPage.this.markDirtyWithoutCommit();
            }
        }

        public void dragSetData(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof org.eclipse.swt.widgets.List)) {
                if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
                    this.selected = ((org.eclipse.swt.widgets.List) control).getSelectionIndex();
                    event.data = ((org.eclipse.swt.widgets.List) control).getSelection()[0];
                }
            }
        }

        public void dragStart(DragSourceEvent event) {
            Control control = ((DragSource) event.widget).getControl();
            if ((control instanceof org.eclipse.swt.widgets.List)) {
                event.doit = (((org.eclipse.swt.widgets.List) control).getItemCount() > 0);
            }
        }
    }

    class DCDropTargetListener implements DropTargetListener {

        public void dragEnter(DropTargetEvent event) {
            // priority to copy
            if ((event.operations & DND.DROP_COPY) == DND.DROP_COPY) {
                event.detail = DND.DROP_COPY;
            } else if ((event.operations & DND.DROP_MOVE) == DND.DROP_MOVE) {
                event.detail = DND.DROP_MOVE;
            } else {
                event.detail = DND.DROP_NONE;
            }
        }

        public void dragLeave(DropTargetEvent event) {
        }

        public void dragOperationChanged(DropTargetEvent event) {
        }

        public void dragOver(DropTargetEvent event) {
        }

        public void drop(DropTargetEvent event) {
            Control control = ((DropTarget) event.widget).getControl();
            if ((control instanceof List) && ((event.operations & DND.DROP_COPY) == DND.DROP_COPY)) {
                if (TextTransfer.getInstance().isSupportedType(event.currentDataType)) {
                    if (!Arrays.asList(((org.eclipse.swt.widgets.List) control).getItems()).contains(event.data)) {
                        ((List) control).add(event.data);
                        RoutingRuleMainPage.this.markDirtyWithoutCommit();
                    }
                }
            }
        }

        public void dropAccept(DropTargetEvent event) {
        }

    }

    // private void createCompDropTarget() {
    // DropTarget dropTarget = new DropTarget(serviceParametersText, DND.DROP_MOVE | DND.DROP_LINK);
    // // dropTarget.setTransfer(new ByteArrayTransfer[] { });
    // dropTarget.setTransfer(new TreeObjectTransfer[] { TreeObjectTransfer.getInstance() });
    // dropTarget.addDropListener(new DropTargetAdapter() {
    //
    // public void dragEnter(DropTargetEvent event) {
    // }
    //
    // public void dragLeave(DropTargetEvent event) {
    // }
    //
    // public void dragOver(DropTargetEvent event) {
    // event.feedback |= DND.FEEDBACK_EXPAND | DND.FEEDBACK_SCROLL;
    // }
    //
    // public void drop(DropTargetEvent event) {
    // if (event.data instanceof TreeObject[])
    // if (((TreeObject[]) event.data)[0].getType() == TreeObject.TRANSFORMER)
    // serviceParametersText.setText(serviceParametersText.getText().replace("?", "")
    // + ((TreeObject[]) event.data)[0].getDisplayName());
    // else
    // serviceParametersText.setText(serviceParametersText.getText()
    // + ((TreeObject[]) event.data)[0].getDisplayName());
    // }
    // });
    //
    // }
    @Override
    public Object getAdapter(Class adapter) {
        if (adapter == TreeParent.class) {
            return Util.getServerTreeParent(getXObject());
        }
        return super.getAdapter(adapter);
    }

    @Override
    public boolean beforeDoSave() {
        if (isSynchronousButton.getSelection()) {
            String text = orderText.getText();
            if (!isValidDigit(text)) {
                MessageDialog.openError(getSite().getShell(), Messages._Error, Messages.RoutingRuleMainPage_invalidOrder);
                return false;
            }
        }

        if (serviceNameCombo.getText() == null || serviceNameCombo.getText().length() == 0) {
            MessageDialog.openError(this.getSite().getShell(), Messages.errorSaveTitleLabel, Messages.errorSaveMsgLabel);
            return false;
        }

        return true;
    }

    @Override
    protected void createActions() {

    }

    private void refreshParameterEditor(String serviceName) {
        serviceParametersEditor.setPageGroup(serviceName);
        addSourceServiceParameterEditorPage(serviceName);
        serviceParametersEditor.addExtensibleXMLEditorPageListener(parameterEditorListener);
        if (externalInfoName2Holder.containsKey(serviceName)) {
            for (ExternalInfoHolder<?> eachInfoHolder : externalInfoName2Holder.get(serviceName)) {
                serviceParametersEditor.setExternalInfoHolder(eachInfoHolder);
            }
        }
    }

    class ParameterEditorListener implements ExtensibleContentEditorPageListener {

        public void onXMLDocumentChanged(ExtensibleContentEditorPage source, ExtensibleEditorContent newCotent) {

            if (refreshing) {
                return;
            }
            markDirtyWithoutCommit();

        }

    }
}
