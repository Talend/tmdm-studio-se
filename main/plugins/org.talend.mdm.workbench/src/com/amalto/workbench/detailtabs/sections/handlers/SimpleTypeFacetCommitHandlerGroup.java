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

import java.util.ArrayList;
import java.util.List;

import com.amalto.workbench.detailtabs.sections.model.simpletype.SimpleTypeWrapper;
import com.amalto.workbench.utils.IConstants;

public class SimpleTypeFacetCommitHandlerGroup extends CompositeCommitHandler<SimpleTypeWrapper> {

    public SimpleTypeFacetCommitHandlerGroup(SimpleTypeWrapper submittable) {
        super(submittable);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected CommitHandler<SimpleTypeWrapper>[] createChildHandlers() {

        List<CommitHandler<SimpleTypeWrapper>> results = new ArrayList<CommitHandler<SimpleTypeWrapper>>();

        for (String eachFacetName : getCommitedObj().getFacetNames()) {

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_PATTERN))
                results.add(new SimpleTypeFacetCommitHandler_Pattern(getCommitedObj()));

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_ENUM))
                results.add(new SimpleTypeFacetCommitHandler_Enum(getCommitedObj()));

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_LENGTH))
                results.add(new SimpleTypeFacetCommitHandler_Length(getCommitedObj()));

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_MINLENGTH))
                results.add(new SimpleTypeFacetCommitHandler_MinLength(getCommitedObj()));

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_MAXLENGTH))
                results.add(new SimpleTypeFacetCommitHandler_MaxLength(getCommitedObj()));

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_TOTALDIGITS))
                results.add(new SimpleTypeFacetCommitHandler_TotalDigits(getCommitedObj()));

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_FRACTIONDIGITS))
                results.add(new SimpleTypeFacetCommitHandler_FractionDigits(getCommitedObj()));

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_MAXINCLUSIVE))
                results.add(new SimpleTypeFacetCommitHandler_MaxInclusive(getCommitedObj()));

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_MAXEXCLUSIVE))
                results.add(new SimpleTypeFacetCommitHandler_MaxExclusive(getCommitedObj()));

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_MININCLUSIVE))
                results.add(new SimpleTypeFacetCommitHandler_MinInclusive(getCommitedObj()));

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_MINEXCLUSIVE))
                results.add(new SimpleTypeFacetCommitHandler_MinExclusive(getCommitedObj()));

            if (eachFacetName.equals(IConstants.SIMPLETYPE_FACETNAME_WHITESPACE))
                results.add(new SimpleTypeFacetCommitHandler_WhiteSpace(getCommitedObj()));
        }

        return results.toArray(new CommitHandler[0]);
    }

}
