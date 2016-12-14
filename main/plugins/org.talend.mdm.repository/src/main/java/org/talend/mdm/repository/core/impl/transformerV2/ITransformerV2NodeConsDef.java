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
package org.talend.mdm.repository.core.impl.transformerV2;

public interface ITransformerV2NodeConsDef {

    public static final int TYPE_PROCESS = 1;

    public static final int TYPE_BEFORESAVE = 2;

    public static final int TYPE_BEFOREDEL = 3;

    public static final int TYPE_ENTITYACTION = 4;

    public static final int TYPE_WELCOMEACTION = 5;

    public static final int TYPE_SMARTVIEW = 6;

    public static final int TYPE_OTHER = 7;

    public static final String PREFIX_BEFOREDEL_UPPER = "beforeDeleting_";//$NON-NLS-1$

    public static final String PREFIX_BEFORESAVE_UPPER = "beforeSaving_";//$NON-NLS-1$

    public static final String PREFIX_RUNNABLE_UPPER = "Runnable_";//$NON-NLS-1$

    public static final String PREFIX_STANDLONE_UPPER = "Runnable#";//$NON-NLS-1$

    public static final String PREFIX_STANDLONE_UPPER_OLD = "Runnable$";//$NON-NLS-1$

    public static final String PREFIX_SMARTVIEW_UPPER = "Smart_view_";//$NON-NLS-1$

    public static final String PATH_PROCESS = "Process";//$NON-NLS-1$

    public static final String PATH_BEFORESAVE = "beforesaving";//$NON-NLS-1$

    public static final String PATH_BEFOREDEL = "beforedeleting";//$NON-NLS-1$

    public static final String PATH_ENTITYACTION = "entityaction";//$NON-NLS-1$

    public static final String PATH_WELCOMEACTION = "welcomeaction";//$NON-NLS-1$

    public static final String PATH_SMARTVIEW = "smartview";//$NON-NLS-1$

    public static final String PATH_OTHER = "other";//$NON-NLS-1$
}
