package com.qull.springarch.util;

import jdk.internal.dynalink.beans.StaticClass;
import jdk.nashorn.internal.ir.ReturnNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/14 15:40
 */
public abstract class ClassUtils {
    private static final Logger log = LoggerFactory.getLogger(ClassUtils.class);

    /**
     * 包分隔符
     */
    private static final char PACKAGE_SEPARATOR = '.';

    /**
     * 路径分隔符
     */
    private static final char PATH_SEPARATOR = '/';

    /**
     * 内部类分隔符
     */
    private static final char INNER_CLASS_SEPARATOR = '$';

    /**
     * CGLIB 分隔符
     */
    private static final String CGLIB_CLASS_SEPARATOR = "$$";

    /**
     * 包装类 --> 原生类
     */
    private static final Map<Class<?>, Class<?>> wrapperToPrimitiveTypeMap = new HashMap<>(11);
    private static final Map<Class<?>, Class<?>> primitiveTypeToWrapperMap = new HashMap<>(11);

    static {
        wrapperToPrimitiveTypeMap.put(Boolean.class, boolean.class);
        wrapperToPrimitiveTypeMap.put(Byte.class, byte.class);
        wrapperToPrimitiveTypeMap.put(Short.class, short.class);
        wrapperToPrimitiveTypeMap.put(Character.class, char.class);
        wrapperToPrimitiveTypeMap.put(Integer.class, int.class);
        wrapperToPrimitiveTypeMap.put(Float.class, float.class);
        wrapperToPrimitiveTypeMap.put(Double.class, double.class);
        wrapperToPrimitiveTypeMap.put(Long.class, double.class);

        wrapperToPrimitiveTypeMap.forEach((k,v) -> primitiveTypeToWrapperMap.put(v, k));
    }

    public static ClassLoader getDefaultClassLoader() {
        ClassLoader c1 = null;
        try {
            c1 =Thread.currentThread().getContextClassLoader();
        }catch (Throwable ex) {
            log.warn("cannot access thread context ClassLoader...");
        }
        if(c1 == null) {
            // no thread context classLoad, use class loader of this class
            c1 = ClassUtils.class.getClassLoader();
            if (c1 == null) {
                // 如果为null，说明是BootstrapClassLoader
                try {
                    c1 = ClassLoader.getSystemClassLoader();
                }catch(Throwable ex) {
                    log.warn("cannot access system classLoader");
                }
            }
        }
        return c1;
    }

    public static boolean isAssignableValue(Class<?> type, Object value) {
        Assert.notNull(type, "Type must not be null");
        return (value != null ? isAssignable(type, value.getClass()) : !type.isPrimitive());
    }

    private static boolean isAssignable(Class<?> lhsType, Class<?> rhsType) {
        Assert.notNull(lhsType, "Left-hand side type must not be null");
        Assert.notNull(rhsType, "Right-hand side type must not null");
        if (lhsType.isAssignableFrom(rhsType)) {
            return true;
        }

        if (lhsType.isPrimitive()) {
            Class<?> resolvedPrimitive = wrapperToPrimitiveTypeMap.get(rhsType);
            if (resolvedPrimitive != null && lhsType.equals(resolvedPrimitive)) {
                return true;
            }
        }else {
            Class<?> resolvedWrapper = primitiveTypeToWrapperMap.get(rhsType);
            if (resolvedWrapper != null && lhsType.isAssignableFrom(resolvedWrapper)) {
                return true;
            }
        }
        return false;
    }

    public static String convertResourcePathToClassName(String resourcePath) {
        Assert.notNull(resourcePath, "Resource path must not be null");
        return resourcePath.replace(PATH_SEPARATOR, PACKAGE_SEPARATOR);
    }

    public static String convertClassNameToResourcePath(String className) {
        Assert.notNull(className, "Class name must not be null");
        return className.replace(PACKAGE_SEPARATOR, PATH_SEPARATOR);
    }

    public static String getShortName(String className) {
        int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
        int nameEndIndex = className.indexOf(CGLIB_CLASS_SEPARATOR);
        if (nameEndIndex == -1) {
            nameEndIndex = className.length();
        }
        String shortName = className.substring(lastDotIndex + 1, nameEndIndex);
        shortName = shortName.replace(INNER_CLASS_SEPARATOR, PACKAGE_SEPARATOR);
        return shortName;
    }


    public static Set<Class> getAllInterfacesForClassAsSet(Class<?> targetClass) {
        return getAllInterfacesForClassAsSet(targetClass, null);
    }

    /**
     * Return all interfaces that the given class implements as Set, including ones implemented by supperClass.
     * if the class itself is an interface, it gets returned as sole interface.
     *
     * @param clazz the class to analyze for interface
     * @param classLoader the ClassLoader that the interfaces need to be visible in (may be {@code null}
     *                    when accepting all declared interfaces)
     * @return all interfaces that the given object implements as Set
     */
    private static Set<Class> getAllInterfacesForClassAsSet(Class<?> clazz, ClassLoader classLoader) {
        Assert.notNull(clazz, "Class must not be null");
        if (clazz.isInterface() && isVisible(clazz, classLoader)) {
            return Collections.singleton(clazz);
        }
        Set<Class> interfaces = new LinkedHashSet<>();
        while (clazz != null) {
            Class<?>[] ifcs = clazz.getInterfaces();
            for (Class<?> ifc : ifcs) {
                interfaces.addAll(getAllInterfacesForClassAsSet(ifc, classLoader));
            }
            clazz = clazz.getSuperclass();
        }
        return interfaces;
    }

    private static boolean isVisible(Class<?> clazz, ClassLoader classLoader) {
        if (classLoader == null) {
            return true;
        }

        try {
            Class<?> atualClass = classLoader.loadClass(clazz.getName());
            return clazz == atualClass;
            // Else different interface class found
        } catch (ClassNotFoundException e) {
            // No interface class found
            return false;
        }
    }
}
