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
package org.talend.mdm.repository.ui.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.views.markers.MarkerField;
import org.eclipse.ui.views.markers.internal.MarkerSupportRegistry;
import org.eclipse.wst.validation.internal.ValidationResultSummary;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.marker.ValidateMarkerUtil;
import org.talend.mdm.repository.core.service.IModelValidationService;
import org.talend.mdm.repository.core.validate.IValidationPreference;
import org.talend.mdm.repository.core.validate.datamodel.MarkerEntry;
import org.talend.mdm.repository.core.validate.datamodel.model.IDataModelMarkerConst;
import org.talend.mdm.repository.core.validate.datamodel.validator.rule.IComponentValidationRule;
import org.talend.mdm.repository.core.validate.i18n.Messages;
import org.talend.mdm.repository.ui.markers.datamodel.ElementPathField;
import org.talend.mdm.repository.ui.markers.datamodel.ElementTypeField;
import org.talend.mdm.repository.ui.markers.datamodel.EntityField;
import org.talend.mdm.repository.ui.markers.datamodel.ModelField;

/**
 * created by Huang Zhenlong on Jan 25, 2013 Detailled comment
 *
 */
@SuppressWarnings("restriction")
public class ValidationResultDialog extends IconAndMessageDialog {

    private static final String MARKERFIELD_DESC = "org.eclipse.ui.ide.allSeverityField"; //$NON-NLS-1$

    private class MarkerColumnLabelProvider extends ColumnLabelProvider {

        MarkerField field;

        private ResourceManager imageManager;

        /**
         * Create a MarkerViewLabelProvider on a field.
         *
         * @param field
         */
        MarkerColumnLabelProvider(MarkerField field) {
            FieldDecorationRegistry.getDefault();
            this.field = field;
            imageManager = new LocalResourceManager(IDEWorkbenchPlugin.getDefault().getResourceManager());
            field.setImageManager(imageManager);
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.jface.viewers.BaseLabelProvider#dispose()
         */
        @Override
        public void dispose() {
            super.dispose();
            imageManager.dispose();
        }

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.jface.viewers.ColumnLabelProvider#update(org.eclipse.jface.viewers.ViewerCell)
         */
        @Override
        public void update(ViewerCell cell) {
            field.update(cell);
        }
    }

    class MessageComparator extends ViewerComparator implements IDataModelMarkerConst {

        protected static final String BLANK = ""; //$NON-NLS-1$

        /*
         * (non-Javadoc)
         *
         * @see org.eclipse.jface.viewers.ViewerComparator#compare(org.eclipse.jface.viewers.Viewer, java.lang.Object,
         * java.lang.Object)
         */
        @Override
        public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1 instanceof MarkerEntry && e2 instanceof MarkerEntry) {
                MarkerEntry m1 = (MarkerEntry) e1;
                MarkerEntry m2 = (MarkerEntry) e2;

                // 1 to compare severity

                int result = compareSeverity(m2, m1);
                if (result == 0) {
                    // 2: to compare model name
                    result = compareModelName(m1, m2);
                    if (result == 0) {
                        // 3: to compare entity name
                        result = compareEntityName(m1, m2);
                        if (result == 0) {
                            // 4: to compare type
                            result = compareEntityType(m1, m2);
                            if (result == 0) {
                                // 5: to compare path
                                result = comparePath(m1, m2);

                            }
                        }
                    }
                }
                return result;
            }
            return 0;
        }

        private int compareSeverity(MarkerEntry m1, MarkerEntry m2) {
            int s1 = m1.getAttributeValue(IMarker.SEVERITY, 0);
            int s2 = m2.getAttributeValue(IMarker.SEVERITY, 0);
            return s1 - s2;
        }

        private int compareModelName(MarkerEntry m1, MarkerEntry m2) {
            String name1 = m1.getAttributeValue(DATA_MODEL, BLANK);
            String name2 = m2.getAttributeValue(DATA_MODEL, BLANK);
            return name1.compareTo(name2);
        }

