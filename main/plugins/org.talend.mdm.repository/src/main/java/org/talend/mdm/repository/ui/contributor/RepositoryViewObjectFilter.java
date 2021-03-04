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

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.IFilter;
import org.talend.commons.exception.PersistenceException;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.core.repository.model.ProxyRepositoryFactory;
import org.talend.mdm.repository.model.mdmproperties.ContainerItem;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class RepositoryViewObjectFilter implements IFilter {



	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IFilter#select(java.lang.Object)
	 */
	public boolean select(Object arg0) {
		if(arg0 instanceof IRepositoryViewObject){
	        	Item item=((IRepositoryViewObject)arg0).getProperty().getItem();
	        	if(item.getProperty().getLabel()==null || item instanceof ContainerItem){
	        		return false;
	        	}
	        	return true;
		}
		return false;
	}

}
