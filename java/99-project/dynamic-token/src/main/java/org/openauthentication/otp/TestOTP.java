package org.openauthentication.otp;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TestOTP {

	/**
	 * @param args
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
		System.out.println(OneTimePasswordAlgorithm.generateOTP("12345678901234567890".getBytes(), 0, 6, false, -1));
		System.out.println(OneTimePasswordAlgorithm.generateOTP("12345678901234567890".getBytes(), 1, 6, false, -1));
		System.out.println(OneTimePasswordAlgorithm.generateOTP("12345678901234567890".getBytes(), 2, 6, false, -1));
		System.out.println(OneTimePasswordAlgorithm.generateOTP("12345678901234567890".getBytes(), 3, 6, false, -1));
		System.out.println(OneTimePasswordAlgorithm.generateOTP("12345678901234567890".getBytes(), 4, 6, false, -1));
		System.out.println(OneTimePasswordAlgorithm.generateOTP("12345678901234567890".getBytes(), 5, 6, false, -1));
	}

}
