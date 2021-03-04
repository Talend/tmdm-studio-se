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
package org.talend.mdm.repository.core.validate;

import org.eclipse.wst.validation.internal.ValidationResultSummary;

/**
 * created by HHB on 2013-1-23 Detailled comment
 *
 */
public interface IValidationPreference {

    public boolean shouldShowResults(ValidationResultSummary result);

    public void setShowResults(Boolean showing, ValidationResultSummary resultSummary);

    public void updateLastSelectedBun(int selectedBun, ValidationResultSummary resultSummary);

    /**
     *
     * @return IModelValidationService.VALIDATE_XXX
     */
    public int getValidationCondition();
}
