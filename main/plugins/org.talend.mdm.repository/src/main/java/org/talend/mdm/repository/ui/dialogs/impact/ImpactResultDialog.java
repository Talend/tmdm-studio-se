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
package org.talend.mdm.repository.ui.dialogs.impact;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.service.ModelImpactAnalyseService;
import org.talend.mdm.repository.core.service.ModelImpactAnalyseService.Change;
import org.talend.mdm.repository.core.service.ModelImpactAnalyseService.EntitiesToDrop;
import org.talend.mdm.repository.core.service.ModelImpactAnalyseService.ImpactOperation;
import org.talend.mdm.repository.core.service.ModelImpactAnalyseService.Result;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.EclipseResourceManager;

import com.amalto.workbench.MDMWorbenchPlugin;

/**
 * created by HHB on 2014-3-11 Detailled comment
 *
 */
public class ImpactResultDialog extends Dialog {

    private class TreeContentProvider implements ITreeContentProvider {

        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        }

        @Override
        public void dispose() {
        }

        @Override
        public Object[] getElements(Object inputElement) {
            return getChildren(inputElement);
        }

        @Override
        public Object[] getChildren(Object parentElement) {
            if (parentElement instanceof Collection) {
                return ((Collection) parentElement).toArray();
            } else if (parentElement instanceof IRepositoryViewObject) {
                Result result = input.get(parentElement);
                List changes = result.getChanges();
                EntitiesToDrop entitiesToDrop = result.getEntitiesToDrop();
                if (entitiesToDrop != null && entitiesToDrop.getEntities() != null && !entitiesToDrop.getEntities().isEmpty()) {
                    changes.add(entitiesToDrop);
                }
                if (changes != null) {
                    return changes.toArray();
                }
            } else if (parentElement instanceof EntitiesToDrop) {
                List<String> entities = ((EntitiesToDrop) parentElement).getEntities();
                return entities.toArray();
            }
            return new Object[0];
        }

        @Override
        public Object getParent(Object element) {
            return null;
        }

