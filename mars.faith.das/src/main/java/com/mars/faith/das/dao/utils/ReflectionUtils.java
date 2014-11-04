/*********************************************************************************
*
* <p>
* Perforce File Stats:
* <pre>
* $Id: //brazil/src/appgroup/appgroup/libraries/FBAMICDataAccessLayer/mainline/src/com/amazon/fba/mic/dao/utils/ReflectionUtils.java#1 $
* $DateTime: 2012/12/26 05:52:14 $
* $Change: 6702674 $
* </pre>
* </p>
*
* @author $Author: wdong $
* @version $Revision: #1 $
*
* Copyright Notice
*
* This file contains proprietary information of Amazon.com.
* Copying or reproduction without prior written approval is prohibited.
*
* Copyright (c) 2012 Amazon.com.  All rights reserved.
*
*********************************************************************************/
package com.mars.faith.das.dao.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import lombok.extern.apachecommons.CommonsLog;

/**
 * reflection utility class
 * @author wdong
 *
 */
@CommonsLog
public class ReflectionUtils {
    //private static final Logger LOGGER = Logger.getLogger(ReflectionUtils.class);

    /**
	 * 直接读取对象属性值,无视private/protected修饰符,不经过getter函数.
	 */
    public static Object getFieldValue(final Object object, final String fieldName) {
        Field field = getDeclaredField(object, fieldName);

        if (field == null)
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + object + "]");

        makeAccessible(field);

        Object result = null;
        try {
            result = field.get(object);
        } catch (IllegalAccessException e) {
            log.error(e.getMessage());
        }
        return result;
    }

    /**
	 * 直接设置对象属性值,无视private/protected修饰符,不经过setter函数.
	 */
    public static void setFieldValue(final Object object, final String fieldName, final Object value) {
        Field field = getDeclaredField(object, fieldName);

        if (field == null) {
            log.warn("Could not find field [" + fieldName + "] on target [" + object + "]");
            return;
        }

        makeAccessible(field);

        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {

        }
    }
    
    /**
     * 接调用对象方法,无视private/protected修饰符.
     * @param object
     * @param methodName
     * @param parameterTypes
     * @param parameters
     * @return
     * @throws InvocationTargetException
     */
    public static Object invokeMethod(final Object object, final String methodName, final Class<?>[] parameterTypes, final Object[] parameters) throws InvocationTargetException {
    	Method method = getDeclaredMethod(object, methodName, parameterTypes);
    	
    	if(method == null) {
    		throw new IllegalArgumentException(String.format("Could not find method [%s] on target [%s]", methodName, object));
    	}
    	
    	method.setAccessible(true);
    	
    	try {
    		return method.invoke(object, parameters);
    	} catch(IllegalAccessException e) {
    		log.error(e.getMessage(), e);
    	}
    	
    	return null;
    }

    protected static Field getDeclaredField(final Object object, final String fieldName) {
        for (Class<?> superClass = object.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {}
        }
        return null;
    }

    protected static void makeAccessible(final Field field) {
        if (!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers())) {
            field.setAccessible(true);
        }
    }
    
    /**
	 * 循环向上转型,获取对象的DeclaredMethod.
	 */
    protected static Method getDeclaredMethod(Object object, String methodName, Class<?>[] parameterTypes) {
    	for(Class<?> superClass = object.getClass(); superClass !=Object.class; superClass= superClass.getSuperclass()) {
    		try {
    			return superClass.getDeclaredMethod(methodName, parameterTypes);
    		} catch (NoSuchMethodException e) {
    			log.error(e.getMessage(), e);
    		}
    	}
    	return null;
    }
    
    /**
	 * 通过反射,获得Class定义中声明的父类的泛型参数的类型.
	 * eg.
	 * public UserDao extends HibernateDao<User>
	 *
	 * @param clazz The class to introspect
	 * @return the first generic declaration, or Object.class if cannot be determined
	 */
	@SuppressWarnings("unchecked")
	public static <T> Class<T> getSuperClassGenricType(final Class<?> clazz) {
		return getSuperClassGenricType(clazz, 0);
	}

	/**
	 * 通过反射,获得Class定义中声明的父类的泛型参数的类型.
	 * eg.
	 * public UserDao extends HibernateDao<User>
	 *
	 * @param clazz The class to introspect
	 * @return the first generic declaration, or Object.class if cannot be determined
	 */
	@SuppressWarnings({"rawtypes" })
	public static Class getSuperClassGenricType(final Class clazz, final int index) {

		Type genType = clazz.getGenericSuperclass();

		if (!(genType instanceof ParameterizedType)) {
			log.warn(clazz.getSimpleName() + "'s superclass not ParameterizedType");
			return Object.class;
		}

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {
			log.warn("Index: " + index + ", Size of " + clazz.getSimpleName() + "'s Parameterized Type: "
					+ params.length);
			return Object.class;
		}
		if (!(params[index] instanceof Class)) {
			log.warn(clazz.getSimpleName() + " not set the actual class on superclass generic parameter");
			return Object.class;
		}

		return (Class) params[index];
	}

	
	/**
	 * 将反射时的checked exception转换为unchecked exception.
	 */
	public static IllegalArgumentException convertToUncheckedException(Exception e) {
		if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
				|| e instanceof NoSuchMethodException)
			return new IllegalArgumentException("Refelction Exception.", e);
		else
			return new IllegalArgumentException(e);
	}
}
