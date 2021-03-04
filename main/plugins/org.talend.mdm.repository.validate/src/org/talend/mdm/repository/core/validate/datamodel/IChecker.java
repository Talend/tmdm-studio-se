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
package org.talend.mdm.repository.core.validate.datamodel;

import java.io.File;
import java.util.List;

import org.talend.mdm.repository.core.validate.ValidationMessage;

/**
 * Interface for all operations that validates MDM repository objects.
 *
 * @param <T>
 */
public interface IChecker<T extends ValidationMessage> {

    /**
     * Checks the content in <code>file</code> and returns all warning/error messages or empty list if there's no
     * error/warning to report.
     *
     * @param file A file that must exists.
     * @return A list of warning/error or empty list if nothing to report.
     */
    List<T> toCheck(File file);

}