        private int compareEntityName(MarkerEntry m1, MarkerEntry m2) {
            int group1 = m1.getAttributeValue(MSG_GROUP, IComponentValidationRule.MSG_GROUP_UNKNOW);
            int group2 = m2.getAttributeValue(MSG_GROUP, IComponentValidationRule.MSG_GROUP_UNKNOW);
            String name1 = BLANK;
            if (isBelongGroup(IComponentValidationRule.MSG_GROUP_ELEMENT, group1)
                    || isBelongGroup(IComponentValidationRule.MSG_GROUP_ENTITY, group1)) {
                name1 = m1.getAttributeValue(ENTITY, BLANK);
            }
            String name2 = BLANK;
            if (isBelongGroup(IComponentValidationRule.MSG_GROUP_ELEMENT, group2)
                    || isBelongGroup(IComponentValidationRule.MSG_GROUP_ENTITY, group2)) {
                name2 = m2.getAttributeValue(ENTITY, BLANK);

            }
            return compareString(name1, name2);
        }

        private int compareString(String str1, String str2) {
            int len1 = str1.length();
            int len2 = str2.length();
            if (len1 == 0 && len2 == 0) {
                return 0;
            }
            if (len1 > 0 && len2 == 0) {
                return -1;
            }
            if (len1 == 0 && len2 > 0) {
                return 1;
            }
            return str1.compareTo(str2);
        }

        protected boolean isBelongGroup(int group, int cur) {
            return (group & cur) == group;
        }

        private int compareEntityType(MarkerEntry m1, MarkerEntry m2) {
            int group1 = m1.getAttributeValue(MSG_GROUP, IComponentValidationRule.MSG_GROUP_UNKNOW);
            int group2 = m2.getAttributeValue(MSG_GROUP, IComponentValidationRule.MSG_GROUP_UNKNOW);

            String name1 = BLANK;
            String name2 = BLANK;
            if (isBelongGroup(IComponentValidationRule.MSG_GROUP_ELEMENT, group1)
                    || isBelongGroup(IComponentValidationRule.MSG_GROUP_TYPE, group1)) {
                name1 = m1.getAttributeValue(ELEMENT_TYPE, BLANK);
            }
            if (isBelongGroup(IComponentValidationRule.MSG_GROUP_ELEMENT, group2)) {
                name2 = m2.getAttributeValue(ELEMENT_TYPE, BLANK);
            }

            return compareString(name1, name2);
        }

