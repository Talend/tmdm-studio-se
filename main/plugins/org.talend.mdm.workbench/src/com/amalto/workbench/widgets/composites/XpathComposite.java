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
package com.amalto.workbench.widgets.composites;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDSchema;

import com.amalto.workbench.detailtabs.sections.XpathSection;
import com.amalto.workbench.detailtabs.sections.providers.StringViewerSorter;
import com.amalto.workbench.dialogs.datamodel.SelectXPathDialog;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.image.EImage;
import com.amalto.workbench.image.ImageCache;
import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;
import com.amalto.workbench.models.infoextractor.XSDComponentChildElementsHolder;
import com.amalto.workbench.providers.ListContentProvider;
import com.amalto.workbench.providers.ListStringLabelProvider;
import com.amalto.workbench.utils.Util;

public class XpathComposite extends ListStringContentsComposite {
	
    protected XpathSection section;
	
	private ComboViewer comboInfos;

	public XpathComposite(Composite parent, int style, XpathSection section) {
		super(parent, style, null, section);
		this.section = section;
	}
	public XpathComposite(Composite parent) {
		super(parent, SWT.NONE, new Object[] {}, null);
	}

	protected XSDComponent getXSD() {
		return section.getEditedObj();
	}

	@Override
	protected String getInfoColTitle() {
		return "XPath"; //$NON-NLS-1$
	}

	@Override
	protected void createExtentUIArea(Composite parent) {
	}

	protected String getDatamodel() {
		return section.getDataModelName();
	}

	protected String getConcept() {
		Set<String> entities = section.getEntities();
		if (null != entities) {
			StringBuilder builder = new StringBuilder();
			for (String entity : entities) {
				if (builder.length() > 0) {
					builder.append("|");
				}
				builder.append(entity);
			}
			return builder.toString();
		}
		return null;
	}

	protected IAllDataModelHolder getDataholder() {
		return section.getDataHolder();
	}

	XSDSchema getDatamodelXSD(XSDConcreteComponent component) {
		if (null == component.getContainer()) {
			return (XSDSchema) component;
		}
		return getDatamodelXSD(component.getContainer());
	}

	XSDSchema getDatamodelXSD() {
		return getDatamodelXSD(this.getXSD());
	}

	Object[] getXSDSchemaChildren(XSDSchema schema) {
		List<XSDElementDeclaration> declarations = new ArrayList<XSDElementDeclaration>();

		EList<XSDElementDeclaration> elementDeclarations = schema
				.getElementDeclarations();
		for (XSDElementDeclaration declaration : elementDeclarations) {
			if (declaration.eContainer().equals(schema)) {
                declarations.add(declaration);
            }
		}

		Object[] schemaChildren = Util.filterOutDuplicatedElems(declarations
				.toArray(new XSDNamedComponent[declarations.size()]));

		return schemaChildren;
	}

	protected String[] getItems() {
		Set<String> entities = section.getEntities();

		if (null != entities) {
			XSDSchema schema = getDatamodelXSD();
			Object[] child = getXSDSchemaChildren(schema);
			List<String> items = new LinkedList<String>();
			for (Object obj : child) {
				if (obj instanceof XSDNamedComponent) {
					if (entities.contains(((XSDNamedComponent) obj).getName())) {
						XSDComponentChildElementsHolder holder = new XSDComponentChildElementsHolder(
								(XSDNamedComponent) obj);
						String[] eles = holder.getAllElements();
						if (null == eles) {
							continue;
						}
						for (String ele : eles) {
							items.add(ele);
						}
					}
				}
			}
			return items.toArray(new String[0]);

		}
		return null;
	}

	protected String[] getElementPathsFrXSDElementDeclaration(
			XSDElementDeclaration parent) {
		try {
			return Util.getChildElementNames(parent.getName(), parent).toArray(
					new String[0]);
		} catch (Exception e) {
			return new String[0];
		}
	}

	@Override
	protected void createCandidateInfoUIArea(Composite parent) {
		Composite compSimpleXPath = new Composite(this, SWT.NONE);
		compSimpleXPath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		compSimpleXPath.setLayout(new GridLayout(2, false));
		comboInfos = new ComboViewer(compSimpleXPath, SWT.DROP_DOWN);
		comboInfos.getControl().setLayoutData(
				new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboInfos.setContentProvider(new ListContentProvider());
		comboInfos.setLabelProvider(new ListStringLabelProvider());
		comboInfos.setSorter(new StringViewerSorter());
		comboInfos.getCombo().addMouseListener(new MouseAdapter() {

			@Override
			public void mouseDown(MouseEvent e) {
				comboInfos.setInput(getItems());
			}
		});
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.CENTER)
				.grab(true, false).applyTo(comboInfos.getControl());
		Button btnSelectXPath = new Button(compSimpleXPath, SWT.NONE);
		btnSelectXPath.setImage(ImageCache.getCreatedImage(EImage.DOTS_BUTTON.getPath()));
		btnSelectXPath.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				SelectXPathDialog dialog = new SelectXPathDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						getDataholder(), getDatamodel(), getConcept(), null);
				if (dialog.open() != Window.OK) {
                    return;
                }
				String xpath = dialog.getSelectedXPath();
				if (null != xpath) {
					XpathComposite.this.addInfoToInfoTree(xpath);
				}
			}
		});
		btnSelectXPath.setToolTipText(Messages.SchematronExpressBuilder_selectXPath);
	}

	@Override
	protected boolean hasCandidateInfo() {
		return !("".equals(getCandidateInfo()));
	}

	@Override
	protected String getCandidateInfo() {
		return comboInfos.getCombo().getText();
	}

	@Override
	protected void initCandidateInfoUIArea() {
		comboInfos.setSelection(null);
	}

	@Override
	protected void initParas(Object[] paras) {

	}
}
