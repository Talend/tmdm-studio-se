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
package org.talend.mdm.repository.ui.contributor;

import java.text.SimpleDateFormat;
import java.util.List;

import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Project;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.model.repository.RepositoryObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.repository.ProjectManager;

/**
 * DOC jsxie class global comment. Detailled comment
 */
public class ViewObjectPropertyVersionComposite extends Composite {

    protected TabbedPropertySheetWidgetFactory widgetFactory;

    protected IRepositoryViewObject repositoryObject;

    protected static final SimpleDateFormat FORMATTER = new SimpleDateFormat();

    private ISelection selection;

    private TableViewer tableViewer;
    public IRepositoryViewObject getRepositoryObject() {
        return repositoryObject;
    }

    public void setRepositoryObject(IRepositoryViewObject repositoryObject) {
        this.repositoryObject = repositoryObject;
    }



    public ViewObjectPropertyVersionComposite(Composite parent, int style) {
        super(parent, style);
        this.widgetFactory = new TabbedPropertySheetWidgetFactory();

        FormLayout layout = new FormLayout();
        setLayout(layout);

        FormData thisFormData = new FormData();
        thisFormData.left = new FormAttachment(0, 0);
        thisFormData.right = new FormAttachment(100, 0);
        thisFormData.top = new FormAttachment(0, 0);
        thisFormData.bottom = new FormAttachment(100, 0);
        setLayoutData(thisFormData);

        Composite composite = widgetFactory.createFlatFormComposite(this);
        composite.setLayoutData(thisFormData);

        tableViewer = new TableViewer(composite, SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);
        final Table table = tableViewer.getTable();
        TableLayout tableLayout = new TableLayout();
        table.setLayout(tableLayout);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        final String[] columnProperties = new String[] { Messages.Property_version,
                Messages.Property_creationdate,
                Messages.Property_modifydate, Messages.Property_status };

        final TableColumn column1 = new TableColumn(table, SWT.NONE);
        tableLayout.addColumnData(new ColumnPixelData(125, true));
        column1.setText(columnProperties[0]);

        final TableColumn column2 = new TableColumn(table, SWT.NONE);
        tableLayout.addColumnData(new ColumnPixelData(125, true));
        column2.setText(columnProperties[1]);

        final TableColumn column3 = new TableColumn(table, SWT.NONE);
        tableLayout.addColumnData(new ColumnPixelData(125, true));
        column3.setText(columnProperties[2]);

        final TableColumn column4 = new TableColumn(table, SWT.NONE);
        tableLayout.addColumnData(new ColumnWeightData(1, 150, true));
        column4.setText(columnProperties[3]);

        tableViewer.setColumnProperties(columnProperties);

        Object layoutData = parent.getLayoutData();
        if (layoutData instanceof GridData) {
            GridData gridData = (GridData) layoutData;
            gridData.grabExcessVerticalSpace = true;
            gridData.verticalAlignment = SWT.FILL;
        }

        FormData formData = new FormData();
        formData.left = new FormAttachment(0);
        formData.top = new FormAttachment(0);
        formData.right = new FormAttachment(100,0);
        formData.bottom = new FormAttachment(100,0);
        table.setLayoutData(formData);
        formData.height=80;
        tableViewer.setContentProvider(new IStructuredContentProvider() {

            public Object[] getElements(Object inputElement) {
                IRepositoryViewObject repositoryObject = ((IRepositoryViewObject) inputElement);
                if (repositoryObject.getProperty() == null) {
                    return null;
                }
                try {
                    Project project = ProjectManager.getInstance().getProject(repositoryObject.getProperty());

                    List<IRepositoryViewObject> allVersion = ProxyRepositoryFactory.getInstance().getAllVersion(
                            new org.talend.core.model.general.Project(project), repositoryObject.getId(),
                            repositoryObject.getProperty().getItem().getState().getPath(),
                            ERepositoryObjectType.getItemType(repositoryObject.getProperty().getItem()));

                    return allVersion.toArray();
                } catch (PersistenceException e) {
                    return null;
                }
            }



            public void dispose() {
            }

            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }
        });

        tableViewer.setLabelProvider(new ITableLabelProvider() {

            public Image getColumnImage(Object element, int columnIndex) {
                return null;
            }

            public String getColumnText(Object element, int columnIndex) {
                RepositoryObject repositoryNode = (RepositoryObject) element;
                switch (columnIndex) {
                case 0:
                    return repositoryNode.getVersion();
                case 1:
                    if (repositoryNode.getCreationDate() != null) {
                        return FORMATTER.format(repositoryNode.getCreationDate());
                    } else {
                        return null;
                    }
                case 2:
                    if (repositoryNode.getModificationDate() != null) {
                        return FORMATTER.format(repositoryNode.getModificationDate());
                    } else {
                        return null;
                    }
                case 3:
                    if (repositoryNode.getStatusCode() != null) {
                        return repositoryNode.getStatusCode();
                    } else {
                        return null;
                    }

                default:
                    return null;
                }
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
        });
    }
    public void setData() {

        if (tableViewer.getContentProvider() != null) {
            if (repositoryObject != null && repositoryObject.getProperty() != null) {
                tableViewer.setInput(repositoryObject);
            } else {
                tableViewer.setInput(null);
            }
        }
    }
    public void refresh() {
        Display.getDefault().syncExec(new Runnable() {
            public void run() {
                getParent().layout();
            }
        });
    }


    public ISelection getSelection() {
        return this.selection;
    }
}
