package com.lzc.pineapple.util;

public class UrlDecodeUtils {
	public static String encrypt(int key, String str) {
		byte[] bytes = str.getBytes();
		int bytes_len = bytes.length;

		int encrypt_len = bytes_len * 2;
		byte[] encrypt_bytes = new byte[encrypt_len];
		int i = 0;
		int j = 0;
		for (i = 0; i < bytes_len; i++) {

			int b1 = bytes[i];

			int b2 = (key ^ b1);
			int c1 = (b2 % 16);
			int c2 = (b2 / 16);
			c1 = (c1 + 65);
			c2 = (c2 + 65);

			encrypt_bytes[j] = (byte) c1;
			encrypt_bytes[j + 1] = (byte) c2;
			j = j + 2;
		}

		return new String(encrypt_bytes);

	}

	public static String decrypt(int key, String str) {
		byte[] bytes = str.getBytes();
		int len = bytes.length;
		if (len % 2 != 0) {
			return "";
		}
		len = len / 2;
		byte[] decrypt_bytes = new byte[len];
		int i = 0;
		int j = 0;
		for (i = 0; i < len; i++) {
			int c1 = bytes[j];
			int c2 = bytes[j + 1];
			j = j + 2;
			c1 = (c1 - 65);
			c2 = (c2 - 65);
			int b2 = (c2 * 16 + c1);
			int b1 = (b2 ^ key);
			decrypt_bytes[i] = (byte) b1;

		}

		return new String(decrypt_bytes);
	}
}