        @Override
        public boolean hasChildren(Object element) {
            return getChildren(element).length > 0;
        }
    }

    private static final String[] OPERATIONS_HIGH = { Messages.ModelImpactAnalyseService_recreateTable,
            Messages.ModelImpactAnalyseService_cancelDeploying };

    private static final String[] OPERATIONS_FULL = { Messages.ModelImpactAnalyseService_recreateTable,
            Messages.ModelImpactAnalyseService_applyChange, Messages.ModelImpactAnalyseService_cancelDeploying };

    private static final String[] OPERATIONS_LITE = { Messages.ModelImpactAnalyseService_applyChange,
            Messages.ModelImpactAnalyseService_cancelDeploying };

    private TreeViewer treeViewer;

    private final Map<IRepositoryViewObject, Result> input;

    private final Map<IRepositoryViewObject, ImpactOperation> result = new HashMap<IRepositoryViewObject, ModelImpactAnalyseService.ImpactOperation>();

    private static final Image IMG_MODEL = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "icons/datamodel.png"); //$NON-NLS-1$;

    private static final Image IMG_ENTITY = EclipseResourceManager.getImage(MDMWorbenchPlugin.ID, "icons/concept.png"); //$NON-NLS-1$;

    private static final Image IMG_HIGH = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "icons/high.gif"); //$NON-NLS-1$;

    private static final Image IMG_MEDIUM = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "icons/medium.gif"); //$NON-NLS-1$;

    private static final Image IMG_LOW = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "icons/low.gif"); //$NON-NLS-1$;

    private class ViewerLabelProvider extends LabelProvider implements ITableLabelProvider {

        @Override
        public Image getImage(Object element) {
            return super.getImage(element);
        }

        @Override
        public String getText(Object element) {
            return super.getText(element);
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            if (columnIndex == 0) {
                if (element instanceof IRepositoryViewObject) {
                    return IMG_MODEL;
                } else if (element instanceof Change) {
                    switch (((Change) element).getSeverity()) {
                    case ModelImpactAnalyseService.HIGH:
                        return IMG_HIGH;
                    case ModelImpactAnalyseService.MEDIUM:
                        return IMG_MEDIUM;
                    case ModelImpactAnalyseService.LOW:
                        return IMG_LOW;
                    }
                } else if (element instanceof String) {
                    return IMG_ENTITY;
                }
            }
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof IRepositoryViewObject) {
                IRepositoryViewObject viewObject = (IRepositoryViewObject) element;
                if (columnIndex == 0) {

                    return viewObject.getLabel();
                } else if (columnIndex == 2) {
                    ImpactOperation operation = getOperation(viewObject);
                    return operation.getDescription();
                }

            } else if (element instanceof Change) {
                Change change = (Change) element;
                switch (columnIndex) {
                case 0:
                    return change.getMessage();
                case 1:
                    switch (change.getSeverity()) {
                    case ModelImpactAnalyseService.HIGH:
                        return Messages.ImpactResultDialog_high;
                    case ModelImpactAnalyseService.MEDIUM:
                        return Messages.ImpactResultDialog_medium;
                    case ModelImpactAnalyseService.LOW:
                        return Messages.ImpactResultDialog_low;
                    }
                    break;
                case 2:

                }

            } else if (element instanceof EntitiesToDrop && columnIndex == 0) {
                return Messages.ImpactResultDialog_recreatedEntities;
            } else if (element instanceof String && columnIndex == 0) {
                return (String) element;
            }
            return ""; //$NON-NLS-1$
        }
    }

    public ImpactResultDialog(Shell parentShell, Map<IRepositoryViewObject, Result> changes) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.input = changes;
    }

    /**
     * Create contents of the dialog.
     *
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        container.setLayout(new GridLayout(1, false));

        Label messageLabel = new Label(container, SWT.WRAP);
        messageLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        messageLabel.setText(Messages.ImpactResultDialog_titleMessage);

        treeViewer = new TreeViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
        Tree tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        GridData gd_tree = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
        gd_tree.verticalIndent = 5;
        tree.setLayoutData(gd_tree);

        TreeColumn changeColumn = new TreeColumn(tree, SWT.NONE);
        changeColumn.setWidth(340);
        changeColumn.setText(Messages.ImpactResultDialog_modelChange);

        TreeColumn levelColumn = new TreeColumn(tree, SWT.NONE);
        levelColumn.setWidth(60);
        levelColumn.setText(Messages.ImpactResultDialog_level);

        TreeColumn operationColumn = new TreeColumn(tree, SWT.NONE);
        operationColumn.setWidth(130);
        operationColumn.setText(Messages.ImpactResultDialog_operation);

        Group grpLevel = new Group(container, SWT.NONE);
        grpLevel.setText(Messages.ImpactResultDialog_changeLegend);
        grpLevel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        grpLevel.setLayout(new GridLayout(2, false));

        CLabel lowLabel = new CLabel(grpLevel, SWT.NONE);
        lowLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        lowLabel.setText(Messages.ImpactResultDialog_low);
        lowLabel.setImage(IMG_LOW);
        Label lowMsgLabel = new Label(grpLevel, SWT.WRAP);
        lowMsgLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        lowMsgLabel.setText(Messages.ImpactResultDialog_lowMessage);

        CLabel mediumLabel = new CLabel(grpLevel, SWT.NONE);
        mediumLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        mediumLabel.setText(Messages.ImpactResultDialog_medium);
        mediumLabel.setImage(IMG_MEDIUM);
        Label mediumMsgLabel = new Label(grpLevel, SWT.WRAP);
        mediumMsgLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        mediumMsgLabel.setText(Messages.ImpactResultDialog_mediumMessage);

        CLabel highLabel = new CLabel(grpLevel, SWT.NONE);
        highLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
        highLabel.setText(Messages.ImpactResultDialog_high);
        highLabel.setImage(IMG_HIGH);
        Label highMsgLabel = new Label(grpLevel, SWT.WRAP);
        highMsgLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        highMsgLabel.setText(Messages.ImpactResultDialog_highMessage);
        treeViewer.setContentProvider(new TreeContentProvider());
        treeViewer.setLabelProvider(new ViewerLabelProvider());

        initTree();
        return container;
    }

    private int getCurrentTopSeverityLevel(IRepositoryViewObject viewObj) {
        Result result = input.get(viewObj);
        List<Change> changes = result.getChanges();
        int level = 0;
        for (Change change : changes) {
            if (change.getSeverity() > level) {
                level = change.getSeverity();
            }
        }
        return level;

    }

    private ImpactOperation getOperation(IRepositoryViewObject viewObj) {
        ImpactOperation operation = result.get(viewObj);
        if (operation == null) {
            int topLevel = getCurrentTopSeverityLevel(viewObj);

            if (topLevel == ModelImpactAnalyseService.LOW || topLevel == ModelImpactAnalyseService.MEDIUM) {
                operation = ImpactOperation.APPLY_LOW_CHANGE;
            } else {
                operation = ImpactOperation.CANCEL;
            }

            result.put(viewObj, operation);
        }
        return operation;

    }

    private void initTree() {
        treeViewer.setInput(input.keySet());

        Tree tree = treeViewer.getTree();
        TreeItem[] tableItems = tree.getItems();
        System.out.println(tableItems.length);
        for (TreeItem item : tableItems) {
            Object data = item.getData();
            if (data != null && data instanceof IRepositoryViewObject) {
                final IRepositoryViewObject viewObj = (IRepositoryViewObject) data;
                final TreeEditor editor = new TreeEditor(tree);
                final CCombo combo = new CCombo(tree, SWT.READ_ONLY);
                // combo.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
                String[] items = getDefaultComboItems(viewObj);
                combo.setItems(items);
                ImpactOperation operation = getOperation(viewObj);
                combo.setText(operation.getDescription());
                combo.addSelectionListener(new SelectionAdapter() {

                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        modifyOperationValue(viewObj, combo.getSelectionIndex());
                    }

                });
                editor.grabHorizontal = true;
                editor.setEditor(combo, item, 2);

            }
        }
        //
        treeViewer.expandAll();

    }

    private void modifyOperationValue(IRepositoryViewObject viewObj, int index) {

        if (index < 0) {
            return;
        }

        int severity = getCurrentTopSeverityLevel(viewObj);

        switch (severity) {
        case ModelImpactAnalyseService.LOW:
            index += 1;
            break;
        case ModelImpactAnalyseService.MEDIUM:
            break;
        case ModelImpactAnalyseService.HIGH:
            if (index > 0) {
                index = 2;
            }
            break;
        }

        ImpactOperation operation = ImpactOperation.getOperation(index);
        result.put(viewObj, operation);

    }

    private String[] getDefaultComboItems(IRepositoryViewObject viewObj) {
        int severity = getCurrentTopSeverityLevel(viewObj);
        switch (severity) {
        case ModelImpactAnalyseService.LOW:
            return OPERATIONS_LITE;
        case ModelImpactAnalyseService.MEDIUM:
            return OPERATIONS_FULL;
        case ModelImpactAnalyseService.HIGH:
            return OPERATIONS_HIGH;
        default:
            return new String[0];
        }
    }

    /**
     * Create contents of the button bar.
     *
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(580, 495);
    }

    public Map<IRepositoryViewObject, ImpactOperation> getImpactConfiguration() {
        return result;
    }
}
