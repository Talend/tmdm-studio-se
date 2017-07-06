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
package com.amalto.workbench.detailtabs.sections.handlers;

import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDFacet;
import org.eclipse.xsd.XSDSimpleTypeDefinition;
import org.eclipse.xsd.util.XSDSchemaBuildingTools;

import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.simpletype.SimpleTypeWrapper;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.Util;

public abstract class SimpleTypeFacetCommitHandler extends CommitHandler<SimpleTypeWrapper> {

    protected String facetName = "";//$NON-NLS-1$

    public SimpleTypeFacetCommitHandler(SimpleTypeWrapper submittedObj, String facetName) {
        super(submittedObj);

        this.facetName = facetName;
    }

    @Override
    protected boolean doSubmit() throws CommitException {
        return getCommitHelper().doCommit(facetName);
    }

    @Override
    protected void validateCommit() throws CommitValidationException {
        getCommitHelper().validateCommit(facetName);
    }

    protected abstract XSDFacet[] getOldFacets();

    protected abstract XSDConstrainingFacet creatNewFacet();

    protected abstract SimpleTypeFacetCommitHandlerHelper getCommitHelper();
}

class SimpleTypeFacetCommitHandler_FractionDigits extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_FractionDigits(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_FRACTIONDIGITS);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return new XSDFacet[] { getCommitedObj().getXSDSimpleType().getFractionDigitsFacet() };
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDFractionDigitsFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {
        return SimpleTypeFacetCommitHandlerHelper.createHelperForIntFacet(this);
    }
}

class SimpleTypeFacetCommitHandler_TotalDigits extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_TotalDigits(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_TOTALDIGITS);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return new XSDFacet[] { getCommitedObj().getXSDSimpleType().getTotalDigitsFacet() };
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDTotalDigitsFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {
        return SimpleTypeFacetCommitHandlerHelper.createHelperForIntFacet(this);
    }

}

class SimpleTypeFacetCommitHandler_MaxLength extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_MaxLength(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_MAXLENGTH);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return new XSDFacet[] { getCommitedObj().getXSDSimpleType().getMaxLengthFacet() };
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDMaxLengthFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {
        return SimpleTypeFacetCommitHandlerHelper.createHelperForIntFacet(this);
    }
}

class SimpleTypeFacetCommitHandler_MinLength extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_MinLength(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_MINLENGTH);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return new XSDFacet[] { getCommitedObj().getXSDSimpleType().getMinLengthFacet() };
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDMinLengthFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {
        return SimpleTypeFacetCommitHandlerHelper.createHelperForIntFacet(this);
    }
}

class SimpleTypeFacetCommitHandler_Length extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_Length(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_LENGTH);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return new XSDFacet[] { getCommitedObj().getXSDSimpleType().getLengthFacet() };
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDLengthFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {
        return SimpleTypeFacetCommitHandlerHelper.createHelperForIntFacet(this);
    }
}

class SimpleTypeFacetCommitHandler_Enum extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_Enum(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_ENUM);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return getCommitedObj().getXSDSimpleType().getEnumerationFacets().toArray(new XSDFacet[0]);
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDEnumerationFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {
        return SimpleTypeFacetCommitHandlerHelper.createHelperForStringArrayFacet(this);
    }

}

class SimpleTypeFacetCommitHandler_Pattern extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_Pattern(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_PATTERN);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return getCommitedObj().getXSDSimpleType().getPatternFacets().toArray(new XSDFacet[0]);
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDPatternFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {
        return SimpleTypeFacetCommitHandlerHelper.createHelperForStringArrayFacet(this);
    }
}

class SimpleTypeFacetCommitHandler_WhiteSpace extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_WhiteSpace(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_WHITESPACE);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return new XSDFacet[] { getCommitedObj().getXSDSimpleType().getWhiteSpaceFacet() };
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDWhiteSpaceFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {
        return SimpleTypeFacetCommitHandlerHelper.createHelperForStringFacet(this);
    }
}

class SimpleTypeFacetCommitHandler_MaxExclusive extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_MaxExclusive(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_MAXEXCLUSIVE);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return new XSDFacet[] { getCommitedObj().getXSDSimpleType().getMaxExclusiveFacet() };
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDMaxExclusiveFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {

        XSDSimpleTypeDefinition type = getCommitedObj().getNewBaseType();
        if (Util.isDouble(type) || Util.isFloat(type) || Util.isDecimal(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDoubleFacet(this);
        }
        if (Util.isDate(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDateFacet(this);
        }
        if (Util.isDateTime(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDateTimeFacet(this);
        }
        if (Util.isTime(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForTimeFacet(this);
        }
        return SimpleTypeFacetCommitHandlerHelper.createHelperForIntFacet(this);
    }
}

class SimpleTypeFacetCommitHandler_MaxInclusive extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_MaxInclusive(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_MAXINCLUSIVE);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return new XSDFacet[] { getCommitedObj().getXSDSimpleType().getMaxInclusiveFacet() };
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDMaxInclusiveFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {

        XSDSimpleTypeDefinition type = getCommitedObj().getNewBaseType();
        if (Util.isDouble(type) || Util.isFloat(type) || Util.isDecimal(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDoubleFacet(this);
        }
        if (Util.isDate(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDateFacet(this);
        }
        if (Util.isDateTime(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDateTimeFacet(this);
        }
        if (Util.isTime(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForTimeFacet(this);
        }
        return SimpleTypeFacetCommitHandlerHelper.createHelperForIntFacet(this);
    }
}

class SimpleTypeFacetCommitHandler_MinExclusive extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_MinExclusive(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_MINEXCLUSIVE);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return new XSDFacet[] { getCommitedObj().getXSDSimpleType().getMinExclusiveFacet() };
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDMinExclusiveFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {

        XSDSimpleTypeDefinition type = getCommitedObj().getNewBaseType();
        if (Util.isDouble(type) || Util.isFloat(type) || Util.isDecimal(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDoubleFacet(this);
        }
        if (Util.isDate(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDateFacet(this);
        }
        if (Util.isDateTime(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDateTimeFacet(this);
        }
        if (Util.isTime(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForTimeFacet(this);
        }
        return SimpleTypeFacetCommitHandlerHelper.createHelperForIntFacet(this);
    }
}

class SimpleTypeFacetCommitHandler_MinInclusive extends SimpleTypeFacetCommitHandler {

    public SimpleTypeFacetCommitHandler_MinInclusive(SimpleTypeWrapper submittedObj) {
        super(submittedObj, IConstants.SIMPLETYPE_FACETNAME_MININCLUSIVE);
    }

    @Override
    protected XSDFacet[] getOldFacets() {
        return new XSDFacet[] { getCommitedObj().getXSDSimpleType().getMinInclusiveFacet() };
    }

    @Override
    protected XSDConstrainingFacet creatNewFacet() {
        return XSDSchemaBuildingTools.getXSDFactory().createXSDMinInclusiveFacet();
    }

    @Override
    protected SimpleTypeFacetCommitHandlerHelper getCommitHelper() {

        XSDSimpleTypeDefinition type = getCommitedObj().getNewBaseType();
        if (Util.isDouble(type) || Util.isFloat(type) || Util.isDecimal(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDoubleFacet(this);
        }
        if (Util.isDate(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDateFacet(this);
        }
        if (Util.isDateTime(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForDateTimeFacet(this);
        }
        if (Util.isTime(type)) {
            return SimpleTypeFacetCommitHandlerHelper.createHelperForTimeFacet(this);
        }
        return SimpleTypeFacetCommitHandlerHelper.createHelperForIntFacet(this);
    }
}
