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
package com.amalto.workbench.utils;

public interface IConstants {

    /* Conditions operator */
    static final String[] COMPOSITE_VIEW_CONDITION_OPERATORS = { "Contains", "contains the sentence", "Join With", "Starts With",//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$
        "Is Empty Or Null", "whole content contains", "=", "!=", ">", ">=", "<", "<=" };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$//$NON-NLS-7$ //$NON-NLS-8$

    static final String[] VIEW_CONDITION_OPERATORS = {
            "Contains", "contains the sentence", "Starts With", "Is Empty Or Null", "whole content contains", "=",//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
        "!=", ">", ">=", "<", "<=" };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$

    static final String[] ROUTE_CONDITION_OPERATORS = { "Contains", "Matches", "Starts With", "Is Null", "Is Not Null", "=",//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$//$NON-NLS-6$
        "!=", ">", ">=", "<", "<=" };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$//$NON-NLS-4$//$NON-NLS-5$

    static final String[] SCHEMATRON_TYPES = { "assert", "report" };//$NON-NLS-1$//$NON-NLS-2$

    /* Operate Type */
    static final String[] OPERATE_TYPES = { "UPDATE", "CREATE", "DELETE" };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

    static final String[] PREDICATES = { "", "Or", "And", "Not" };//$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$ //$NON-NLS-4$

    static final String TALEND = "Talend MDM Studio";//$NON-NLS-1$

    // static final String TALEND_MDM="Talend MDM";
    /**
     * Used for Update Report
     */
    public final static String SOURCE_GENERICUI = "genericUI";//$NON-NLS-1$

    public final static String SOURCE_ADMINWORKBENCH = "adminWorkbench";//$NON-NLS-1$

    public final static String SOURCE_DATASYNCHRONIZATION = "dataSynchronization";//$NON-NLS-1$

    public final static String OPERATIONTYPE_CREATE = "CREATE";//$NON-NLS-1$

    public final static String OPERATIONTYPE_UPDATEE = "UPDATE";//$NON-NLS-1$

    public final static String OPERATIONTYPE_DELETE = "DELETE";//$NON-NLS-1$

    // public final static String OPERATIONTYPE_SYNCHRONIZE="SYNCHRONIZE";

    public final static String EXIST_PORT = "8180";//$NON-NLS-1$

    public final static String DEFAULT_NAME_SPACE = "http://www.w3.org/2001/XMLSchema";//$NON-NLS-1$

    /**
     * Used for simple type facet
     */

    public static final String SIMPLETYPE_FACETNAME_PATTERN = "pattern";//$NON-NLS-1$

    public static final String SIMPLETYPE_FACETNAME_ENUM = "enumeration";//$NON-NLS-1$

    public static final String SIMPLETYPE_FACETNAME_LENGTH = "length";//$NON-NLS-1$

    public static final String SIMPLETYPE_FACETNAME_MINLENGTH = "minLength";//$NON-NLS-1$

    public static final String SIMPLETYPE_FACETNAME_MAXLENGTH = "maxLength";//$NON-NLS-1$

    public static final String SIMPLETYPE_FACETNAME_TOTALDIGITS = "totalDigits";//$NON-NLS-1$

    public static final String SIMPLETYPE_FACETNAME_FRACTIONDIGITS = "fractionDigits";//$NON-NLS-1$

    public static final String SIMPLETYPE_FACETNAME_MAXINCLUSIVE = "maxInclusive";//$NON-NLS-1$

    public static final String SIMPLETYPE_FACETNAME_MAXEXCLUSIVE = "maxExclusive";//$NON-NLS-1$

    public static final String SIMPLETYPE_FACETNAME_MININCLUSIVE = "minInclusive";//$NON-NLS-1$

    public static final String SIMPLETYPE_FACETNAME_MINEXCLUSIVE = "minExclusive";//$NON-NLS-1$

    public static final String SIMPLETYPE_FACETNAME_WHITESPACE = "whiteSpace";//$NON-NLS-1$

    /**
     * Item pk split
     */
    public static final String ITEM_PK_SPLIT = ".."; //$NON-NLS-1$
}
