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
package org.talend.mdm.repository.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.emf.ecore.EClass;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectPackage;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class EMFClassUtil {

    static Logger log = Logger.getLogger(EMFClassUtil.class);

    private List<EClass> definedClass = new LinkedList<EClass>();

    private EMFClassUtil() {
        initFixedMap();
    }

    Map<String, String> fixedNamedMap = new HashMap<String, String>();

    private void initFixedMap() {
        fixedNamedMap.put("WSMenuEntryDescriptions", "WSMenuMenuEntriesDescriptionsE");
    }

    private static EMFClassUtil instance = new EMFClassUtil();

    /**
     * Getter for instance.
     *
     * @return the instance
     */
    public static EMFClassUtil getInstance() {
        return instance;
    }

    public EClass guessEClassByClassName(Class cls) {
        String simpleName = cls.getSimpleName();
        String eClassName = null;
        Class enclosingClass = cls.getEnclosingClass();
        while (enclosingClass != null) {
            String enclosingName = enclosingClass.getSimpleName();
            simpleName = enclosingName + simpleName;
            enclosingClass = enclosingClass.getEnclosingClass();
        }
        eClassName = fixedNamedMap.get(simpleName);
        if (eClassName == null) {
            eClassName = simpleName + "E"; //$NON-NLS-1$
        }
        for (EClass eCls : getAllEClass()) {
            if (eCls.getName().equals(eClassName)) {
                //                System.out.println(">>For class:" + cls.getName() + " (Found:)=> " + eCls.getName()); //$NON-NLS-1$ //$NON-NLS-2$
                return eCls;
            }
        }
        log.debug(">>For class:" + simpleName + " Not Found corresponding EClass "); //$NON-NLS-1$//$NON-NLS-2$
        return null;
    }

    private List<EClass> getAllEClass() {
        if (definedClass.isEmpty()) {
            Class cls = MdmserverobjectPackage.Literals.class;
            for (Field field : cls.getDeclaredFields()) {
                if (field.getType() == EClass.class) {
                    try {
                        Object object = field.get(cls);
                        definedClass.add((EClass) object);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }

            }
        }
        return definedClass;
    }

}
