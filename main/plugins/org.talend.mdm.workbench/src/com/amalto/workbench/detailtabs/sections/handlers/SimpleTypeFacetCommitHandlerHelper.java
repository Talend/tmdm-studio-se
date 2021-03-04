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
import java.util.Arrays;
import java.util.List;

import org.eclipse.xsd.XSDConstrainingFacet;
import org.eclipse.xsd.XSDFacet;

import com.amalto.workbench.detailtabs.exception.CommitException;
import com.amalto.workbench.detailtabs.exception.CommitValidationException;
import com.amalto.workbench.detailtabs.sections.model.simpletype.SimpleTypeWrapper;
import com.amalto.workbench.i18n.Messages;
import com.amalto.workbench.utils.XSDUtil;

public abstract class SimpleTypeFacetCommitHandlerHelper {

    protected SimpleTypeFacetCommitHandler commitHandler;

    public SimpleTypeFacetCommitHandlerHelper(SimpleTypeFacetCommitHandler commitHandler) {
        this.commitHandler = commitHandler;
    }

    public static SimpleTypeFacetCommitHandlerHelper createHelperForStringFacet(SimpleTypeFacetCommitHandler commitHandler) {
        return new SimpleTypeFacetCommitHandlerHelper_StringFacet(commitHandler);
    }

    public static SimpleTypeFacetCommitHandlerHelper createHelperForDoubleFacet(SimpleTypeFacetCommitHandler commitHandler) {
        return new SimpleTypeFacetCommitHandlerHelper_DoubleFacet(commitHandler);
    }

    public static SimpleTypeFacetCommitHandlerHelper createHelperForIntFacet(SimpleTypeFacetCommitHandler commitHandler) {
        return new SimpleTypeFacetCommitHandlerHelper_IntFacet(commitHandler);
    }

    public static SimpleTypeFacetCommitHandlerHelper createHelperForStringArrayFacet(SimpleTypeFacetCommitHandler commitHandler) {
        return new SimpleTypeFacetCommitHandlerHelper_StringArrayFacet(commitHandler);
    }

    public static SimpleTypeFacetCommitHandlerHelper createHelperForDateFacet(SimpleTypeFacetCommitHandler commitHandler) {
        return new SimpleTypeFacetCommitHandlerHelper_DateFacet(commitHandler);
    }

    public static SimpleTypeFacetCommitHandlerHelper createHelperForDateTimeFacet(SimpleTypeFacetCommitHandler commitHandler) {
        return new SimpleTypeFacetCommitHandlerHelper_DateTimeFacet(commitHandler);
    }

    public static SimpleTypeFacetCommitHandlerHelper createHelperForTimeFacet(SimpleTypeFacetCommitHandler commitHandler) {
        return new SimpleTypeFacetCommitHandlerHelper_TimeFacet(commitHandler);
    }

    protected abstract void validateCommit(String facetName) throws CommitValidationException;

    protected abstract boolean doCommit(String facetName) throws CommitException;
}

class SimpleTypeFacetCommitHandlerHelper_StringFacet extends SimpleTypeFacetCommitHandlerHelper {

    public SimpleTypeFacetCommitHandlerHelper_StringFacet(SimpleTypeFacetCommitHandler commitHandler) {
        super(commitHandler);
    }

    @Override
    protected void validateCommit(String facetName) throws CommitValidationException {

        if (!(commitHandler.getCommitedObj().getFacetValue(facetName) instanceof String)) {
            throw new CommitValidationException(Messages.SimpleTypeFacetCommitHandlerHelper_ValueTypeOfFacet + facetName
                    + Messages.SimpleTypeFacetCommitHandlerHelper_ShouldbeString);
        }

    }

    @Override
    protected boolean doCommit(String facetName) throws CommitException {

        String newValue = (String) commitHandler.getCommitedObj().getFacetValue(facetName);
        String oldValue = getOldFacetValue();

        if (oldValue.equals(newValue)) {
            return false;
        }

        if (!"".equals(oldValue)) {
            commitHandler.getCommitedObj().getXSDSimpleType().getFacetContents()
                    .removeAll(Arrays.asList(commitHandler.getOldFacets()));
        }

        if (!"".equals(newValue)) {//$NON-NLS-1$
            XSDConstrainingFacet f = commitHandler.creatNewFacet();
            f.setLexicalValue("" + newValue);//$NON-NLS-1$
            commitHandler.getCommitedObj().getXSDSimpleType().getFacetContents().add(f);
        }

        return true;

    }

    protected String getOldFacetValue() {

        if (commitHandler.getOldFacets().length == 0) {
            return "";//$NON-NLS-1$
        }

        XSDFacet facet = commitHandler.getOldFacets()[0];

        if (facet == null || facet.getLexicalValue() == null) {
            return "";//$NON-NLS-1$
        }

        return facet.getLexicalValue();
    }

}

class SimpleTypeFacetCommitHandlerHelper_DoubleFacet extends SimpleTypeFacetCommitHandlerHelper {

    public SimpleTypeFacetCommitHandlerHelper_DoubleFacet(SimpleTypeFacetCommitHandler commitHandler) {
        super(commitHandler);
    }

