package com.xtu.crypt;

public class PhoneCryptImpl implements Crypt{

	@Override
	public String encrypt(String plain) {
		return plain.substring(0, 3) + "****" + plain.substring(7,11);
	}

	@Override
	public String decrypt(String cipher) {
		// TODO Auto-generated method stub
		return null;
	}

}
