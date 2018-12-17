package org.talend.mdm.repository.core.validate.view;

import java.io.InputStream;
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

    @Override
    public ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context) {
        ViewValidationReport viewValidationReport = new ViewValidationReport(uri);
        
        String fileName = uri.substring(uri.lastIndexOf("/")+1); //$NON-NLS-1$
        String viewName = getViewName(fileName);
        viewName = viewName.replace("$", "#"); //$NON-NLS-1$ //$NON-NLS-2$
        IRepositoryViewObject viewObj = RepositoryResourceUtil.findViewObjectByName(IServerObjectRepositoryType.TYPE_VIEW,
                viewName);
        if (viewObj != null) {
            
            WSViewItem item = (WSViewItem) viewObj.getProperty().getItem();
            WSViewE view = (WSViewE)item.getMDMServerObject();
            EList<WSWhereConditionE> whereConditions = view.getWhereConditions();
            if(whereConditions != null && whereConditions.size() >0) {
                for (WSWhereConditionE conditionE : whereConditions) {
                    String operator = conditionE.getOperator().getValue();
                    String userVarValue = conditionE.getRightValueOrPath();
                    if (WSWhereOperator.EMPTY_NULL.name().equals(operator)) {
                        if (userVarValue.length() > 0) {
                            String validateMsg = Messages.bind(Messages.ViewValidator_EmptyNullOperatorValue_error, viewName,
                                    conditionE.getLeftPath(), Util.toReadable(WSWhereOperator.fromValue(operator)));
                            viewValidationReport.addValidationMessage(new ValidationMessage(validateMsg, -1, -1));
                        }
                    } else if (StringUtils.isBlank(userVarValue) || !UserVarValueValidator.validate(userVarValue)) {
                        String validateMsg = Messages.bind(Messages.ViewValidator_error, viewName, conditionE.getLeftPath(),
                                Util.toReadable(WSWhereOperator.fromValue(operator)), userVarValue);
                        viewValidationReport.addValidationMessage(new ValidationMessage(validateMsg, -1, -1));
                    }
                }
            }
        }
        return viewValidationReport;
    }

    private String getViewName(String fileName) {
        Pattern pattern = Pattern.compile("(\\w*?\\$?\\w*?)_(\\d*?)\\.(\\d*?)\\.item"); //$NON-NLS-1$
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return fileName;
    }
}
