package com.fm.commons.http;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpPoster {
	
	private static final int DEFAULT_TIMEOUT = 60 * 1000;
	
	protected static Logger logger = LoggerFactory.getLogger("HttpPoster");
	
	public static String post(String url, String param) {
		return post(url, param, DEFAULT_TIMEOUT);
	}
	
	public static String post(String url, Map<String, Object> params){
		return post(url, params, DEFAULT_TIMEOUT);
	}
	
	public static String post(String url, Map<String, Object> params, int timeout) {
		return post(url, encodeParams(params), timeout);
	}
	
	private static String encodeParams(Map<String, Object> params){
		try {
			StringBuilder builder = new StringBuilder();
			for (Map.Entry<String, Object> entry : params.entrySet()) {
			    if (builder.length() != 0) builder.append('&');
			    builder.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
			    builder.append('=');
			    builder.append(URLEncoder.encode(String.valueOf(entry.getValue()), "UTF-8"));
			}
			return builder.toString();
		} catch (Exception e) {
			throw new RuntimeException("encode params error", e);
		}
	}
	
	public static String post(String url, String param, int timeout) {
		try {
			HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();			
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(timeout);
			conn.setConnectTimeout(timeout);
			writeParam(param, conn);
//			logger.debug("contentLength="+conn.getContentLength()
//					+", contentEncoding : "+conn.getContentEncoding()
//					+", contentType : "+conn.getContentType());
			InputStream in = conn.getInputStream();
			String result = readResponse(in);
			return result;
		} catch (Exception e) {
			throw new RuntimeException("post http request error : url="+url, e);
		}
	}

	public static InputStream post(String url){
		try {
			HttpURLConnection conn = (HttpURLConnection)new URL(url).openConnection();			
//			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(DEFAULT_TIMEOUT);
			conn.setConnectTimeout(DEFAULT_TIMEOUT);
			conn.connect();
			InputStream in = conn.getInputStream();
			return in;
		} catch (Exception e) {
			throw new RuntimeException("post http request error : url="+url, e);
		}
	}
	
	private static void writeParam(String param, URLConnection conn) throws Exception {
		if(param==null) return;
		byte[] body = param.getBytes("utf-8");
		OutputStream out = conn.getOutputStream();
		out.write(body);
		out.flush();
		out.close();
	}
	
	private static String readResponse(InputStream input) throws Exception{		
		StringBuffer buffer = new StringBuffer(300); 
		BufferedReader br = new BufferedReader(new InputStreamReader(input, "utf-8"));
		String line = null;
		while ((line = br.readLine()) != null){
			buffer.append(line).append("\n");
		}
		br.close();
		input.close();
		return buffer.toString();
	}
}