        private int comparePath(MarkerEntry m1, MarkerEntry m2) {
            String name1 = m1.getAttributeValue(PATH, BLANK);
            String name2 = m2.getAttributeValue(PATH, BLANK);
            return name1.compareTo(name2);
        }
    }

    private Composite detailsComposite;

    private Button detailsButton;

    private ValidationResultSummary result;

    private IValidationPreference validationPref;

    private List<MarkerEntry> markerEntries;

    private Button skipErrBun;

    private Button skipErrWarningBun;

    private Button cancelAllBun;

    public ValidationResultDialog(Shell parentShell, ValidationResultSummary result, IValidationPreference validationPref,
            Map<IRepositoryViewObject, IFile> viewFileMap) {
        super(parentShell);
        this.result = result;
        this.validationPref = validationPref;
        this.markerEntries = createMarkerEntries(viewFileMap);
        initMessage(viewFileMap.size());
        setShellStyle(getShellStyle() | SWT.RESIZE);
    }

    private List<MarkerEntry> createMarkerEntries(Map<IRepositoryViewObject, IFile> viewFileMap) {
        List<MarkerEntry> ret = new ArrayList<MarkerEntry>();

        for (IRepositoryViewObject viewObj : viewFileMap.keySet()) {
            IFile file = viewFileMap.get(viewObj);
            if (file != null) {
                ERepositoryObjectType type = viewObj.getRepositoryObjectType();
                if (type != null) {
                    try {
                        String[] types = ValidateMarkerUtil.getMarkerTypeByViewType(type);
                        if (types != null) {
                            for (String markerId : types) {
                                IMarker[] markers = file.findMarkers(markerId, false, IResource.DEPTH_ONE);
                                for (IMarker marker : markers) {
                                    ret.add(new MarkerEntry(marker));
                                }
                            }
                        }
                    } catch (CoreException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return ret;
    }

    private void initMessage(int totalSize) {
        int errors = result.getSeverityError();
        int warnings = result.getSeverityWarning();
        this.message = NLS.bind(Messages.ValidationResultDialog_Message, new Object[] { totalSize, errors, warnings });
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(Messages.ValidationResultDialog_Title);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite dialogAreaComposite = (Composite) super.createDialogArea(parent);
        ((GridData) dialogAreaComposite.getLayoutData()).horizontalSpan = 2;

        createMessageArea(dialogAreaComposite);
        createRadioButtons(dialogAreaComposite);
        createCheckboxBun(dialogAreaComposite);
        return dialogAreaComposite;
    }

    /**
     * DOC HHB Comment method "createRadioButtons".
     *
     * @param dialogAreaComposite
     */
    private void createRadioButtons(Composite dialogAreaComposite) {
        int condition = validationPref.getValidationCondition();
        if (condition == IModelValidationService.VALIDATE_BEFORE_DEPLOY) {
            skipErrBun = createRadioButton(dialogAreaComposite, Messages.ValidationPreferencePage_SkipErrorsButton_Text,
                    IModelValidationService.BUTTON_SKIP_ERROR);
            skipErrWarningBun = createRadioButton(dialogAreaComposite,
                    Messages.ValidationPreferencePage_SkipErrorsAndWarningsButton_Text,
                    IModelValidationService.BUTTON_SKIP_ERROR_WARNING);

            cancelAllBun = createRadioButton(dialogAreaComposite, Messages.ValidationPreferencePage_CancelButton_Text,
                    IModelValidationService.BUTTON_CANCEL);
            skipErrBun.setSelection(true);
        }

    }

    private int getSelectedRadioBun() {
        Button bun;
        if (skipErrBun.getSelection()) {
            bun = skipErrBun;
        } else if (skipErrWarningBun.getSelection()) {
            bun = skipErrWarningBun;
        } else {
            bun = cancelAllBun;
        }
        return (Integer) bun.getData();
    }

    private Button createRadioButton(Composite parent, String text, int id) {
        Button bun = new Button(parent, SWT.RADIO);
        bun.setText(text);
        bun.setData(new Integer(id));
        GridDataFactory.fillDefaults().align(SWT.LEFT, SWT.BEGINNING).applyTo(bun);
        return bun;
    }

    private void createCheckboxBun(Composite parent) {
        final Button button = new Button(parent, SWT.CHECK);
        button.setText(Messages.ValidationResultDialog_NotShowThis);
        button.setSelection(false);
        button.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                validationPref.setShowResults(!button.getSelection(), result);
            }

        });
    }

    @Override
    protected Control createMessageArea(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(false).applyTo(composite);
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).applyTo(composite);

        Control control = super.createMessageArea(composite);
        return control;
    }

    @Override
    protected Control createButtonBar(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(2).extendedMargins(5, -3, 5, 5).equalWidth(false).applyTo(composite);

        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).span(2, 1).applyTo(composite);
        composite.setFont(parent.getFont());
        createButtonsForButtonBar(composite);
        return composite;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        detailsButton = createButton(parent, IDialogConstants.DETAILS_ID, IDialogConstants.SHOW_DETAILS_LABEL, false);
        GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.CENTER).applyTo(detailsButton);

        Composite rightArea = new Composite(parent, SWT.NONE);
        GridLayoutFactory.fillDefaults().numColumns(0).equalWidth(true).applyTo(rightArea);
        GridDataFactory.fillDefaults().align(SWT.END, SWT.CENTER).grab(true, false).applyTo(rightArea);

        createButton(parent, IModelValidationService.BUTTON_OK, IDialogConstants.OK_LABEL, true);
    }

    @Override
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.DETAILS_ID == buttonId) {
            toggleDetailsArea();
            return;
        }
        int condition = validationPref.getValidationCondition();
        if (condition == IModelValidationService.VALIDATE_BEFORE_DEPLOY) {
            int returnCode = getSelectedRadioBun();
            setReturnCode(returnCode);
            if (!validationPref.shouldShowResults(result)) {
                validationPref.updateLastSelectedBun(returnCode, result);
            }
            close();
        } else {
            super.buttonPressed(buttonId);
        }

    }

    private void toggleDetailsArea() {
        Point windowSize = getShell().getSize();
        if (detailsComposite != null) {
            detailsComposite.dispose();
            detailsComposite = null;
            detailsButton.setText(IDialogConstants.SHOW_DETAILS_LABEL);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(2, 1).applyTo(dialogArea);
        } else {
            detailsComposite = createDetailsComposite((Composite) getContents());
            detailsButton.setText(IDialogConstants.HIDE_DETAILS_LABEL);
            GridDataFactory.fillDefaults().align(SWT.FILL, SWT.CENTER).grab(true, false).span(2, 1).applyTo(dialogArea);
        }
        ((Composite) getContents()).layout();
        Point newSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
        getShell().setSize(new Point(windowSize.x, newSize.y));
    }

    private Composite createDetailsComposite(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).span(2, 1).hint(5, 200).applyTo(composite);
        TableViewer tableViewer = new TableViewer(composite, SWT.FULL_SELECTION | SWT.BORDER);
        tableViewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
        createColumns(tableViewer);
        Table table = tableViewer.getTable();
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        tableViewer.setContentProvider(new ArrayContentProvider());
        tableViewer.setComparator(new MessageComparator());
        tableViewer.setInput(markerEntries);
        return composite;
    }

    private void createColumns(TableViewer tableViewer) {
        Table table = tableViewer.getTable();
        TableLayout layout = new TableLayout();
        String[] markerFieldIds = new String[] { MARKERFIELD_DESC, ModelField.ID, EntityField.ID, ElementTypeField.ID,
                ElementPathField.ID };
        for (int i = 0; i < markerFieldIds.length; i++) {
            TableViewerColumn column = new TableViewerColumn(tableViewer, SWT.NONE);
            column.getColumn().setResizable(true);
            column.getColumn().setMoveable(true);

            MarkerField markerField = MarkerSupportRegistry.getInstance().getField(markerFieldIds[i]);
            column.getColumn().setData("MARKER_FIELD", markerField); //$NON-NLS-1$
            column.setLabelProvider(new MarkerColumnLabelProvider(markerField));
            column.getColumn().setText(markerField.getColumnHeaderText());
            column.getColumn().setToolTipText(markerField.getColumnTooltipText());
            column.getColumn().setImage(markerField.getColumnHeaderImage());
            int columnWidth = -1;

            if (i == 0) {
                GC gc = new GC(table);
                gc.setFont(table.getFont());
                FontMetrics fontMetrics = gc.getFontMetrics();
                gc.dispose();
                columnWidth = Math.max(markerField.getDefaultColumnWidth(table), fontMetrics.getAverageCharWidth() * 5);
            }
            if (columnWidth < 0) {
                layout.addColumnData(new ColumnPixelData(markerField.getDefaultColumnWidth(table), true, true));
            } else {
                layout.addColumnData(new ColumnPixelData(columnWidth, true));
            }
        }
        table.setLayout(layout);
    }

    @Override
    protected Image getImage() {
        int error = result.getSeverityError();
        int warnings = result.getSeverityWarning();
        if (error > 0) {
            return getErrorImage();
        }
        if (warnings > 0) {
            return getWarningImage();
        }
        return getInfoImage();
    }

}
