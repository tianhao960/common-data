package com.mars.faith.das.dao.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.BindingException;

/**
 * Utility class for MyBatis arguments parsing, some code copy from MyBatis
 * MapperMethod class.
 * 
 * @author wdong
 */
public class ParamMethod {

    private List<Integer> paramPositions;
    private boolean hasNamedParameters;
    private List<String> paramNames;
    private Method method;

    public ParamMethod(Method method) {
        paramPositions = new ArrayList<Integer>();
        paramNames = new ArrayList<String>();
        hasNamedParameters = false;
        this.method = method;
        
        setupMethodSignature();
    }

    public Object getParam(Object[] args) {
        final int paramCount = paramPositions.size();
        if (args == null || paramCount == 0) {
            return null;
        } else if (!hasNamedParameters && paramCount == 1) {
            return args[paramPositions.get(0)];
        } else {
            Map<String, Object> param = new MapperParamMap<Object>();
            for (int i = 0; i < paramCount; i++) {
                param.put(paramNames.get(i), args[paramPositions.get(i)]);
            }
            
            for (int i = 0; i < paramCount; i++) {
                String genericParamName = "param" + String.valueOf(i + 1);
                if (!param.containsKey(genericParamName)) {
                    param.put(genericParamName, args[paramPositions.get(i)]);
                }
            }
            return param;
        }
    }

    private void setupMethodSignature() {
        final Class<?>[] argTypes = method.getParameterTypes();
        for (int i = 0; i < argTypes.length; i++) {
            String paramName = String.valueOf(paramPositions.size());
            paramName = getParamNameFromAnnotation(i, paramName);
            paramNames.add(paramName);
            paramPositions.add(i);
        }
    }
    
    private String getParamNameFromAnnotation(int i, String paramName) {
        Object[] paramAnnos = method.getParameterAnnotations()[i];
        for (int j = 0; j < paramAnnos.length; j++) {
          if (paramAnnos[j] instanceof Param) {
            hasNamedParameters = true;
            paramName = ((Param) paramAnnos[j]).value();
          }
        }
        return paramName;
      }

    public static class MapperParamMap<V> extends HashMap<String, V> {

        private static final long serialVersionUID = -2212268410512043556L;

        @Override
        public V get(Object key) {
            if (!super.containsKey(key)) {
                throw new BindingException("Parameter '" + key + "' not found. Available parameters are "
                    + this.keySet());
            }
            return super.get(key);
        }

    }
}
