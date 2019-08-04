package com.xtu.discover;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.apache.ibatis.annotations.Param;
import org.springframework.core.ParameterNameDiscoverer;

/**
 * mybatis 接口参数名发现器
 * @author zx929
 *
 */
public class MyBatisParameterNameDiscoverer implements ParameterNameDiscoverer{

	@Override
	public String[] getParameterNames(Method method) {
		// TODO Auto-generated method stub
		return getParameterNames(method.getParameters(),method.getParameterAnnotations());
	}

	@Override
	public String[] getParameterNames(Constructor<?> ctor) {
		// TODO Auto-generated method stub
		return getParameterNames(ctor.getParameters(), ctor.getParameterAnnotations());
	}
	
	/**
	 * 获取 mybaits 的参数名
	 * @param parameters
	 * @param parameterAnnotations
	 * @return
	 */
	private String[] getParameterNames(Parameter[] parameters,Annotation[][] parameterAnnotations) {
		String[] parameterNames = new String[parameters.length];
		
		for(int i=0; i < parameters.length; i++) {
			Parameter param = parameters[i];
			String paramName = param.getName();
			
			for(Annotation annotation: parameterAnnotations[i]) {
				// mybatis 自定义参数  使用 @Param 标注
				if(annotation instanceof Param) {
					//获取 @Param 的 value 值
					String custName = ((Param)annotation).value();
					if(custName != null) {
						paramName = custName;
						break;
					}
				}
			}
			parameterNames[i] = paramName;
			
		}
		return parameterNames;
	}

}