    @Override
    protected void validateCommit(String facetName) throws CommitValidationException {
        if (commitHandler.getCommitedObj().getFacetValue(facetName) != null) {
            if (!(commitHandler.getCommitedObj().getFacetValue(facetName) instanceof Double)
                    || ((Double) commitHandler.getCommitedObj().getFacetValue(facetName)) < 0) {
                throw new CommitValidationException(Messages.SimpleTypeFacetCommitHandlerHelper_ValueTypeOfFacet + facetName
                        + Messages.SimpleTypeFacetCommitHandlerHelper_ShouldbeDoubleOr0);
            }
        }

    }

    @Override
    protected boolean doCommit(String facetName) throws CommitException {

        Double newValue = (Double) commitHandler.getCommitedObj().getFacetValue(facetName);
        Double oldValue = getOldFacetValue();

        if ((newValue == null && oldValue == null) || (oldValue != null && oldValue.equals(newValue))
                || (newValue != null && newValue.equals(oldValue))) {
            return false;
        }

        if (oldValue != null) {
            commitHandler.getCommitedObj().getXSDSimpleType().getFacetContents()
                    .removeAll(Arrays.asList(commitHandler.getOldFacets()));
        }

        if (newValue != null && newValue >= 0) {
            XSDConstrainingFacet f = commitHandler.creatNewFacet();
            f.setLexicalValue("" + newValue);//$NON-NLS-1$
            commitHandler.getCommitedObj().getXSDSimpleType().getFacetContents().add(f);
        }

        return true;
    }

    protected Double getOldFacetValue() {

        if (commitHandler.getOldFacets().length == 0) {
            return null;
        }

        XSDFacet facet = commitHandler.getOldFacets()[0];

        if (facet == null || facet.getLexicalValue() == null) {
            return null;
        }

        return Double.parseDouble(facet.getLexicalValue());
    }

}

class SimpleTypeFacetCommitHandlerHelper_IntFacet extends SimpleTypeFacetCommitHandlerHelper {

    public SimpleTypeFacetCommitHandlerHelper_IntFacet(SimpleTypeFacetCommitHandler commitHandler) {
        super(commitHandler);
    }

    @Override
    protected void validateCommit(String facetName) throws CommitValidationException {
        if (commitHandler.getCommitedObj().getFacetValue(facetName) != null) {
            if (!(commitHandler.getCommitedObj().getFacetValue(facetName) instanceof Integer)
                    || ((Integer) commitHandler.getCommitedObj().getFacetValue(facetName)) < 0) {
                throw new CommitValidationException(Messages.SimpleTypeFacetCommitHandlerHelper_ValueTypeOfFacet + facetName
                        + Messages.SimpleTypeFacetCommitHandlerHelper_ShouldbePosition);
            }
        }
    }

    @Override
    protected boolean doCommit(String facetName) throws CommitException {

        Integer newValue = (Integer) commitHandler.getCommitedObj().getFacetValue(facetName);
        Integer oldValue = getOldFacetValue();

        if ((newValue == null && oldValue == null) || (oldValue != null && oldValue.equals(newValue))
                || (newValue != null && newValue.equals(oldValue))) {
            return false;
        }

        if (oldValue != null) {
            commitHandler.getCommitedObj().getXSDSimpleType().getFacetContents()
                    .removeAll(Arrays.asList(commitHandler.getOldFacets()));
        }

        if (newValue != null && newValue >= 0) {
            XSDConstrainingFacet f = commitHandler.creatNewFacet();
            f.setLexicalValue("" + newValue);//$NON-NLS-1$
            commitHandler.getCommitedObj().getXSDSimpleType().getFacetContents().add(f);
        }

        return true;

    }

    protected Integer getOldFacetValue() {

        if (commitHandler.getOldFacets().length == 0) {
            return 0;
        }

        XSDFacet facet = commitHandler.getOldFacets()[0];

        if (facet == null || facet.getLexicalValue() == null) {
            return null;
        }

        return Integer.parseInt(facet.getLexicalValue());
    }
}

abstract class SimpleTypeFacetCommitHandlerHelper_AbstractDateFacet extends SimpleTypeFacetCommitHandlerHelper {

    public SimpleTypeFacetCommitHandlerHelper_AbstractDateFacet(SimpleTypeFacetCommitHandler commitHandler) {
        super(commitHandler);
    }

    @Override
    protected void validateCommit(String facetName) throws CommitValidationException {
        Object facetValue = commitHandler.getCommitedObj().getFacetValue(facetName);
        if (facetValue != null) {
            String value = (String) facetValue;
            value = value.trim();
            if (!value.isEmpty() && !validate(value)) {
                throw new CommitValidationException(Messages.SimpleTypeFacetCommitHandlerHelper_ValueTypeOfFacet + facetName
                        + Messages.SimpleTypeFacetCommitHandlerHelper_ShouldbePosition);
            }
        }
    }

    abstract boolean validate(String newValue);

