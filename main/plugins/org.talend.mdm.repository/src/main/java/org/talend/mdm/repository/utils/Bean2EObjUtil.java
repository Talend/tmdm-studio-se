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

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.talend.mdm.repository.model.mdmserverobject.MDMServerObject;
import org.talend.mdm.repository.model.mdmserverobject.MdmserverobjectFactory;

import com.amalto.workbench.models.TreeObject;

/**
 * DOC hbhong class global comment. Detailled comment
 */
public class Bean2EObjUtil {

    static Logger log = Logger.getLogger(Bean2EObjUtil.class);

    BidiMap classMap = new DualHashBidiMap();

    Map<Object, EStructuralFeature> fieldMap = new HashMap<Object, EStructuralFeature>();

    BeanClassUtil beanClassUtil = new BeanClassUtil(this);

    EMFClassUtil emfClassUtil = EMFClassUtil.getInstance();

    static Bean2EObjUtil instance = new Bean2EObjUtil();

    /**
     * DOC hbhong Bean2EObjUtil constructor comment.
     */
    private Bean2EObjUtil() {
    }

    public static Bean2EObjUtil getInstance() {
        return instance;
    }

    public void registerClassMap(Class cls) {
        if (cls == null) {
            return;
        }
        EClass eCls;
        eCls = emfClassUtil.guessEClassByClassName(cls);
        if (eCls != null) {
            classMap.put(cls, eCls);
            beanClassUtil.refactorClassStructure(cls);
            guessField(cls, eCls);
        }
    }

    private void guessField(Class cls, EClass eCls) {
        Map<Object, Method[]> map = beanClassUtil.findFieldMap(cls);

        if (map != null) {
            EList<EStructuralFeature> features = eCls.getEAllStructuralFeatures();
            if (cls.isEnum()) {
                for (EStructuralFeature feature : features) {
                    String featureName = feature.getName();
                    if ("value".equals(featureName)) { //$NON-NLS-1$
                        fieldMap.put(cls, feature);
                        break;
                    }
                }
            } else {

                for (Object fieldObj : map.keySet()) {
                    Field field = (Field) fieldObj;
                    String fieldName = field.getName();
                    boolean found = false;
                    for (EStructuralFeature feature : features) {
                        String featureName = feature.getName();
                        if (fieldName.equalsIgnoreCase(featureName)) {
                            fieldMap.put(field, feature);
                            found = true;
                            //                        System.out.println("\t Found Field Map:" + fieldName + "\n\tfield=" + field + "\n\tfeature=" + feature); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                            break;
                        }
                    }
                    if (!found) {
                        log.debug("\t Failed to Found Field Map:" + fieldName); //$NON-NLS-1$
                    }
                }
            }
        }
    }

