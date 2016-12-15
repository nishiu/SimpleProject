package com.fm.commons.service;

import com.fm.commons.logic.BeanFactory;
import com.fm.commons.logic.LocalStorage;
import com.fm.commons.util.AESUtils;
import com.fm.commons.util.LocalConfig;
import com.fm.commons.util.StringUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by admin on 2016/10/19.
 */

public class RpcServiceFactory {

    public static final String API_BASE_URL = LocalConfig.get("app_url");
    public static final boolean openEncryp = LocalConfig.get("openEncryp",true);

    private static final String Session_TAG = "c_sessionId";
    private static final int DEFAULT_TIMEOUT = 10;

    private static OkHttpClient.Builder httpClient  = new OkHttpClient.Builder()
            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            .addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Interceptor.Chain chain) throws IOException {
                    Request original = chain.request();
                    RequestBody oldBody = original.body();
                    Buffer buffer = new Buffer();
                    oldBody.writeTo(buffer);
                    final String src = buffer.readUtf8();
                    final RequestBody requestBody = new RequestBody() {
                        @Override
                        public MediaType contentType() {
                            return MediaType.parse("application/json; charset=UTF-8");
                        }


                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {
                            byte[] bytes = openEncryp ? encrypt(src) : src.getBytes();
                            sink.write(bytes);
                        }
                    };

                    // Request customization: add request headers
                    Request request = new Request.Builder()
                            .url(original.url())
                            .post(requestBody)
                            .header("Accept-Encoding", "gzip")
                            .header("Cookie", getHttpSessionId())
                            .build();

                    //Response decrypt
                    Response response = chain.proceed(request);
                    final String responseBody = parseResponse(decrypt(response.body().bytes()));
                    Response.Builder responseBuilder = response.newBuilder()
                            .body(new ResponseBody() {
                                @Override
                                public MediaType contentType() {
                                    return null;
                                }

                                @Override
                                public long contentLength() {
                                    return responseBody.length();
                                }

                                @Override
                                public BufferedSource source() {
                                    return (new Buffer()).writeString(responseBody, Util.UTF_8);
                                }
                            });
                    return responseBuilder.build();
                }
            });

    private static Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(API_BASE_URL)
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create());

    public static <T> T createService(Class<T> serviceClass) {
        Retrofit retrofit = builder.client(httpClient.build()).build();
        return retrofit.create(serviceClass);
    }

    private static String initSession() {
        String sessionId = UUID.randomUUID().toString().replace("-", "");
        BeanFactory.getBean(LocalStorage.class).put(Session_TAG, sessionId);
        return sessionId;
    }

    private static String getHttpSessionId() {
        String sessionId = BeanFactory.getBean(LocalStorage.class).get(Session_TAG, "");
        if (StringUtils.isEmpty(sessionId))
            return initSession();
        return sessionId;
    }

    private static byte[] encrypt(String oldBody) {
        String aes = AESUtils.encryptAES(oldBody);
        return AESUtils.encryptGZIP(aes);
    }

    private static String decrypt(byte[] oldBody) {
        String unGZip = AESUtils.decryptGZIP(oldBody);
        return AESUtils.decryptAES(unGZip);
    }

    private static String parseResponse(String value) {
        JsonParser jsonParser = new JsonParser();
        Gson gson = new Gson();
        JsonObject json = jsonParser.parse(value).getAsJsonObject();
        JsonElement error = json.get("error");
        if (error != null && !error.isJsonNull()) {
            throw new RuntimeException(error.toString());
        } else {
            JsonElement resultJson = json.get("result");
            return resultJson.toString();
        }
    }
}
