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
package org.talend.mdm.workbench.serverexplorer.migration;

import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.talend.commons.exception.ExceptionHandler;
import org.talend.commons.utils.PasswordEncryptUtil;
import org.talend.core.model.properties.Item;
import org.talend.core.model.repository.ERepositoryObjectType;
import org.talend.mdm.repository.model.mdmmetadata.MDMServerDef;
import org.talend.mdm.repository.model.mdmproperties.MDMServerDefItem;
import org.talend.mdm.workbench.serverexplorer.core.ServerDefService;
import org.talend.repository.model.migration.UnifyPasswordEncryption4ItemMigrationTask;
import org.talend.utils.security.CryptoMigrationUtil;

import com.amalto.workbench.utils.PasswordUtil;

/**
 * created by HHB on 2014年11月27日 Detailled comment
 *
 */
public class UnifyPasswordEncryption4MDMServerDefMigrationTask extends UnifyPasswordEncryption4ItemMigrationTask {

    @Override
    public List<ERepositoryObjectType> getTypes() {
        return Collections.singletonList(ServerDefService.REPOSITORY_TYPE_SERVER_DEF);
    }

    @Override
    public Date getOrder() {
        GregorianCalendar gc = new GregorianCalendar(2019, 10, 10, 12, 0, 0);
        return gc.getTime();
    }

    @Override
    public ExecutionResult execute(Item item) {
        if (item != null && item instanceof MDMServerDefItem) {
            MDMServerDef serverDef = ((MDMServerDefItem) item).getServerDef();
            String algorithm = serverDef.getAlgorithm();
            if (algorithm != null) {
                String decryptedPassword = null;
                if (algorithm.equals(PasswordUtil.ALGORITHM_COMMON)) {
                    try {
                        decryptedPassword = PasswordEncryptUtil.decryptPassword(serverDef.getPasswd());
                    } catch (Exception e) {
                        return ExecutionResult.FAILURE;
                    }
                } else if (algorithm.equals(PasswordUtil.ALGORITHM_COMMON_V2)) {
                    try {
                        decryptedPassword = CryptoMigrationUtil.decrypt(serverDef.getPasswd());
                    } catch (Exception e) {
                        return ExecutionResult.FAILURE;
                    }
                }

                if (decryptedPassword != null) {
                    serverDef.setPasswd(PasswordUtil.encryptPassword(decryptedPassword));
                    serverDef.setAlgorithm(PasswordUtil.ALGORITHM_COMMON_V3);
                    try {
                        factory.save(item, true);
                        return ExecutionResult.SUCCESS_NO_ALERT;
                    } catch (Exception e) {
                        ExceptionHandler.process(e);
                        return ExecutionResult.FAILURE;
                    }
                }
            }
        }

        return ExecutionResult.NOTHING_TO_DO;
    }

}
