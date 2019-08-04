package com.xtu.annotation;
/*
    * 自定义注解
 */

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.xtu.enumeration.CryptTypeEnum;

@Target({ElementType.FIELD,ElementType.PARAMETER,ElementType.METHOD}) // 注解在字段，参数，方法 中有效
@Retention(RetentionPolicy.RUNTIME) // 保留到运行时刻
@Documented  //生成对应文档
public @interface CryptField {
	
	CryptTypeEnum value() default CryptTypeEnum.AES;

}
