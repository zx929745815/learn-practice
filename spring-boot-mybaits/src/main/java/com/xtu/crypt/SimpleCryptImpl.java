package com.xtu.crypt;



import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *     使用AES 进行加解密
 * @author zx929
 *
 */
public class SimpleCryptImpl implements Crypt{
	
	private static final String sKey = "1234567887654321";
	
	Logger logger = LoggerFactory.getLogger(SimpleCryptImpl.class);

	@Override
	public String encrypt(String plain){
		if (sKey == null) {
			logger.info("Key为空null");
            return null;
        }
        // 判断Key是否为16位
        if (sKey.length() != 16) {
        	logger.info("Key长度不是16位");
            return null;
        }
        byte[] encrypted = null;
		try {
			byte[] raw = sKey.getBytes("utf-8");
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			encrypted = cipher.doFinal(plain.getBytes("utf-8"));
		} catch (Exception e) {
			logger.info(plain +" encrypt error:{}",e);
		}

        return new Base64().encodeToString(encrypted);//此处使用BASE64做转码功能，同时能起到2次加密的作用。
	}

	@Override
	public String decrypt(String sSrc) {
		try {
            // 判断Key是否正确
            if (sKey == null) {
            	logger.info("Key为空null");
                return null;
            }
            // 判断Key是否为16位
            if (sKey.length() != 16) {
            	logger.info("Key长度不是16位");
                return null;
            }
            byte[] raw = sKey.getBytes("utf-8");
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
            byte[] encrypted1 = new Base64().decode(sSrc);//先用base64解密
            try {
                byte[] original = cipher.doFinal(encrypted1);
                String originalString = new String(original,"utf-8");
                return originalString;
            } catch (Exception e) {
                logger.info(sSrc + " decrypt error:{}",e.toString());
                return null;
            }
        } catch (Exception ex) {
            logger.info(sSrc + " decrypt error:{}",ex.toString());
            return null;
        }
	}

}
