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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.xsd.XSDComponent;
import org.eclipse.xsd.XSDNamedComponent;

import com.amalto.workbench.detailtabs.sections.model.ISubmittable;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship.PrimaryKeyInfosAnnoInfo;
import com.amalto.workbench.detailtabs.sections.util.FixDMNameBasePropertySectionDataModelExtractor;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.models.infoextractor.IAllDataModelHolder;
import com.amalto.workbench.utils.XSDAnnotationsStructure;
import com.amalto.workbench.widgets.composites.XpathComposite;

public class EntityPKInfosSection extends XpathSection {

	private XpathComposite composite;

	FixDMNameBasePropertySectionDataModelExtractor holder;
	@Override
	public Set<String> getEntities() {
		if(curXSDComponent instanceof XSDNamedComponent){
			return Collections.singleton(((XSDNamedComponent) curXSDComponent).getName());
		}
		return null;
	}

	@Override
	public IAllDataModelHolder getDataHolder() {
		return holder;
	}

    @Override
    public void refresh() {
    	composite.setInfos(list.toArray(new String[0]));
        updateSectionEnabled();
    }
    Collection<String> list = new LinkedList<String>();
    @Override
    protected void initUIContents(XSDComponent editedObj) {
        super.initUIContents(editedObj);

        list.clear();
        holder.setDefaultDataModel(getDataModelName());
        holder.setDefaultEntity(getEntityName());
        XSDAnnotationsStructure annoStruct = new XSDAnnotationsStructure(curXSDComponent);
        for (String eachLookUpFields : annoStruct.getPrimaryKeyInfos().values())
            list.add(eachLookUpFields);
    }

    @Override
    protected ISubmittable getSubmittedObj() {
        String[] pkinfos = getPkInfos();
        return new PrimaryKeyInfosAnnoInfo(curXSDComponent, pkinfos);
    }

    private String[] getPkInfos() {
    	return composite.getInfos();
    }

    @Override
    protected String getSectionTitle() {
        return Messages.EntityPKInfosSection_pkInfoTitle;
    }

    @Override
    protected void createControlsInSection(Composite compSectionClient) {
    	composite = new XpathComposite(compSectionClient,SWT.NONE,this);
    	holder =new FixDMNameBasePropertySectionDataModelExtractor(this);
        String tooltips = Messages.EntityPKInfosSection_pkInfoTooltips;
        composite.getInfosTreeViewer().getTree().setToolTipText(tooltips);
    }

}
