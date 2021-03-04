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
package org.talend.designer.routines.mdmprovider;

import org.talend.core.language.ECodeLanguage;
import org.talend.core.model.routines.IRoutineProviderCreator;
import org.talend.core.model.routines.IRoutinesProvider;

/**
 * bqian class global comment. Detailled comment
 */
public class MdmRoutinesProviderCreator implements IRoutineProviderCreator {

    IRoutinesProvider perlProvider = null;

    IRoutinesProvider javaProvider = null;

    public MdmRoutinesProviderCreator() {
        javaProvider = new MdmJavaRoutinesProvider();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.talend.core.model.routines.IRoutineProviderCreator#createIRoutinesProviderByLanguage(org.talend.core.language
     * .ECodeLanguage)
     */
    public IRoutinesProvider createIRoutinesProviderByLanguage(ECodeLanguage lan) {
        if (lan == ECodeLanguage.PERL) {
            return perlProvider;
        } else {
            return javaProvider;
        }

    }

}
