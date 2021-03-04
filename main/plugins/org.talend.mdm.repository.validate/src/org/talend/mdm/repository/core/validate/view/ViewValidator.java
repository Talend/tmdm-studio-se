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
package org.talend.mdm.repository.core.validate.view;

import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.core.validate.i18n.Messages;
import org.talend.mdm.repository.model.mdmproperties.WSViewItem;
import org.talend.mdm.repository.model.mdmserverobject.WSViewE;
import org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE;
import org.talend.mdm.repository.ui.widgets.UserVarValueValidator;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;

import com.amalto.workbench.utils.Util;
import com.amalto.workbench.webservices.WSWhereOperator;


public class ViewValidator extends AbstractNestedValidator implements IValidatorJob {

    private UserVarValueValidator userVarValueValidator;

    @Override
    public ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context) {
        ViewValidationReport viewValidationReport = new ViewValidationReport(uri);

        String viewName = getViewName(uri);

        IRepositoryViewObject viewObj = RepositoryResourceUtil.findViewObjectByName(IServerObjectRepositoryType.TYPE_VIEW,
                viewName);
        if (viewObj != null) {

            WSViewItem item = (WSViewItem) viewObj.getProperty().getItem();
            WSViewE view = (WSViewE) item.getMDMServerObject();
            EList<WSWhereConditionE> whereConditions = view.getWhereConditions();
            validateConditions(viewValidationReport, viewName, whereConditions);
        }
        return viewValidationReport;
    }

    private void validateConditions(ViewValidationReport viewValidationReport, String viewName,
            List<WSWhereConditionE> whereConditions) {
        if (whereConditions != null && whereConditions.size() > 0) {
            for (WSWhereConditionE conditionE : whereConditions) {
                String operator = conditionE.getOperator().getValue();
                String userVarValue = conditionE.getRightValueOrPath();
                if (WSWhereOperator.EMPTY_NULL.name().equals(operator)) {
                    if (userVarValue.length() > 0) {
                        String validateMsg = Messages.bind(Messages.ViewValidator_EmptyNullOperatorValue_error, viewName,
                                conditionE.getLeftPath(), Util.toReadable(WSWhereOperator.fromValue(operator)));
                        viewValidationReport.addValidationMessage(new ValidationMessage(validateMsg, -1, -1));
                    }
                } else if (StringUtils.isBlank(userVarValue) || !getUserVarValueValidator().validate(userVarValue)) {
                    String validateMsg = Messages.bind(Messages.ViewValidator_error, viewName, conditionE.getLeftPath(),
                            Util.toReadable(WSWhereOperator.fromValue(operator)), userVarValue);
                    viewValidationReport.addValidationMessage(new ValidationMessage(validateMsg, -1, -1));
                }
            }
        }
    }

    private String getViewName(String uri) {
        String viewName = uri.substring(uri.lastIndexOf("/") + 1); //$NON-NLS-1$

        Pattern pattern = Pattern.compile("(\\w*?\\$?\\w*?)_(\\d*?)\\.(\\d*?)\\.item"); //$NON-NLS-1$
        Matcher matcher = pattern.matcher(viewName);
        if (matcher.find()) {
            viewName = matcher.group(1);
        }

        viewName = viewName.replace("$", "#"); //$NON-NLS-1$ //$NON-NLS-2$

        return viewName;
    }

    private UserVarValueValidator getUserVarValueValidator() {
        if (userVarValueValidator == null) {
            userVarValueValidator = new UserVarValueValidator();
        }

        return userVarValueValidator;
    }

}
