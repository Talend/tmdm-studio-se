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
package com.amalto.workbench.detailtabs.sections.composites;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;

import com.amalto.workbench.detailtabs.sections.BasePropertySection;
import com.amalto.workbench.detailtabs.sections.handlers.RefreshPropertySheetTitleHandler;
import com.amalto.workbench.detailtabs.sections.providers.XSDNamedComponentLabelProvider;
import com.amalto.workbench.detailtabs.sections.util.simpletype.SimpleTypeFacetPropSourceBuilder;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.providers.ListContentProvider;
import com.amalto.workbench.providers.ListStringLabelProvider;
import com.amalto.workbench.providers.datamodel.SchemaElementSorter;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.widgets.composites.property.IPropertySource;
import com.amalto.workbench.widgets.composites.property.PropertyComposite;

public class SimpleTypeConfigComposite extends Composite {

    private ComboViewer comboCustomTypes;

    private ComboViewer comboBuildInTypes;

    private Text txtName;

    private PropertyComposite compProperty;

    private XSDSimpleTypeDefinition xsdSimpleType;

    private Button radCustomTypes;

    private Button radBuildInTypes;
    protected BasePropertySection section;
    public SimpleTypeConfigComposite(Composite parent, int style,BasePropertySection section) {
    	super(parent,style);
    	this.section=section;

        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        setLayout(gridLayout);

        final Label lblName = new Label(this, SWT.NONE);
        lblName.setText(Messages.SimpleTypeConfigComposite_Name);

        txtName = new Text(this, SWT.BORDER);
        final GridData gd_txtName = new GridData(SWT.FILL, SWT.CENTER, true, false);
        txtName.setLayoutData(gd_txtName);

        final Group baseTypeGroup = new Group(this, SWT.NONE);
        baseTypeGroup.setText(Messages.SimpleTypeConfigComposite_BaseType);
        final GridData gd_baseTypeGroup = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
        baseTypeGroup.setLayoutData(gd_baseTypeGroup);
        baseTypeGroup.setLayout(new GridLayout());

        final Composite composite = new Composite(baseTypeGroup, SWT.NONE);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        final GridLayout gridLayout_1 = new GridLayout();
        gridLayout_1.numColumns = 2;
        composite.setLayout(gridLayout_1);

        radCustomTypes = new Button(composite, SWT.RADIO);
        radCustomTypes.setSelection(true);
        radCustomTypes.setText(Messages.SimpleTypeConfigComposite_CustomTypes);

        comboCustomTypes = new ComboViewer(composite, SWT.READ_ONLY);
        final GridData gd_comboCustomTypes = new GridData(SWT.FILL, SWT.CENTER, true, false);
        comboCustomTypes.getCombo().setLayoutData(gd_comboCustomTypes);
        comboCustomTypes.setContentProvider(new ListContentProvider());
        comboCustomTypes.setLabelProvider(new ListStringLabelProvider());
        comboCustomTypes.setSorter(new CustomTypeSorter());

        radBuildInTypes = new Button(composite, SWT.RADIO);
        radBuildInTypes.setText(Messages.SimpleTypeConfigComposite_BuildinTypes);

        comboBuildInTypes = new ComboViewer(composite, SWT.READ_ONLY);
        Combo combo = comboBuildInTypes.getCombo();
        combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        comboBuildInTypes.setContentProvider(new ListContentProvider());
        comboBuildInTypes.setLabelProvider(new XSDNamedComponentLabelProvider());
        comboBuildInTypes.setSorter(new SchemaElementSorter());

        compProperty = new PropertyComposite(this, SWT.NONE, "", "", Messages.SimpleTypeConfigComposite_Facet, Messages.SimpleTypeConfigComposite_Value,section);//$NON-NLS-1$//$NON-NLS-2$
        GridData data = new GridData(SWT.FILL, SWT.FILL, true, true);
        data.horizontalSpan = 2;
        data.heightHint = 200;
        compProperty.setLayoutData(data);

        initUIListeners();
    }

    public void setSimpleType(XSDSimpleTypeDefinition xsdSimpleType) {

        if (xsdSimpleType == null) {
            return;
        }

        this.xsdSimpleType = xsdSimpleType;

        initUIContent();
    }

    private void initUIContent() {

        initUIContentForTxtName();

        initUIContentForComboBuildInTypes();

        initUIContentsForComboCustomTypes();

        initUIContentForCompFacet(xsdSimpleType.getBaseTypeDefinition());

        refresh();
    }

