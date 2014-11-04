package com.mars.faith.das.shard.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.util.Assert;

import com.mars.faith.das.shard.ShardId;
import com.mars.faith.das.shard.annotation.PrimaryKey;

/**
 * 参数处理类
 * @author kriswang
 *
 */
public class ParameterUtil {
	
	private ParameterUtil() {
	}
	
	/**
	 * 参数解析
	 * 
	 * @param obj
	 * @param shardId
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object resolve(final Object obj, final ShardId shardId) {
		try {
			// 基本类型
			if (obj instanceof String || obj instanceof Number
					|| obj instanceof Boolean || obj instanceof Character) {
				Enhancer enhancer = new Enhancer();
				enhancer.setSuperclass(HashMap.class);
				enhancer.setCallback(new MethodInterceptor() {

					private final String prefix = shardId.getPrefix();
					private final String suffix = shardId.getSuffix();
					private final Object parameter = obj;

					public Object intercept(Object object, Method method,
							Object[] args, MethodProxy proxy) throws Throwable {

						if("containsKey".equals(method.getName())){
							//对所有关于Map中containsKey的调用均返回TRUE
							return true;
						} 

						if (args.length > 0 && "get".equals(method.getName())) {
							if ("prefix".equals(args[0])) {
								return prefix;
							} else if ("suffix".equals(args[0])) {
								return suffix;
							} else {
								return parameter;
							}
						}

						return proxy.invokeSuper(object, args);
					}
				});

				return (HashMap) enhancer.create();
			} else if (obj instanceof Map) {
				Map parameter = (Map) obj;
				parameter.put("prefix", shardId.getPrefix());
				parameter.put("suffix", shardId.getSuffix());

				return parameter;
			} else if (obj instanceof List) {
				Map<String, Object> parameter = Maps.newHashMap();
				parameter.put("list", obj);
				parameter.put("prefix", shardId.getPrefix());
				parameter.put("suffix", shardId.getSuffix());

				return parameter;
			} else if (obj != null && obj.getClass().isArray()) {
				Map<String, Object> parameter = Maps.newHashMap();
				parameter.put("array", obj);
				parameter.put("prefix", shardId.getPrefix());
				parameter.put("suffix", shardId.getSuffix());

				return parameter;
			} else if (obj instanceof Object) {
				
				Map<String, Object> parameter = PropertyUtils.describe(obj);
				parameter.put("prefix", shardId.getPrefix());
				parameter.put("suffix", shardId.getSuffix());

				return parameter;
				
			} else if (obj != null) {
				throw new UnsupportedOperationException(String.format(
						"The parameter of type {%s} is not supported.",
						obj.getClass()));
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return null;
	}

	public static Serializable extractPrimaryKey(Object object) {
		if (object != null) {
			Class<?> clazz = object.getClass();
			Field[] first = clazz.getDeclaredFields();
			Field[] second = clazz.getSuperclass().getDeclaredFields();

			Field[] fields = Arrays.copyOf(first, first.length + second.length);
			System.arraycopy(second, 0, fields, first.length, second.length);

			for (Field field : fields) {
				field.setAccessible(true);

				PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
				if (primaryKey != null) {
					try {
						//去除0的情况
						Object result = field.get(object);
						if(result != null && "0".equals(result.toString())){
							return null;
						}
						return (Serializable) result;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

		return null;
	}

	public static Object generatePrimaryKey(Object object, Serializable id) {
		if (object != null) {
			Assert.notNull(id, "generated id can not be null.");

			Class<?> clazz = object.getClass();
			Field[] first = clazz.getDeclaredFields();
			Field[] second = clazz.getSuperclass().getDeclaredFields();

			Field[] fields = Arrays.copyOf(first, first.length + second.length);
			System.arraycopy(second, 0, fields, first.length, second.length);

			for (Field field : fields) {
				field.setAccessible(true);

				PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
				if (primaryKey != null) {
					try {
						//set id
						field.set(object, id);

						return object;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}

		return null;
	}
}