    public EObject convertFromBean2EObj(Object bean, EObject eObj) {
        Class beanCls = bean.getClass();
        EClass eCls = (EClass) classMap.get(beanCls);
        if (eObj == null) {
            eObj = MdmserverobjectFactory.eINSTANCE.create(eCls);
        }
        if (eCls != null) {
            Map<Object, Method[]> beanFieldMap = beanClassUtil.findFieldMap(beanCls);
            if (beanFieldMap == null) {
                return null;
            }
            if (beanCls.isEnum()) {
                Method[] methods = beanFieldMap.get(beanCls);
                EStructuralFeature feature = fieldMap.get(beanCls);
                if (feature != null) {
                    try {
                        Object value = methods[0].invoke(bean);
                        eObj.eSet(feature, value);
                    } catch (IllegalArgumentException e) {
                        log.error(e.getMessage(), e);
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage(), e);
                    } catch (InvocationTargetException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            } else {
                for (Object fieldObj : beanFieldMap.keySet()) {
                    Field field = (Field) fieldObj;
                    try {
                        EStructuralFeature feature = fieldMap.get(field);
                        // System.out.println("Field>>\t" + field + "\n\tfeature>>" + feature);
                        if (feature == null) {
                            dumpMap();
                        }
                        if (feature != null) {
                            Method getMethod = beanFieldMap.get(field)[0];
                            Object value = getMethod.invoke(bean);
                            if (beanClassUtil.isCollectionField(field)) {
                                if (value != null) {
                                    ((EList) eObj.eGet(feature)).clear();
                                    for (Object childObj : (Collection) value) {
                                        if (childObj != null) {
                                            Object eChildObj = null;
                                            if (beanClassUtil.isJavaClass(childObj.getClass())) {
                                                eChildObj = childObj;
                                            } else {
                                                eChildObj = convertFromBean2EObj(childObj, null);
                                            }
                                            if (eChildObj != null) {
                                                ((EList) eObj.eGet(feature)).add(eChildObj);
                                            }
                                        }
                                    }
                                }
                            } else if (beanClassUtil.isArrayField(field)) {
                                if (value != null) {
                                    ((EList) eObj.eGet(feature)).clear();
                                    for (Object childObj : (Object[]) value) {
                                        if (childObj != null) {
                                            Object eChildObj = null;
                                            if (beanClassUtil.isJavaClass(childObj.getClass())) {
                                                eChildObj = childObj;
                                            } else {
                                                eChildObj = convertFromBean2EObj(childObj, null);
                                            }
                                            if (eChildObj != null) {
                                                ((EList) eObj.eGet(feature)).add(eChildObj);
                                            }
                                        }
                                    }
                                }
                            } else {
                                Object eValue = null;
                                if (beanClassUtil.isJavaField(field)) {
                                    eValue = value;
                                } else {
                                    // a object reference
                                    if (value != null) {
                                        eValue = convertFromBean2EObj(value, null);
                                    } else {
                                        eValue = null;
                                    }
                                }
                                eObj.eSet(feature, eValue);
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        log.error(e.getMessage(), e);
                    } catch (IllegalAccessException e) {
                        log.error(e.getMessage(), e);
                    } catch (InvocationTargetException e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }
            return eObj;
        }
        return null;
    }

    public Object convertFromEObj2Bean(EObject eObj) {
        EClass eCls = eObj.eClass();
        Class beanCls = (Class) classMap.getKey(eCls);
        if (beanCls != null) {
            Map<Object, Method[]> beanFieldMap = beanClassUtil.findFieldMap(beanCls);
            if (beanFieldMap == null) {
                return null;
            }

            try {
                if (beanCls.isEnum()) {
                    EStructuralFeature feature = fieldMap.get(beanCls);
                    if (feature != null) {
                        Method convertMethod = beanFieldMap.get(beanCls)[1];
                        Object value = eObj.eGet(feature);
                        try {
                            return convertMethod.invoke(null, value);
                        } catch (IllegalArgumentException e) {
                            log.error(e.getMessage(), e);
                        } catch (InvocationTargetException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                } else {
                    Object bean = null;
                    bean = beanCls.newInstance();

                    for (Object fieldObj : beanFieldMap.keySet()) {
                        Field field = (Field) fieldObj;
                        try {
                            EStructuralFeature feature = fieldMap.get(field);
                            if (feature != null) {
                                Method setMethod = beanFieldMap.get(field)[1];

                                Object value = eObj.eGet(feature);
                                if (value != null) {
                                    if (feature.isMany()) {
                                        EList list = (EList) value;
                                        if (beanClassUtil.isCollectionField(field)) {
                                            Object collectionObj = setMethod.invoke(bean);
                                            if (collectionObj instanceof Collection) {
                                                for (Object echildObj : list) {
                                                    Object childObj = null;
                                                    if (echildObj != null && beanClassUtil.isJavaClass(echildObj.getClass())) {
                                                        childObj = echildObj;
                                                    } else {
                                                        childObj = convertFromEObj2Bean((EObject) echildObj);
                                                    }
                                                    ((Collection) collectionObj).add(childObj);
                                                }
                                            }

                                        } else if (beanClassUtil.isArrayField(field)) {

                                            Object newInstance = Array.newInstance(field.getType().getComponentType(),
                                                    list.size());
                                            Object[] children = (Object[]) newInstance;
                                            int i = 0;
                                            for (Object echildObj : list) {
                                                Object childObj = null;
                                                if (echildObj != null && beanClassUtil.isJavaClass(echildObj.getClass())) {
                                                    childObj = echildObj;
                                                } else {
                                                    childObj = convertFromEObj2Bean((EObject) echildObj);
                                                }
                                                children[i] = childObj;
                                                i++;
                                            }
                                            setMethod.invoke(bean, newInstance);
                                        }

                                    } else {
                                        Object eValue = null;
                                        if (beanClassUtil.isJavaField(field)) {
                                            eValue = value;
                                        } else {
                                            // a object reference
                                            eValue = convertFromEObj2Bean((EObject) value);
                                        }
                                        setMethod.invoke(bean, eValue);
                                    }
                                }
                            }
                        } catch (IllegalArgumentException e) {
                            log.error(e.getMessage(), e);
                        } catch (IllegalAccessException e) {
                            log.error(e.getMessage(), e);
                        } catch (InvocationTargetException e) {
                            log.error(e.getMessage(), e);
                        }
                    }
                    return bean;
                }
            } catch (InstantiationException e) {
                log.error(e.getMessage(), e);
            } catch (IllegalAccessException e) {
                log.error(e.getMessage(), e);
            }
        }
        return null;
    }

    public void dumpMap() {
        System.out.println("ClassMap : " + classMap.size()); //$NON-NLS-1$
        for (Object obj : classMap.keySet()) {
            System.out.println(obj + "\n\t=> " + classMap.get(obj)); //$NON-NLS-1$
        }
        System.out.println("FieldMap : " + fieldMap.size()); //$NON-NLS-1$
        for (Object obj : fieldMap.keySet()) {
            System.out.println(obj + "\n\t=> " + fieldMap.get(obj)); //$NON-NLS-1$
        }
    }

    public TreeObject wrapEObjWithTreeObject(EObject eobj) {
        if (eobj instanceof MDMServerObject) {
            MDMServerObject serverObject = (MDMServerObject) eobj;
            Object wsObj = convertFromEObj2Bean(eobj);
            return wrapEObjWithTreeObject(eobj, wsObj);
        }
        return null;
    }

    public TreeObject wrapEObjWithTreeObject(EObject eobj, Object wsObj) {
        if (eobj instanceof MDMServerObject) {
            MDMServerObject serverObject = (MDMServerObject) eobj;
            TreeObject treeObj = new TreeObject(serverObject.getName(), null, serverObject.getType(), serverObject.getName(),
                    wsObj);
            return treeObj;
        }
        return null;
    }
}
