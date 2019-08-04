package com.xtu.interceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.xtu.annotation.CryptField;
import com.xtu.crypt.CryptContext;
import com.xtu.crypt.CryptLoader;

/**
 * mybaits 拦截器
 * @author zx929
 *
 */

//对哪些方法进行拦截
@Intercepts(value = 
	{@Signature(type = Executor.class,method = "query",
				args = {MappedStatement.class,Object.class,RowBounds.class,ResultHandler.class} )
})
@Component
public class CryptInterceptor implements Interceptor{
	
	Logger logger = LoggerFactory.getLogger(CryptInterceptor.class);
	
    public CryptInterceptor() {
        (new CryptLoader()).loadCrypt();
    }


	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		
		 MappedStatement statement = (MappedStatement) args[0];
		
		  // 获得出参
        Object returnValue = invocation.proceed();

        // 出参解密
        if (isNotCrypt(returnValue)) {
            return returnValue;
        }

        // 获得方法注解(针对返回值)
        CryptField cryptField = getMethodAnnotations(statement);
        if (returnValue instanceof String) {
            return stringEncrypt((String) returnValue, cryptField);
        }
        if (returnValue instanceof List) {
        	listEncrypt((List) returnValue, cryptField);
            return returnValue;
        }

        return returnValue;

	}

	@Override
	public Object plugin(Object target) {
		// TODO Auto-generated method stub
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
		// TODO Auto-generated method stub
		
	}
	
	
    /**
                 * 判断是否需要加解密
     *
     * @param obj
                  *            待加密对象
     * @return 是否需要加密
     */
    private boolean isNotCrypt(Object obj) {
        return obj == null || obj instanceof Double || obj instanceof Integer || obj instanceof Long
                || obj instanceof Boolean;
    }
    
    /**
     * String 加密
     *
     * @param name
     *            参数名称
     * @param plain
     *            参数明文
     * @param paramAnnotations
     *            加密注解
     * @return 密文
     */
    private String stringEncrypt(String name, String plain, Map<String, CryptField> paramAnnotations) {
        return stringEncrypt(plain, paramAnnotations.get(name));
    }
    
    /**
     * list 加密
     *
     * @param plainList
     *            明文列表
     * @param cryptField
     *            加密方式注解
     * @return 密文列表
     * @throws IllegalAccessException
     */
    private List listEncrypt(List plainList, CryptField cryptField) throws IllegalAccessException {
        for (int i = 0; i < plainList.size(); i++) {
            Object plain = plainList.get(i);
            // 判断不需要解析的类型
            if (isNotCrypt(plain) || plain instanceof Map) {
                break;
            }
            if (plain instanceof String) {
                plainList.set(i, stringEncrypt((String) plain, cryptField));
                continue;
            }
            beanEncrypt(plain);
        }

        return plainList;
    }
    
    /**
     * bean 加密
     *
     * @param plainObject
     *            明文对象
     * @throws IllegalAccessException
     */
    private void beanEncrypt(Object plainObject) throws IllegalAccessException {
        Class objClazz = plainObject.getClass();
        Field[] objFields = objClazz.getDeclaredFields();
        for (Field field : objFields) {
            CryptField cryptField = field.getAnnotation(CryptField.class);
            if (cryptField != null) {
                field.setAccessible(true);
                //获得 plainObject 这个 field 的值
                Object plain = field.get(plainObject);
                //根据不同的类型进行加密
                if (plain == null) {
                    continue;
                }
                if (field.getType().equals(String.class)) {
                    field.set(plainObject, stringEncrypt((String) plain, cryptField));
                    continue;
                }
                if (field.getType().equals(List.class)) {
                    field.set(plainObject, listEncrypt((List) plain, cryptField));
                    continue;
                }
                field.setAccessible(false);
            }
        }
    }

    /**
     * String 加密
     *
     * @param plain
     *            参数明文
     * @param cryptField
     *            加密注解
     * @return 密文
     */
    private String stringEncrypt(String plain, CryptField cryptField) {
        if (plain.equals("") || cryptField == null) {
            return plain;
        }

        return CryptContext.getCrypt(cryptField.value()).encrypt(plain);
    }
	
	/**
	  * 获取被拦截的 方法的 CryptField 注解
	 * @param statement
	 * @return
	 * @throws ClassNotFoundException
	 */
	private CryptField getMethodAnnotations(MappedStatement statement) throws ClassNotFoundException {
		String id = statement.getId();
		logger.info("id is:{}",id);
		// id is:com.xtu.dao.UserMapper.selectByExample
		Method method = null;
		//获取全类名，反射获取Class对象
		Class<?> clazz = Class.forName(id.substring(0,id.lastIndexOf(".")));
		for(Method _method: clazz.getDeclaredMethods()) {
			// 获取方法名
			if(_method.getName().equals(id.substring(id.lastIndexOf(".")+1))) {
				method = _method;
			}
			
		}
		if(method == null) {
			return null;
		}
		
		return method.getAnnotation(CryptField.class);
	}

}