    private void initUIContentForTxtName() {
    	removeNameTxtListener();
        String name = xsdSimpleType.getName() == null ? "" : xsdSimpleType.getName(); //$NON-NLS-1$
		txtName.setText(name);

        Set<String> uuidTypes = new HashSet<String>();
        for (EUUIDCustomType current : EUUIDCustomType.values()) {
            uuidTypes.add(current.getName());
        }

        txtName.setEditable(!uuidTypes.contains(name));

		if (name != null) {
			int length = name.length();
			if (length >= caretOffset) {
				txtName.setSelection(caretOffset,caretOffset);
			} else {
				txtName.setSelection(length,length);
			}
		}
		addNameTxtListener();
    }

    private void initUIContentForComboBuildInTypes() {
    	comboBuildInTypes.removeSelectionChangedListener(buildInChangedListener);
        comboBuildInTypes.setInput(Util.getAllBuildInTypes(xsdSimpleType.getSchema()));
        if(xsdSimpleType.getBaseType() != null) {
            comboBuildInTypes.setSelection(new StructuredSelection(xsdSimpleType.getBaseType()));
        }

        radBuildInTypes.setSelection(!comboBuildInTypes.getSelection().isEmpty());
        comboBuildInTypes.addSelectionChangedListener(buildInChangedListener);
    }

    private void initUIContentsForComboCustomTypes() {
    	comboCustomTypes.removeSelectionChangedListener(customChangedListener);

    	 List<String> allCustomTypeNames = null;
    	if(xsdSimpleType.getSchema() !=null){
    	     allCustomTypeNames = Util.getAllCustomTypeNames(xsdSimpleType.getSchema());
    	}

        if (xsdSimpleType.getName() != null && allCustomTypeNames != null) {
            allCustomTypeNames.remove(xsdSimpleType.getName());
        }
        if (allCustomTypeNames != null) {
            comboCustomTypes.setInput(allCustomTypeNames);
        }
        if(xsdSimpleType.getBaseType() != null && xsdSimpleType.getBaseType().getName() != null){
               comboCustomTypes.setSelection(new StructuredSelection(xsdSimpleType.getBaseType().getName()));
        }
        radCustomTypes.setSelection(!comboCustomTypes.getSelection().isEmpty());
        comboCustomTypes.addSelectionChangedListener(customChangedListener);
    }

    private void initUIContentForCompFacet(XSDSimpleTypeDefinition baseTypeDef) {

        if (baseTypeDef == null) {
            compProperty.setPropertySources(new IPropertySource<?>[0]);
            return;
        }

        List<IPropertySource<?>> propertySources = new ArrayList<IPropertySource<?>>();

        for (String eachFacetName : baseTypeDef.getValidFacets()) {

            IPropertySource<?> propSource = SimpleTypeFacetPropSourceBuilder.createFacetPropSource(xsdSimpleType, baseTypeDef,
                    eachFacetName, compProperty.getPropertyViewer().getTree());

            if (propSource == null) {
                continue;
            }

            propertySources.add(propSource);

        }

        compProperty.setPropertySources(propertySources.toArray(new IPropertySource<?>[0]));

    }

    public Map<String, IPropertySource<?>> getPropertySources() {

        Map<String, IPropertySource<?>> results = new HashMap<String, IPropertySource<?>>();

        for (IPropertySource<?> eachPropSource : compProperty.getProperySources()) {
            results.put(eachPropSource.getPropertyName(), eachPropSource);
        }

        return results;
    }

    public Map<String, Object> getPropertyName2Values() {

        Map<String, Object> results = new HashMap<String, Object>();

        for (Entry<String, IPropertySource<?>> eachPropName2PropSource : getPropertySources().entrySet()) {
            results.put(eachPropName2PropSource.getKey(), eachPropName2PropSource.getValue().getPropertyValue());
        }

        return results;
    }

