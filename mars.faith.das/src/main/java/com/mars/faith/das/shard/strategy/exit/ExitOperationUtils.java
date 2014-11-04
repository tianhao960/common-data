package com.mars.faith.das.shard.strategy.exit;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import com.mars.faith.das.shard.util.Lists;
import com.mars.faith.das.shard.util.StringUtil;

/**
 * 
 * @author kriswang
 * 
 */
public class ExitOperationUtils {

	public static List<Object> getNonNullList(List<Object> list) {
		List<Object> nonNullList = Lists.newArrayList();

		for (Object obj : list) {
			if (obj != null) {
				nonNullList.add(obj);
			}
		}

		return nonNullList;
	}

	@SuppressWarnings("unchecked")
	public static List<Comparable<Object>> getComparableList(List<Object> results) {
		List<Comparable<Object>> result = (List<Comparable<Object>>) (List<?>) results;
		return result;
	}

	public static Comparable<Object> getPropertyValue(Object obj, String propertyName) {

		try {
			StringBuilder propertyPath = new StringBuilder();

			for (int i = 0; i < propertyName.length(); i++) {
				String s = propertyName.substring(i, i + 1);

				if (i == 0 || propertyName.charAt(i - 1) == '.') {
					propertyPath.append(StringUtil.capitalize(s));
				} else {
					propertyPath.append(s);
				}
			}

			String[] methods = ("get" + propertyPath.toString().replaceAll("\\.", ".get")).split("\\.");

			Object root = obj;

			for (String method : methods) {
				Class<?> clazz = root.getClass();
				Method m = clazz.getMethod(method);
				root = m.invoke(root);

				if (null == root) {
					break;
				}
			}
			
			@SuppressWarnings("unchecked")
			Comparable<Object> result = (Comparable<Object>) root;
			return result;

		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
}
