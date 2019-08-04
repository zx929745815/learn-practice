package com.xtu.crypt;

import com.xtu.enumeration.CryptTypeEnum;

public class CryptLoader {

    /**
                 * 加载所有加密方式实现类
     */
    public void loadCrypt() {
    	//现在仅加密手机号
        CryptContext.setCrypt(CryptTypeEnum.phone, new PhoneCryptImpl());
    }
}