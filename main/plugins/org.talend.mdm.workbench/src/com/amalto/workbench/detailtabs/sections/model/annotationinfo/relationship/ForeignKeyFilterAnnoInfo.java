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
package com.amalto.workbench.detailtabs.sections.model.annotationinfo.relationship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.xsd.XSDComponent;

import com.amalto.workbench.detailtabs.sections.handlers.CommitHandler;
import com.amalto.workbench.detailtabs.sections.handlers.ElementForeignKeyFilterCommitHandler;
import com.amalto.workbench.detailtabs.sections.model.annotationinfo.SingleContentAnnotationInfo;

public class ForeignKeyFilterAnnoInfo extends SingleContentAnnotationInfo {

    public ForeignKeyFilterAnnoInfo(XSDComponent sourceComponent, String fkFilter) {
        super(sourceComponent, fkFilter);
    }

    public CommitHandler<ForeignKeyFilterAnnoInfo> createCommitHandler() {
        return new ElementForeignKeyFilterCommitHandler(this);
    }

    public static ForeignKeyFilterAnnoInfoDefUnit[] getFKFilterCfgInfos(String filterExpression) {

        if ("".equals(filterExpression)) { //$NON-NLS-1$
            return new ForeignKeyFilterAnnoInfoDefUnit[0];
        }

        List<ForeignKeyFilterAnnoInfoDefUnit> fkFilterInfos = new ArrayList<ForeignKeyFilterAnnoInfoDefUnit>();

        if (filterExpression != null) {
            String[] criterias = filterExpression.split("#");//$NON-NLS-1$
            for (String cria : criterias) {
                String[] values = cria.split("\\$\\$");//$NON-NLS-1$
                List<String> list = new ArrayList<String>();
                list.addAll(Arrays.asList(values));
                int num = 4 - list.size();
                for (int i = 0; i < num; i++) {
                    list.add("");//$NON-NLS-1$
                }

                fkFilterInfos.add(new ForeignKeyFilterAnnoInfoDefUnit(list.get(0), list.get(1), list.get(2).replaceAll("&quot;",//$NON-NLS-1$
                        "\""), list.get(3)));//$NON-NLS-1$

            }
        }
        return fkFilterInfos.toArray(new ForeignKeyFilterAnnoInfoDefUnit[0]);

    }

    public static String getFKFilterByFKFilterCfgInfos(ForeignKeyFilterAnnoInfoDefUnit[] fkFilterCfgInfos) {

        StringBuffer sb = new StringBuffer();
        for (ForeignKeyFilterAnnoInfoDefUnit eachFKFilterInfo : fkFilterCfgInfos) {

            if (eachFKFilterInfo.getXpath().trim().length() == 0 && eachFKFilterInfo.getOperator().trim().length() == 0
                    && eachFKFilterInfo.getNormalizeValue().trim().length() == 0
                    && eachFKFilterInfo.getPredicate().trim().length() == 0) {
                continue;
            }

            sb.append(eachFKFilterInfo.getXpath() + "$$" + eachFKFilterInfo.getOperator() + "$$"//$NON-NLS-1$//$NON-NLS-2$
                    + eachFKFilterInfo.getNormalizeValue() + "$$" + eachFKFilterInfo.getPredicate() + "#");//$NON-NLS-1$//$NON-NLS-2$
        }

        return sb.toString();
    }
}
