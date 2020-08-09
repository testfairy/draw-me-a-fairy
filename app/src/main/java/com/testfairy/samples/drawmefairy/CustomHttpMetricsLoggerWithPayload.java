package com.testfairy.samples.drawmefairy;

import com.testfairy.TestFairy;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

/**
 * Here is a sample implementation of an okhttp3 Interceptor.
 * <p>
 * This interceptor sends the http/s request information, and response data
 * to TestFairy, to log it along the session currently being recorded.
 * <p>
 * This implementation also keeps the payload of the request and of the
 * response. It requires a specific attribute to be set on your account,
 * so if you're in such a requirement, please contact TestFairy's support team.
 */
public class CustomHttpMetricsLoggerWithPayload implements Interceptor {

	private byte[] getBytes(Request request) throws IOException {

		try {
			Buffer buffer = new Buffer();
			Request copy = request.newBuilder().build();
			copy.body().writeTo(buffer);
			return buffer.readByteArray();
		} catch (Throwable t) {
			return new byte[0];
		}
	}

	private byte[] getBytes(Response response) {
		try {
			ResponseBody body = response.peekBody(128 * 1024);
			return body.bytes();
		} catch (Throwable t) {
			return new byte[0];
		}
	}

	@Override
	public Response intercept(Chain chain) throws IOException {

		Request request = chain.request();
		long startTimeMillis = System.currentTimeMillis();
		long requestSize = (request.body() != null ? request.body().contentLength() : 0);

		Response response;

		byte[] requestBody = getBytes(request);

		try {
			response = chain.proceed(request);
		} catch (IOException e) {
			long endTimeMillis = System.currentTimeMillis();
			TestFairy.addNetworkEvent(request.url().uri(), request.method(), -1, startTimeMillis, endTimeMillis, requestSize, -1, e.getMessage());
			throw e;
		}

		long endTimeMillis = System.currentTimeMillis();
		long responseSize = response.body() != null ? response.body().contentLength() : 0;

		String requestHeaders = request.headers().toString();
		String responseHeaders = response.headers().toString();

		byte[] responseBody = getBytes(response);
		TestFairy.addNetworkEvent(
			request.url().uri(),
			request.method(),
			response.code(),
			startTimeMillis,
			endTimeMillis,
			requestSize,
			responseSize,
			null,
			/* optional */
			requestHeaders,
			requestBody,
			responseHeaders,
			responseBody
		);

		return response;
	}
}