    private void initUIListeners() {

        initUIListenerForBaseTypeRadioBtns();
        initUIListenerForBaseTypeCombos();
        initUIListenerForText();

    }
    private int caretOffset;
    ModifyListener nameTxtListener;
    private void initUIListenerForText(){
    nameTxtListener = new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				caretOffset = txtName.getCaretPosition();
                if (section != null && !txtName.getText().equals(xsdSimpleType.getName())) {
                    section.autoCommit();

                    RefreshPropertySheetTitleHandler.refreshPropertySheetTitle(section, xsdSimpleType);
                }
			}
		};

    }
    private void addNameTxtListener(){
    	txtName.addModifyListener(nameTxtListener);
    }
    private void removeNameTxtListener(){
    	txtName.removeModifyListener(nameTxtListener);
    }
    private void initUIListenerForBaseTypeRadioBtns() {

        radCustomTypes.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                refresh();
            }

        });

        radBuildInTypes.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                refresh();
            }

        });

    }
    ISelectionChangedListener customChangedListener;
    ISelectionChangedListener buildInChangedListener;
    private void initUIListenerForBaseTypeCombos() {
    	customChangedListener=new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                if(section!=null && getSelectedBaseTypeName().length()>0){
					section.autoCommit();
                }
            }
        };


        comboCustomTypes.getCombo().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {

                ISelection oldSelection = comboCustomTypes.getSelection();

                List<String> allCustomTypeNames = Util.getAllCustomTypeNames(xsdSimpleType.getSchema());
                allCustomTypeNames.remove(xsdSimpleType.getName());
                comboCustomTypes.setInput(allCustomTypeNames);

                comboCustomTypes.setSelection(oldSelection);
            }
        });

        buildInChangedListener=new ISelectionChangedListener() {

            public void selectionChanged(SelectionChangedEvent event) {
                if(section!=null && getSelectedBaseTypeName().length()>0){
					section.autoCommit();
                }
            }
        };

        comboBuildInTypes.getCombo().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseDown(MouseEvent e) {

                ISelection oldSelection = comboBuildInTypes.getSelection();

                comboBuildInTypes.setInput(Util.getAllBuildInTypes(xsdSimpleType.getSchema()));
                comboBuildInTypes.setSelection(oldSelection);
            }

        });
    }

    private void refresh() {

        boolean isBuildInType = Util.isBuildInType(xsdSimpleType);

        txtName.setEnabled(!isBuildInType);
        radCustomTypes.setEnabled(!isBuildInType);
        radBuildInTypes.setEnabled(!isBuildInType);
        compProperty.setEditable(!isBuildInType);

        comboCustomTypes.getCombo().setEnabled(radCustomTypes.getSelection() && !isBuildInType);
        comboBuildInTypes.getCombo().setEnabled(radBuildInTypes.getSelection() && !isBuildInType);

        initUIContentForCompFacet(getSelectedBaseType());

    }

    public XSDSimpleTypeDefinition getSelectedBaseType() {

        if (radCustomTypes.getSelection()) {
            return getCurSelectedCustomBaseType();
        }

        return getCurSelectedBuildInBaseType();
    }

    public String getSelectedBaseTypeName() {

        if (radCustomTypes.getSelection()) {
            return comboCustomTypes.getCombo().getText();
        }

        return comboBuildInTypes.getCombo().getText();
    }

    private XSDSimpleTypeDefinition getCurSelectedBuildInBaseType() {

        IStructuredSelection selection = (IStructuredSelection) comboBuildInTypes.getSelection();

        if (selection == null || selection.isEmpty()) {
            return null;
        }

        return (XSDSimpleTypeDefinition) selection.getFirstElement();
    }

    private XSDSimpleTypeDefinition getCurSelectedCustomBaseType() {

        IStructuredSelection selection = (IStructuredSelection) comboCustomTypes.getSelection();

        if (selection == null || selection.isEmpty()) {
            return null;
        }

        XSDSimpleTypeDefinition curSelectedCustomBaseType = xsdSimpleType.getSchema().resolveSimpleTypeDefinition(
                xsdSimpleType.getSchema().getSchemaForSchemaNamespace(), (String) selection.getFirstElement());

        if (!xsdSimpleType.getSchema().getTypeDefinitions().contains(curSelectedCustomBaseType))
         {
            return xsdSimpleType.getSchema().resolveSimpleTypeDefinition(xsdSimpleType.getSchema().getSchemaForSchemaNamespace(),
                    "string");//$NON-NLS-1$
        }

        return curSelectedCustomBaseType;
    }

    public String getSimpleTypeName() {
        return txtName.getText().trim();
    }

}

class CustomTypeSorter extends ViewerSorter {

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {

        String typeName1 = getTypeName(e1);
        String typeName2 = getTypeName(e2);

        if ("".equals(typeName1) || "".equals(typeName2)) {
            return super.compare(viewer, e1, e2);
        }

        int typeCode1 = getTypeCode(typeName1);
        int typeCode2 = getTypeCode(typeName2);

        if (typeCode1 != typeCode2) {
            return (typeCode1 - typeCode2);
        }

        return typeName1.compareTo(typeName2);
    }

    private int getTypeCode(String typeName) {
        Set<String> uuidTypes = new HashSet<String>();
        for (EUUIDCustomType current : EUUIDCustomType.values()) {
            uuidTypes.add(current.getName());
        }

        if (uuidTypes.contains(typeName)) {
            return 0;
        }

        return 1;
    }

    private String getTypeName(Object typeObj) {

        if (typeObj instanceof XSDSimpleTypeDefinition) {
            return ((XSDSimpleTypeDefinition) typeObj).getName();
        }

        if (typeObj instanceof String) {
            return (String) typeObj;
        }

        return "";//$NON-NLS-1$
    }
}
