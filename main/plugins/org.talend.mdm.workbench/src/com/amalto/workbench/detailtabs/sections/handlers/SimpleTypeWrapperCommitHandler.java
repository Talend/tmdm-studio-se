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
package com.amalto.workbench.detailtabs.sections.handlers;

import com.amalto.workbench.detailtabs.sections.model.simpletype.SimpleTypeWrapper;

public class SimpleTypeWrapperCommitHandler extends CompositeCommitHandler<SimpleTypeWrapper> {

    public SimpleTypeWrapperCommitHandler(SimpleTypeWrapper submittable) {
        super(submittable);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected CommitHandler<SimpleTypeWrapper>[] createChildHandlers() {

        return new CommitHandler[] { new SimpleTypeNameCommitHandler(getCommitedObj()),
                new SimpleTypeBaseTypeCommitHandler(getCommitedObj()), new SimpleTypeFacetCommitHandlerGroup(getCommitedObj()) };
    }

}
