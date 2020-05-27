package com.example.comp2100_6442_androidproject.utils;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class ConnectionTemplate {

    //do not use localhost, because the default localhost is 127.0.0.1 which is not correct
    public static final String BASE_URL = "http://49.234.105.82:8080/ms"; // on the cloud server
//    public static final String BASE_URL = "http://192.168.1.100:8080/ms";//local for test

    public static Call getConnection(String servlet, String requestParameter) {
        //the following are connections
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .readTimeout(20000, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .get()
                .url(BASE_URL + servlet + requestParameter)
                .build();

        return okHttpClient.newCall(request);

    }

    public static Call postConnection(String servlet,String bodyString){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(20000, TimeUnit.MILLISECONDS)
                .build();


        MediaType mediaType = MediaType.parse("application/json");

        RequestBody requestBody = RequestBody.create(bodyString, mediaType);

        Request request = new Request.Builder()
                .post(requestBody)
                .url(BASE_URL + servlet)
                .build();

        return client.newCall(request);
    }







}
