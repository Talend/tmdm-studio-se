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
package com.amalto.workbench.detailtabs.sections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.common.util.EList;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xsd.XSDAnnotation;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDConcreteComponent;
import org.eclipse.xsd.XSDElementDeclaration;
import org.eclipse.xsd.XSDNamedComponent;
import org.eclipse.xsd.XSDParticle;
import org.eclipse.xsd.XSDSchema;
import org.eclipse.xsd.XSDTerm;

import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship.ForeignKeyInfosAnnoInfo;
import com.amalto.workbench.detailtabs.sections.util.FixDMNameBasePropertySectionDataModelExtractor;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XSDAnnotationsStructure;
import com.amalto.workbench.widgets.composites.ElementFKInfoComposite;

public class ElementFKInfosSection extends XpathSection {

	private List<String> fkInfos = new ArrayList<String>();

    private String formatFKinfo = null;

    private ElementFKInfoComposite composite;

	private FixDMNameBasePropertySectionDataModelExtractor holder;

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

	XSDSchema getDatamodelXSD(XSDConcreteComponent component) {
		if (null == component.getContainer()) {
			return (XSDSchema) component;
		}
		return getDatamodelXSD(component.getContainer());
	}

	@Override
	public void refresh() {
		composite.setInfos(fkInfos.toArray(new String[0]));
        composite.setFormatFKInfo(formatFKinfo);
		updateSectionEnabled();
	}

    @Override
    protected boolean isReadOnly() {
        return super.isReadOnly() || isComplexTypeElement(curXSDComponent);
    }
	@Override
	protected void initUIContents(XSDComponent editedObj) {
		super.initUIContents(editedObj);

		fkInfos.clear();

		XSDAnnotationsStructure annoStruct = new XSDAnnotationsStructure(
				curXSDComponent);

		for (String eachFKInfo : annoStruct.getForeignKeyInfos().values()) {
            fkInfos.add(eachFKInfo);
        }

        formatFKinfo = annoStruct.getFormatForeignKeyInfo();//

		holder.setDefaultDataModel(getDataModelName());
		holder.setDefaultEntity(getEntityName());
	}

	@Override
	protected ISubmittable getSubmittedObj() {
        return new ForeignKeyInfosAnnoInfo(curXSDComponent, composite.getInfos(), composite.getFormatFKInfo());
	}

	@Override
	protected void createControlsInSection(Composite compSectionClient) {
        composite = new ElementFKInfoComposite(compSectionClient, SWT.NONE, this);
		holder = new FixDMNameBasePropertySectionDataModelExtractor(this);
	}

	@Override
	protected String getSectionTitle() {
		return Messages.ElementFKInfosSection_ForeignKeyInfos;
	}

	@Override
	public Set<String> getEntities() {
		if (curXSDComponent instanceof XSDParticle) {
			XSDParticle ele = ((XSDParticle) curXSDComponent);
			XSDTerm term = ele.getTerm();
			if (term instanceof XSDElementDeclaration) {
				XSDAnnotation anno = ((XSDElementDeclaration) term)
						.getAnnotation();
				if (null != anno) {
					Set<String> list = new HashSet<String>();
					Util.getForeignKeyofParcle(list, anno);
					return list;
				}
			}
		}
		return null;
	}

	@Override
	public IAllDataModelHolder getDataHolder() {
		return holder;
	}
}
