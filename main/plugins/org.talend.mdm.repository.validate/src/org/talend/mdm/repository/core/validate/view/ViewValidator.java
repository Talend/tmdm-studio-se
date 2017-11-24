package org.talend.mdm.repository.core.validate.view;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.emf.common.util.EList;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.eclipse.wst.xml.core.internal.validation.core.AbstractNestedValidator;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.talend.core.model.repository.IRepositoryViewObject;
import org.talend.mdm.repository.core.IServerObjectRepositoryType;
import org.talend.mdm.repository.model.mdmproperties.WSViewItem;
import org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE;
import org.talend.mdm.repository.ui.widgets.UserSecurityComboBoxDialogCellEditor.UserVarValueValidator;
import org.talend.mdm.repository.utils.RepositoryResourceUtil;


public class ViewValidator extends AbstractNestedValidator implements IValidatorJob {

    @Override
    public ValidationReport validate(String uri, InputStream inputstream, NestedValidatorContext context) {
        String fileName = uri.substring(uri.lastIndexOf("/")+1); //$NON-NLS-1$
        String viewName = getViewName(fileName);
        IRepositoryViewObject viewObj = RepositoryResourceUtil.findViewObjectByName(IServerObjectRepositoryType.TYPE_VIEW,
                viewName);
        if (viewObj != null) {
            WSViewItem item = (WSViewItem) viewObj.getProperty().getItem();
            EList<WSWhereConditionE> whereConditions = item.getWsView().getWhereConditions();
            for (WSWhereConditionE conditionE : whereConditions) {
                String userVarValue = conditionE.getRightValueOrPath();
                String validateMsg = UserVarValueValidator.validate(userVarValue);
                System.out.println(userVarValue);
                if(validateMsg != null) {
                    validateMsg = "Value \"" + userVarValue + "\" in where condition of view \"" + viewName +"\" is not valid.";
                    ViewValidationReport viewValidationReport = new ViewValidationReport(uri);
                    viewValidationReport.messages.add(new ValidationMessage(validateMsg, -1, -1));
                    return viewValidationReport;
                }
            }
        }
        return null;
    }

    private String getViewName(String fileName) {
        Pattern pattern = Pattern.compile("(\\w*?)_(\\d*?)\\.(\\d*?)\\.item"); //$NON-NLS-1$
        Matcher matcher = pattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return fileName;
    }
}
