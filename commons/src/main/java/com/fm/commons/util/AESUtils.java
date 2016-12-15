package com.fm.commons.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;

public class AESUtils {

	private static final String IV_STRING = "z9y8I@x78xYLb%E$";
	private static final String KEY = "SakVgM!Tk#%M6%XG";

	private static final int BUFFER_SIZE = 1024;  
	
	/**
	 * 加密
	 * @param content	要加密的内容
	 * @return			加密后的字符串
	 */
	public static String encryptAES(String content) {
		try {
			byte[] byteContent = content.getBytes("UTF-8");
			// 注意，为了能与 iOS 统一
			// 这里的 key 不可以使用 KeyGenerator、SecureRandom、SecretKey 生成
			byte[] enCodeFormat = KEY.getBytes("UTF-8");
			SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, "AES");

			byte[] initParam = IV_STRING.getBytes("UTF-8");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);

			// 指定加密的算法、工作模式和填充方式
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);

			byte[] encryptedBytes = cipher.doFinal(byteContent);

			// 同样对加密后数据进行 base64 编码
			return Base64.encodeToString(encryptedBytes,Base64.DEFAULT);
		} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException
				| IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException
				| NoSuchPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解密
	 * @param content	要解密的字符串
	 * @return			解密后的字符串
	 */
	public static String decryptAES(String content) {
		// base64 解码

		Cipher cipher;
		try {
			byte[] encryptedBytes = Base64.decode(content.getBytes("UTF-8"),Base64.DEFAULT);
			byte[] enCodeFormat = KEY.getBytes("UTF-8");
			SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, "AES");
			
			byte[] initParam = IV_STRING.getBytes("UTF-8");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

			byte[] result = cipher.doFinal(encryptedBytes);

			return new String(result, "UTF-8");
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	/**
	 * GZIP 加密
	 * 
	 * @param src
	 * @return
	 */
	public static byte[] encryptGZIP(String src) {
		if (StringUtils.isEmpty(src)) {
			return null;
		}
		ByteArrayOutputStream baos = null;
		try {
			// gzip压缩
			baos = new ByteArrayOutputStream();
			GZIPOutputStream gzip = new GZIPOutputStream(baos);
			gzip.write(src.getBytes("UTF-8"));
			gzip.close();
			byte[] encode = baos.toByteArray();
			baos.flush();
			return encode;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != baos)
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}

	/**
	 * GZIP 解密
	 * @param src
	 * @return
	 */
	public static String decryptGZIP(byte[] decode) {
		if (null == decode || decode.length == 0) {
			return null;
		}

		try {
			// gzip 解压缩
			ByteArrayInputStream bais = new ByteArrayInputStream(decode);
			GZIPInputStream gzip = new GZIPInputStream(bais);

			byte[] buf = new byte[BUFFER_SIZE];
			int len = 0;

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			while ((len = gzip.read(buf, 0, BUFFER_SIZE)) != -1) {
				baos.write(buf, 0, len);
			}
			gzip.close();
			baos.flush();

			decode = baos.toByteArray();
			baos.close();
			return new String(decode, "UTF-8");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}
	
	/**
	 * GZIP 解密
	 * @param src 必须是UTF-8 格式
	 * @return
	 */
	public static String decryptGZIP(String src) {
		if (StringUtils.isEmpty(src)) {
			return null;
		}
		try {
			return decryptGZIP(src.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
