package com.testfairy.samples.drawmefairy;


import com.testfairy.TestFairy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class CustomHttpMetricsLogger implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        long startTimeMillis = System.currentTimeMillis();
        Long requestSize = request.body() != null ? request.body().contentLength() : 0;
        Response response;
        try {
            response = chain.proceed(request);
        } catch (IOException e) {
            long endTimeMillis = System.currentTimeMillis();
            TestFairy.addNetworkEvent(request.url().uri(), request.method(), -1, startTimeMillis, endTimeMillis, requestSize, -1, e.getMessage());
            throw e;
        }

        long endTimeMillis = System.currentTimeMillis();
        long responseSize = response.body() != null ? response.body().contentLength() : 0;
        TestFairy.addNetworkEvent(
            request.url().uri(),
            request.method(),
            response.code(),
            startTimeMillis,
            endTimeMillis,
            requestSize,
            responseSize,
            null
        );
        return response;
    }
}