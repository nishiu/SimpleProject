package com.fm.commons.http;

import android.util.Log;

import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.LocalStorage;
import com.fm.commons.thread.UIThread;
import com.fm.commons.util.AESUtils;
import com.fm.commons.util.ConnectionUtils;
import com.fm.commons.util.StringUtils;
import com.fm.commons.util.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;

/**
 * 使用前一定调用ContextHolder.set()
 * 后续内容会使用ContextHolder
 * @author L
 *
 */
public class JsonRpcClient {
	
	private static final String TAG = "Mu.JsonRpcClient";

	private static final int DEFAULT_TIMEOUT = 10 * 1000;

	private static final String Session_TAG = "c_sessionId";

	private String url;
	private long timeout;
	private Gson gson;
	private JsonParser jsonParser;
	private boolean openEncryp = false;	//是否开启加密

	public JsonRpcClient(String url,boolean openEncryp) {
		this.url = url;
		gson = new Gson();
		jsonParser = new JsonParser();
		timeout = DEFAULT_TIMEOUT;
		this.openEncryp = openEncryp;
	}

	/**
	 * 调用服务端接口
	 * 
	 * @param method
	 * @param params
	 *            自定义义对象、 Map（不能直接使用JsonObject）
	 * @param resultType
	 * @return
	 */
	public <T> T invoke(String method, Object params, Type resultType) {
		return invoke(method, params, (Object) resultType);
	}

	/**
	 * 调用服务端接口
	 * 
	 * @param method
	 * @param params
	 *            自定义义对象、 Map（不能直接使用JsonObject）
	 * @param resultType
	 * @return
	 */
	public <T> T invoke(String method, Object params, Class<T> resultType) {
		return invoke(method, params, (Object) resultType);
	}

	@SuppressWarnings("unchecked")
	private <T> T invoke(String method, Object params, Object resultType) {
		try {
			if (!ConnectionUtils.isConnectionAvailable(ContextHolder.get())) {
				UIThread.postDelayed(new Runnable() {
					@Override
					public void run() {
						ToastUtils.showShortToast(ContextHolder.get(), "您的网络飞走了，快把它捉回来!");
					}
				}, 500);
				return null;
			}
			String request = buildRequest(method, params);
			String response = okHttpAESGZipRequest(request);
			if (StringUtils.isEmpty(response)){
				ToastUtils.showShortToast(ContextHolder.get(), "亲，登录失效，请重新登录!");
				return null;
			}
			T result = (T) parseResponse(response, resultType);
			return result;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
			return null;
		}
	}

	private final OkHttpClient client = new OkHttpClient();
	
	private String okHttpAESGZipRequest(final String src) {
		RequestBody requestBody = new RequestBody() {
			@Override
			public MediaType contentType() {
				return MediaType.parse("application/json; charset=UTF-8");
			}

			@Override
			public void writeTo(BufferedSink sink) throws IOException {
				if(openEncryp){					
					String aes = AESUtils.encryptAES(src);
					byte[] bytes = AESUtils.encryptGZIP(aes);
					sink.write(bytes);
				}else sink.write(src.getBytes());
			}
		};
		
		Request req = new Request.Builder()
		        .url(url)
		        .post(requestBody)
		        .addHeader("Accept-Encoding", "gzip")
		        .addHeader("Cookie", getHttpSessionId())
		        .build();
	    Response response;
		try {
			response = client.newCall(req).execute();
			if(response.code() == 401){
				return null;
			}
			if(openEncryp){
				byte[] content = response.body().bytes();
				String unGZip = AESUtils.decryptGZIP(content);
				return AESUtils.decryptAES(unGZip);
			}
			return response.body().string();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}


	private String getHttpSessionId() {
		String sessionId = BeanFactory.getBean(LocalStorage.class).get(Session_TAG, "");
		if (StringUtils.isEmpty(sessionId))
			initSession();
		return sessionId;
	}

	private void initSession() {
		String sessionId = UUID.randomUUID().toString().replace("-", "");
		BeanFactory.getBean(LocalStorage.class).put(Session_TAG,sessionId);
	}

	@Deprecated
	@SuppressWarnings({ "unchecked", "unused" })
	private <T> T invokeHttpURLConnect(String method, Object params, Object resultType) {
		try {
			String request = buildRequest(method, params);
			HttpURLConnection connection = createConnection();
			sendRequest(connection, request);
			String response = getResponse(connection);
			connection.disconnect();
			T result = (T) parseResponse(response, resultType);
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Deprecated
	private HttpURLConnection createConnection() throws IOException, MalformedURLException, ProtocolException {
		HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection(Proxy.NO_PROXY);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Cookie", getSessionId());
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setAllowUserInteraction(false);
		connection.setDefaultUseCaches(false);
		connection.setUseCaches(false);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestProperty("Content-Type", "application/json-rpc");
		connection.setConnectTimeout((int) timeout);
		connection.setReadTimeout((int) timeout);
		connection.connect();
		return connection;
	}

	/**
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private String getSessionId() throws MalformedURLException, IOException {
		String sessionId = BeanFactory.getBean(LocalStorage.class).get(Session_TAG, "");
		if (!StringUtils.isEmpty(sessionId))
			return sessionId;
		sessionId = UUID.randomUUID().toString().replace("-", "");
		return sessionId;
	}

	private void sendRequest(HttpURLConnection connection, String request) throws IOException {
		OutputStream output = connection.getOutputStream();
		output.write(request.getBytes("UTF-8"));
		output.flush();
		output.close();
	}

	private String getResponse(HttpURLConnection connection) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		InputStream in = connection.getInputStream();
		while ((len = in.read(buffer)) != -1) {
			out.write(buffer, 0, len);
		}
		in.close();
		return out.toString("UTF-8");
	}

	private String buildRequest(String method, Object params) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("id", 1);
		request.put("method", method);
		if (!params.getClass().isArray())
			params = new Object[] { params };
		request.put("params", params);
		return gson.toJson(request);
	}

	private Object parseResponse(String value, Object resultType) {
		JsonObject json = jsonParser.parse(value).getAsJsonObject();
		JsonElement error = json.get("error");
		if (error != null && !error.isJsonNull()) {
			throw new RuntimeException(error.toString());
		} else {
			JsonElement resultJson = json.get("result");
			if (resultType instanceof Class<?>)
				return gson.fromJson(resultJson, (Class<?>) resultType);
			if (resultType instanceof Type)
				return gson.fromJson(resultJson, (Type) resultType);
			return gson.fromJson(resultJson, Object.class);
		}
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}
}
