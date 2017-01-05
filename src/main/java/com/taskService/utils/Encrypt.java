package com.taskService.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class Encrypt {
	private static int workload = 12;

	public String textEncrypt(String plainText) {
		return hashPassword(plainText);
	}
	public Boolean compareWithEncryptText(String plainText,String encryptText) {
		
		return checkPassword(plainText, encryptText)
				? true : false;
	}
	public static boolean checkPassword(String password_plaintext, String stored_hash) {
		boolean password_verified = false;

		if(null == stored_hash || !stored_hash.startsWith("$2a$"))
			throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");

		password_verified = BCrypt.checkpw(password_plaintext, stored_hash);

		return(password_verified);
	}
	public static String hashPassword(String password_plaintext) {
		String salt = BCrypt.gensalt(workload);
		String hashed_password = BCrypt.hashpw(password_plaintext, salt);

		return(hashed_password);
	}



}
