// ============================================================================
//
// Copyright (C) 2006-2018 Talend Inc. - www.talend.com
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

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;
import org.talend.mdm.repository.model.mdmserverobject.WSWhereConditionE;
import org.talend.mdm.repository.model.mdmserverobject.WSWhereOperatorE;
import org.talend.mdm.repository.ui.widgets.UserVarValueValidator;

import com.amalto.workbench.webservices.WSWhereOperator;

@SuppressWarnings("nls")
public class ViewValidatorTest {

    @Test
    public void testGetViewName() throws Exception {
        // without filter name
        String uri = "file:///D:/workspace/TEST/MDM/view/web/Browse_items_Product_0.1.item";
        String viewName = "Browse_items_Product";

        ViewValidator viewValidator = new ViewValidator();
        Method getViewNameMethod = viewValidator.getClass().getDeclaredMethod("getViewName", String.class);
        getViewNameMethod.setAccessible(true);
        String name = (String) getViewNameMethod.invoke(viewValidator, uri);
        assertEquals(viewName, name);

        // with filter name
        uri = "file:///D:/workspace/TEST/MDM/view/web/Browse_items_Product$Stores_0.1.item";
        viewName = "Browse_items_Product#Stores";
        name = (String) getViewNameMethod.invoke(viewValidator, uri);
        assertEquals(viewName, name);
    }

    @Test
    public void testValidateConditions() throws Exception {
        String uri = "file:///D:/workspace/TEST/MDM/view/web/Browse_items_Product_0.1.item";
        String viewName = "Browse_items_Product";
        String method = "validateConditions";

        ViewValidationReport viewValidationReport = new ViewValidationReport(uri);

        ViewValidator viewValidator = new ViewValidator();
        Method validateConditionsMethod = viewValidator.getClass().getDeclaredMethod(method, ViewValidationReport.class,
                String.class, List.class);
        validateConditionsMethod.setAccessible(true);

        List<WSWhereOperator> operators = new ArrayList<>(Arrays.asList(WSWhereOperator.values()));
        operators.remove(WSWhereOperator.EMPTY_NULL);

        List<String> conditionValues = new ArrayList<>(operators.size());
        for (int i = 0; i < operators.size(); i++) {
            conditionValues.add("");
        }

        List<WSWhereConditionE> whereCondions = createWhereCondions(operators, conditionValues);
        // empty string value
        validateConditionsMethod.invoke(viewValidator, viewValidationReport, viewName, whereCondions);
        assertEquals(viewValidationReport.getValidationMessages().length, conditionValues.size());

        // blank string value
        viewValidationReport.messages.clear();
        Collections.fill(conditionValues, " ");
        whereCondions = createWhereCondions(operators, conditionValues);
        validateConditionsMethod.invoke(viewValidator, viewValidationReport, viewName, whereCondions);
        assertEquals(conditionValues.size(), viewValidationReport.getValidationMessages().length);

        // normal string
        viewValidationReport.messages.clear();
        Collections.fill(conditionValues, "condionValue");
        whereCondions = createWhereCondions(operators, conditionValues);
        validateConditionsMethod.invoke(viewValidator, viewValidationReport, viewName, whereCondions);
        assertEquals(0, viewValidationReport.getValidationMessages().length);

        // EMPTY_NULL operator, empty string value
        viewValidationReport.messages.clear();
        operators = new ArrayList<>(1);
        operators.add(WSWhereOperator.EMPTY_NULL);
        conditionValues = new ArrayList<>(operators.size());
        conditionValues.add("");
        whereCondions = createWhereCondions(operators, conditionValues);
        validateConditionsMethod.invoke(viewValidator, viewValidationReport, viewName, whereCondions);
        assertEquals(0, viewValidationReport.getValidationMessages().length);

        // EMPTY_NULL, blank string value
        viewValidationReport.messages.clear();
        conditionValues.clear();
        conditionValues.add(" ");
        whereCondions = createWhereCondions(operators, conditionValues);
        validateConditionsMethod.invoke(viewValidator, viewValidationReport, viewName, whereCondions);
        assertEquals(1, viewValidationReport.getValidationMessages().length);

        // EMPTY_NULL, normal string value
        viewValidationReport.messages.clear();
        conditionValues.clear();
        conditionValues.add("anyvalue");
        whereCondions = createWhereCondions(operators, conditionValues);
        validateConditionsMethod.invoke(viewValidator, viewValidationReport, viewName, whereCondions);
        assertEquals(1, viewValidationReport.getValidationMessages().length);

        // user variable value for non-EMPTY_NULL operator
        viewValidationReport.messages.clear();
        operators = new ArrayList<>(Arrays.asList(WSWhereOperator.values()));
        operators.remove(WSWhereOperator.EMPTY_NULL);
        conditionValues.clear();
        conditionValues.add("${user_context.");
        whereCondions = createWhereCondions(operators, conditionValues);
        UserVarValueValidator mock = Mockito.mock(UserVarValueValidator.class);
        Field declaredField = viewValidator.getClass().getDeclaredField("userVarValueValidator");
        declaredField.setAccessible(true);
        declaredField.set(viewValidator, mock);
        validateConditionsMethod.invoke(viewValidator, viewValidationReport, viewName, whereCondions);
        Mockito.verify(mock, times(operators.size())).validate(anyString());
    }

    private List<WSWhereConditionE> createWhereCondions(List<WSWhereOperator> operators, List<String> conditionValues) {
        List<WSWhereConditionE> whereCondions = new ArrayList<>();

        int size = conditionValues.size();

        for (int i = 0; i < operators.size(); i++) {
            WSWhereConditionE wsWhereConditionE = MdmserverobjectFactory.eINSTANCE.createWSWhereConditionE();
            WSWhereOperatorE whereOperatorE = MdmserverobjectFactory.eINSTANCE.createWSWhereOperatorE();
            whereOperatorE.setValue(operators.get(i).name());
            wsWhereConditionE.setLeftPath("path/" + i);
            wsWhereConditionE.setOperator(whereOperatorE);
            wsWhereConditionE.setRightValueOrPath(conditionValues.get(i % size));
            whereCondions.add(wsWhereConditionE);
        }

        return whereCondions;
    }
}
