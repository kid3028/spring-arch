package com.qull.springarch.util;

import jdk.nashorn.internal.ir.ReturnNode;
import jdk.nashorn.internal.ir.Statement;

import java.lang.reflect.*;
import java.security.interfaces.RSAKey;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 7:44
 */
public class ReflectionUtils {

    /**
     * Naming prefix for CGLIB-renamed methods
     */
    private static final String CGLIB_RENAMED_METHOD_PREFIX = "CGLIB$";

    /**
     * Pattern for detecting CGLIB-rename method
     */
    private static final Pattern CGLIB_RENAMED_METHOD_PATTERN = Pattern.compile("(.+)\\$\\d+");

    private static final Map<Class<?>, Method[]> declareMehtodsCache = new ConcurrentHashMap<>(256);

    public static Field findField(Class<?> clazz, String name) {
        return findField(clazz, name, null);
    }

    private static Field findField(Class<?> clazz, String name, Class<?> type) {
        Assert.notNull(clazz, "Class must not be null");
        Class<?> searchType = clazz;
        while (!Object.class.equals(searchType) && searchType != null) {
            Field[] fields = searchType.getDeclaredFields();
            for (Field field : fields) {
                if (name == null || name.equals(field.getName()) && (type == null || type.equals(field.getType()))) {
                    return field;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    public static void setField(Field field, Object target, Object value) {
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            handleReflectionException(e);
            throw new IllegalStateException("Unexpected reflection exception - " + e.getClass().getName() + " : " + e.getMessage());
        }
    }

    public static Object getField(Field field, Object target) {
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            handleReflectionException(e);
            throw new IllegalStateException("Unexpected reflection exception - " + e.getClass().getName() + " : " + e.getMessage());
        }
    }

    public static Method findMethod(Class<?> clazz, String name) {
        return findMethod(clazz, name, new Class<?>[0]);
    }

    public static Method findMethod(Class<?> clazz, String name, Class<?>... paramType) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(name, "Method name must not be null");
        Class<?> searchType = clazz;
        while (searchType != null) {
            Method[] methods = (searchType.isInterface() ? searchType.getMethods() : getDeclaredMethods(searchType));
            for (Method method : methods) {
                if (name.equals(method.getName()) && (paramType == null || Arrays.equals(paramType, method.getParameterTypes()))) {
                    return method;
                }
            }
            searchType = searchType.getSuperclass();
        }
        return null;
    }

    private static Method[] getDeclaredMethods(Class<?> clazz) {
        Method[] result = declareMehtodsCache.get(clazz);
        if (result == null) {
            result = clazz.getDeclaredMethods();
            declareMehtodsCache.put(clazz, result);
        }
        return result;
    }

    public static Object invokeMethod(Method method, Object target, Object... args) {
        try {
            return method.invoke(target, args);
        } catch (Exception e) {
            handleReflectionException(e);
            throw new IllegalStateException("Should never get here");
        }
    }

    public static void makeAccessible(Field field) {
        if ((!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()) ||
            Modifier.isFinal(field.getModifiers()) &&
                    !field.isAccessible()
        )) {
            field.setAccessible(true);
        }
    }

    private static void handleReflectionException(Exception e) {
        if(e instanceof NoSuchMethodException) {
            throw new IllegalStateException("Method not found : " + e.getMessage());
        }else if(e instanceof IllegalAccessException) {
            throw new IllegalStateException("Could not access method : " + e.getMessage());
        }else if(e instanceof InvocationTargetException) {
            handlerInvocationTargetException((InvocationTargetException) e);
        }else if(e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
    }

    private static void handlerInvocationTargetException(InvocationTargetException e) {
        rethrowRuntimeException(e.getTargetException());
    }

    private static void rethrowRuntimeException(Throwable e) {
        if(e instanceof RuntimeException) {
            throw (RuntimeException)e;
        }else if(e instanceof Error) {
            throw (Error)e;
        }else {
            throw new UndeclaredThrowableException(e);
        }
    }

}
