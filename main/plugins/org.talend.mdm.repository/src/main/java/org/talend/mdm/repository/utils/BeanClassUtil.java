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
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * DOC hbhong class global comment. Detailled comment
 */
class BeanClassUtil {

    static Logger log = Logger.getLogger(BeanClassUtil.class);

    Map<Class, Map<Object, Method[]>> map = new HashMap<Class, Map<Object, Method[]>>();

    private Bean2EObjUtil mapUtil;

    /**
     * DOC hbhong BeanClassUtil constructor comment.
     *
     * @param mapUtil
     */
    public BeanClassUtil(Bean2EObjUtil mapUtil) {
        this.mapUtil = mapUtil;
    }

    public Map<Object, Method[]> findFieldMap(Class cls) {
        return map.get(cls);
    }

    private String calGetMethodName(Field field) {
        String name = field.getName();
        if (name.startsWith("_")) {
            name = name.substring(1);
        }
        Class type = field.getType();
        // in normal the var name's length>1 ,there is a potential bug

        return (((type.isPrimitive() && type.getName().equals("boolean")) || type.getName().equals("java.lang.Boolean")) ? "is" : "get") + name.substring(0, 1).toUpperCase() + name.substring(1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private String calSetMethodName(Field field) {
        String name = field.getName();
        if (name.startsWith("_")) {
            name = name.substring(1);
        }
        // in normal the var name's length>1 ,there is a potential bug
        return "set" + name.substring(0, 1).toUpperCase() + name.substring(1); //$NON-NLS-1$
    }

    private void fillMap(Class cls, Object member, Method[] methods) {
        Map<Object, Method[]> fieldMap = map.get(cls);
        if (fieldMap == null) {
            fieldMap = new HashMap<Object, Method[]>();
            map.put(cls, fieldMap);
        }
        //
        fieldMap.put(member, methods);
    }

    private Method findMethod(Class cls, String methodName, Class... paramTypes) {
        for (Method method : cls.getMethods()) {
            String name = method.getName();
            if (methodName.equalsIgnoreCase(name)) {
                if (paramTypes == null) {
                    return method;
                } else {
                    Class<?>[] curParamTypes = method.getParameterTypes();
                    if (curParamTypes.length == paramTypes.length) {
                        boolean isOK = true;
                        for (int i = 0; i < curParamTypes.length; i++) {
                            if (curParamTypes[i] != paramTypes[i]) {
                                isOK = false;
                                break;
                            }
                        }
                        if (isOK) {
                            return method;
                        }
                    }
                }
            }
        }
        return null;
    }

    private Method[] getEnumMethods(Class cls) {
        Method[] methods = new Method[2];
        for (Method method : cls.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("value")) { //$NON-NLS-1$
                methods[0] = method;
            } else if (methodName.equals("fromValue")) { //$NON-NLS-1$
                methods[1] = method;
            }
        }
        return methods;
    }

    public void refactorClassStructure(Class cls) {
        if (map.containsKey(cls)) {// map.clear()
            return;
        }
        // Enum
        if (cls.isEnum()) {
            Method[] enumMethods = getEnumMethods(cls);
            fillMap(cls, cls, enumMethods);
        } else {
            // other class
            for (Field field : cls.getDeclaredFields()) {
                if (field.isEnumConstant()) {
                    continue;
                }
                try {
                    String getMethodName = calGetMethodName(field);
                    Method getMethod = findMethod(cls, getMethodName);
                    Method[] methods = new Method[2];
                    Class fieldType = field.getType();

                    if (getMethod != null && getMethod.getReturnType() == fieldType) {
                        //                    System.out.println(field + "\n\t" + getMethod); //$NON-NLS-1$
                        methods[0] = getMethod;
                    } else {
                        log.debug("can't find getMethod for " + field); //$NON-NLS-1$
                    }
                    if (!isCollectionField(field)) {
                        //
                        String setMethodName = calSetMethodName(field);
                        Method setMethod = findMethod(cls, setMethodName, fieldType);
                        if (setMethod != null) {
                            //                    System.out.println(field + "\n\t" + setMethod); //$NON-NLS-1$
                            methods[1] = setMethod;
                        } else {
                            log.debug("can't find setMethod for " + field); //$NON-NLS-1$
                        }
                    } else {
                        // List field
                        methods[1] = methods[0];
                    }
                    if (methods[0] != null && methods[1] != null) {
                        fillMap(cls, field, methods);
                        handleChildClass(field);
                    }
                    //

                } catch (SecurityException e) {
                    log.error(e.getMessage(), e);
                }
                // catch (NoSuchMethodException e) {
                //                log.debug(e.getMessage() + "=>Skip it"); //$NON-NLS-1$
                // }
            }
        }
    }

    private Class getComponentType(Field field) {
        if (isCollectionField(field)) {
            Type genericType = field.getGenericType();
            if (genericType instanceof ParameterizedType) {
                Type[] types = ((ParameterizedType) genericType).getActualTypeArguments();
                if (types != null && types.length > 0) {
                    return (Class) types[0];
                }
            }

        }
        if (isArrayField(field)) {
            Class<?> cls = field.getType();
            if (cls.isArray()) {
                return cls.getComponentType();
            }
        }
        return field.getType();
    }

    private Class getComponentType(Class cls) {
        if (cls.isArray()) {
            return cls.getComponentType();
        }
        return cls;
    }

    private void handleChildClass(Field field) {
        Class childCls = getComponentType(field);

        if (!isJavaClass(childCls)) {
            if (!map.containsKey(childCls)) {
                if (mapUtil != null) {
                    mapUtil.registerClassMap(childCls);
                }
            }
        }
    }

    public boolean isArrayField(Field field) {
        return field.getType().isArray();
    }

    public boolean isCollectionField(Field field) {
        String typeName = field.getType().getName();
        return typeName.equals("java.util.List");
    }

    public boolean isJavaField(Field field) {
        return isJavaClass(field);
    }

    public boolean isJavaClass(Field field) {
        if (field.getType().isPrimitive()) {
            return true;
        }
        return getComponentType(field).getName().startsWith("java"); //$NON-NLS-1$
    }

    public boolean isJavaClass(Class cls) {
        if (cls.isPrimitive()) {
            return true;
        }
        return getComponentType(cls).getName().startsWith("java"); //$NON-NLS-1$
    }

    public void dumpMap() {
        for (Class cls : map.keySet()) {
            System.out.println(cls.getName());
            Map<Object, Method[]> fieldMap = map.get(cls);
            for (Object field : fieldMap.keySet()) {
                System.out.println("\t|--- " + field); //$NON-NLS-1$
                Method[] methods = fieldMap.get(field);
                System.out.println("\t\t\t" + methods[0].getName()); //$NON-NLS-1$
                System.out.println("\t\t\t" + methods[1].getName()); //$NON-NLS-1$
            }
        }
    }
}
