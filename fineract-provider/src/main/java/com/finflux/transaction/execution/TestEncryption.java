package com.finflux.transaction.execution;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by dhirendra on 25/01/17.
 */
public class TestEncryption {

	public static void main(String args[]){
//		DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSSSSSSSS");
//		String d1 = "2017-01-31 11:04:04.340419";
//		String d2 = "2017-01-31 11:04:04.3";
//		String d3 = "2017-01-31 11:04:04.0";
//		System.out.println(dateTimeFormatter.print(dateTimeFormatter.parseDateTime(d1)));
//		System.out.println(dateTimeFormatter.print(dateTimeFormatter.parseDateTime(d2)));
//		System.out.println(dateTimeFormatter.print(dateTimeFormatter.parseDateTime(d3)));
		System.out.println("abcdsdsd-asdasdasd-asdasdasd".replaceAll("\\w(?=\\w{4})", "X"));
		System.out.println("asdasdasd".replaceAll("\\w(?=.{4})", "X"));
//		String password = "chaitanya";
//		String salt = "5c07";
//		String plainText = "test";
//		TextEncryptor encryptor = Encryptors.text(password, salt);
//		//String encryptedString = encryptor.encrypt(plainText);
//		//System.out.println(encryptedString);
//		String encryptedString = "de4e07fa160d897eecd0ccb208adf18600a176b3c5ce58b1c71db5ce42cf6515";
//		System.out.println(encryptor.decrypt(encryptedString));
//		String dateStr1 = ""

	}
}
