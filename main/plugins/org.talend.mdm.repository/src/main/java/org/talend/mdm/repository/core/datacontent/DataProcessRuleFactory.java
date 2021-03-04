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
package org.talend.mdm.repository.core.datacontent;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.amalto.workbench.webservices.TMDMService;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSGetConceptsInDataCluster;
import com.amalto.workbench.webservices.WSStringArray;

/**
 * created by HHB on 2012-10-8 Detailled comment
 *
 */
public class DataProcessRuleFactory {

    /**
     * if using XML DB and the return records exceed limit, it will return null;
     *
     * @param port
     * @param dataClusterName
     * @return
     */
    public static DataProcessRule createProcessRouterFromRemote(TMDMService service, String dataClusterName) {

        WSGetConceptsInDataCluster param = new WSGetConceptsInDataCluster(new WSDataClusterPK(dataClusterName));
        WSStringArray concepts = service.getConceptsInDataCluster(param);
        if (concepts != null) {
            DataProcessRule rule = new DataProcessRule();
            for (String concept : concepts.getStrings()) {
                rule.addNewEnityUnit(concept);
            }
            return rule;
        }
        return null;
    }

    public static DataProcessRule createProcessRouterFromLocal(File folder, String dataClusterName) {
        DataProcessRule rule = new DataProcessRule();
        if (folder.exists() && folder.getName().equals(dataClusterName)) {
            String regex = dataClusterName + "\\.(\\w+?)\\.\\w+?"; //$NON-NLS-1$
            Pattern p = Pattern.compile(regex);
            Map<String, Integer> conceptMap = new HashMap<String, Integer>();
            for (File file : folder.listFiles()) {
                String fileName = file.getName();
                Matcher m = p.matcher(fileName);
                if (m.matches()) {
                    String concept = m.group(1);
                    Integer count = conceptMap.get(concept);
                    if (count == null) {
                        count = 1;
                    } else {
                        count = count.intValue() + 1;
                    }
                    conceptMap.put(concept, count);
                }

            }
            // construct rule
            for (String concept : conceptMap.keySet()) {
                DataEntityUnit unit = new DataEntityUnit(concept);
                unit.setCount(conceptMap.get(concept));
                rule.addNewEnityUnit(unit);
            }

        }
        return rule;
    }

}