    @Override
    protected boolean doCommit(String facetName) throws CommitException {

        String newValue = (String) commitHandler.getCommitedObj().getFacetValue(facetName);
        String oldValue = getOldFacetValue();

        if ((newValue == null && oldValue == null) || (oldValue != null && oldValue.equals(newValue))
                || (newValue != null && newValue.equals(oldValue))) {
            return false;
        }

        if (oldValue != null) {
            commitHandler.getCommitedObj().getXSDSimpleType().getFacetContents()
                    .removeAll(Arrays.asList(commitHandler.getOldFacets()));
        }

        if (newValue != null && !newValue.trim().isEmpty()) {
            XSDConstrainingFacet f = commitHandler.creatNewFacet();
            f.setLexicalValue("" + newValue);//$NON-NLS-1$
            commitHandler.getCommitedObj().getXSDSimpleType().getFacetContents().add(f);
        }

        return true;

    }

    protected String getOldFacetValue() {

        if (commitHandler.getOldFacets().length == 0) {
            return ""; //$NON-NLS-1$
        }

        XSDFacet facet = commitHandler.getOldFacets()[0];

        if (facet == null || facet.getLexicalValue() == null) {
            return null;
        }

        return facet.getLexicalValue();
    }
}

class SimpleTypeFacetCommitHandlerHelper_DateFacet extends SimpleTypeFacetCommitHandlerHelper_AbstractDateFacet {

    public SimpleTypeFacetCommitHandlerHelper_DateFacet(SimpleTypeFacetCommitHandler commitHandler) {
        super(commitHandler);
    }

    @Override
    boolean validate(String newValue) {
        return XSDUtil.isValidatedXSDDate(newValue);
    }

}

class SimpleTypeFacetCommitHandlerHelper_DateTimeFacet extends SimpleTypeFacetCommitHandlerHelper_AbstractDateFacet {

    public SimpleTypeFacetCommitHandlerHelper_DateTimeFacet(SimpleTypeFacetCommitHandler commitHandler) {
        super(commitHandler);
    }

    @Override
    boolean validate(String newValue) {
        return XSDUtil.isValidatedXSDDateTime(newValue);
    }

}

class SimpleTypeFacetCommitHandlerHelper_TimeFacet extends SimpleTypeFacetCommitHandlerHelper_AbstractDateFacet {

    public SimpleTypeFacetCommitHandlerHelper_TimeFacet(SimpleTypeFacetCommitHandler commitHandler) {
        super(commitHandler);
    }

    @Override
    boolean validate(String newValue) {
        return XSDUtil.isValidatedXSDTime(newValue);
    }

}

class SimpleTypeFacetCommitHandlerHelper_StringArrayFacet extends SimpleTypeFacetCommitHandlerHelper {

    public SimpleTypeFacetCommitHandlerHelper_StringArrayFacet(SimpleTypeFacetCommitHandler commitHandler) {
        super(commitHandler);
    }

    @Override
    protected void validateCommit(String facetName) throws CommitValidationException {

        if (!(commitHandler.getCommitedObj().getFacetValue(facetName) instanceof String[])) {
            throw new CommitValidationException(Messages.SimpleTypeFacetCommitHandlerHelper_ValueTypeOfFacet + facetName
                    + Messages.SimpleTypeFacetCommitHandlerHelper_ShouldbeStringArray);
        }

    }

    @Override
    protected boolean doCommit(String facetName) throws CommitException {

        if (!hasChange(commitHandler.getCommitedObj(), facetName)) {
            return false;
        }

        commitHandler.getCommitedObj().getXSDSimpleType().getFacetContents()
                .removeAll(Arrays.asList(commitHandler.getOldFacets()));
        for (String eachNewFacetValue : (String[]) commitHandler.getCommitedObj().getFacetValue(facetName)) {

            XSDConstrainingFacet f = commitHandler.creatNewFacet();
            f.setLexicalValue(eachNewFacetValue);
            commitHandler.getCommitedObj().getXSDSimpleType().getFacetContents().add(f);
        }

        return true;

    }

    private boolean hasChange(SimpleTypeWrapper simpleTypeWrapper, String facetName) {

        List<String> oldFacetValues = getOldFacetValues();

        List<String> newFacetValues = Arrays.asList((String[]) simpleTypeWrapper.getFacetValue(facetName));

        if (oldFacetValues.size() != newFacetValues.size()) {
            return true;
        }

        for (String eachNewValue : newFacetValues) {

            if (!oldFacetValues.contains(eachNewValue)) {
                return true;
            }
        }

        for (String eachOldFacetValue : oldFacetValues) {

            if (!newFacetValues.contains(eachOldFacetValue)) {
                return true;
            }
        }

        return false;
    }

    private List<String> getOldFacetValues() {

        List<String> oldPatterns = new ArrayList<String>();
        for (XSDFacet eachOldFacet : commitHandler.getOldFacets()) {

            if (eachOldFacet.getLexicalValue() == null || "".equals(eachOldFacet.getLexicalValue().trim())) {
                continue;
            }

            oldPatterns.add(eachOldFacet.getLexicalValue());
        }

        return oldPatterns;

    }

}
