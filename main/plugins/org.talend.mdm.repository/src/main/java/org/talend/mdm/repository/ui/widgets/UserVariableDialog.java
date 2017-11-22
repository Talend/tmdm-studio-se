// ============================================================================
//
// Copyright (C) 2006-2017 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.repository.ui.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.talend.mdm.repository.i18n.Messages;
import org.talend.mdm.repository.plugin.RepositoryPlugin;
import org.talend.mdm.repository.utils.EclipseResourceManager;

import com.amalto.workbench.utils.Util;


/**
 * created by liusongbo on 2014-3-25
 */
class UserVariableDialog extends Dialog {

    private static Logger log = Logger.getLogger(UserVariableDialog.class);

    private final String USER = "User"; //$NON-NLS-1$

    private TreeViewer tv;

    private String schema;

    private String result;

    public UserVariableDialog(Shell parentShell, String schema) {
        super(parentShell);
        setShellStyle(getShellStyle() | SWT.RESIZE);
        this.schema = schema;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setSize(400, 500);
        newShell.setText(Messages.UserVariableDialog_SelectVariable);
    }

    @Override
    protected void initializeBounds() {
        super.initializeBounds();
        Point location = getInitialLocation(getShell().getSize());
        getShell().setLocation(location.x, location.y);
    }

    @Override
    protected Control createDialogArea(Composite parent) {
        Composite control = (Composite) super.createDialogArea(parent);
        control.setLayout(new GridLayout());

        createUserVariableTree(control);
        return control;
    }

    private void createUserVariableTree(Composite parent) {
        int style = SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER;
        tv = new TreeViewer(parent, style);
        tv.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        tv.setLabelProvider(getLabelProvider());
        tv.setContentProvider(getContentProvider());
        tv.addSelectionChangedListener(getSelectionChangedListener());
        tv.setAutoExpandLevel(2);
        tv.setInput(new Object());
    }

    private ILabelProvider getLabelProvider() {
        return new CustomLabelProvider();
    }
    
    class CustomLabelProvider extends LabelProvider implements IColorProvider {
        Image IMG = EclipseResourceManager.getImage(RepositoryPlugin.PLUGIN_ID, "icons/methpub_obj.gif"); //$NON-NLS-1$;
        
        @Override
        public Image getImage(Object element) {
            if (USER.equals(element)) {
                return null;
            }
            return IMG;
        }

        @Override
        public String getText(Object element) {
            return element.toString();
        }

        @Override
        public Color getForeground(Object element) {
            if(!isValid(element.toString())) {
                return getColor(SWT.COLOR_GRAY);
            }
            return null;
        }

        @Override
        public Color getBackground(Object element) {
            return null;
        }

        private Color getColor(int systemColorID) {
            Display display = Display.getCurrent();

            return display.getSystemColor(systemColorID);
        }
    }

    private IContentProvider getContentProvider() {
        return new ITreeContentProvider() {

            @Override
            public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            }

            @Override
            public void dispose() {
            }

            @Override
            public boolean hasChildren(Object element) {
                return getChildren(element).length > 0;
            }

            @Override
            public Object getParent(Object element) {
                return null;
            }

            @Override
            public Object[] getElements(Object inputElement) {
                return new String[] { USER };
            }

            @Override
            public Object[] getChildren(Object parentElement) {
                if (USER.equals(parentElement)) {
                    return getUserFields().toArray();
                }
                return new Object[0];
            }
        };
    }

    protected ISelectionChangedListener getSelectionChangedListener() {
        return new ISelectionChangedListener() {

            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event.getSelection();
                Object firstElement = selection.getFirstElement();
                if (firstElement != null && !USER.equals(firstElement.toString()) && isValid(firstElement.toString())) {
                    getButton(IDialogConstants.OK_ID).setEnabled(true);
                } else {
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                }
            }

        };
    }
    
    private boolean isValid(String ufield) {
        boolean contains = false;
        for(UserField userfield:UserField.values()) {
            if(userfield.field.equals(ufield)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    private List<String> getUserFields() {
        try {
            if (schema != null) {

                XSDSchema xsd = Util.createXsdSchema(schema, null);

                List<String> results = new ArrayList<String>();
                EList<XSDElementDeclaration> elementDeclarations = xsd.getElementDeclarations();
                for (XSDElementDeclaration decl : elementDeclarations) {
                    if (decl.getName().equals(USER)) {
                        Map<String, XSDParticle> childElements = Util.getChildElements("", decl, true, new HashSet<Object>()); //$NON-NLS-1$
                        for (String element : childElements.keySet()) {
                            String prefix = "//"; //$NON-NLS-1$
                            if (element.startsWith(prefix)) {
                                element = element.substring(prefix.length());
                            }
                            results.add(element);
                        }
                    }
                }

                return results;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        super.createButtonsForButtonBar(parent);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
    }

    @Override
    protected void okPressed() {
        IStructuredSelection selection = (IStructuredSelection) tv.getSelection();
        result = selection.getFirstElement().toString();
        super.okPressed();
    }

    public String getResult() {
        return result;
    }

    static enum UserField {
        // TAC
        User_Name("username"), //$NON-NLS-1$
        First_Name("givenname"), //$NON-NLS-1$
        Last_Name("familyname"), //$NON-NLS-1$
        //MDM
        Id("id"), //$NON-NLS-1$
        Language("language"), //$NON-NLS-1$
        Properties("properties"); //$NON-NLS-1$
        
        ////////
        String field;
        UserField(String f) {
            this.field = f;
        }
    }
}
